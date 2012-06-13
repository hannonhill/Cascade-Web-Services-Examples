#!/usr/bin/php
<?php
/*
This script will search for all pages 
 - within a certain folder (set on line 41)
 - with a certin Configuration Set applied (set on line 73)
 - with no Data Definition applied (if not desired, remove "  && $pageDetails->structuredData == ''" on line 90),
remove the configuration set, and apply a Content Type (set on line 75).
*/

date_default_timezone_set('America/Chicago');
function getResult($result,$fullName) {
	if (!isSuccess($result))
	{
		echo "\nError";
		echo "\n".extractMessage($result)."\n";
	}	
	else
	{
		echo $fullName." Page Added\n";
	}
}

function isSuccess($text) {
	return substr($text, strpos($text, "<success>")+9,4)=="true";
}
function extractMessage($text) {
	return substr($text, strpos($text, "<message>")+9,strpos($text, "</message>")-(strpos($text, "<message>")+9));
}

// Cascade Server login credentials
$username = "yourusername";
$password = "supersecret";
$cascadePath = "http://localhost:8080";

$cascade = new SoapClient($cascadePath . "/ws/services/AssetOperationService?wsdl",array('trace' => 1));
$auth = array('username' => $username, 'password' => $password);
echo "Process started at ".date("m/d/y H:i:s",time())."\r\n";

// the ID of your root folder, or the folder you want to start from (if not root)
$rootFolderID = 'ROOT_FOLDER_ID';
readFolder($rootFolderID, $auth, $cascade);

function readFolder ($folderReadID, $auth, $cascade) {
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
				readFolder($folderChildren->id, $auth, $cascade);
			} elseif ($folderChildren->type == 'page') {
				readPage($folderChildren->id, $auth, $cascade);
			}
		} elseif (count($folderChildren) > 1) {
			foreach ($folderChildren as $value) {
				if ($value->type == 'folder') {
					readFolder($value->id, $auth, $cascade);
				} elseif ($value->type == 'page') {
					readPage($value->id, $auth, $cascade);
				}
			}
		}
	}
}

function readPage ($pageReadID, $auth, $cascade) {
	// the Configuration Set to search for
	$configSetID = 'CONFIG_SET_ID';
	// the Content Type to assign
	$contentTypeID = 'CONFIG_SET_PATH';

	$id = array('id' => $pageReadID, 'type' => 'page');
	$readParams = array( 'authentication' => $auth, 'identifier' => $id );	
	$pageRead=$cascade->read($readParams);
	if ( $pageRead->readReturn->success != 'true' ) {
		echo "looks like it didn't work".$pageReadID."\r\n";
	} else {
		$pageDetails = $pageRead->readReturn->asset->page;
		echo 'page-'.$pageDetails->id.' | conf: ';
		echo $pageDetails->configurationSetId;
		echo ' | cont: ';
		echo $pageDetails->contentTypeId;
		echo ' | ';
		
		if ($pageDetails->configurationSetId == $configSetID  && $pageDetails->structuredData == '') {
			$pageDetails->configurationSetId = '';
			$pageDetails->contentTypeId = $contentTypeID;
			$template_params = array(
				'authentication' => $auth,
					'asset' => array('page' => $pageDetails)
				);
			
			$cascade->edit($template_params);
			$result = $cascade->__getLastResponse();
			getResult($result,$pageDetails->id);
		}
		echo '\r\n';
	}
}

echo "Process completed at ".date("m/d/y H:i:s",time())."\r\n";
?>
