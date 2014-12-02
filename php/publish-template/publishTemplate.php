<?php
// Initialize variables
$ct = array(); // the content types array
$totalConfigSets = $totalContentTypes = $totalPages = 0;
$txt = "<pre>\n"; // saves publish results in plain text

// Get the asset ID passed by the query string.
$templateId = $_REQUEST['templateId'];

// Supply Cascade Server URL, username & password.
require_once('auth_username.php');

// Get the configuration sets related to the template
$configSets = $cascade->getAsset(Template::TYPE, $templateId )->getSubscribers();

// Record the number of related configuration sets
$totalConfigSets = count( $configSets );

 
// Loop through all related configuration sets
foreach( $configSets as $configSet )
{
    // For each related configuration set, get all related content types 
    $cs = $configSet->getAsset( $service );
    $contentTypes = $cs->getSubscribers();

    // Save the total content types related to this config set.
    $countCT = count( $contentTypes );


    // Loop through all related content types..

    foreach( $contentTypes as $contentType )

        // ..and save them to an associative array, removing repeated keys
        $ct[ $contentType->getPathPath() ] = $contentType;

    // Save the overall total number of content types.
    $totalContentTypes += $countCT;
}

if( count( array_keys( $ct )) > 0 )
{   
    // Loop through content type array
    foreach( $ct as $ct_name => $ct_child )
    {
        // get the related pages
        $pages = $ct_child->getAsset( $service )->getSubscribers();
        
        // Save the total pages related to this content type
        $pageCount = count( $pages );
        
        $txt .= "Publishing content type " . $ct_name . ":\n";
        if( $pageCount > 0 )
        {
            foreach( $pages as $page )
            {
                // report the page paths
                $txt .= $page->getPathPath() . "\n";
                // publish page
                $service->publish( $page->toStdClass() );
            }
        }
        $txt .= "\n";
        // Save the overall total number of pages.
        $totalPages += $pageCount;
    }
}
$txt .= "\ntemplate subscribers = $totalConfigSets configuration sets.\n";
$txt .= "configuration set subscribers = " . $totalContentTypes . " content types.\n";
$txt .= "content type subscribers = $totalPages pages.\n";
$txt .= "</pre>";
echo $txt;
?>