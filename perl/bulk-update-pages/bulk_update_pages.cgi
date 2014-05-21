#!/usr/bin/perl
use SOAP::Lite +trace;
use CGI qw( :standard :html :param );

# DECLARE VARIABLES
my ($user, $pass, $eid, $auth, $soap_suffix, $soap);
my ($server, $site_name, $directory, $ignore_names, $ignore_these_files, $region_name, $block_path, $no_block, $format_path, $no_format);
my @child_arr; # Arrary child pages to update


## GET CREDENTIALS
$soap_suffix = '/ws/services/AssetOperationService?wsdl';
$user = param('username');
$pass = param('password');
$auth = SOAP::Data->name('authentication')->value([SOAP::Data->name('password')->value($pass), SOAP::Data->name('username')->value($user)]);


# SET VARIABLES
$server = param('server');
$site_name = param('site');
$directory = param('directory');
$ignore_names = param('ignore_names');
$ignore_these_files = param('ignore_these_files');
$region_name = param('region_name');
$block_path = param('block_path');
$block_path =~ s/^\s+//;
	if( $block_path != ''){
		# start with valid char
		while($block_path !~ /^[a-zA-Z0-9_-]/){
			$block_path = reverse($block_path);
	    	chop($block_path);    	
		    $block_path = reverse($block_path);
		}
	}

$no_block = param('no_block');

$format_path = param('format_path');
$format_path =~ s/^\s+//;
	if( $format_path != ''){
		# start with valid char
		while($format_path !~ /^[a-zA-Z_-]/){
			$format_path = reverse($format_path);
	    	chop($format_path);    	
		    $format_path = reverse($format_path);
		}
	}

$no_format = param('no_format');


# CREATE SOAP OBJECT
$server = $server . $soap_suffix;
$soap = SOAP::Lite->new( proxy => $server);

$soap->on_action( sub { "http://www.hannonhill.com/ws/ns/AssetOperationService" });
$soap->autotype(0)->readable(1);
$soap->default_ns('http://www.hannonhill.com/ws/ns/AssetOperationService');

print header, start_html(-title=>'Cascade Server - Bulk Page Update Tool', -BGCOLOR=>'#FFFFFF', -dtd=>'-//W3C//DTD XHTML 1.0 Strict//EN');

# IF NO DIRECTORY - EXIT
if($directory eq ''){
	print "<span class='error'>No Directory specified</span> ";
	print '</body></html>';
	exit;
}

# READ IN ALL PAGES
@child_arr = get_children_in_directory($directory, $soap, $auth, $site_name);

# IF NO PAGES - EXIT
if(scalar @child_arr == 0){
	print "<span class='info'>No pages found. Check the directory:</span> ",$directory;
	print '</body></html>';
	exit;
}

# REMOVE UNWATED PAGES
if($ignore_names ne ''){
	@child_arr = remove_children_by_name(\@child_arr, $ignore_names, $ignore_these_files);
}

# UPDATE EACH PAGE
for my $location (@child_arr) {
	do_action($location, $soap);
}

print '</body></html>';





#****************************************************************************
#********************		GET CHILDREN IN DIRECTORY		  ***************
#****************************************************************************
sub get_children_in_directory{
	my @assets_received; # assets (pages, folders, other) received from query
	my $path = @_[0];
	my $soap = @_[1];
	my $auth = @_[2];
	my $site_name = @_[3];

	my $return = $soap->call('read',
		$auth,
		SOAP::Data->name('identifier')->value(
			[
			SOAP::Data->name('path')->value(
				[
					SOAP::Data->name('path')->value($path),
					SOAP::Data->name('siteName')->value($site_name)
				]
			),
			SOAP::Data->name('type')->value('folder')
			]
		)
	);

	# Read in assets
	if ($return->result->{'success'}){
		if(ref($return->result()->{'asset'}->{'folder'}->{'children'}->{'child'}) eq 'ARRAY' ){
			@assets_received = $return->result()->{'asset'}->{'folder'}->{'children'}->{'child'};
			@assets_received = @{$assets_received[0]};
		}
		else{
			$assets_received = $return->result()->{'asset'}->{'folder'}->{'children'}->{'child'};
			push(@assets_received, ($assets_received))
		}
	}
	else{
		print "ERROR!";	
		print "Error occurred when reading. Fault: ";
		print "<span style='color: red;'>", $return->fault->{ faultstring }, "</span>";	
		die;
	}

	# If there's one child and it's a page, add it
	if($assets_received->{type} eq "page"){
		push(@assets_received, ($assets_received));
	}

	# If multiple children
	my @temp_pages;        
    for my $child (@assets_received) {

		# If folder, recursively call subroutine then add its child pages
		if($child->{type} eq "folder"){
			my @deeper_arr = get_children_in_directory($child->{path}->{path}, $soap, $auth, $site_name);
			for my $child (@deeper_arr) {
				push(@temp_pages, ($child));
			}
		}
		
		# If page, add it
		elsif($child->{type} eq "page"){
			my $path = $child->{path}->{path};
			push(@temp_pages, ($path));
		}
		
		# This isn't a page, silly
		else{}
    }
	return @temp_pages;
}



