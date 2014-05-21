#!/usr/bin/perl
use SOAP::Lite +trace;
use CGI qw( :standard :html :param );

#####
#	Cascade Server - Web services example
#		Displays path of all pages in a directory
#####

# DECLARE VARIABLES
my ($user, $pass, $auth, $soap);
my $server = 'https://cascade.jmu.edu';
my $site_name = 'example.com';
my $directory = '/company/careers';


# SET CREDENTIALS
$user = 'USERNAME';
$pass = 'PASSWORD';
$auth = SOAP::Data->name('authentication')->value([SOAP::Data->name('password')->value($pass), SOAP::Data->name('username')->value($user)]);


# CREATE SOAP OBJECT
$server = $server . '/ws/services/AssetOperationService?wsdl';
$soap = SOAP::Lite->new( proxy => $server);

$soap->on_action( sub { "http://www.hannonhill.com/ws/ns/AssetOperationService" });
$soap->autotype(0)->readable(1);
$soap->default_ns('http://www.hannonhill.com/ws/ns/AssetOperationService');

print header, start_html(-title=>'Cascade Server - Web services example', -BGCOLOR=>'#FFFFFF', -dtd=>'-//W3C//DTD XHTML 1.0 Strict//EN');


# READ PAGES
my $reply = $soap->call('read',
	$auth,
	SOAP::Data->name('identifier')->value(
		[
		SOAP::Data->name('path')->value(
			[
				SOAP::Data->name('path')->value($directory),
				SOAP::Data->name('siteName')->value($site_name)
			]
		),
		SOAP::Data->name('type')->value('folder')
		]
	)
);


# IF SUCCESS SET ARRAY - ELSE DIE
if ($reply->result->{'success'}){
	@assets_received = $reply->result()->{'asset'}->{'folder'}->{'children'}->{'child'};
	@assets_received = @{$assets_received[0]};
}
else{
	print "Error occurred when reading. Fault: ";
	print $reply->fault->{ faultstring };	
	die;
}

# PRINT PAGE PATH
for my $child (@assets_received) {
	if($child->{type} eq "page"){
		my $path = $child->{path}->{path};
		print $path,"<br />";
	}
}

print end_html;
