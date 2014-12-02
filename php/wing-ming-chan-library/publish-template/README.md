Publishes all related pages, given a Cascade Server template ID.

**How it Works**

1. Loop through the template's related configuration sets
2. For each configuration set, loop through all related content types
3. For each content type, loop through all related pages
4. Publish each page

**Example**

publishTemplate.php?templateId=0b8ef427956aa05d06c4a2dad422b5a1

**Requirements**

An [auth_user.php](https://github.com/espanae/Cascade-Web-Services-Examples/blob/master/php/wing-ming-chan-library/auth_user.php) file (line 11) that includes Wing Ming Chan's [web services library](http://www.upstate.edu/cascade-admin/projects/web-services/), your Cascade Server URL, username and password.

**Issues**

Each page is published individually so templates with numerous pages [may clog](http://help.hannonhill.com/discussions/how-do-i/14581-how-to-clear-all-active-publish-jobs) your Cascade Server publish queue.
