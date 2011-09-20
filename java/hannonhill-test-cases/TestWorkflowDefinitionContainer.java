/*
 * Created on Jul 18, 2008 by Tim Reilly
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinitionContainer;

/**
 * Tests for performing web services operations on workflow definition containers
 * 
 * @author 	Tim Reilly
 * @version $Id$
 * @since   5.0
 */
public class TestWorkflowDefinitionContainer extends CascadeWebServicesTestCase
{
    private WorkflowDefinitionContainer wfdc;
    private String workflowDefinitionContainerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        wfdc = new WorkflowDefinitionContainer();
        wfdc.setName("ws_workflow_definition_container");
        wfdc.setParentContainerId(RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setWorkflowDefinitionContainer(wfdc);
        workflowDefinitionContainerId = create(asset, EntityTypeString.workflowdefinitioncontainer);
    }

    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(workflowDefinitionContainerId, null, EntityTypeString.workflowdefinitioncontainer, null));
        assertOperationSuccess(rr, EntityTypeString.workflowdefinitioncontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        WorkflowDefinitionContainer fetchedContainer = rr.getAsset().getWorkflowDefinitionContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), wfdc.getName());
        assertEquals(fetchedContainer.getParentContainerId(), wfdc.getParentContainerId());
    }

    public void testMove() throws Exception
    {
        MoveParameters mp = new MoveParameters();
        mp.setNewName("ws_workflow_definition_container_new_name");

        //edit the workflow definition container
        OperationResult result = client.move(auth, new Identifier(workflowDefinitionContainerId, null, EntityTypeString.workflowdefinitioncontainer,
                null), mp, null);
        assertOperationSuccess(result, EntityTypeString.workflowdefinitioncontainer);

        //read the workflow definition container back again
        ReadResult rr = client.read(auth, new Identifier(workflowDefinitionContainerId, null, EntityTypeString.workflowdefinitioncontainer, null));
        assertOperationSuccess(rr, EntityTypeString.workflowdefinitioncontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        WorkflowDefinitionContainer fetchedContainer = asset.getWorkflowDefinitionContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), "ws_workflow_definition_container_new_name");
    }
}
