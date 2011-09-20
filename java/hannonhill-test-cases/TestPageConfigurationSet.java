/*
 * Created on Feb 17, 2009 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfiguration;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.PageRegion;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.SerializationType;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.Template;
import com.hannonhill.www.ws.ns.AssetOperationService.XsltFormat;

/**
 * Test for performing web services operations on PageConfigurationSets.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   6.0
 */
public class TestPageConfigurationSet extends CascadeWebServicesTestCase
{

    private PageConfigurationSet pc;
    private PageConfigurationSet pcSite;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {

        pc = generatePageConfigurationSet("ws_pcs", null);

        Site site = generateSite("site");
        pcSite = generatePageConfigurationSet("ws_pcs", site.getId());
    }

    /**
     * Test for renaming a page configuration in a configuration set.
     */
    /*
     * Commented out until CSCD-5188 is fixed
    public void testRenamingPageConfiguration() throws Exception
    {

        //TODO(6.x)?: This test will fail until page configuration ids are passed from schema object to dom when
        // editing page configuration sets
        PageConfigurationSet set = generatePageConfigurationSet("ws_test_rename", null);

        PageConfiguration[] configs = set.getPageConfigurations();
        configs[0].setName("ws_new_config_name");

        Asset asset = new Asset();
        asset.setPageConfigurationSet(set);
        // edit the config with both a name and an id
        // should result in a single configuration with the new name
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.pageconfigurationset);

        ReadResult readResult = client.read(auth, new Identifier(set.getId(), null, EntityTypeString.pageconfigurationset, null));
        asset = readResult.getAsset();
        PageConfigurationSet readSet = asset.getPageConfigurationSet();

        PageConfiguration[] configurations = readSet.getPageConfigurations();
        assertEquals(configurations.length, 1);
        assertEquals(configurations[0].getId(), set.getPageConfigurations()[0].getId());
        assertEquals(configurations[0].getName(), "ws_new_config_name");

        // edit the config with just a name, no id
        // should result in a NEW configuration with the new name since there is no way
        // to tell if the edited configuration matches an old configuration
        configurations[0].setId(null);
        configurations[0].setName("ws_no_id_name");

        result = client.edit(auth, asset);

        readResult = client.read(auth, new Identifier(set.getId(), null, EntityTypeString.pageconfigurationset, null));
        asset = readResult.getAsset();
        readSet = asset.getPageConfigurationSet();

        configurations = readSet.getPageConfigurations();
        assertEquals(configurations.length, 1);
        assertFalse(configurations[0].getId().equals(set.getPageConfigurations()[0].getId()));
        assertEquals(configurations[0].getName(), "ws_no_id_name");
    }
    */

