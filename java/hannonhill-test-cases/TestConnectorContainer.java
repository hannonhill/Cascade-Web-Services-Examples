/*
 * Created on Sep 10, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Tests for performing web services operations on connector containers
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.4
 */
public class TestConnectorContainer extends CascadeWebServicesTestCase
{
    private ConnectorContainer connectorContainer;
    private Site site;
    private String siteId;
    private String connectorContainerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        site = generateSite("site");
        siteId = site.getId();

        connectorContainer = new ConnectorContainer();
        connectorContainer.setName("ws_connector_container");
        connectorContainer.setSiteId(siteId);
        connectorContainer.setParentContainerId(getRootConnectorContainerId(siteId));

        Asset asset = new Asset();
        asset.setConnectorContainer(connectorContainer);
        connectorContainerId = create(asset, EntityTypeString.connectorcontainer);
    }

    /**
     * Tests reading a connector container
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(connectorContainerId, null, EntityTypeString.connectorcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.connectorcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        ConnectorContainer fetchedContainer = rr.getAsset().getConnectorContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), connectorContainer.getName());
        assertEquals(fetchedContainer.getParentContainerId(), connectorContainer.getParentContainerId());
    }

    /**
     * Tests moving a connector container.
     * 
     * @throws Exception
     */
    public void testMove() throws Exception
    {
        MoveParameters moveParams = new MoveParameters();
        moveParams.setNewName("ws_connector_container_new_name");

        //move the connector container
        OperationResult result = client.move(auth, new Identifier(connectorContainerId, null, EntityTypeString.connectorcontainer, null), moveParams,
                null);
        assertOperationSuccess(result, EntityTypeString.connectorcontainer);

        //read the connector container back again
        ReadResult rr = client.read(auth, new Identifier(connectorContainerId, null, EntityTypeString.connectorcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.connectorcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        ConnectorContainer fetchedContainer = asset.getConnectorContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), "ws_connector_container_new_name");
    }

}
