<?php
$pages = array(
    "1d01114d956aa05200ad31cd9fcea31f", // /_Site Support/Banks/Banners/Commencement Bank
    "c970d8b6956aa0782ed53d82af7b4541",
    "998927ac956aa0520043168676565a24"
);
require_once('auth_espanae.php');

// Page variables
$site_name   = "reboot";
$output = "PHP";

// For above page, clear these regions:
$region1 = "NAVIGATIONANCESTORS";
$region2 = "NAVIGATIONCONTEXTUAL";



?>
<h1>Bulk Clear Regions</h1>
<pre>
<?php
for ($i=0; $i < count($pages); $i++) {
    try
    {
        // Select page
        $page = $cascade->getAsset( Page::TYPE, $pages[$i] );
        //$page = $cascade->getAsset( Page::TYPE, $pages[$i], $site_name );
        
    
        // Remove inherited page-level block/formats
        $page->setRegionNoBlock( $output, $region1, true)->
        setRegionNoFormat( $output, $region1, true)->
        setRegionNoBlock( $output, $region2, true)->
        setRegionNoFormat( $output, $region2, true)->edit();
    
        //echo "Regions updated successfully.\n";
        echo "\$pages[$i]: ". $pages[$i] ." regions updated successfully.\n";
    }
    catch( Exception $e )
    {
        echo S_PRE . $e . E_PRE;
    }
}
?>
</pre>