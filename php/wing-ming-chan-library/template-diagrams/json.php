<?php
// Supply Cascade Server URL, username & password
require_once( '../../auth_user.php' );

// Get the asset ID passed by the query string
$templateId = $_REQUEST[‘templateId’];

// initialize counter variables
$totalPages = $totalContentTypes = 0;

/* Uses the following classes:

- Asset class
  http://upstate.edu/cascade-admin/projects/web-services/oop/classes/asset-classes/

- Property class
  http://upstate.edu/cascade-admin/projects/web-services/oop/classes/property-classes/child.php
  Template::getSubscribers() returns an array of Identifier objects.
  Identifier objects are Child objects.
  
  The Child::getPathPath() method is defined in Child.
  
- Utility Class
  Uses the StringUtility::getNameFromPath( $path ) method to get the asset name from its path.
  http://www.upstate.edu/cascade-admin/projects/web-services/oop/classes/utility-classes.php
  The Child::getPathPath() method is defined in Child.
  Note that for a child, there is no name. Therefore, there is no getName method. But if you want to get the name of an asset from a path, use StringUtility::getNameFromPath( $path ), and if you are interested in the parent container of the asset, use StringUtility::getParentPathFromPath( $path ).To get the name of an asset from a path, use 
*/

try {

$template = $cascade->getAsset(Template::TYPE, $templateId, 'reboot' );

// get configuration sets related to template
$configSets = $template->getSubscribers();


$totalConfigSets = count( $configSets );

$ctTxt .= "[";
// for each configuration set, loop through content types

foreach( $configSets as $configSet ) { // CONFIGURATION SET
    
	// get content types related to configuration set
    $contentTypes = $configSet->getAsset( $service )->getSubscribers();
    $countCT = count( $contentTypes );
    $totalContentTypes += $countCT; // keep a running total


	// name of configuration set
    // Replace '#' with link(/link-fetching code)
	$ctTxt .= "{\"name\": \"" . $configSet->getPathPath() . "\", \"type\":" .
        "\"pageconfigurationset\", \"children\":";
    $ctTxt .= "[";
    foreach( $contentTypes as $contentType ) { // CONTENT TYPE
        
    	$pages = $contentType->getAsset( $service )->getSubscribers();
    	$pageCount = count( $pages );
        $totalPages += $pageCount;

        // Replace '#' with link(/link-fetching code)
    	$ctTxt .= "{\"name\": \"" . StringUtility::getNameFromPath($contentType->getPathPath()) . "\"," .
                  " \"type\": \"contenttype\", \"children\":";
        $ctTxt .= "[";
        if( $pageCount > 0 ) {
            foreach( $pages as $page ) { // PAGE
                // Replace '#' with link(/link-fetching code)
                $ctTxt .= "{\"name\": \"" . $page->getPathPath() . "\", \"url\":".
                    "\"https://cascade.union.edu:8443/entity/open.act?id="
                    .$page->getId()."&type=page\", \"type\": \"page\"},";
            }
        }
        $ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
        $ctTxt .= "],"; // list of content type pages    
    	$ctTxt .= "\"count\": ". $pageCount ."},";
    }
    $ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
	$ctTxt .= "],"; // list of config set content types
	$ctTxt .= "\"count\": ". $countCT ."},";
}

$ctTxt = rtrim($ctTxt, ","); // Remove last comma, which is invalid.
$ctTxt .= "],"; // list of template config sets

$path = $template->getPath();
$name = $template->getName();

// TEMPLATE
// Replace '#' with link(/link-fetching code)
$txt .= "{\"name\": \"$name\", \"url\": \"#\", \"type\": \"template\", \"children\":";
$txt .= $ctTxt . "\"count\": ". $totalConfigSets ."}";

echo $txt;
}
catch(Exception $e) {
    echo S_PRE . $e . E_PRE;
    throw $e;
}
