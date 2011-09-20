/*
 * Created on May 11, 2009 by Mike Strauch
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteDestinationContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.Target;

/**
 * Web Services tests for Site Destination Container.
 * 
 * @author  Mike Strauch
 * @since   6.0.3
 */
public class TestSiteDestinationContainer extends CascadeWebServicesTestCase
{

    /**
     * Tests creating a SiteDestinationContainer via web services. 
     */
    public void testCreate() throws Exception
    {
        Site site = generateSite("ws_test_site");

        SiteDestinationContainer container = new SiteDestinationContainer();
        container.setSiteName(site.getName());
        container.setParentContainerId(site.getRootSiteDestinationContainerId());
        container.setName("ws_test_site_dest_cont");

        Asset asset = new Asset();
        asset.setSiteDestinationContainer(container);

        CreateResult result = client.create(auth, asset);
        assertOperationSuccess(result, EntityTypeString.sitedestinationcontainer);

        String id = result.getCreatedAssetId();

        ReadResult readResult = client.read(auth, new Identifier(id, null, EntityTypeString.sitedestinationcontainer, null));
        assertOperationSuccess(readResult, EntityTypeString.sitedestinationcontainer);
        Asset readAsset = readResult.getAsset();

        SiteDestinationContainer readContainer = readAsset.getSiteDestinationContainer();
        assertEquals(readContainer.getSiteName(), container.getSiteName());
        assertEquals(readContainer.getName(), container.getName());
        assertEquals(readContainer.getParentContainerId(), container.getParentContainerId());
    }

    /**
     * Test creating invalid containers.  Example: container without a site id. 
     */
    public void testInvalidContainers() throws Exception
    {
        Site site = generateSite("ws_test_site");
        SiteDestinationContainer container = new SiteDestinationContainer();

        container.setParentContainerId(site.getRootSiteDestinationContainerId());
        container.setName("ws_test_site_dest_cont");

        Asset asset = new Asset();
        asset.setSiteDestinationContainer(container);

        // should have failed because the container has no site
        assertFalse(SUCCESS.equals(client.create(auth, asset).getSuccess()));

        // test assigning a target as the parent of a site destination container
        Target target = generateTarget("ws_test_target");
        container.setParentContainerId(target.getId());
        container.setSiteId(site.getId());

        CreateResult result = client.create(auth, asset);
        assertFalse(SUCCESS.equals(result.getSuccess()));
    }

    /**
     * Tests editing a site destination container via web services.
     */
    public void testMove() throws Exception
    {
        Site site = generateSite("ws_test_site");
        SiteDestinationContainer container = generateSiteDestinationContainer("ws_test_container", site.getId());

        MoveParameters moveParameters = new MoveParameters();
        moveParameters.setNewName("ws_new_name");

        OperationResult result = client.move(auth, new Identifier(container.getId(), null, EntityTypeString.sitedestinationcontainer, false),
                moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.sitedestinationcontainer);

        ReadResult readResult = client.read(auth, new Identifier(container.getId(), null, EntityTypeString.sitedestinationcontainer, null));
        Asset readAsset = readResult.getAsset();

        SiteDestinationContainer readContainer = readAsset.getSiteDestinationContainer();
        assertEquals("ws_new_name", readContainer.getName());
    }

    /**
     * Tests creating a site destination container by using paths only to specify relationships.
     * @throws Exception
     */
    public void testCreatingWithPathsOnly() throws Exception
    {
        Site site = generateSite("ws_test_site");
        String containerRootId = getRootDestinationContainerId(site.getId());
        SiteDestinationContainer container = client
                .read(auth, new Identifier(containerRootId, null, EntityTypeString.sitedestinationcontainer, null)).getAsset()
                .getSiteDestinationContainer();

        SiteDestinationContainer toCreate = new SiteDestinationContainer();
        toCreate.setName("ws_test_site_dest_container");
        toCreate.setParentContainerPath(container.getPath());
        toCreate.setSiteName(site.getName());

        Asset asset = new Asset();
        asset.setSiteDestinationContainer(toCreate);

        CreateResult createResult = client.create(auth, asset);
        assertOperationSuccess(createResult, EntityTypeString.sitedestinationcontainer);
        String id = createResult.getCreatedAssetId();

        ReadResult readResult = client.read(auth, new Identifier(id, null, EntityTypeString.sitedestinationcontainer, null));
        Asset readAsset = readResult.getAsset();
        SiteDestinationContainer readContainer = readAsset.getSiteDestinationContainer();

        assertEquals(readContainer.getSiteId(), site.getId());
        assertEquals(readContainer.getParentContainerId(), containerRootId);
        assertEquals(readContainer.getName(), toCreate.getName());
    }
}
