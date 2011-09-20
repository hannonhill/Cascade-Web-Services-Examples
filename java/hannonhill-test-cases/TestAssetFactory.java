/*
 * Created on Jun 13, 2008 by Mike Strauch
 * 
 * Copyright(c) 2010 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.junit.Test;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactory;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryPlugin;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryPluginParameter;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryWorkflowMode;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web services test for asset factories.
 * 
 * @author  Mike Strauch
 * @since   5.5
 */
public class TestAssetFactory extends CascadeWebServicesTestCase
{
    private AssetFactory assetFactory;
    private String assetFactoryId;

    private AssetFactory assetFactorySite;
    private String assetFactorySiteId;
    private Site site;

    AssetFactoryPlugin plugin;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#setUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        plugin = new AssetFactoryPlugin();
        plugin.setName("com.cms.assetfactory.DisplayToSystemNamePlugin");
        AssetFactoryPluginParameter[] parameters = new AssetFactoryPluginParameter[1];
        AssetFactoryPluginParameter parameter = new AssetFactoryPluginParameter();
        parameter.setName("paramName");
        parameter.setValue("paramValue");
        parameters[0] = parameter;
        plugin.setParameters(parameters);
        AssetFactoryPlugin[] plugins = new AssetFactoryPlugin[1];
        plugins[0] = plugin;

        Asset asset = new Asset();

        assetFactory = new AssetFactory();
        assetFactory.setParentContainerId(getRootAssetFactoryContainerId(null));
        assetFactory.setAssetType("page");
        assetFactory.setFolderPlacementPosition(new NonNegativeInteger("0"));
        assetFactory.setName("ws+af");
        assetFactory.setWorkflowMode(AssetFactoryWorkflowMode.fromString("none"));
        assetFactory.setApplicableGroups("Administrators");
        assetFactory.setPlugins(plugins);

        asset.setAssetFactory(assetFactory);

        CreateResult result = client.create(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactory);
        assetFactoryId = result.getCreatedAssetId();

        // site stuff
        site = generateSite("site");

        assetFactorySite = new AssetFactory();
        assetFactorySite.setParentContainerId(getRootAssetFactoryContainerId(site.getId()));
        assetFactorySite.setAssetType("page");
        assetFactorySite.setFolderPlacementPosition(new NonNegativeInteger("0"));
        assetFactorySite.setName("ws+af");
        assetFactorySite.setWorkflowMode(AssetFactoryWorkflowMode.fromString("none"));
        assetFactorySite.setApplicableGroups("Administrators");
        assetFactorySite.setSiteId(site.getId());
        assetFactorySite.setPlugins(plugins);
        asset.setAssetFactory(assetFactorySite);

