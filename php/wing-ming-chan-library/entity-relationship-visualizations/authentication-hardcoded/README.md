

index.php
--------------
This is the main page of the project. Once a template is selected from the dropdown, it displays an entity relationship tree by sending a GET request to json-data.php to fetch data about all entities in the folder in JSON format. It passes this JSON data to the D3 API, which visualizes them as a tree.

How D3.js visualizes the tree
-----------------------------
Create a new tree layout with a pre-defined width and height.

`var tree = d3.layout.tree().size([height, width]);`

Run the tree layout, returning the array of nodes associated with the specified root node.

`var nodes = tree.nodes(root).reverse(),
     links = tree.links(nodes);`

index-block.php
-------------
auth.php receives login information from visaulize.php as a POST request and authenticates it using the Cascade PHP API.

Customizing the icons
---------------------
To customize icon, change the 'src' attribute of the image in the form in visualize.php. To remove the image, just remove or comment out the <img /> tag.

auth_user.php
-------------
This file is included in index.php. It saves the Cascade login information.

select-template.php
--------------
This file is included in index.php. It generates a template dropdown menu. It requires the 32-digit ID of the folder that contains the Templates.

Resources
---------

- [D3 Tutorial](http://alignedleft.com/tutorials/d3/fundamentals)
- [Tree diagrams in d3.js](http://www.d3noob.org/2014/01/tree-diagrams-in-d3js_11.html)
- [Example: Reingold–Tilford Tree](http://bl.ocks.org/mbostock/4339184)
- [Tree Layout](https://github.com/mbostock/d3/wiki/Tree-Layout)