    /**
     * Tests editing a page configuration set.
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        // do a normal edit and verify the template is changed
        PageConfigurationSet setNormalEdit = generatePageConfigurationSet("ws_test_edit", null);

        Template temp = generateTemplate("ws_test_edit_template", null);
        PageConfiguration[] normalEditConfigs = setNormalEdit.getPageConfigurations();
        normalEditConfigs[0].setTemplateId(temp.getId());
        String configOldId = normalEditConfigs[0].getId();

        Asset asset = new Asset();
        asset.setPageConfigurationSet(setNormalEdit);
        OperationResult normalEditResult = client.edit(auth, asset);
        assertOperationSuccess(normalEditResult, EntityTypeString.pageconfigurationset);

        ReadResult read = client.read(auth, new Identifier(setNormalEdit.getId(), null, EntityTypeString.pageconfigurationset, null));
        asset = read.getAsset();
        PageConfigurationSet readNormalSet = asset.getPageConfigurationSet();

        normalEditConfigs = readNormalSet.getPageConfigurations();
        assertEquals(normalEditConfigs[0].getTemplateId(), temp.getId());
        assertEquals(normalEditConfigs[0].getId(), configOldId);

        delete(setNormalEdit.getId(), EntityTypeString.pageconfigurationset);
    }

    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(pc.getId(), null, EntityTypeString.pageconfigurationset, null));
        assertOperationSuccess(rr, EntityTypeString.pageconfigurationset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        PageConfigurationSet fetched = asset.getPageConfigurationSet();
        assertNotNull(fetched);
        assertEquals(fetched.getName(), pc.getName());
        assertEquals(fetched.getParentContainerId(), pc.getParentContainerId());

        PageConfiguration pageConfig = pc.getPageConfigurations()[0];
        PageConfiguration fetchedPageConfig = fetched.getPageConfigurations()[0];
        assertNotNull(fetchedPageConfig);
        assertEquals(fetchedPageConfig.getName(), pageConfig.getName());
        assertEquals(fetchedPageConfig.getTemplateId(), pageConfig.getTemplateId());
    }

    public void testSiteRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(pcSite.getId(), null, EntityTypeString.pageconfigurationset, null));
        assertOperationSuccess(rr, EntityTypeString.pageconfigurationset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        PageConfigurationSet fetched = asset.getPageConfigurationSet();
        assertNotNull(fetched);
        assertEquals(fetched.getName(), pcSite.getName());
        assertEquals(fetched.getParentContainerId(), pcSite.getParentContainerId());
        assertEquals(fetched.getSiteId(), pcSite.getSiteId());

        PageConfiguration pageConfig = pcSite.getPageConfigurations()[0];
        PageConfiguration fetchedPageConfig = fetched.getPageConfigurations()[0];
        assertNotNull(fetchedPageConfig);
        assertEquals(fetchedPageConfig.getName(), pageConfig.getName());
        assertEquals(fetchedPageConfig.getTemplateId(), pageConfig.getTemplateId());
        assertEquals(fetchedPageConfig.getSerializationType(), pageConfig.getSerializationType());
    }

    /**
     * Tests reading a configuration set that has recycled blocks and formats assigned to page regions.
     * 
     * @throws Exception
     */
    public void testGetRecycledBlockAndFormat() throws Exception
    {
        String formatId = generateXsltFormat("ws_stylesheet", null).getId();
        String blockId = generateXmlBlock("ws_block", null).getId();

        PageRegion pageRegion = new PageRegion();
        pageRegion.setBlockId(blockId);
        pageRegion.setFormatId(formatId);
        pageRegion.setName("DEFAULT");

        pc.getPageConfigurations()[0].setPageRegions(new PageRegion[]
        {
            pageRegion
        });
        pc.getPageConfigurations()[0].setFormatId(formatId);

        Asset asset = new Asset();
        asset.setPageConfigurationSet(pc);
        OperationResult editResult = client.edit(auth, asset);

        assertOperationSuccess(editResult, EntityTypeString.pageconfigurationset);

        ReadResult rr = client.read(auth, new Identifier(pc.getId(), null, EntityTypeString.pageconfigurationset, null));
        assertOperationSuccess(rr, EntityTypeString.pageconfigurationset);
        PageConfigurationSet fetched = rr.getAsset().getPageConfigurationSet();
        assertNotNull(fetched);

        assertFalse(fetched.getPageConfigurations()[0].getPageRegions()[0].getBlockRecycled());
        assertFalse(fetched.getPageConfigurations()[0].getPageRegions()[0].getFormatRecycled());
        assertFalse(fetched.getPageConfigurations()[0].getFormatRecycled());

        delete(formatId, EntityTypeString.format);
        delete(blockId, EntityTypeString.block);

        rr = client.read(auth, new Identifier(pc.getId(), null, EntityTypeString.pageconfigurationset, null));
        assertOperationSuccess(rr, EntityTypeString.pageconfigurationset);
        fetched = rr.getAsset().getPageConfigurationSet();
        assertNotNull(fetched);

        assertTrue(fetched.getPageConfigurations()[0].getPageRegions()[0].getBlockRecycled());
        assertTrue(fetched.getPageConfigurations()[0].getPageRegions()[0].getFormatRecycled());
        assertTrue(fetched.getPageConfigurations()[0].getFormatRecycled());
    }

    /**
     * Tests creating a page configuration set by specifying paths for all relationships.
     * @throws Exception
     */
    public void testCreatePageConfigSetUsingPaths() throws Exception
    {
        Site site = generateSite("ws_pcs_site");
        PageConfigurationSetContainer container = generatePageConfigurationSetContainer("ws_pcs_cont", site.getId());
        XsltFormat format = generateXsltFormat("ws_format", site.getId());
        Template pcTemplate = generateTemplate("ws_template", site.getId());

        PageConfigurationSet configSet = new PageConfigurationSet();
        configSet.setSiteName(site.getName());
        configSet.setParentContainerPath(container.getPath());

        PageConfiguration[] configs = new PageConfiguration[1];
        PageConfiguration config = new PageConfiguration();
        config.setTemplatePath(pcTemplate.getPath());
        config.setName("ws_name");
        config.setOutputExtension(".html");
        config.setSerializationType(SerializationType.HTML);
        config.setFormatPath(format.getPath());
        configs[0] = config;
        configSet.setPageConfigurations(configs);
        configSet.setName("ws_test_pcs");

        Asset asset = new Asset();
        asset.setPageConfigurationSet(configSet);
        asset = createAsset(asset, EntityTypeString.pageconfigurationset);

        PageConfigurationSet readSet = asset.getPageConfigurationSet();
        assertEquals(readSet.getParentContainerPath(), container.getPath());
        assertEquals(readSet.getSiteName(), site.getName());
        assertEquals(readSet.getPageConfigurations()[0].getFormatPath(), format.getPath());
        assertEquals(readSet.getPageConfigurations()[0].getTemplatePath(), pcTemplate.getPath());

        // verify that you can read the asset by path and by site name rather than ids
        ReadResult read = client.read(auth, new Identifier(null, new Path(asset.getPageConfigurationSet().getPath(), null, site.getName()),
                EntityTypeString.pageconfigurationset, null));
        assertOperationSuccess(read, EntityTypeString.pageconfigurationset);
    }
}
