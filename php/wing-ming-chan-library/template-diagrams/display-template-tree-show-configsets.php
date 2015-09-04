<?php
// initialize counter variables
$totalPages = $totalContentTypes = 0;

// get URL parameter
$templateId = $_REQUEST['templateId'];
$template = $cascade->getAsset(Template::TYPE, $templateId, 'reboot' );

// get configuration sets related to template
$configSets = $template->getSubscribers();
$totalConfigSets = count( $configSets );

$ctTxt .= "<ol>\n";
// for each configuration set, loop through content types

foreach( $configSets as $configSet ) {
	
	// get content types related to configuration set
    $contentTypes = $configSet->getAsset( $service )->getSubscribers();
    $countCT = count( $contentTypes );
    $totalContentTypes += $countCT; // keep a running total


	// name of configuration set
	$ctTxt .= "<li><b>Configuration Set:</b> " . $configSet->getPathPath() . " (" . $countCT . " content types)\n";

    $ctTxt .= "<ol>\n";
    foreach( $contentTypes as $contentType ) {
        
    	$pages = $contentType->getAsset( $service )->getSubscribers();
    	$pageCount = count( $pages );
        $totalPages += $pageCount;

        // name of content type
    	$ctTxt .= "<li><b>Content Type:</b> " . $contentType->getPathPath() . " (" . $pageCount . " pages)\n";

        if( $pageCount > 0 ) {
        	
        	// comment to hide page listing
        	/*
            $ctTxt .= "<ol>\n";
            foreach( $pages as $page ) {
                // print the page paths
                $ctTxt .= "<li>" . $page->getPathPath() . "</li>\n";
            }
            $ctTxt .= "</ol>\n"; // list of content type pages
            */
        }
        $ctTxt .= "</li>\n";

    	

    	$ctTxt .= "</li>\n"; 
    }

	$ctTxt .= "</ol>\n"; // list of config set content types
	$ctTxt .= "</li>\n"; 
}

$ctTxt .= "</ol>\n"; // list of template config sets

$path = $template->getPath();
$name = $template->getName();
//echo "Path: " . $path . "</br>\n";
//echo "Name:" . $name . "\n";
//echo "\ntemplate subscribers = $totalConfigSets configuration sets.\n";
//echo "configuration set subscribers = " . $totalContentTypes . " content types.\n";
$txt .= "<p><b>Template:</b> $path has $totalConfigSets configuration sets, $totalContentTypes content types and $totalPages pages.</p>\n";
$txt .= $ctTxt;