/*
 * Created on Mar 24, 2009 by Syl Turner
 * 
 * Copyright(c) 2010 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinition;

/**
 * Tests operations on the {@link WorkflowDefinition} stub
 * 
 * @author  Syl Turner
 * @since   6.0
 */
public class TestWorkflowDefinition extends CascadeWebServicesTestCase
{
    private WorkflowDefinition workflow;
    private WorkflowDefinition workflowSite;
    private Site site;
    private String siteId;

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        workflow = generateWorkflowDefinition("ws_workflow", null);

        site = generateSite("site");
        siteId = site.getId();
        workflowSite = generateWorkflowDefinition("ws_workflow", siteId);

    }

    /**
     * Test reading the metadata set
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(rr, EntityTypeString.workflowdefinition);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        WorkflowDefinition wf = asset.getWorkflowDefinition();
        assertNotNull(wf);
        assertEquals(wf.getName(), workflow.getName());
        assertEquals(wf.getParentContainerId(), workflow.getParentContainerId());
        assertEquals(wf.getXml(), workflow.getXml());
    }

    /**
     * Tests editing of WorkflowDefinitions containing:
     * - NULL applicable groups
     * - empty ("") applicable groups
     * - multiple, semicolon separated applicable groups
     * 
     * @throws Exception
     */
    @Test
    public void testEditWithApplicableGroups() throws Exception
    {
        // TEST 1: test persisting of multiple applicable groups

        // create two groups
        String group1 = "wd_marketers";
        String group2 = "wd_developers";
        List<String> groups = new ArrayList<String>();
        groups.add(group1);
        groups.add(group2);
        generateGroup(group1);
        generateGroup(group2);

        ReadResult rr = client.read(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(rr, EntityTypeString.workflowdefinition);

        Asset asset = rr.getAsset();
        assertNotNull(asset);
        WorkflowDefinition wf = asset.getWorkflowDefinition();
        assertNotNull(wf);

        // set the groups to semicolon-separated list of groups
        wf.setApplicableGroups(group1 + ";" + group2);

        // edit the workflow definition
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.workflowdefinition);

        // read the workflow definition again
        ReadResult readAgain = client.read(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(readAgain, EntityTypeString.workflowdefinition);

        // ensure both groups were persisted
        WorkflowDefinition refetched = readAgain.getAsset().getWorkflowDefinition();
        assertContains(groups, refetched.getApplicableGroups());

        // TEST 2: test persisting of NULL groups
        refetched.setApplicableGroups(null);

        result = client.edit(auth, readAgain.getAsset());
        assertOperationSuccess(result, EntityTypeString.workflowdefinition);

        // read workflow definition again  
        readAgain = client.read(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(readAgain, EntityTypeString.workflowdefinition);

        // ensure applicable groups String is NULL
        refetched = readAgain.getAsset().getWorkflowDefinition();
        assertTrue(refetched.getApplicableGroups() == null);

        // TEST 3: testing persisting of empty groups string
        refetched.setApplicableGroups("");

        result = client.edit(auth, readAgain.getAsset());
        assertOperationSuccess(result, EntityTypeString.workflowdefinition);

        // read workflow definition again  
        readAgain = client.read(auth, new Identifier(workflow.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(readAgain, EntityTypeString.workflowdefinition);

        // ensure applicable groups String is NULL
        refetched = readAgain.getAsset().getWorkflowDefinition();
        assertTrue(refetched.getApplicableGroups() == null);
    }

    /**
     * Test reading the metadata set from a site
     * @throws Exception
     */
    public void testSiteRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(workflowSite.getId(), null, EntityTypeString.workflowdefinition, null));
        assertOperationSuccess(rr, EntityTypeString.workflowdefinition);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        WorkflowDefinition wf = asset.getWorkflowDefinition();
        assertNotNull(wf);
        assertEquals(wf.getName(), workflowSite.getName());
        assertEquals(wf.getParentContainerId(), workflowSite.getParentContainerId());
        assertEquals(wf.getXml(), workflowSite.getXml());
        assertEquals(wf.getSiteId(), workflowSite.getSiteId());
    }

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
}