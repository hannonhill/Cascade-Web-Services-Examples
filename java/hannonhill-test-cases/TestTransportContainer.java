/*
 * Created on Sep 12, 2008 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CopyParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.TransportContainer;

/**
 * Tests Web Services operations for Transport containers
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   5.7
 */
public class TestTransportContainer extends CascadeWebServicesTestCase
{

    private TransportContainer container;
    private String containerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        container = new TransportContainer();
        container.setName("ws_transport_container");
        container.setParentContainerId(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setTransportContainer(container);

        containerId = create(asset, EntityTypeString.transportcontainer);
    }

    /**
     * Tests reading a Transport Container using Web Services
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(containerId, null, EntityTypeString.transportcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.transportcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        TransportContainer sddc = asset.getTransportContainer();
        assertNotNull(sddc);
        assertEquals(sddc.getName(), container.getName());
        assertEquals(sddc.getParentContainerId(), container.getParentContainerId());
    }

    /**
     * Test copying a Transport Container using Web Services
     * 
     * @throws Exception
     */
    public void testCopy() throws Exception
    {
        CopyParameters copyParameters = new CopyParameters();
        copyParameters.setDestinationContainerIdentifier(new Identifier(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID, null,
                EntityTypeString.transportcontainer, null));
        copyParameters.setDoWorkflow(false);
        copyParameters.setNewName("copyOfTransportContainer");
        OperationResult or = client.copy(auth, new Identifier(containerId, null, EntityTypeString.transportcontainer, null), copyParameters, null);

        assertOperationSuccess(or, EntityTypeString.transportcontainer);

        Path path = new Path("/copyOfTransportContainer", "", null);
        ReadResult rr = client.read(auth, new Identifier(null, path, EntityTypeString.transportcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.transportcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        TransportContainer copied = asset.getTransportContainer();
        assertEquals(container.getParentContainerId(), copied.getParentContainerId());
        assertEquals("copyOfTransportContainer", copied.getName());

        Identifier identifier = new Identifier(copied.getId(), null, EntityTypeString.transportcontainer, null);
        client.delete(auth, identifier);
    }

    /**
     * Tests moving a Transport Container using Web Services
     * 
     * @throws Exception
     */
    public void testMove() throws Exception
    {
        MoveParameters moveParams = new MoveParameters();
        moveParams.setNewName("ws_new_name");

        OperationResult or = client.move(auth, new Identifier(containerId, null, EntityTypeString.transportcontainer, null), moveParams, null);
        assertOperationSuccess(or, EntityTypeString.transportcontainer);

        ReadResult rr = client.read(auth, new Identifier(containerId, null, EntityTypeString.transportcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.transportcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        TransportContainer transportContainer = asset.getTransportContainer();
        assertNotNull(transportContainer);

        assertEquals("ws_new_name", transportContainer.getName());
    }
}
