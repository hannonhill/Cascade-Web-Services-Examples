<?php

require_once('../../../web-services/auth_espanae_dev.php');

// initialize counter variables
$totalPages = $totalContentTypes = 0;

// get URL parameter
$templateId = $_REQUEST['templateId'];
$template = $cascade->getAsset(Template::TYPE, $templateId, 'reboot' );

// get configuration sets related to template
$configSets = $template->getSubscribers();
$totalConfigSets = count( $configSets );

$ctTxt .= "[";
// for each configuration set, loop through content types

foreach( $configSets as $configSet ) {
    
	// get content types related to configuration set
    $contentTypes = $configSet->getAsset( $service )->getSubscribers();
    $countCT = count( $contentTypes );
    $totalContentTypes += $countCT; // keep a running total


	// name of configuration set
    // Replace '#' with link(/link-fetching code)
	$ctTxt .= "{\"name\": \"" . $configSet->getPathPath() . "\", \"type\":" .
        "\"pageconfigurationset\", \"children\":";
    $ctTxt .= "[";
    foreach( $contentTypes as $contentType ) {
        
    	$pages = $contentType->getAsset( $service )->getSubscribers();
    	$pageCount = count( $pages );
        $totalPages += $pageCount;

        // name of content type
        // Replace '#' with link(/link-fetching code)
    	$ctTxt .= "{\"name\": \"" . $contentType->getPathPath() . "\"," .
                  " \"type\": \"contenttype\", \"children\":";
        $ctTxt .= "[";
        if( $pageCount > 0 ) {
            foreach( $pages as $page ) {
                // print the page paths
                // Replace '#' with link(/link-fetching code)
                $ctTxt .= "{\"name\": \"" . $page->getPathPath() . "\", \"url\":".
                "\"https://union.edu/".$page->getPathPath()."\", \"type\": \"page\"},";
            }
        }
        $ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
        $ctTxt .= "]"; // list of content type pages    
    	$ctTxt .= "},"; 
    }
    $ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
	$ctTxt .= "]"; // list of config set content types
	$ctTxt .= "},"; 
}

$ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
$ctTxt .= "]"; // list of template config sets

$path = $template->getPath();
$name = $template->getName();

 // Replace '#' with link(/link-fetching code)
$txt .= "{\"name\": \"$name\", \"url\": \"#\", \"type\": \"template\", \"children\":";
$txt .= $ctTxt . "}";

echo $txt;