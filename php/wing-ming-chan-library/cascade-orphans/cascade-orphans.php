<?php
require_once('auth_espanae.php');
echo "<p>included auth_espanae.php</p>";
// Set Placement Folder for news asset factories
$folderID = '68250e75956aa078003f6ca45ac13246';

echo "<h1>Reporting Orphans In Cascade</h1>\n";
// if the asset in question has no relationship, then store its path in the $results array.

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

$results = array();
    
Asset::getAsset( 
    $service, Folder::TYPE, $folderID )->
    getAssetTree()->
    traverse( 
        array( File::TYPE => array( F::REPORT_ORPHANS ) ), 
        NULL, 
        &$results );

if( count( $results[ F::REPORT_ORPHANS ] ) > 0 )
{
    echo S_UL;
        
    foreach( $results[ F::REPORT_ORPHANS ] as $type => $paths )
    {
        try {
            echo S_H2 . $type . E_H2 . S_UL;
                
            foreach( $paths as $path )
            {
                echo S_LI . $path . E_LI;
            }
                
            echo E_UL;
        } catch( Exception $e ) {
            echo S_PRE . $e . E_PRE;
        }
    }
}

?>