        CreateResult siteResult = client.create(auth, asset);
        assertOperationSuccess(siteResult, EntityTypeString.assetfactory);
        assetFactorySiteId = siteResult.getCreatedAssetId();

    }

    /**
     * Tests reading an asset factory
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertEquals(result.getSuccess(), SUCCESS);

        AssetFactory readFactory = result.getAsset().getAssetFactory();
        assertNotNull(readFactory);
        assertEquals(readFactory.getParentContainerId(), assetFactory.getParentContainerId());
        assertEquals(readFactory.getAssetType(), assetFactory.getAssetType());
        assertEquals(readFactory.getFolderPlacementPosition(), assetFactory.getFolderPlacementPosition());
        assertEquals(readFactory.getName(), assetFactory.getName());
        assertEquals(readFactory.getWorkflowMode(), assetFactory.getWorkflowMode());
        assertEquals(readFactory.getApplicableGroups(), assetFactory.getApplicableGroups());
        assertNotNull(readFactory.getPlugins());
        assertEquals(1, readFactory.getPlugins().length);
        assertEquals(plugin.getName(), readFactory.getPlugins()[0].getName());
        assertNotNull(readFactory.getPlugins()[0].getParameters());
        assertEquals(1, readFactory.getPlugins()[0].getParameters().length);
        assertEquals("paramName", readFactory.getPlugins()[0].getParameters()[0].getName());
        assertEquals("paramValue", readFactory.getPlugins()[0].getParameters()[0].getValue());
    }

    /**
     * Tests reading an asset factory from a site
     * @throws Exception
     */
    public void testSiteRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(assetFactorySiteId, null, EntityTypeString.assetfactory, null));
        assertEquals(result.getSuccess(), SUCCESS);

        AssetFactory readFactory = result.getAsset().getAssetFactory();
        assertNotNull(readFactory);
        assertEquals(readFactory.getParentContainerId(), assetFactorySite.getParentContainerId());
        assertEquals(readFactory.getAssetType(), assetFactorySite.getAssetType());
        assertEquals(readFactory.getFolderPlacementPosition(), assetFactorySite.getFolderPlacementPosition());
        assertEquals(readFactory.getName(), assetFactorySite.getName());
        assertEquals(readFactory.getWorkflowMode(), assetFactorySite.getWorkflowMode());
        assertEquals(readFactory.getApplicableGroups(), assetFactorySite.getApplicableGroups());
        assertEquals(readFactory.getSiteId(), assetFactorySite.getSiteId());
        assertNotNull(readFactory.getPlugins());
        assertEquals(1, readFactory.getPlugins().length);
        assertEquals(plugin.getName(), readFactory.getPlugins()[0].getName());
        assertNotNull(readFactory.getPlugins()[0].getParameters());
        assertEquals(1, readFactory.getPlugins()[0].getParameters().length);
        assertEquals("paramName", readFactory.getPlugins()[0].getParameters()[0].getName());
        assertEquals("paramValue", readFactory.getPlugins()[0].getParameters()[0].getValue());
    }

    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactory);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        AssetFactory af = asset.getAssetFactory();
        assertNotNull(af);

        af.setAssetType("page");
        af.setFolderPlacementPosition(new NonNegativeInteger("0"));
        af.setWorkflowMode(AssetFactoryWorkflowMode.fromString("none"));
        af.setApplicableGroups("Administrators");

        AssetFactoryPlugin plugin = new AssetFactoryPlugin();
        plugin.setName("com.cms.assetfactory.FileLimitPlugin");
        AssetFactoryPluginParameter[] parameters = new AssetFactoryPluginParameter[1];
        AssetFactoryPluginParameter parameter = new AssetFactoryPluginParameter();
        parameter.setName("paramName2");
        parameter.setValue("paramValue2");
        parameters[0] = parameter;
        plugin.setParameters(parameters);
        AssetFactoryPlugin[] plugins = new AssetFactoryPlugin[1];
        plugins[0] = plugin;

        af.setPlugins(plugins);

        // edit the metadata set
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactory);

        // read the content type back again
        rr = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactory);

        asset = rr.getAsset();
        assertNotNull(asset);

        AssetFactory fetchedMs = asset.getAssetFactory();

        assertNotNull(fetchedMs.getPlugins());
        assertEquals(1, fetchedMs.getPlugins().length);
        assertEquals(plugin.getName(), fetchedMs.getPlugins()[0].getName());
        assertNotNull(fetchedMs.getPlugins()[0].getParameters());
        assertEquals(1, fetchedMs.getPlugins()[0].getParameters().length);
        assertEquals("paramName2", fetchedMs.getPlugins()[0].getParameters()[0].getName());
        assertEquals("paramValue2", fetchedMs.getPlugins()[0].getParameters()[0].getValue());
    }

    /**
     * Tests editing of AssetFactory assets containing:
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
        String group1 = "af_marketers";
        String group2 = "af_developers";
        List<String> groups = new ArrayList<String>();
        groups.add(group1);
        groups.add(group2);
        generateGroup(group1);
        generateGroup(group2);

        ReadResult rr = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(rr, EntityTypeString.assetfactory);

        Asset asset = rr.getAsset();
        assertNotNull(asset);
        AssetFactory af = asset.getAssetFactory();
        assertNotNull(af);

        // set the groups to semicolon-separated list of groups
        af.setApplicableGroups(group1 + ";" + group2);

        // edit the asset factory
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.assetfactory);

        // read the asset factory again
        ReadResult readAgain = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactory);

        AssetFactory refetched = readAgain.getAsset().getAssetFactory();
        assertContains(groups, refetched.getApplicableGroups());

        // TEST 2: edit with empty ("") applicable groups
        refetched.setApplicableGroups("");

        result = client.edit(auth, readAgain.getAsset());
        assertOperationSuccess(result, EntityTypeString.assetfactory);

        // read the asset factory again
        readAgain = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactory);

        // verify groups are NULL
        refetched = readAgain.getAsset().getAssetFactory();
        assertTrue(refetched.getApplicableGroups() == null);

        // TEST 3: ediwth with null applicable groups
        refetched.setApplicableGroups(null);

        result = client.edit(auth, readAgain.getAsset());
        assertOperationSuccess(result, EntityTypeString.assetfactory);

        // read the asset factory again
        readAgain = client.read(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(readAgain, EntityTypeString.assetfactory);

        // verify groups are NULL
        refetched = readAgain.getAsset().getAssetFactory();
        assertTrue(refetched.getApplicableGroups() == null);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        OperationResult result = client.delete(auth, new Identifier(assetFactoryId, null, EntityTypeString.assetfactory, null));
        if (!SUCCESS.equals(result.getSuccess()))
        {
            fail("Failed to delete asset factory: " + result.getMessage());
        }
    }

    /**
     * Tests reading an asset factory that has a recycled base asset and placement folder
     * 
     * @throws Exception
     */
    public void testGetRecycledAssets() throws Exception
    {
        AssetFactory assetFactory = generateAssetFactory("ws_assetfactory", null);
        Page page = generatePage("ws_page", null);
        Folder folder = generateFolder("ws_folder_af", null);
        assetFactory.setBaseAssetId(page.getId());
        assetFactory.setPlacementFolderId(folder.getId());
        Asset asset = new Asset();
        asset.setAssetFactory(assetFactory);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.assetfactory);

        ReadResult result = client.read(auth, new Identifier(assetFactory.getId(), null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(result, EntityTypeString.assetfactory);
        AssetFactory fetched = result.getAsset().getAssetFactory();
        assertNotNull(fetched);

        assertFalse(fetched.getBaseAssetRecycled());
        assertFalse(fetched.getPlacementFolderRecycled());

        delete(page.getId(), EntityTypeString.page);
        delete(folder.getId(), EntityTypeString.folder);

        result = client.read(auth, new Identifier(assetFactory.getId(), null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(result, EntityTypeString.assetfactory);
        fetched = result.getAsset().getAssetFactory();
        assertNotNull(fetched);

        assertTrue(fetched.getBaseAssetRecycled());
        assertTrue(fetched.getPlacementFolderRecycled());
    }
}
