<?php
// Source variables
$site_name   = "reboot";
$page_name   = "/_Site Support/Banks/Banners/Academics Bank";
$output = "PHP";
$region1 = "NAVIGATIONANCESTORS";
$region2 = "NAVIGATIONCONTEXTUAL";
?>
<h1>Attach Block to Page Region</h1>
<pre>
<?php

try
{
    require_once('auth_espanae_dev.php');
    $page        = $cascade->getAsset( Page::TYPE, $page_name, $site_name );
    echo "\$page: \$page\n";
    
    // Try passing in a NULL
    $page->setRegionBlock( $output, $region1, NULL)->edit();
    echo $region1." block: Tried passing in a NULL\n";

    // Try just passing in two, not three, arguments, leaving the last one empty.
    $page->setRegionBlock( $output, $region2)->edit();
    echo $region2." block: Tried passing in a NULL\n";

}
catch( Exception $e )
{
    echo S_PRE . $e . E_PRE;
}
?>
</pre>