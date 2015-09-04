<?php
// Start the session
session_start();
?>
<html>
    <head>
        <style>
            html, body {
                font-family: Helvetica, sans-serif;
            }
            
            .node circle {
                fill: #fff;
                stroke: steelblue;
                stroke-width: 1.5px;
            }

            .node {
                font: 10px sans-serif;
            }

            .link {
                fill: none;
                stroke: #ccc;
                stroke-width: 1.5px;
            }
            
            input {
                width: 250px;
                height: 37px;
                margin-bottom: 10px;
                
                font-size: 14px;
                
                padding: 5px;
                border: 1px solid #ccc;
                
                -moz-border-radius: 3px;
                -webkit-border-radius: 3px;
                border-radius: 3px;
                
            }
            
            input:focus {
                outline-width: 0;
            }

            #login-form {
                width: 250px;
                height: 300px;
                
                position: relative;
                top: 50%;
                left: 50%;
                
                margin: -250px 0 0 -175px;
                
                color: #ccc;
                border: 1px dotted #ccc;
                border-radius: 10px;
                
                padding: 30px 50px 10px 50px;
            }
            
            #bt {
                border: 1px #ccc solid;
            }
            #bt:hover {
                border: 1px #000 solid;
            }
            
        </style>
        <script src="http://code.jquery.com/jquery-1.9.1.min.js" type="text/javascript" language="javascript"></script>
    </head>
    <body>
        <?php
        
        if(isset($_REQUEST["destroy"])) {
            session_unset();
            session_destroy();
        }
        
        if(!isset($_SESSION['login']) || 
            (isset($_SESSION['login']) && $_SESSION['login'] == "false") && 
            isset($_REQUEST['login'])) {
            
            $_SESSION['login'] = $_REQUEST['login'];
        }
        
        if($_SESSION['login'] == "true" && !isset($_SESSION['user'])) {
            $_SESSION['user'] = $_REQUEST['user'];
            $_SESSION['pass'] = $_REQUEST['pass'];
        }
        
        if(!isset($_SESSION['login']) || 
            (isset($_SESSION['login']) && $_SESSION['login'] == "false")) {
            
            echo "
        <div id='login-form'>";
            
            echo "
            <img src='https://cascade.union.edu:8443/css/images/logo-medium.png' style='margin: 0 0 10px -40px; padding-bottom: 10px;' />";
            
            echo "
            <form action='auth.php' method='POST'>";
            
            echo "
                <input class='field' name = 'user' type='text' placeholder = 'Username' />
                <br />";
            echo "
                <input class='field' name = 'pass' type='password' placeholder = 'Password' />
                <br />";
            
            echo "
                <label style='color: #000'>";
            echo "https://&nbsp;&nbsp;
                    <input style='width: 193px;' class='field' name = 'site' type='text' placeholder = 'URL' />";
            echo "
                </label>"; 
            
            echo "
                <br />";

            echo "
                <input class='field' name = 'folder' type='text' placeholder = 'Folder ID' />
                <br />";
            
            echo "
                <input id='bt' type='submit' value='Login' style='background-color: #e9e9e9; color: #000;' />";
            
            echo "
            </form>";
            echo "
        </div>";
            
            exit(0);
        }
        
        if(!isset($_SESSION["site"])) {
            if(isset($_REQUEST["site"]) && $_SESSION['login'] == "true")
                $_SESSION["site"] = $_REQUEST["site"];
            
            if(!isset($_REQUEST["site"]) && $_SESSION['login'] == "true")
                header('Location: login-test.php?destroy=true');
        }
        
        if(!isset($_SESSION["folder"])) {
            if(isset($_REQUEST["folder"]) && $_SESSION['login'] == "true")
                $_SESSION["folder"] = $_REQUEST["folder"];
                
            if($_SESSION['login'] == "true" && (!isset($_REQUEST["folder"]) || $_REQUEST["folder"] == ""))
                header('Location: login-test.php?destroy=true');
        }
        
        require_once( '/www/PHP/classes/cascade_ws/ws_lib.php' );
        
        $site = $_SESSION["site"];
        
        $wsdl = "https://$site/ws/services/AssetOperationService?wsdl";

        $auth = new stdClass();

        $auth->username = $_SESSION["user"];
        $auth->password = $_SESSION["pass"];

        // set up the service
        $service = new AssetOperationHandlerService( $wsdl, $auth );
        $cascade = new Cascade( $service );
        // create an empty object for a one-time operation
        $asset = new stdClass();
  
        echo "
        <h3 style='font-family: helvetica, sans-serif; border-bottom: 1px dotted #000;'>
                  Choose Template to Visualize:
              </h3>";
        
        echo "
        <form action='login-test.php?destroy=true' method='post'>";
        echo "
            <input type='submit' value='LogOut' />";
        echo "
        </form>";
        
        echo "
        <br />";
        
        echo "
        <span id='user'>User: 
            <b>" . $_SESSION["user"] . "</b>
        </span>
        <br />";
        echo "
        <span id='site'>Site: 
            <b>" . $_SESSION["site"] . "</b>
        </span>
        <br />";
        echo "
        <span id='folder'>Folder ID: 
            <b>" . $_SESSION["folder"] . "</b>
        </span>";
        
        echo "
        <br />
        <br />";
        
        echo "
        <form action=''>";
        
        try {
            // reboot templates folder
            $folderId = $_SESSION['folder'];
            $folder = Asset::getAsset ( $service, T::FOLDER, $folderId) or die("Incorrect login");
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
            <button type='submit'>Visualize!</button>";
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
            //$txt .= S_PRE . $e . E_PRE;
            //print($txt);
            header("/");
        }

        ?>
        <script src="http://d3js.org/d3.v3.min.js"></script>
        <script>
            <?php
                if(isset($_REQUEST['templateId'])) {
                    $templateId = $_REQUEST['templateId'];
                    echo "var jsonFile = 'json-gen.php?templateId=$templateId&user=". $_SESSION['user'] ."&pass=". $_SESSION['pass'] ."';";
                    echo "$('#templateId').val('".$templateId."');";
                }
            ?>
            
            var str = $("#templateId").find(":selected").text();
            str = str.substring(str.lastIndexOf('/') + 1) + " | ";
            var countRecord = [0, 0, 0];
            
            var changeName = function(text) {
                $("#templateName").text(text);
            }
            
            // Traverse through JSON and get count
            var myFunction = function(data, level) {
                console.log(level);
                
                if(data.count)
                    countRecord[level] += data.count;
                        
                if(data.children && level < 2) {
                    data.children.forEach(function(d) {
                        myFunction(d, level + 1);
                    });
                }
            }
            
            var margin = {top: 20, right: 120, bottom: 20, left: 120},
                width = $(window).width() - margin.right - margin.left,
                height = 800 - margin.top - margin.bottom;
                
            var i = 0,
                duration = 750,
                root;

            var tree = d3.layout.tree()
                .size([height, width]);

            var diagonal = d3.svg.diagonal()
                .projection(function(d) { return [d.y, d.x]; });

            var svg = d3.select("body").append("svg")
                .style("width", width + margin.right + margin.left)
                .style("height", height + margin.top + margin.bottom)
                .style("padding-bottom", 20)
                .style("padding-left", 20)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
            
            var generate = function(error, data) {
                myFunction(data, 0);
                str += "Configuration Sets: " + countRecord[0] + ", "
                str += "Content Types: " +  countRecord[1] + ", "
                str += "Pages: " +  countRecord[2]
                    
                changeName(str);
                    
                root = data;
                root.x0 = height / 2;
                root.y0 = 0;
                
                function collapse(d) {
                    if (d.children) {
                      d._children = d.children;
                      d._children.forEach(collapse);
                      d.children = null;
                    }
                }

                root.children.forEach(collapse);
                update(root);
            }
                
            
            <?php
            
            if(isset($_REQUEST['templateId'])) {
                echo "d3.json(jsonFile, generate);";
            }
            
            ?>
            
            d3.select(self.frameElement).style("height", "800px");

            function update(source) {
                // compute the new height
                var levelWidth = [1];
                var childCount = function(level, n) {
                  
                  if(n.children && n.children.length > 0) {
                    if(levelWidth.length <= level + 1) levelWidth.push(0);
                    
                    levelWidth[level+1] += n.children.length;
                    n.children.forEach(function(d) {
                      childCount(level + 1, d);
                    });
                  }
                };

                childCount(0, root);  
                var newHeight = d3.max(levelWidth) * 20; // 20 pixels per line  
                if(newHeight < 800)
                    newHeight = 800;

                tree = tree.size([newHeight, width]);
                
                d3.select("svg").style("height", newHeight);
                
                // Compute the new tree layout.
                var nodes = tree.nodes(root).reverse(),
                    links = tree.links(nodes);

                // Normalize for fixed-depth.
                nodes.forEach(function(d) { d.y = d.depth * 180; });

                // Update the nodes…
                var node = svg.selectAll("g.node")
                    .data(nodes, function(d) { return d.id || (d.id = ++i); });

                // Enter any new nodes at the parent's previous position.
                var nodeEnter = node.enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
                    .on("click", click);

                nodeEnter.append("svg:image")
                    .attr("xlink:href", 
                    function(d) { 
                        return "icons/" + (
                            (d.type === "pageconfigurationset")? "pageconfigurationset.gif":
                            (d.type === "contenttype")?  "contenttype.gif":
                            (d.type === "template")? "template.gif":
                            "page.gif"
                        )
                    })
                    .attr("width", 16)
                    .attr("height", 16)
                    .attr("y", -8)
                    .attr("x", -8);    
                
                nodeEnter.append("svg:a")
                    .attr("xlink:href", function(d) { return d.url; })
                    .attr("target", "_blank")
                    .append("text")
                        .attr("x", function(d) { return d.children || d._children ? -10 : 10; })
                        .attr("dy", ".35em")
                        .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
                        .text(
                            function(d) {
                                if(d.count)
                                    return d.name + " (" + d.count + ")";
                                return d.name; 
                            }
                        )
                    .style("fill-opacity", 1e-6);

                // Transition nodes to their new position.
                var nodeUpdate = node.transition()
                    .duration(duration)
                    .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

                /*nodeUpdate.select("circle")
                    .attr("r", 4.5)
                    .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });*/

                nodeUpdate.select("text")
                    .style("fill-opacity", 1);

                // Transition exiting nodes to the parent's new position.
                var nodeExit = node.exit().transition()
                    .duration(duration)
                    .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
                    .remove();

                /*nodeExit.select("circle")
                    .attr("r", 1e-6);*/

                nodeExit.select("text")
                    .style("fill-opacity", 1e-6);

                // Update the links…
                var link = svg.selectAll("path.link")
                    .data(links, function(d) { return d.target.id; });

                // Enter any new links at the parent's previous position.
                link.enter().insert("path", "g")
                    .attr("class", "link")
                    .attr("d", function(d) 
                    {
                        var o = {x: source.x0, y: source.y0};
                        return diagonal({source: o, target: o});
                    }
                );

                // Transition links to their new position.
                link.transition()
                    .duration(duration)
                    .attr("d", diagonal);

                // Transition exiting nodes to the parent's new position.
                link.exit().transition()
                    .duration(duration)
                    .attr("d", function(d) 
                    {
                        var o = {x: source.x, y: source.y};
                        return diagonal({source: o, target: o});
                    }
                )
                .remove();

                // Stash the old positions for transition.
                nodes.forEach(function(d) {
                    d.x0 = d.x;
                    d.y0 = d.y;
                });
            }

            // Toggle children on click.
            function click(d) {
                if (d.children) {
                    d._children = d.children;
                    d.children = null;
                } 
                else {
                    d.children = d._children;
                    d._children = null;
                }
                
                update(d);
            }
        
        </script>
    </body>
</html>
