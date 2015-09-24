<?php
require_once('auth_espanae.php');

// Set Placement Folder for news asset factories
$folderID = '68250e75956aa078003f6ca45ac13246';
echo "<p>$folderID</p>";

echo "<h1>Reporting Orphans In Cascade</h1>\n";


function assetTreeReportOrphans( 
    AssetOperationHandlerService $service, 
    Child $child, $params=NULL, $results=NULL )
{
    if( is_array( $results ) ) {
    
        $subscribers = $child->getAsset( $service )->getSubscribers();
        
        if( $subscribers == NULL ) {
        
            $results[ F::REPORT_ORPHANS ][ $child->getType() ][] = 
                $child->getPathPath();
        }
    }
}

echo "<p>funcion assetTreeReportOrphans</p>";

$results = array();

echo "<p>\$results = array();</p>";

Asset::getAsset( 
    $service, Folder::TYPE, $folderID )->
    getAssetTree()->
    traverse( 
        array( File::TYPE => array( F::REPORT_ORPHANS ) ), 
        NULL, 
        &$results );
        
echo "<p>\Asset::getAsset</p>";

if( count( $results[ F::REPORT_ORPHANS ] ) > 0 )
{
    echo S_UL;
} else {
    echo "<p>else</p>\n";
}
echo "<p>if-else</p>\n";
?>
