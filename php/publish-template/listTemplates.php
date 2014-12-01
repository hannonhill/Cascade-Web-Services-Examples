<?php   
    require_once('../auth_espanae_dev.php');
    echo "require_once\n";

    try {
        echo "try\n";

        // reboot templates folder
        $id = '39ba0d27956aa05200c85bbbfba2a20b';
        $folder = Asset::getAsset ( $service, T::FOLDER, $id);
        $at = $folder->getAssetTree();
        
        function assetTreeGetTemplateId(AssetOperationHandlerService $service,
                                        Child $child, $params=NULL, &$results=NULL )
        {
            // Make sure that $results (the third parameter passed in) is indeed an array
            //if( is_array( $results ) )
            //   $results[ 'assetTreeGetTemplateId' ] = array();
            
            // Make sure that the type of the $child is indeed Template::TYPE
            if( $child->getType() == Template::TYPE )
                // Since you only need the path and ID strings, just store them in the array
                $results[ $child->getPathPath() ] = $child->getId();
        }
        echo "function assetTreeGetTemplateID\n";
        
        $function_array = array(
            Template::TYPE => array( assetTreeGetTemplateId )
        );
        echo "function_array = array();\n";
        
        $results = array();
        echo "results = array();\n";
        
        // When you call AssetTree::traverse, make sure you pass in an array as the third argument.
        $at->traverse( $function_array, NULL, $results );
        echo "at->traverse();";
        
        // After the call, $results[ 'assetTreeGetTemplateId' ] should be an array storing string keys and string values. You can then do this:
        //$path_ids = $results[ 'assetTreeGetTemplateId' ];
        //echo "path_ids = results[];\n";
        
        <ul>\n";
        foreach( $results as $path => $id )
        {
            echo "
            <li>$path => $id</li>\n";
        }
        echo "
        </ul>\n";
        
        //$templates = $results[F::GET_ASSETS][Template::TYPE];
        //var_dump( $templates );
    }
    catch ( Exception $e )
    {
        echo S_PRE . $e . E_PRE;
    }
    
?>