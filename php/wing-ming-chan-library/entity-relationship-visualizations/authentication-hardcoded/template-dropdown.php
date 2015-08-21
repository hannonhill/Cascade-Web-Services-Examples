<?php
    // Templates folder
    $folderId = '39ba0d27956aa05200c85bbbfba2a20b';
    
    require_once('../../auth_user.php');
    
    echo "<h3 style='font-family: helvetica, sans-serif; border-bottom: 1px dotted #000;'>
              Choose Template to Visualize:
          </h3>";
    
    echo "
<form action=\"index.php\">";

    try {
        $folder = Asset::getAsset ( $service, T::FOLDER, $folderId);
        $at = $folder->getAssetTree();
        
        $txt .= "
        
    <select id='templateId' name='templateId'>\n";
    
        function assetTreeGetTemplateId(AssetOperationHandlerService $service,
                                Child $child, $params=NULL, &$results=NULL) {
            // Make sure that the type of the $child is indeed Template::TYPE
            if( $child->getType() == Template::TYPE )
                // Since you only need the path and ID strings, just store them in the array
                $results[ $child->getPathPath() ] = $child->getId();
        }

        $function_array = array(Template::TYPE => array( assetTreeGetTemplateId));
        $results = array();
        // When you call AssetTree::traverse, make sure you pass in an array as the third argument.
        $at->traverse( $function_array, NULL, $results );
        
        // $results should have an array of key/value pairs allowing us to do this:
        foreach($results as $path => $id)
            $txt .= "
            
        <option value='$id'>$path</option>\n";
        
        $txt .= "
        
    </select>\n";
        
        echo $txt;
        
        echo "
        
    <br />";
        echo "
        
    <br />";
        echo "
        
    <button type=\"submit\">Visualize!</button>";
        echo "
    
</form>";
        
        echo "
    
<h5 id='templateName' style='margin: 20px 0 10px 0; font-family: helvetica, sans-serif;'>";
        if(isset($_REQUEST['templateId']))
            echo "Loading...";
        else
            echo "Select a Template";
        echo "
    
</h4>";
    }
    catch ( Exception $e ) {
        $txt .= S_PRE . $e . E_PRE;
        print($txt);
    }

?>
