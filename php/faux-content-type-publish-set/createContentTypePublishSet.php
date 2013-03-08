<?php
// ******************************************************************************************
// *                                       SET VALUES                                       *
// ******************************************************************************************
// You'll need to create a Publish Set in Cascade. Don't worry about adding any pages; that's
//   what this script is going to do. All existing pages within a Publish Set will be 
//   overwritten.
$publishSetID = '32digitidnumberoftargetpublishet';
// You'll need to create a simple XML page. The DEFAULT block should be an index block (most
//    likely a Content Type index block or a Data Definition block with multiple CT index 
//    blocks. The index blocks don't need any Indexed Asset Content or Other Indexed Info, 
//    since all we need is the page ID.
$xmlFilePath = 'http://www.path.to/your/published/contenttype/index/block/file.xml';
$username = "some.user"; 
$password = "password1"; 
$cascadePath = "http://cascade.server.url"; 
// if you want to do multiple Content Type Publish Sets, you can repeat this function call, replacing
//   $xmlFilePath with a path to an XML file, and $publishSetID with the 32-digit id number of your target 
//   publish set.
updatePublishSet($auth, $cascade, $xmlFilePath, $publishSetID);

// ******************************************************************************************
// shouldn't need to edit anything below this line
$cascade = new SoapClient($cascadePath . "/ws/services/AssetOperationService?wsdl",array('trace' => 1));
$auth = array('username' => $username, 'password' => $password);

function updatePublishSet ($auth, $cascade, $xmlFilePath, $publishSetID) {
	// open XML file, read all system-page nodes, store page IDs in array
	$xml = simplexml_load_file($xmlFilePath);
	$xmlResult = $xml->xpath('//system-page');
	$psPagesArray = array();
	while(list( , $node) = each($xmlResult)) {
		$pageID = utf8_encode($node[0]['id']);
		$psPagesArray[] = array ('type' => 'page', 'id' => $pageID);
	}
	
	// get the publish set, replace pages with array from XML file
	$id = array('id' => $publishSetID, 'type' => 'publishset');
	$readParams = array( 'authentication' => $auth, 'identifier' => $id );				
	$publishSetRead = $cascade->read($readParams);
	if ( $publishSetRead->readReturn->success == 'true' ) {
		$asset = $publishSetRead->readReturn->asset->publishSet;
		$asset->pages->publishableAssetIdentifier = $psPagesArray;
		$editParams = array('authentication' => $auth, 'asset' => array('publishSet' => $asset));
		$cascade->edit($editParams);
		$result = $cascade->__getLastResponse();
		getResultAction($result,$asset->name,"updated.");
	} else {
		echo "Couldn't find publish set with ID ".$publishSetID."\n";
	}
}
function getResult($result,$fullName) {
	if (!isSuccess($result)) {
		echo "\nError";
		echo "\n".extractMessage($result)."\n";
	} else {
		echo $fullName." Added ".date("H:i:s",time())."\n";
	}
}
function getResultAction($result,$fullName,$action) {
	if (!isSuccess($result))
	{
		echo "\nError";
		echo "\n".extractMessage($result)."\n";
	}	
	else
	{
		echo $fullName." - ".$action." ".date("H:i:s",time())."\n";
	}
}
function isSuccess($text) {
	return substr($text, strpos($text, "<success>")+9,4)=="true";
}
function extractMessage($text) {
	return substr($text, strpos($text, "<message>")+9,strpos($text, "</message>")-(strpos($text, "<message>")+9));
}
function extractID($text) {
	return substr($text, strpos($text, "<createdAssetId>")+16,strpos($text, "</createdAssetId>")-(strpos($text, "<createdAssetId>")+16));
}
?>