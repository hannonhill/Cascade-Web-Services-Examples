auth_user.php
-------------
This file is included in index.php. It saves the Cascade login information.

select-template.php
--------------
This file is included in index.php. It generates a template dropdown menu. It requires the 32-digit ID of the folder that contains the Templates.

index.php
--------------
This is the main page of the project. Once a template is selected from the dropdown, it displays an entity relationship tree. utilizing Cascade web services and D3.js.
How the D3.js works
-----
Create a new tree layout with a pre-defined width and height.

`var tree = d3.layout.tree().size([height, width]);`


 tree(root) 
# tree.nodes(root)

Runs the tree layout, returning the array of nodes associated with the specified root node.

**Visualization:** Visualize.php sends a GET request to json-gen.php to fetch data about all entities in the folder in JSON format. It passes this JSON data to the D3 API, which visualizes them as a tree.

index-block.php
-------------
auth.php receives login information from visaulize.php as a POST request and authenticates it using the Cascade PHP API.

Customizing the icons
---------------------
To customize icon, change the 'src' attribute of the image in the form in visualize.php. To remove the image, just remove or comment out the <img /> tag.
