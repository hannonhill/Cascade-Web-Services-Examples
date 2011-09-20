/*
 * Created on Jun 16, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.commons.util.StringUtil;
import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfiguration;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.PageRegion;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ScriptFormat;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredData;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataNode;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataType;
import com.hannonhill.www.ws.ns.AssetOperationService.Template;
import com.hannonhill.www.ws.ns.AssetOperationService.XmlBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.XsltFormat;

/**
 * Test for performing web services operations on pages. 
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.5
 */
public class TestPage extends CascadeWebServicesTestCase
{
    private Page page;
    private Page pageWithContentType;
    private String pageId;
    private String pageWithContentTypeId;
    private String contentType2Id;
    private String configurationSetPath;
    private String templatePath;
    private String formatPath;
    private String formatId;
    private String blockId;
    private String blockPath;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#setUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        contentType2Id = generateContentType("ws_content_type_2", false, null).getId();

        Asset asset = new Asset();
        page = new Page();
        page.setName("ws_page");
        page.setConfigurationSetId(generatePageConfigurationSet("ws_page_test", null).getId());
        page.setMetadataSetId(generateMetadataSet("ws_page_test", null).getId());
        page.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        page.setXhtml("some content\nmore");
        asset.setPage(page);
        pageId = create(asset, EntityTypeString.page);

        Asset asset2 = new Asset();
        pageWithContentType = new Page();
        pageWithContentType.setName("ws_page_content_type");
        pageWithContentType.setContentTypeId(generateContentType("ws_content_type", false, null).getId());
        pageWithContentType.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        pageWithContentType.setXhtml("some content");
        asset2.setPage(pageWithContentType);
        pageWithContentTypeId = create(asset2, EntityTypeString.page);

        Asset asset3 = new Asset();
        PageConfigurationSet configurationSet = new PageConfigurationSet();
        configurationSetPath = "ws_config_set";
        configurationSet.setName(configurationSetPath);
        String templateId = generateTemplate("ws_template", null).getId();
        ReadResult result = client.read(auth, new Identifier(templateId, null, EntityTypeString.template, null));
        assertOperationSuccess(result, EntityTypeString.template);
        assertNotNull(result.getAsset().getTemplate());
        Template template = result.getAsset().getTemplate();
        templatePath = template.getPath();
        PageConfiguration html = generatePageConfigurationObject("html", template, null);
        PageConfiguration xml = generatePageConfigurationObject("xml", template, null);
        html.setDefaultConfiguration(true);

        configurationSet.setPageConfigurations(new PageConfiguration[]
        {
                html, xml
        });
        configurationSet.setParentContainerId(RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID);
        asset3.setPageConfigurationSet(configurationSet);

        create(asset3, EntityTypeString.pageconfigurationset);

        formatId = generateXsltFormat("ws_stylesheet", null).getId();
        result = client.read(auth, new Identifier(formatId, null, EntityTypeString.format_XSLT, null));
        assertOperationSuccess(result, EntityTypeString.format_XSLT);
        assertNotNull(result.getAsset().getXsltFormat());
        XsltFormat xsltFormat = result.getAsset().getXsltFormat();
        formatPath = xsltFormat.getPath();

