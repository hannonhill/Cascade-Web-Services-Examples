visualize.php
--------------
Visualize.php is the main page of the project. This displays the entity relationship tree on logging in. Visualize.php accepts Cascade login information, Cascade Site URL and the ID of the folder whose entities must be visualized, and passes it on to auth.php, which authenticates this information utilizing Cascade PHP API. If the authentication is successful and the folder ID is valid, it visualizes the relationship tree for all entities in the given folder.

**Visualization:** Visualize.php sends a GET request to json-gen.php to fetch data about all entities in the folder in JSON format. It passes this JSON data to the D3 API, which visualizes them as a tree.

auth.php
-------------
auth.php receives login information from visaulize.php as a POST request and authenticates it using the Cascade PHP API.

Customizing the icons
---------------------
To customize icon, change the 'src' attribute of the image in the form in visualize.php. To remove the image, just remove or comment out the <img /> tag.
