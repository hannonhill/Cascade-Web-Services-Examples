<?php
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
    echo "require_once\n";
    $page        = $cascade->getAsset( Page::TYPE, $page_name, $site_name );
    echo "\$page = \$page\n";
    
    $page->
        setRegionBlock(
            $output,
            $region1,
            $cascade->getAsset( 
                Block::TYPE, '3dcb4d6b956aa05200c85bbb924fd235' ))->
        edit();
    echo "\$page->setRegionBlock()\n";
}
catch( Exception $e )
{
    echo S_PRE . $e . E_PRE;
}
?>
</pre>