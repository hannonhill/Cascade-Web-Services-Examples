<?php
// Include Wing's web services library:
require_once( '/your/path/cascade_ws/ws_lib.php' );
// Download the library from:
// http://www.upstate.edu/cascade-admin/projects/web-services/oop/classes/index.php

$wsdl = "https://cascade.domain.edu:port/ws/services/AssetOperationService?wsdl";
$auth           = new stdClass();
$auth->username = "my_username"; /* change this */
$auth->password = "my_password"; /* change this */

try {
  // set up the service
  $service = new AssetOperationHandlerService( $wsdl, $auth );
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