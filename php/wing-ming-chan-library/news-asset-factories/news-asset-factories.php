<?php
require_once('auth_espanae.php');

// Set Placement Folder for news asset factories
$articles = 'news/stories/2015/04';
$photos = $articles . '/photos';

// Asset factory's to update
$story = '856a06fc956aa0520003c37ee3ea4e35';
$inTheMedia = '8aa64e6a956aa07801a565c62180ba82';
$photoL = '09be0c17956aa05200af1cb10203468a';
$photoP = '0aac3e18956aa05200af1cb1997c8969';
$galleryL = '0b0166cc956aa05200af1cb1407a50c9';
$galleryP = '0aad607f956aa05200af1cb194fbada9';
$noResize = '0e7d1c4b956aa05200af1cb174f5d4ee';

// Key-value pairs of asset factory ID's and corresponding placement folders.
$factories = array(
    $story => $articles,
    $inTheMedia => $articles,
    $photoL => $photos,
    $photoP => $photos,
    $galleryL => $photos,
    $galleryP => $photos,
    $noResize => $photos
);

echo "
<h1>News Asset Factories</h1>";

// Iterate through news asset factories

foreach ($factories as $key => $value) {
    /*echo "
<p>\$factories[" . $key . "]: " . $value . "</p>\n";*/
    
    try {
        // Retrieve the asset factory object
        $af = AssetFactory::getAsset( $service, AssetFactory::TYPE, $key);
        echo "
<p>Changed 
    <b>" . $af->getId();
             //"Placement folder path: " . $af->getPlacementFolderPath() . BR . BR;
        
        // Change placement folder, using the mutating methods defined in the class AssetFactory.
        $a = $af->setPlacementFolder( $cascade->getFolder( $value, 'reboot' ))->edit();
        //if( isset( $a ) ) echo $a->getId() . BR;
        echo "</b> placement folder to 
    <b>" . $af->getPlacementFolderPath() . "</b>
</p>";
        
        // Verify that the placement folder was changed.
        //echo "
<b>After</b>" .L::ID . $af->getId() . BR;
        
    } catch( Exception $e ) {
        echo S_PRE . $e . E_PRE;
    }
    
}
?>