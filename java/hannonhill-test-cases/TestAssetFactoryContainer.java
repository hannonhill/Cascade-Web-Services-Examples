/*
 * Created on October 5, 2010 by Bradley Wagner
 * 
 * Copyright(c) 2010 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Test operations on {@link AssetFactoryContainer}
 * 
 * @author Bradley Wagner
 * @since  6.7.3
 */
public class TestAssetFactoryContainer extends CascadeWebServicesTestCase
{
    private AssetFactoryContainer afContainer;
    private Site site;
    private String siteId;
    private String afContainerId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        site = generateSite("site");
        siteId = site.getId();

        afContainer = new AssetFactoryContainer();
        afContainer.setName("ws_af_container");
        afContainer.setSiteId(siteId);
        afContainer.setParentContainerId(getRootAssetFactoryContainerId(siteId));
        afContainer.setApplicableGroups("");

        Asset asset = new Asset();
        asset.setAssetFactoryContainer(afContainer);
        afContainerId = create(asset, EntityTypeString.assetfactorycontainer);
    }

    /**
     * Tests reading an AssetFactoryContainer
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactorycontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        AssetFactoryContainer fetchedContainer = rr.getAsset().getAssetFactoryContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), afContainer.getName());
        assertEquals(fetchedContainer.getParentContainerId(), afContainer.getParentContainerId());
    }

    /**
     * Tests moving a AssetFactoryContainer
     * 
     * @throws Exception
     */
    public void testMove() throws Exception
    {
        MoveParameters moveParams = new MoveParameters();
        moveParams.setNewName("ws_af_container_new_name");

        //move the container
        OperationResult result = client.move(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null), moveParams,
                null);
        assertOperationSuccess(result, EntityTypeString.assetfactorycontainer);

        //read the connector container back again
        ReadResult rr = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactorycontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        AssetFactoryContainer fetchedContainer = asset.getAssetFactoryContainer();
        assertNotNull(fetchedContainer);
        assertEquals(fetchedContainer.getName(), "ws_af_container_new_name");
    }

    /**
     * Tests editing of AssetFactoryContaining containing:
     * - NULL applicable groups
     * - empty ("") applicable groups
     * - multiple, semicolon separated applicable groups
     * 
     * @throws Exception
     */
    @Test
    public void testEditWithApplicableGroups() throws Exception
    {
        // TEST 1: edit with multiple applicable groups

        // create two groups
        String group1 = "afc_marketers";
        String group2 = "afc_developers";
        List<String> groups = new ArrayList<String>();
        groups.add(group1);
        groups.add(group2);
        generateGroup(group1);
        generateGroup(group2);

        ReadResult rr = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactorycontainer);

        Asset asset = rr.getAsset();
        assertNotNull(asset);
        AssetFactoryContainer afc = asset.getAssetFactoryContainer();
        assertNotNull(afc);

        // set the groups to semicolon-separated list of groups
        afc.setApplicableGroups(group1 + ";" + group2);

        // edit the asset factory container
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactorycontainer);

        // read the asset factory container again
        ReadResult readAgain = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactorycontainer);

        AssetFactoryContainer refetched = readAgain.getAsset().getAssetFactoryContainer();
        assertContains(groups, refetched.getApplicableGroups());

        // TEST 2: Edit with NULL applicable groups

        // set the groups to NULL
        afc.setApplicableGroups(null);

        // edit the asset factory container
        result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactorycontainer);

        // read the asset factory container again
        readAgain = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactorycontainer);

        refetched = readAgain.getAsset().getAssetFactoryContainer();
        assertTrue(refetched.getApplicableGroups() == null);

        // TEST 3: Edith with empty ("") applicable groups

        // set the groups to empty string
        afc.setApplicableGroups("");

        // edit the asset factory container
        result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactorycontainer);

        // read the asset factory container again
        readAgain = client.read(auth, new Identifier(afContainerId, null, EntityTypeString.assetfactorycontainer, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactorycontainer);

        // assert the groups are null
        refetched = readAgain.getAsset().getAssetFactoryContainer();
        assertTrue(refetched.getApplicableGroups() == null);
    }
}
