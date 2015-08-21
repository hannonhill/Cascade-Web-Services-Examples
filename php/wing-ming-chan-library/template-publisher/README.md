A script to publish all pages related to a Cascade Server template.

**Example**

publishTemplate.php?templateId=[32-digit template ID]

**Requires**

- See the main [README](../) for a full requirements list.
- Cascade Server 7.8 tested.

**Known Issue**

Templates with numerous pages [may clog](http://help.hannonhill.com/discussions/how-do-i/14581-how-to-clear-all-active-publish-jobs) your Cascade Server publish queue.
See **How it Works**, **step 4**.

**How it Works**

1. Loops through the template's related configuration sets.
2. For each configuration set, loops through all related content types.
3. For each content type, loops through all related pages.
4. For each page, publish it.

**Future Improvements**

- The ability to schedule large publish jobs off hours.
- Wrap publish requests together in a "batch" and submit as a single request.