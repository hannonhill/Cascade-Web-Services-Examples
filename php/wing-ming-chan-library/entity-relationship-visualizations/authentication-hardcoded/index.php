<html>
    <head>
        <style>
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
        </style>
        <script src="http://code.jquery.com/jquery-1.9.1.min.js" type="text/javascript" language="javascript"></script>
    </head>
    
    <body>
    
        <?php
        
        require_once('../../auth_espanae.php');
        
        echo "<h3 style='font-family: helvetica, sans-serif; border-bottom: 1px dotted #000;'>
                  Choose Template to Visualize:
              </h3>";
        
        echo "<form action=\"index.php\">";

        try {
            // reboot templates folder
            $folderId = '39ba0d27956aa05200c85bbbfba2a20b';
            $folder = Asset::getAsset ( $service, T::FOLDER, $folderId);
            $at = $folder->getAssetTree();
            
            $txt .= "<select id='templateId' name='templateId'>\n";
        
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
                $txt .= "<option value='$id'>$path</option>\n";
            
            $txt .= "</select>\n";
            
            echo $txt;
            
            echo "<br />";
            echo "<br />";
            echo "<button type=\"submit\">Visualize!</button>";
            echo "</form>";
            
            echo "<h5 id='templateName' style='margin: 20px 0 10px 0; font-family: helvetica, sans-serif;'>";
            if(isset($_REQUEST['templateId']))
                echo "Loading...";
            else
                echo "Select a Template";
            echo "</h4>";
        }
        catch ( Exception $e ) {
            $txt .= S_PRE . $e . E_PRE;
            print($txt);
        }

        ?>
        
        <script src="http://d3js.org/d3.v3.min.js"></script>
        <script>
            
            <?php
                if(isset($_REQUEST['templateId'])) {
                    $templateId = $_REQUEST['templateId'];
                    echo "var jsonFile = 'json-gen.php?templateId=". $templateId ."';";
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
