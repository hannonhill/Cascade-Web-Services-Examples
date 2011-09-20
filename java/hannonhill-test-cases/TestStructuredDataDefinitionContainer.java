/*
 * Created on Jul 11, 2008 by Mike Strauch
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.DataDefinitionContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Tests Web Services operations for Structured Data Definition containers
 * 
 * @author 	Tim Reilly
 * @version $Id$
 * @since   5.5
 */
public class TestStructuredDataDefinitionContainer extends CascadeWebServicesTestCase
{
    private DataDefinitionContainer container;
    private String containerId;
    private String containerPath;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        containerPath = "ws_str_data_def_container";
        container = new DataDefinitionContainer();
        container.setName(containerPath);
        container.setParentContainerId(RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID);

        Asset asset = new Asset();
        asset.setDataDefinitionContainer(container);

        containerId = create(asset, EntityTypeString.datadefinitioncontainer);
    }

    /**
     * Tests reading a Structured Data Definition Container using Web Services by id
     * 
     * @throws Exception
     */
    public void testReadById() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(containerId, null, EntityTypeString.datadefinitioncontainer, null));
        assertOperationSuccess(rr, EntityTypeString.datadefinitioncontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        DataDefinitionContainer sddc = asset.getDataDefinitionContainer();
        assertNotNull(sddc);
        assertEquals(sddc.getName(), container.getName());
        assertEquals(sddc.getParentContainerId(), container.getParentContainerId());
    }

    /**
     * Tests reading a Structured Data Definition Container using Web Services by path
     * 
     * @throws Exception
     */
    public void testReadByPath() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(null, new Path(containerPath, null, "Global"), EntityTypeString.datadefinitioncontainer,
                null));
        assertOperationSuccess(rr, EntityTypeString.datadefinitioncontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        DataDefinitionContainer sddc = asset.getDataDefinitionContainer();
        assertNotNull(sddc);
        assertEquals(sddc.getName(), container.getName());
        assertEquals(sddc.getParentContainerId(), container.getParentContainerId());
    }
}
