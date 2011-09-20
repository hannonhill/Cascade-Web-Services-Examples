/*
 * Created on Jun 17, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypeContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Tests web services operations for content type containers.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.5
 */
public class TestContentTypeContainer extends CascadeWebServicesTestCase
{
    private ContentTypeContainer container;
    private String containerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        container = new ContentTypeContainer();
        container.setName("ws_content_type_container");
        container.setParentContainerId(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setContentTypeContainer(container);

        containerId = create(asset, EntityTypeString.contenttypecontainer);
    }

    /**
     * Tests creating a content type container via web services.
     */
    public void testCreate() throws Exception
    {
        String id = "";
        ContentTypeContainer container = new ContentTypeContainer();
        container.setName("ws_content_type_container_create");
        container.setParentContainerId(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setContentTypeContainer(container);

        id = create(asset, EntityTypeString.contenttypecontainer);

        ReadResult result = client.read(auth, new Identifier(id, null, EntityTypeString.contenttypecontainer, null));
        assertOperationSuccess(result, EntityTypeString.contenttypecontainer);
        ContentTypeContainer fetchedContainer = result.getAsset().getContentTypeContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), container.getName());
        assertEquals(fetchedContainer.getParentContainerId(), container.getParentContainerId());
        assertEquals(fetchedContainer.getChildren().length, 0);
    }

    /**
     * Tests moving a content type container via web services
     * @throws Exception
     */
    public void testMove() throws Exception
    {
        MoveParameters mp = new MoveParameters();
        mp.setNewName("ws_content_type_container_new_name");

        // move the content type container
        OperationResult result = client.move(auth, new Identifier(containerId, null, EntityTypeString.contenttypecontainer, null), mp, null);
        assertOperationSuccess(result, EntityTypeString.contenttypecontainer);

        // read the content type container back again
        ReadResult readResult = client.read(auth, new Identifier(containerId, null, EntityTypeString.contenttypecontainer, null));
        assertOperationSuccess(readResult, EntityTypeString.contenttypecontainer);

        Asset asset = readResult.getAsset();
        assertNotNull(asset);

        ContentTypeContainer fetchedCtContainer = asset.getContentTypeContainer();
        assertNotNull(fetchedCtContainer);
        assertEquals(fetchedCtContainer.getName(), "ws_content_type_container_new_name");
    }
}