#****************************************************************************
#********************		REMOVE CHILDREN BY NAME		  ***************
#****************************************************************************
sub remove_children_by_name{
	my $count = 0;
	my @names; # names of pages to ignore
	my @temp_pages = @{$_[0]}; # dereference children array
	my $ignore_names = @_[1];
	my $ignore_these_files = @_[2];	
	
	if($ignore_names eq ''){
		return;}
	
	# put names in an array
	if(index($ignore_names, ',') != -1){
		@names = split(/,/,$ignore_names);
	}
	else{
		push(@names,($ignore_names));
	}
	
	for my $child (@temp_pages) {
		for my $name (@names) {
			# ignore children with specified file names
			if($ignore_these_files eq true){
				if(index($child, $name) != -1){
					$child = '';
				}
			}
			
			# ignore children without specified file names
			else{
				if(index($child, $name) == -1){
					$child = '';
				}
			}
		}
	}
	return @temp_pages;
}





#****************************************************************************
#********************			UPDATE A PAGE				  ***************
#****************************************************************************
sub do_action{

	my $location = $_[0];
	my $soap = $_[1]; # SOAP CLIENT

	# IF LOCATION HAS BEEN REMOVED - SKIP IT
	if($location eq ''){
		return;
	}

	my $reply = $soap->call('read',
		$auth,
		SOAP::Data->name('identifier')->value(
			[
			SOAP::Data->name('path')->value(
				[
					SOAP::Data->name('path')->value($location),
					SOAP::Data->name('siteName')->value($site_name)
				]
			),
			SOAP::Data->name('type')->value('page')
			]
		)
	);


	if ($reply->result->{'success'}){

		# VARIABLES
		my $page = $reply->result->{'asset'}->{'page'};
		my $configs = $page->{'pageConfigurations'}->{'pageConfiguration'};
		my $config; # the one config we're updating (reqires 'html' config)
		my $region; # the one region we're updating
		print "Editing.. ";
		
		# SELECT CONFIG
		for my $cc (@{$configs}) {
			if($cc->{'name'} eq 'html'){
				$config = $cc;
			}
		}
 		my $regions = $config->{'pageRegions'}->{'pageRegion'};

		# SELECT REGION		
		for my $rr (@{$regions}) {
			if($rr->{'name'} eq $region_name){
				$region = $rr;
			}
		}

		# IF THE PAGE DOESN'T HAVE THAT REGION - SKIP IT		
		if(defined($region) != 1){
			print "<span class=\"warning\">Skipping</span> ";
			print print $page->{'path'};
			print " - doesn't have region: ",$region_name,"<br />";
			return;
		}
		
		# UPDATE REGION
		if($no_block eq true){
			$region->{'blockId'} = undef;
			$region->{'noBlock'} = true;
			$region->{'blockPath'} = '';			
		}
		elsif($block_path ne ''){
			$region->{'blockId'} = undef;
			$region->{'noBlock'} = false;
			$region->{'blockPath'} = $block_path;	
		}

		if($no_format eq true){
			$region->{'formatId'} = undef;
			$region->{'noFormat'} = true;
			$region->{'formatPath'} = '';			
		}
		elsif($format_path ne ''){
			$region->{'formatId'} = undef;
			$region->{'noFormat'} = false;
			$region->{'formatPath'} = $format_path;
		}

		# WEB SERVICES: SUBMIT UPDATE						
		my @editedPage =  [
			SOAP::Data->name('id')->value($page->{'id'}),
			SOAP::Data->name('name')->value($page->{'name'}),
			SOAP::Data->name('parentFolderId')->value($page->{'parentFolderId'}),
			SOAP::Data->name('siteName')->value($page->{'siteName'}),
			SOAP::Data->name('contentTypeId')->value($page->{'contentTypeId'}),
			SOAP::Data->name('contentTypePath')->value($page->{'contentTypePath'}),
			SOAP::Data->name('pageConfigurations')->value([
			SOAP::Data->name('pageConfiguration')->value([
				SOAP::Data->name('name')->value($config->{'name'}),
				SOAP::Data->name('defaultConfiguration')->value($config->{'defaultConfiguration'}),
				SOAP::Data->name('templateId')->value($config->{'templateId'}),
					SOAP::Data->name('pageRegions')->value([
					SOAP::Data->name('pageRegion')->value([
		 			SOAP::Data->name('name')->value($region->{'name'}),
		 			SOAP::Data->name('blockPath')->value($region->{'blockPath'}),
		 			SOAP::Data->name('formatPath')->value($region->{'formatPath'})
					]),
				]),
			SOAP::Data->name('outputExtension')->value($config->{'outputExtension'}),
			SOAP::Data->name('serializationType')->value($config->{'serializationType'}),
			SOAP::Data->name('includeXMLDeclaration')->value($config->{'includeXMLDeclaration'}),
			SOAP::Data->name('publishable')->value($config->{'publishable'}),
				])
			])
		];

		my $reply = $soap->call('edit',
			$auth,
			SOAP::Data->name('asset')->value(
				[
					SOAP::Data->name('page')->value(@editedPage)
				]
			)
		);

		if($reply->result->{'success'} eq true){
			print " <span class='success'>SUCCESS</span> ";
			print $page->{'path'}, "<br />";
		}
		else{
			print "<span class='error'>Fail</span> ";
			print $page->{'path'};
			print "Error: ", $reply->result->{'message'},"<br />";
		}
	}
	else{
		print "<span class='error'>Fail</span> ";
		print $location;
		print "Error: ", $reply->result->{'message'},"<br />";	}
}