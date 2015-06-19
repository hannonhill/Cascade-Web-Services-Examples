<?php
// Download the web services library here
// http://www.upstate.edu/cascade-admin/projects/web-services/oop/classes/

// Point to the downloaded library
require_once( '/your/path/cascade_ws/ws_lib.php' );

// Set the AssetOperationService WSDL URL, the entry point to all available Cascade web service operations. 
$wsdl = "https://your.cascade.instance:port/ws/services/AssetOperationService?wsdl";

// stdClass provides an alternative to working with web services arrays.
// http://www.upstate.edu/cascade-admin/projects/web-services/introduction/using-stdclass.php
$auth           = new stdClass();
$auth->username = "my_username"; /* change this */
$auth->password = "my_password"; /* change this */

try {
  // Initialize the $service object so it can be referenced in the calling script.
  $service = new AssetOperationHandlerService( $wsdl, $auth );
  // 
  $cascade = new Cascade( $service );

  // create an empty object for a one-time operation
  $asset = new stdClass();
}

catch( ServerException $e )
{
    echo S_PRE . $e . E_PRE;
    throw $e;
}
?>