        blockId = generateXmlBlock("ws_block", null).getId();
        result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getXmlBlock());
        XmlBlock block = result.getAsset().getXmlBlock();
        blockPath = block.getPath();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // only items that are not created with generate*() methods need to be cleaned up here
        super.tearDown();
    }

    /**
     * Tests reading a page via web services.
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(pageId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        assertNotNull(result.getAsset().getPage());

        Page fetchedPage = result.getAsset().getPage();
        assertEquals(fetchedPage.getConfigurationSetId(), page.getConfigurationSetId());
        assertEquals(fetchedPage.getMetadataSetId(), page.getMetadataSetId());
        assertEquals(fetchedPage.getXhtml(), page.getXhtml());
        assertEquals(fetchedPage.getName(), page.getName());

        result = client.read(auth, new Identifier(pageWithContentTypeId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        assertNotNull(result.getAsset().getPage());

        fetchedPage = result.getAsset().getPage();
        assertEquals(fetchedPage.getContentTypeId(), pageWithContentType.getContentTypeId());
        assertEquals(fetchedPage.getXhtml(), pageWithContentType.getXhtml());
        assertEquals(fetchedPage.getName(), pageWithContentType.getName());
    }

    /**
     * Tests creating an xml page with a content type
     * 
     * @throws Exception
     */
    public void testCreateXmlPageWithContentType() throws Exception
    {
        String pageCtId = "";
        Asset asset = new Asset();

        Page pageCt = new Page();
        pageCt.setName("ws_pagect_xml");
        pageCt.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        pageCt.setXhtml("some content");
        pageCt.setContentTypeId(generateContentType("ws_content_type_pagect_xml", false, null).getId());

        asset.setPage(pageCt);
        pageCtId = create(asset, EntityTypeString.page);

        ReadResult result = client.read(auth, new Identifier(pageCtId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        Page fetchedPage = result.getAsset().getPage();
        assertNotNull(fetchedPage);
        assertTrue(StringUtil.isEmptyTrimmed(fetchedPage.getConfigurationSetId()));
        assertTrue(StringUtil.isEmptyTrimmed(fetchedPage.getMetadataSetId()));
        assertNull(fetchedPage.getStructuredData());

        assertEquals(pageCt.getName(), fetchedPage.getName());
        assertEquals(pageCt.getXhtml(), fetchedPage.getXhtml());
        assertEquals(pageCt.getContentTypeId(), fetchedPage.getContentTypeId());
        assertEquals(pageCt.getParentFolderId(), fetchedPage.getParentFolderId());
    }

    /**
     * Tests creating a structured data page with a content type 
     */
    public void testCreateSDpageWithContentType() throws Exception
    {
        String pageCtId = "";
        Asset asset = new Asset();

        StructuredDataNode node = new StructuredDataNode();
        node.setText("text");
        node.setType(StructuredDataType.text);
        node.setIdentifier("identifier");

        StructuredData sd = new StructuredData();
        sd.setStructuredDataNodes(new StructuredDataNode[]
        {
            node
        });

        Page pageCt = new Page();
        pageCt.setName("ws_pagect_sd");
        pageCt.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        pageCt.setStructuredData(sd);
        pageCt.setContentTypeId(generateContentType("ws_content_type_pagect_sd", true, null).getId());

        asset.setPage(pageCt);
        pageCtId = create(asset, EntityTypeString.page);

        ReadResult result = client.read(auth, new Identifier(pageCtId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        Page fetchedPage = result.getAsset().getPage();
        assertNotNull(fetchedPage);
        assertTrue(StringUtil.isEmptyTrimmed(fetchedPage.getConfigurationSetId()));
        assertTrue(StringUtil.isEmptyTrimmed(fetchedPage.getMetadataSetId()));
        assertNotNull(fetchedPage.getStructuredData());
        assertTrue(StringUtil.isEmptyTrimmed(fetchedPage.getStructuredData().getDefinitionId()));
        assertNotNull(fetchedPage.getStructuredData().getStructuredDataNodes());

        StructuredDataNode[] nodes = fetchedPage.getStructuredData().getStructuredDataNodes();
        assertEquals(nodes.length, 1);
        assertEquals(nodes[0].getType(), node.getType());
        assertEquals(nodes[0].getText(), node.getText());
        assertEquals(nodes[0].getIdentifier(), node.getIdentifier());

        assertEquals(pageCt.getName(), fetchedPage.getName());
        assertEquals(pageCt.getContentTypeId(), fetchedPage.getContentTypeId());
        assertEquals(pageCt.getParentFolderId(), fetchedPage.getParentFolderId());
    }

    /**
     * Tests the ability to edit a page that has a content type attached to it
     * 
     * @throws Exception
     */
    public void testEditWithContentType() throws Exception
    {
        // Make an edit without changing anything just to make sure no error occurs
        Page fetchedPage = getPage(pageWithContentTypeId);
        Asset asset = new Asset();
        asset.setPage(fetchedPage);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Change a content type and make sure the page has a new content type assigned
        fetchedPage = getPage(pageWithContentTypeId);
        fetchedPage.setContentTypeId(contentType2Id);
        // null out configuration ids as they are invalid now
        fetchedPage.setPageConfigurations(null);
        asset = new Asset();
        asset.setPage(fetchedPage);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Make sure the page has a new content type assigned
        fetchedPage = getPage(pageWithContentTypeId);
        assertEquals("ws_content_type_2", fetchedPage.getContentTypePath());
    }

    /**
     * Tests the ability to edit a page which includes a script format in one of the page regions.
     * 
     * @throws Exception
     */
    public void testEditWithScriptFormat() throws Exception
    {
        ScriptFormat scriptFormat = generateScriptFormat("ws_script_format", null);

        Page fetchedPage = getPage(pageId);

        PageConfiguration[] configurations = fetchedPage.getPageConfigurations();
        PageConfiguration config = configurations[0];
        PageRegion[] regions = config.getPageRegions();
        PageRegion region = regions[0];

        region.setFormatId(scriptFormat.getId());

        Asset asset = new Asset();
        asset.setPage(fetchedPage);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);

        fetchedPage = getPage(pageId);
        regions = fetchedPage.getPageConfigurations()[0].getPageRegions();
        region = regions[0];
        assertEquals(region.getFormatId(), scriptFormat.getId());
    }

    /**
     * Tests editing a page configuration level format field when the format supplied is not of
     * type xsl (which is invalid).
     */
    public void testEditGlobalXSLTFieldWithScriptFormat() throws Exception
    {
        ScriptFormat scriptFormat = generateScriptFormat("ws_script_format", null);

        Page fetchedPage = getPage(pageId);

        PageConfiguration[] configurations = fetchedPage.getPageConfigurations();
        PageConfiguration config = configurations[0];
        config.setFormatId(scriptFormat.getId());

        Asset asset = new Asset();
        asset.setPage(fetchedPage);
        OperationResult er = client.edit(auth, asset);
        // make sure that this edit was invalid because you cannot set a script format 
        // at the global page configuration level
        assertEquals(Boolean.parseBoolean(er.getSuccess()), false);
    }

    /**
     * Reads a page and returns it
     * 
     * @param id
     * @return
     * @throws Exception
     */
    private Page getPage(String id) throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(id, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        Page fetchedPage = result.getAsset().getPage();
        assertNotNull(fetchedPage);
        return fetchedPage;
    }

    /**
     * Due to many problems in previous Cascade Server versions that had to do with block and stylesheet 
     * assignment to the page regions and also with finding page configurations by name, a complete set of test cases
     * is needed to test if all types of assignments and unassignments are working correctly. 
     * 
     * @throws Exception
     */
    public void testAssignmentOfBlocksAndStylesheets() throws Exception
    {
        String name; //                           configs: html: B S xml: B S (B - Block assigned, S - stylesheet assigned)
        name = createPage(false, false, false); //               F F      F F - test creating a page without assigning any blocks or stylesheets to it but still providing configurations
        testPage(name, false, false, false, false);
        editPage(name, false, false, false); //                  F F      F F - test editing a page without assigning any blocks or stylesheets to it while there were no blocks or stylesheets assigned
        testPage(name, false, false, false, false);
        editPage(name, true, true, false); //                    T T      T T - test editing a page and assigning blocks and stylesheets to it while there were no blocks or stylesheets assigned before
        testPage(name, true, true, true, true);
        editPage(name, false, true, false); //                   T T      T T - test editing a page and assigning blocks to it leaving already assigned stylesheets alone (the stylesheets should not get unassigned)                    
        testPage(name, true, true, true, true);
        editPage(name, true, false, false); //                   T T      T T - test editing a page and assigning stylesheets to it leaving already assigned blocks alone (the blocks should not get unassigned)
        testPage(name, true, true, true, true);
        editPage(name, true, true, true); //                     T T      T T - test editing a page and assigning blocks and stylesheets to only one configuration leaving alone already assigned stylesheets and blocks in the other configuration 
        testPage(name, true, true, true, true);
        removeStylesheets(name); //                              T F      T F - test editing a page and unassigning stylesheets
        testPage(name, true, false, true, false);
        giveWrongConfigName(name, true, true, false, false); //  T F      T F - test editing a page and assigning blocks and stylesheets and one of the configuration names is incorrect - this should not make any changes to the page and should return an error message
        testPage(name, true, false, true, false);
        giveWrongConfigName(name, true, true, false, true); //   T T      T T - test editing a page and assigning blocks and stylesheets and one of the configuration names has wrong case (XML instead of xml) - this should be ok becasue Cascade Server is case insensitive
        testPage(name, true, true, true, true);
        removeConfig(name); //                                   T T      T T - test editing a page and providing only one of the configurations - this should not give any error
        testPage(name, true, true, true, true);
        removeStylesheetsAndBlocks(name); //                     F F      F F - test editing a page and unassigning stylesheets and blocks 
        testPage(name, false, false, false, false);

        name = createPageNoConfigs(); //                         F F      F F - test creating a page without providing any configurations
        testPage(name, false, false, false, false);
        editPage(name, true, false, true); //                    T F      F F - test editing a page and assigning only one block to it that had no stylesheets or blocks assigned to it before
        testPage(name, true, false, false, false);
        editPage(name, true, true, false); //                    T T      T T - test editing a page and assigning blocks and stylesheets to it while there were only blocks assigned to it before
        testPage(name, true, true, true, true);
        editPageNoConfigs(name); //                              T T      T T - test editing a page without providing any configurations while there were blocks and stylesheets assigned to it
        testPage(name, true, true, true, true);

        name = createPage(true, true, false); //                 T T      T T - test creating a page and assigning blocks and stylesheets to both configurations
        testPage(name, true, true, true, true);
        editPage(name, true, true, false); //                    T T      T T - test editing a page and assigning blocks and stylesheets to it while there were already blocks and stylesheets assigned to it
        testPage(name, true, true, true, true);

        name = createPage(false, true, false); //                F T      F T - test creating a page and assigning only stylesheets to both configurations
        testPage(name, false, true, false, true);
        editPage(name, false, true, false); //                   F T      F T - test editing a page and assigning only stylsheets to both configurations while there were already stylsheets assigned to it
        testPage(name, false, true, false, true);
        editPage(name, true, true, true); //                     T T      F T - test editing a page and assigning blocks and stylesheets only to the first configuration while there were only stylesheets assigned to both configurations before
        testPage(name, true, true, false, true);
        editPage(name, true, false, false); //                   T T      T T - test editing a page and assigning blocks to it while before there were already stylesheets assigned and one configuration had a block assigned and one configuration didn't
        testPage(name, true, true, true, true);

        name = createPage(true, false, false); //                T F      T F - test creating a page and assigning only blocks to both configurations
        testPage(name, true, false, true, false);
        editPage(name, true, false, false); //                   T F      T F - test editing a page and assigning only blocks to both configurations while there were already blocks assigned to it
        testPage(name, true, false, true, false);
        editPage(name, true, true, true); //                     T T      T F - test editing a page and assigning blocks and stylesheets only to one of the configurations while there were only blocks assigned to both configurations before
        testPage(name, true, true, true, false);
        editPage(name, false, true, false); //                   T T      T T - test editing a page and assigning stylsheets to it while before there were already blocks assigned to and one configuration had a stylsheets assigned and one didn't
        testPage(name, true, true, true, true);

        name = createPage(true, true, true); //                  T T      F F - test creating a page and providing blocks and stylesheets only to the first configuration
        testPage(name, true, true, false, false);
        editPage(name, true, true, true); //                     T T      F F - test editing a page and assigning block and stylesheet only to the first configuration while there was already block and stylesheet assigned to the first configuration
        testPage(name, true, true, false, false);
        editPage(name, true, false, false); //                   T T      T F - test editing a page and assigning blocks to both configurations while there was already block and stylesheet assigned to the first configuration
        testPage(name, true, true, true, false);
    }

    /**
     * Creates a page and provides or does not provide blocks and stylesheets. 
     * Also, specifies one or two configurations depending on the parameters.
     * 
     * @param blocks whether to provide blocks
     * @param stylesheets whether to provide stylesheets
     * @param configurationOnly whether to have 1 (true) or 2 (false) configurations provided
     * @return a randomly generated name of that page
     */
    private String createPage(boolean blocks, boolean stylesheets, boolean configurationOnly) throws Exception
    {
        Page page = new Page();
        String name = "" + (int) (Math.random() * 1000000);
        page.setName(name);
        page.setConfigurationSetPath(configurationSetPath);
        page.setParentFolderPath("/");
        page.setXhtml("test");
        PageConfiguration[] pc = new PageConfiguration[configurationOnly ? 1 : 2];
        pc[0] = new PageConfiguration();
        pc[0].setName("html");
        pc[0].setDefaultConfiguration(true);
        pc[0].setTemplatePath(templatePath);
        PageRegion[] pr = new PageRegion[1];
        pr[0] = new PageRegion();
        pr[0].setName("DEFAULT");
        if (blocks)
            pr[0].setBlockId(blockId);
        if (stylesheets)
            pr[0].setFormatId(formatId);
        pc[0].setPageRegions(pr);
        if (!configurationOnly)
        {
            pc[1] = new PageConfiguration();
            pc[1].setName("xml");
            pc[1].setDefaultConfiguration(false);
            pc[1].setTemplatePath(templatePath);
            PageRegion[] pr2 = new PageRegion[1];
            pr2[0] = new PageRegion();
            pr2[0].setName("DEFAULT");
            if (blocks)
                pr2[0].setBlockId(blockId);
            if (stylesheets)
                pr2[0].setFormatId(formatId);
            pc[1].setPageRegions(pr2);
        }
        page.setPageConfigurations(pc);

        Asset asset = new Asset();
        asset.setPage(page);

        String id = create(asset, EntityTypeString.page);
        assertNotNull(id);

        return name;
    }

    /**
     * Creates a page without providing any configurations and returns its randomly generated name
     * 
     * @return
     */
    private String createPageNoConfigs() throws Exception
    {
        Page page = new Page();
        String name = "" + (int) (Math.random() * 1000000);
        page.setName(name);
        page.setConfigurationSetPath(configurationSetPath);
        page.setParentFolderPath("/");
        page.setXhtml("test");

        Asset asset = new Asset();
        asset.setPage(page);

        String id = create(asset, EntityTypeString.page);
        assertNotNull(id);

        return name;
    }

    /**
     * Edits a page with given name and either specifies or does not specify blocks and stylesheets.
     * Also, specifies one or two configurations depending on the parameters.
     * 
     * @param name
     * @param blocks
     * @param stylesheets
     * @param configurationOnly
     * @throws Exception
     */
    private void editPage(String name, boolean blocks, boolean stylesheets, boolean configurationOnly) throws Exception
    {
        Identifier identifier = new Identifier();

        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        assertOperationSuccess(rr, EntityTypeString.page);
        Page page = rr.getAsset().getPage();
        assertNotNull(page);
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        for (int i = 0; i < pageConfigurations.length; i++)
        {
            PageConfiguration pageConfiguration = pageConfigurations[i];
            PageRegion[] pageRegions = pageConfiguration.getPageRegions();
            for (int j = 0; j < pageRegions.length; j++)
            {
                PageRegion pageRegion = pageRegions[j];
                if (stylesheets && (!configurationOnly || pageConfiguration.getName().equals("html")))
                    pageRegion.setFormatPath(formatPath);
                pageRegion.setBlockId(null);
                if (blocks && (!configurationOnly || pageConfiguration.getName().equals("html")))
                    pageRegion.setBlockPath(blockPath);
            }
        }
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Edits a page without providing any configurations to it
     * 
     * @param name
     * @throws Exception
     */
    private void editPageNoConfigs(String name) throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();

        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Read back and make sure that:
     *  - the template is still assigned
     *  - the blocks are still there (if applicable)
     *  - the stylesheets are still there (if applicable)
     *  - the other configuration still has blocks and or stylesheets (if applicable)
     *  Makes sure it can be re-edited   
     * 
     * @param name
     * @param firstBlock
     * @param firstStylesheet
     * @param secondBlock
     * @param secondStylesheet
     * @throws Exception
     */
    private void testPage(String name, boolean firstBlock, boolean firstStylesheet, boolean secondBlock, boolean secondStylesheet) throws Exception
    {
        Path path = new Path("/" + name, "", null);
        Identifier identifier = new Identifier(null, path, EntityTypeString.page, null);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        for (int i = 0; i < pageConfigurations.length; i++)
        {
            PageConfiguration pageConfiguration = pageConfigurations[i];
            assertEquals(pageConfiguration.getTemplatePath(), templatePath);
            PageRegion[] pageRegions = pageConfiguration.getPageRegions();
            for (int j = 0; j < pageRegions.length; j++)
            {
                PageRegion pageRegion = pageRegions[j];
                if ((pageConfigurations[i].getName().equals("html") && firstStylesheet)
                        || (pageConfigurations[i].getName().equals("xml") && secondStylesheet))
                    assertEquals(pageRegion.getFormatPath(), formatPath);
                if ((pageConfigurations[i].getName().equals("html") && !firstStylesheet)
                        || (pageConfigurations[i].getName().equals("xml") && !secondStylesheet))
                    assertEquals(pageRegion.getFormatPath(), null);
                pageRegion.setBlockId(null);
                if ((pageConfigurations[i].getName().equals("html") && firstBlock) || (pageConfigurations[i].getName().equals("xml") && secondBlock))
                    assertEquals(pageRegion.getBlockPath(), blockPath);
                if ((pageConfigurations[i].getName().equals("html") && !firstBlock)
                        || (pageConfigurations[i].getName().equals("xml") && !secondBlock))
                    assertEquals(pageRegion.getBlockPath(), null);
            }
        }
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Edits a page and removes all the stylesheet assignments from its regions
     * 
     * @param name
     * @throws Exception
     */
    private void removeStylesheets(String name) throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        for (int i = 0; i < pageConfigurations.length; i++)
        {
            PageConfiguration pageConfiguration = pageConfigurations[i];
            PageRegion[] pageRegions = pageConfiguration.getPageRegions();
            for (int j = 0; j < pageRegions.length; j++)
            {
                PageRegion pageRegion = pageRegions[j];
                pageRegion.setFormatPath("");
                pageRegion.setFormatId(null);
            }
        }
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Edits a page and provides an incorrect configuration name, either wrong case, or completely incorrect
     * 
     * @param name
     * @param blocks
     * @param stylesheets
     * @param configurationOnly
     * @param wrongCase
     * @throws Exception
     */
    private void giveWrongConfigName(String name, boolean blocks, boolean stylesheets, boolean configurationOnly, boolean wrongCase) throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        for (int i = 0; i < pageConfigurations.length; i++)
        {
            PageConfiguration pageConfiguration = pageConfigurations[i];
            if (pageConfiguration.getName().equals("xml"))
            {
                pageConfiguration.setId(null);
                if (wrongCase)
                    pageConfiguration.setName("XML");
                else
                    pageConfiguration.setName("blah");
            }
            PageRegion[] pageRegions = pageConfiguration.getPageRegions();
            for (int j = 0; j < pageRegions.length; j++)
            {
                PageRegion pageRegion = pageRegions[j];
                if (stylesheets && (!configurationOnly || pageConfiguration.getName().equals("html")))
                    pageRegion.setFormatPath(formatPath);
                if (blocks && (!configurationOnly || pageConfiguration.getName().equals("html")))
                    pageRegion.setBlockPath(blockPath);
            }
        }
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        if (wrongCase)
            assertOperationSuccess(er, EntityTypeString.page);
        else if (er.getSuccess().equals("true"))
            throw new Exception("At this point, there should be an error because the configuration name was incorrect");
    }

    /**
     * Removes a configuration from a page and tries to submit edit (submit with providing only one configuration) 
     * 
     * @param name
     * @throws Exception
     */
    private void removeConfig(String name) throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        PageConfiguration[] newPageConfigurations = new PageConfiguration[1];
        for (int i = 0; i < pageConfigurations.length; i++)
            if (pageConfigurations[i].getName().equals("xml"))
                newPageConfigurations[0] = pageConfigurations[i];
        page.setPageConfigurations(newPageConfigurations);
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Removes all the stylesheets and blocks from the regions and tries to edit the page
     * 
     * @param name
     * @throws Exception
     */
    private void removeStylesheetsAndBlocks(String name) throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setPath(new Path("/" + name, "", null));
        identifier.setType(EntityTypeString.page);

        ReadResult rr = client.read(auth, identifier);
        Page page = rr.getAsset().getPage();
        PageConfiguration[] pageConfigurations = page.getPageConfigurations();
        for (int i = 0; i < pageConfigurations.length; i++)
        {
            PageConfiguration pageConfiguration = pageConfigurations[i];
            PageRegion[] pageRegions = pageConfiguration.getPageRegions();
            for (int j = 0; j < pageRegions.length; j++)
            {
                PageRegion pageRegion = pageRegions[j];
                pageRegion.setFormatId(null);
                pageRegion.setFormatPath(null);
                pageRegion.setBlockPath(null);
                pageRegion.setBlockId(null);
            }
        }
        Asset asset = new Asset();
        asset.setPage(page);
        OperationResult er = client.edit(auth, asset);
        assertOperationSuccess(er, EntityTypeString.page);
    }

    /**
     * Tests reading a page that has recycled blocks and formats assigned to page regions.
     * 
     * @throws Exception
     */
    public void testGetRecycledBlockAndFormat() throws Exception
    {
        String name = createPage(true, true, true);
        Path path = new Path("/" + name, null, "Global");

        ReadResult result = client.read(auth, new Identifier(null, path, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        assertNotNull(result.getAsset().getPage());

        Page fetchedPage = result.getAsset().getPage();

        fetchedPage.getPageConfigurations()[0].setFormatId(formatId);
        Asset asset = new Asset();
        asset.setPage(fetchedPage);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.page);

        result = client.read(auth, new Identifier(null, path, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        fetchedPage = result.getAsset().getPage();
        assertNotNull(fetchedPage);

        PageConfiguration pageLevelConfig = findConfigurationByName("html", fetchedPage);
        assertNotNull(pageLevelConfig);

        PageRegion pr = pageLevelConfig.getPageRegions()[0];
        assertNotNull(pr);
        assertFalse(pr.getBlockRecycled());
        assertFalse(pr.getFormatRecycled());
        assertFalse(pageLevelConfig.getFormatRecycled());

        delete(pr.getFormatId(), EntityTypeString.format);
        delete(pr.getBlockId(), EntityTypeString.block);

        result = client.read(auth, new Identifier(null, path, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        fetchedPage = result.getAsset().getPage();
        assertNotNull(fetchedPage);

        pageLevelConfig = findConfigurationByName("html", fetchedPage);
        assertNotNull(pageLevelConfig);

        pr = pageLevelConfig.getPageRegions()[0];
        assertNotNull(pr);
        assertTrue(pr.getBlockRecycled());
        assertTrue(pr.getFormatRecycled());
    }

    /**
     * Tests creating a page using paths for all relationships.
     * @throws Exception
     */
    public void testCreatePageUsingPaths() throws Exception
    {
        Site site = generateSite("ws_site");
        Folder folder = generateFolder("ws_test_folder", site.getId());
        PageConfigurationSet configSet = generatePageConfigurationSet("ws_test_config", site.getId());
        MetadataSet metadataSet = generateMetadataSet("ws_metadata_set", site.getId());
        Folder expirationFolder = generateFolder("ws_expiration_folder", site.getId());

        Page page = new Page();
        page.setParentFolderPath(folder.getPath());
        page.setConfigurationSetPath(configSet.getPath());
        page.setMetadataSetPath(metadataSet.getPath());
        page.setExpirationFolderPath(expirationFolder.getPath());
        page.setSiteName(site.getName());
        page.setName("ws_test_page");
        page.setXhtml("blah");

        Asset asset = new Asset();
        asset.setPage(page);

        // create the asset with a parent folder using a parent folder path not an id
        asset = createAsset(asset, EntityTypeString.page);
        Page readPage = asset.getPage();

        assertEquals(readPage.getParentFolderPath(), folder.getPath());
        assertEquals(readPage.getConfigurationSetPath(), configSet.getPath());
        assertEquals(readPage.getMetadataSetPath(), metadataSet.getPath());
        assertEquals(readPage.getExpirationFolderPath(), expirationFolder.getPath());

        // verify that you can read the asset by path and by site name rather than ids
        ReadResult read = client.read(auth, new Identifier(null, new Path(asset.getPage().getPath(), null, site.getName()), EntityTypeString.page,
                null));
        assertOperationSuccess(read, EntityTypeString.page);
    }

    /**
     * Locate PageConfiguration by name among Page's configs.
     * 
     * @param name
     * @param page
     * @return Return found PageConfiguration or <code>null</code>, if no match
     */
    private PageConfiguration findConfigurationByName(String name, Page page)
    {
        for (PageConfiguration pc : page.getPageConfigurations())
        {
            if (pc.getName().equals(name))
                return pc;
        }

        return null;
    }
}
