Publishes all related pages, given a Cascade Server template ID.

**How it Works**

1. Loop through the template's related configuration sets
2. For each configuration set, loop through all related content types
3. For each content type, loop through all related pages
4. Publish each page

**Example**

publishTemplate.php?templateId=0b8ef427956aa05d06c4a2dad422b5a1

**Requirements**

Uses Wing Ming Chan's [cascade_ws](http://www.upstate.edu/cascade-admin/projects/web-services/index.php) web services library. Visit [Basic Setup](http://upstate.edu/cascade-admin/projects/web-services/introduction/basic-setup.php) to get started.

**Issues**

Each page is published individually. Templates with many related templates can create a bottleneck in the [Cascade Server publish queue](http://help.hannonhill.com/discussions/how-do-i/14581-how-to-clear-all-active-publish-jobs).
