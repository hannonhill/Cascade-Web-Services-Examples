Problem:
Have you ever updated a Cascade Server template and wanted to republish *only* the related pages?

Solution:
This script loops through all related configuration sets. For each configuration set, it loops through related content types. Finally, for each content type, it publishes each related page. 

Note:
You'll need to supply the Cascade Server URL, username & password.

**publishTemplate.php**

Given a template ID, publishes all related pages.

This script is powered by Wing Ming Chan's [cascade_ws](http://www.upstate.edu/cascade-admin/projects/web-services/index.php) library.