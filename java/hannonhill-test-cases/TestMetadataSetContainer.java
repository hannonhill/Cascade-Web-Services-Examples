/*
 * Created on Jul 15, 2008 by Tim Reilly
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Test for performing web services operations on metadata set containers.
 * 
 * @author 	Tim Reilly
 * @version $Id$
 * @since   5.5
 */
public class TestMetadataSetContainer extends CascadeWebServicesTestCase
{
    private MetadataSetContainer container;
    private String containerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        container = new MetadataSetContainer();
        container.setName("ws_metadata_set_container");
        container.setParentContainerId(RootContainerIds.METADATASET_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setMetadataSetContainer(container);

        containerId = create(asset, EntityTypeString.metadatasetcontainer);
    }

    /**
     * Tests reading a Metadata Set Container using Web Services
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(containerId, null, EntityTypeString.metadatasetcontainer, null));
        assertOperationSuccess(rr, EntityTypeString.metadatasetcontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSetContainer msc = asset.getMetadataSetContainer();
        assertNotNull(msc);
        assertEquals(msc.getName(), container.getName());
        assertEquals(msc.getParentContainerId(), container.getParentContainerId());
    }

}
