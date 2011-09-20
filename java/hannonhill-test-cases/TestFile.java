/*
 * Created on Jul 9, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadWorkflowInformationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.Workflow;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowAction;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowConfiguration;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowStep;

/**
 * Tests web services operations for files.
 * 
 * @author Mike Strauch
 * @since 5.5
 */
public class TestFile extends CascadeWebServicesTestCase
{
    private File file;
    private String fileId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        file = new File();
        file.setName("ws_file.txt");
        file.setMetadataSetId(generateMetadataSet("ws_file_metadataset", null).getId());
        file.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        file.setText("sample text");
        file.setRewriteLinks(true);
        file.setMaintainAbsoluteLinks(true);

        Asset asset = new Asset();
        asset.setFile(file);

        fileId = create(asset, EntityTypeString.file);
    }

    /**
     * Tests reading a file via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(fileId, null, EntityTypeString.file, null));
        assertOperationSuccess(result, EntityTypeString.file);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        File fetchedFile = asset.getFile();
        assertNotNull(fetchedFile);
        assertEquals(file.getName(), fetchedFile.getName());
        assertEquals(fileId, fetchedFile.getId());
        assertEquals(file.getText(), fetchedFile.getText());
        assertEquals(file.getMetadataSetId(), fetchedFile.getMetadataSetId());
        assertEquals(file.getParentFolderId(), fetchedFile.getParentFolderId());
        assertEquals(file.getRewriteLinks(), fetchedFile.getRewriteLinks());
        assertEquals(file.getMaintainAbsoluteLinks(), fetchedFile.getMaintainAbsoluteLinks());
    }

    /**
     * Tests creating a file with byte data via web services and then reading it back.
     */
    public void testCreateWithByteData() throws Exception
    {
        File byteDataFile = new File();
        byteDataFile.setName("ws_byte_file");
        byteDataFile.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);

        byte[] byteData =
        {
                1, 3, 123, 32
        };

        byteDataFile.setData(byteData);
        byteDataFile.setMetadataSetId(generateMetadataSet("ws_byte_file", null).getId());

        Asset asset = new Asset();
        asset.setFile(byteDataFile);

        String byteFileId = create(asset, EntityTypeString.file);

        ReadResult result = client.read(auth, new Identifier(byteFileId, null, EntityTypeString.file, null));
        assertOperationSuccess(result, EntityTypeString.file);

        asset = result.getAsset();
        assertNotNull(asset);

        File fetchedFile = asset.getFile();
        assertNotNull(fetchedFile);

        byte[] fetchedData = fetchedFile.getData();

        // compare byte data
        for (int i = 0; i < fetchedData.length; i++)
        {
            assertEquals(fetchedData[i], byteData[i]);
        }
    }

    /**
     * Tests editing a text file via web services.
     */
    public void testEdit() throws Exception
    {
        Identifier fileIdentifier = new Identifier(fileId, null, EntityTypeString.file, null);
        ReadResult result = client.read(auth, fileIdentifier);
        File fetchedFile = result.getAsset().getFile();

        // change properties and text
        fetchedFile.setText("new text");

        assertTrue(fetchedFile.getRewriteLinks());
        assertTrue(fetchedFile.getMaintainAbsoluteLinks());
        fetchedFile.setRewriteLinks(false);
        fetchedFile.setMaintainAbsoluteLinks(false);
        fetchedFile.setData(null);
        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.file);

        result = client.read(auth, fileIdentifier);
        assertOperationSuccess(result, EntityTypeString.file);

        fetchedFile = result.getAsset().getFile();
        assertEquals("new text", fetchedFile.getText());
        assertEquals(Boolean.FALSE, fetchedFile.getRewriteLinks());
        assertEquals(Boolean.FALSE, fetchedFile.getMaintainAbsoluteLinks());
    }

    /**
     * Tests editing a file via webservices by nulling the text data and supplying byte data.
     * 
     * @throws Exception
     */
    public void testEditWithByteData() throws Exception
    {
        File testFile = generateFile("ws_file", null);
        String testFileId = testFile.getId();

        Identifier identifier = new Identifier(testFileId, null, EntityTypeString.file, null);
        ReadResult result = client.read(auth, identifier);
        assertOperationSuccess(result, EntityTypeString.file);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        File fetchedFile = asset.getFile();
        assertNotNull(fetchedFile);

        byte[] byteData =
        {
                34, 12, 12, 45, 55, 98, 32, 1, 2
        };

        fetchedFile.setText(null);
        fetchedFile.setData(byteData);

        // edit the file with the new byte data
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.file);

        // read back and compare byte data
        result = client.read(auth, identifier);
        assertOperationSuccess(result, EntityTypeString.file);

        fetchedFile = result.getAsset().getFile();
        assertNotNull(fetchedFile);

        byte[] fetchedData = fetchedFile.getData();
        assertNotNull(fetchedData);

        // compare byte data
        for (int i = 0; i < fetchedData.length; i++)
        {
            assertEquals(fetchedData[i], byteData[i]);
        }
    }

    /**
     * Tests creating a file with a workflow.
     * 
     * @throws Exception
     */
    public void testCreateWithWorkflow() throws Exception
    {
        Site site = generateSite("ws_site");

        WorkflowDefinition wfDef = generateWorkflowDefinitionObject("ws_wf_def", site.getId());
        wfDef.setCreate(true);
        wfDef.setEdit(true);
        wfDef.setXml(getFileContents(TestFile.class, "ws_workflow_def.xml"));
        Asset wfDefAsset = new Asset();
        wfDefAsset.setWorkflowDefinition(wfDef);
        String wfDefId = create(wfDefAsset, EntityTypeString.workflowdefinition);

        WorkflowConfiguration workflowConfig = new WorkflowConfiguration();
        workflowConfig.setWorkflowDefinitionId(wfDefId);
        workflowConfig.setWorkflowComments("comment");
        workflowConfig.setWorkflowName("ws_test_workflow");
        Date date = new Date(1294876800000L);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        workflowConfig.setEndDate(cal);

        File toCreate = generateFileObject("ws_file.txt", site.getId());
        toCreate.setText("file text");

        Asset asset = new Asset();
        asset.setFile(toCreate);
        asset.setWorkflowConfiguration(workflowConfig);

        String resultId = create(asset, EntityTypeString.file);
        ReadWorkflowInformationResult wfResult = client.readWorkflowInformation(auth, new Identifier(resultId, null, EntityTypeString.file, false));
        assertOperationSuccess(wfResult, EntityTypeString.file);
        Workflow workflow = wfResult.getWorkflow();
        assertNotNull(workflow);
        assertEquals(cal.getTimeInMillis(), workflow.getEndDate().getTimeInMillis());
        assertEquals("ws_test_workflow", workflow.getName());
        assertEquals("transition", workflow.getCurrentStep());

        WorkflowStep[] orderedSteps = workflow.getOrderedSteps();
        assertEquals("initialize", orderedSteps[0].getIdentifier());
        assertEquals("Initialization", orderedSteps[0].getLabel());
        assertEquals("system", orderedSteps[0].getStepType());

        WorkflowAction[] initializeActions = orderedSteps[0].getActions();
        assertEquals("forward", initializeActions[0].getActionType());
        assertEquals("initialize", initializeActions[0].getIdentifier());
        assertEquals("Initialize", initializeActions[0].getLabel());

        assertEquals("transition", orderedSteps[1].getIdentifier());
        assertEquals("Transition", orderedSteps[1].getLabel());
        assertEquals("transition", orderedSteps[1].getStepType());

        WorkflowAction[] transitionActions = orderedSteps[1].getActions();
        assertEquals("go", transitionActions[0].getIdentifier());
        assertEquals("Go!", transitionActions[0].getLabel());

        assertEquals("finished", orderedSteps[2].getIdentifier());
        assertEquals("Finished", orderedSteps[2].getLabel());
        assertEquals("system", orderedSteps[2].getStepType());

        // Delete workflow so that clean up of the file is possible
        client.delete(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflow, false));
    }
}
