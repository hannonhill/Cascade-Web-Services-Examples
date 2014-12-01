Given a template ID, publishes all related pages.

**publishTemplate.php**

1. Pass Template ID
2. Loop through all related configuration sets
3. For each configuration set, loop through all related content types
4. For each content type, publish each related page.

Note: You'll need to supply the Cascade Server URL, username & password. 

This script is powered by Wing Ming Chan's [cascade_ws](http://www.upstate.edu/cascade-admin/projects/web-services/index.php) library.