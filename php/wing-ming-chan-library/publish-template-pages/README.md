A script to publish all pages related to a Cascade Server template.

**Example**

publishTemplate.php?templateId=[32-digit template ID]

**Requires**

- A local copy of Wing Ming Chan's [web services library](http://www.upstate.edu/cascade-admin/projects/web-services/)
- An [auth_user.php](https://github.com/espanae/Cascade-Web-Services-Examples/blob/master/php/wing-ming-chan-library/auth_user.php) file (line 11) that includes Wing's aformentioned library, your Cascade Server URL, username and password.
- Tested on Cascade Server 7.8

**How it Works**

1. Loop through the template's related configuration sets
2. For each configuration set, loop through all related content types
3. For each content type, loop through all related pages
4. Publish each page

**Issues**

Each page is published individually so templates with numerous pages [may clog](http://help.hannonhill.com/discussions/how-do-i/14581-how-to-clear-all-active-publish-jobs) your Cascade Server publish queue.

**Future Improvements**

Wrap publish requests together in a "batch" and submit as a single request.