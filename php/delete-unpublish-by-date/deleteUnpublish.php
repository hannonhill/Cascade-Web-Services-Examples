<?php
// ******************************************************************************************
// *                                       SET VALUES                                       *
// ******************************************************************************************
// This script will look at every page within a given folder, compare dates .
$theFolderID = '32digitidnumberoftargetfolder';
$unpublishWorkflowID = '32digitidnumberofworkflowdefinition';
$pageDateType = 'createdDate'; // or one of the following: 'lastModifiedDate', 'lastPublishedDate', 'reviewDate', 'startDate', 'endDate'
$targetDate = time() - (30 * 24 * 60 * 60); // time to compare against, a UNIX timestamp (this is 30 days ago)

// Cascade Server URL, username, and password.
$cascadePath = "http://cascade.server.url"; 
$username = "some.user"; 
$password = "password1"; 

// ******************************************************************************************
// shouldn't need to edit anything below this line
$cascade = new SoapClient($cascadePath . "/ws/services/AssetOperationService?wsdl",array('trace' => 1));
$auth = array('username' => $username, 'password' => $password);

readFolder($theFolderID, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID);

function readFolder ($folderReadID, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID) {
	$id = array('id' => $folderReadID, 'type' => 'folder');
	$readParams = array( 'authentication' => $auth, 'identifier' => $id );	
	$folderRead=$cascade->read($readParams);
	if ( $folderRead->readReturn->success != 'true' ) {
		echo "looks like it didn't work".$folderReadID."\r\n";
	} else {
		echo "folder-".$folderReadID."\r\n";
		$folderChildren = $folderRead->readReturn->asset->folder->children->child;
		if (count($folderChildren) == 1) {
			if ($folderChildren->type == 'folder') {
				readFolder($folderChildren->id, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID);
			} elseif ($folderChildren->type == 'page') {
				readPage($folderChildren->id, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID);
			}
		} elseif (count($folderChildren) > 1) {
			foreach ($folderChildren as $value) {
				if ($value->type == 'folder') {
					readFolder($value->id, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID);
				} elseif ($value->type == 'page') {
					readPage($value->id, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID);
				}
			}
		}
	}
}

function readPage ($pageReadID, $auth, $cascade, $pageDateType, $targetDate, $unpublishWorkflowID) {
	$id = array('id' => $pageReadID, 'type' => 'page');
	$readParams = array( 'authentication' => $auth, 'identifier' => $id );	
	$pageRead=$cascade->read($readParams);
	if ( $pageRead->readReturn->success != 'true' ) {
		echo "looks like it didn't work".$pageReadID."\r\n";
	} else {
		$asset = $pageRead->readReturn->asset->page;
		$asset->shouldBePublished = true;
		$fullName = $asset->name;

		if($pageDateType == 'createdDate') $pageDate = strtotime($pageRead->readReturn->asset->page->createdDate);
		if($pageDateType == 'lastModifiedDate') $pageDate = strtotime($pageRead->readReturn->asset->page->lastModifiedDate);
		if($pageDateType == 'lastPublishedDate') $pageDate = strtotime($pageRead->readReturn->asset->page->lastPublishedDate);
		if($pageDateType == 'reviewDate') $pageDate = strtotime($pageRead->readReturn->asset->page->metadata->reviewDate);
		if($pageDateType == 'startDate') $pageDate = strtotime($pageRead->readReturn->asset->page->metadata->startDate);
		if($pageDateType == 'endDate') $pageDate = strtotime($pageRead->readReturn->asset->page->metadata->endDate);

		echo $fullName."\r\n";
		if ($pageDate < $targetDate) {
			$wfParams = array('workflowName' => 'Delete and Unpublish', 'workflowDefinitionId' => $unpublishWorkflowID, 'workflowComments' => 'Deleted via Web Services');
			$editParams = array('authentication' => $auth, 'asset' => array('page' => $asset));
			$editParamWF = array('authentication' => $auth, 'asset' => array('page' => $asset, 'workflowConfiguration' => $wfParams));
			$cascade->edit($editParams);
			$result = $cascade->__getLastResponse();
			getResultAction($result,$fullName,"updated.");
	
			$cascade->edit($editParamWF);
			$result = $cascade->__getLastResponse();
			getResultAction($result,$fullName,"deleted.");
		}
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