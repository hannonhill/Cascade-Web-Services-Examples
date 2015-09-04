<?php
$templateId = $_REQUEST['templateId'];
$ct = array(); // initialize content types array
$totalPages = $totalContentTypes = 0;

$template = $cascade->getAsset(Template::TYPE, $templateId, 'reboot' );

// get the page configuration sets
$configSets = $template->getSubscribers();

$totalConfigSets = count( $configSets );
//echo "<ul>\n";
foreach( $configSets as $configSet )
{
    //echo "<li>" . $configSet->getPathPath() . "</li>\n";
    $cs = $configSet->getAsset( $service );
        
    // get the content types
    $contentTypes = $cs->getSubscribers();
    $countCT = count( $contentTypes );
    $totalContentTypes += $countCT; // keep a running total

        
    // store content types
    
    foreach( $contentTypes as $contentType )
        // using associative array, removing repeated keys
        $ct[ $contentType->getPathPath() ] = $contentType;            
}
    //echo "/<ul>\n";


if( count( array_keys($ct) ) > 0 )
{
    $ctTxt .= "<ol>\n";
    foreach( $ct as $ct_name => $ct_child )
    {
        // get the pages
        $pages = $ct_child->getAsset( $service )->getSubscribers();
        $pageCount = count( $pages );
        $totalPages += $pageCount;
        $ctTxt .= "<li>$ct_name ($pageCount pages)\n";
        
        if( $pageCount > 0 )
        {
            $ctTxt .= "<ol>\n";
            foreach( $pages as $page )
            {
                // print the page paths
                //echo "<li>" . $page->getPathPath() . "</li>\n";
            }
            $ctTxt .= "</ol>\n";
        }
        $ctTxt .= "</li>\n";
    }
    $ctTxt .= "</ol>\n";
}
$path = $template->getPath();
$name = $template->getName();
//echo "Path: " . $path . "</br>\n";
//echo "Name:" . $name . "\n";
//echo "\ntemplate subscribers = $totalConfigSets configuration sets.\n";
//echo "configuration set subscribers = " . $totalContentTypes . " content types.\n";
$txt .= "<p>$path has $totalPages pages.</p>\n";
$txt .= $ctTxt;
?>