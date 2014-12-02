**Given a template ID, publishes all related pages.**

Usage: publishTemplate.php?templateId=0b8ef427956aa05d06c4a2dad422b5a1

**In a nutshell**

1. Loops through all related configuration sets
2. For each config set, loops through all related content types
3. For each content type, loops through all related pages.
4. For each page, publish it.

This script is powered by Wing Ming Chan's [cascade_ws](http://www.upstate.edu/cascade-admin/projects/web-services/index.php) library. Visit [Basic Setup](http://upstate.edu/cascade-admin/projects/web-services/introduction/basic-setup.php) to get started.
