<?php
// Include authentication information
// http://upstate.edu/cascade-admin/projects/web-services/introduction/basic-setup.php
require_once('auth_username.php');

// Get the asset ID passed by the query string.
$templateId = $_REQUEST['templateId'];

// Initialize the content types array
$ct = array();

// Initialize the counter variables
$totalPages = $totalContentTypes = 0;

// Get the configuration sets related to the template
$configSets = $cascade->getAsset(Template::TYPE, $templateId )->getSubscribers();

// Record the number of related configuration sets
$totalConfigSets = count( $configSets );

// Check if at least one related configuration set
if( $totalConfigSets > 0 )
{   
    // Loop through each related configuration set
    foreach( $configSets as $configSet )
    {
        // For each related configuration set ...
        $cs = $configSet->getAsset( $service );
        // ...get the related content types.
        $contentTypes = $cs->getSubscribers();

        // Get the number of content types related to this config set.
        $countCT = count( $contentTypes );
        // Keep a running total number of content types
        $totalContentTypes += $countCT;

        if( $countCT > 0 )
        {
            // store content types
    
            foreach( $contentTypes as $contentType )
                // using associative array, removing repeated keys
                $ct[ $contentType->getPathPath() ] = $contentType;            
        }
    }
}

$txt .= "
<pre>";
if( count( array_keys ($ct ) ) > 0 )
{
    foreach( $ct as $ct_name => $ct_child )
    {
        // get the pages
        $pages = $ct_child->getAsset( $service )->getSubscribers();
        $pageCount = count( $pages );
        $totalPages += $pageCount;
        
        $txt .= "Publishing content type " . $ct_name . ":\n";
        if( $pageCount > 0 )
        {
            foreach( $pages as $page )
            {
                // print the page paths
                $txt .= $page->getPathPath() . "\n";
                // publish pages
                $service->publish( $page->toStdClass() );
            }
        }
        $txt .= "\n";
    }
}
$txt .= "\ntemplate subscribers = $totalConfigSets configuration sets.\n";
$txt .= "configuration set subscribers = " . $totalContentTypes . " content types.\n";
$txt .= "content type subscribers = $totalPages pages.\n";
$txt .= "</pre>";
echo $txt;
?>