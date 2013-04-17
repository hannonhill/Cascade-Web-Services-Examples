###Summary

A script to recursively loop through all pages in a Cascade Server folder, compare dates and delete/unpublish all pages older than
a specified date. 

delete-and-unpublish-workflow.xml - Workflow definition required to unpublish. Create new workflow definition with this XML, replace username (line 32) with a Cascade user with delete/unpublish permissions for the target file(s). After creation, supply the ID in the deleteUnpublish.php script.

deleteUnpublish.php - The Web Services script. You'll need to supply the following:
- 32-digit ID of the folder to check
- 32-digit ID of workflow definition above
- Cascade Server page date variable to check against - one of the following values: 'createdDate', 'lastModifiedDate', 'lastPublishedDate', 'reviewDate', 'startDate', 'endDate'
- UNIX timestamp of date to compare against
- Cascade Server URL, username, password
