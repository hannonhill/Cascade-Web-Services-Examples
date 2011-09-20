/*
 * Created on Jul 9, 2008 by User
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Test for performing web services operations on page configuration set containers.  
 * 
 * @author  Tim Reilly
 * @version $Id$
 * @since   5.5
 */
public class TestPageConfigurationSetContainer extends CascadeWebServicesTestCase
{
    private PageConfigurationSetContainer container;
    private String containerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        container = new PageConfigurationSetContainer();
        container.setName("new config set container");
        container.setParentContainerId(generatePageConfigurationSetContainer("ws_container_parent", null).getId());

        Asset asset = new Asset();
        asset.setPageConfigurationSetContainer(container);

        containerId = create(asset, EntityTypeString.pageconfigurationsetcontainer);
    }

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Tests reading a page configuration set
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(containerId, null, EntityTypeString.pageconfigurationsetcontainer, null));
        assertOperationSuccess(result, EntityTypeString.pageconfigurationsetcontainer);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        PageConfigurationSetContainer fetchedContainer = asset.getPageConfigurationSetContainer();
        assertEquals(container.getName(), fetchedContainer.getName());
        assertEquals(containerId, fetchedContainer.getId());
        assertNotNull(fetchedContainer.getPath());
        assertNotSame("", fetchedContainer.getPath());
        assertEquals(fetchedContainer.getChildren().length, 0);
        assertEquals(container.getParentContainerId(), fetchedContainer.getParentContainerId());
        assertNotNull(fetchedContainer.getParentContainerPath());
        assertNotSame("", fetchedContainer.getParentContainerPath());
    }

    /**
     * Tests moving a page configurationset container via web services
     * @throws Exception
     */
    public void testMove() throws Exception
    {
        Identifier identifier = new Identifier(containerId, null, EntityTypeString.pageconfigurationsetcontainer, null);
        ReadResult result = client.read(auth, identifier);
        result.getAsset().getPageConfigurationSetContainer();

        MoveParameters moveParameters = new MoveParameters();
        moveParameters.setNewName("ws_page_config_set_new");

        OperationResult moveResult = client.move(auth, identifier, moveParameters, null);
        assertOperationSuccess(moveResult, EntityTypeString.pageconfigurationsetcontainer);

        result = client.read(auth, identifier);
        assertOperationSuccess(result, EntityTypeString.pageconfigurationsetcontainer);

        PageConfigurationSetContainer moveFetched = result.getAsset().getPageConfigurationSetContainer();
        assertEquals("ws_page_config_set_new", moveFetched.getName());
    }
}
