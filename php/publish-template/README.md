Given a template ID, publishes all related pages.

**How it Works**

1. Loops through the template's related configuration sets
2. For each configuration set, loops through all related content types
3. For each content type, loops through all related pages.
4. For each page, it gets published.

**Example Usage**
publishTemplate.php?templateId=0b8ef427956aa05d06c4a2dad422b5a1

**Requirements**
Uses Wing Ming Chan's [cascade_ws](http://www.upstate.edu/cascade-admin/projects/web-services/index.php) web services library. Primer: [Basic Setup](http://upstate.edu/cascade-admin/projects/web-services/introduction/basic-setup.php).
