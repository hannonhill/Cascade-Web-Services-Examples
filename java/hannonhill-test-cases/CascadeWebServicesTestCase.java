/*
 * Created on Jun 12, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.axis.types.URI;
import org.apache.commons.collections.CollectionUtils;

import com.hannonhill.commons.junit.HHTestCase;
import com.hannonhill.commons.util.ClassUtil;
import com.hannonhill.commons.util.StringUtil;
import com.hannonhill.commons.util.io.StreamReader;
import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactory;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryWorkflowMode;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetOperationHandler;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetOperationHandlerServiceLocator;
import com.hannonhill.www.ws.ns.AssetOperationService.Authentication;
import com.hannonhill.www.ws.ns.AssetOperationService.Block;
import com.hannonhill.www.ws.ns.AssetOperationService.CheckOutResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypeContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.DataDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.DatabaseTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.DublinAwareAsset;
import com.hannonhill.www.ws.ns.AssetOperationService.DynamicMetadataField;
import com.hannonhill.www.ws.ns.AssetOperationService.DynamicMetadataFieldDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.DynamicMetadataFieldType;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.FeedBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.FieldValue;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.FileSystemTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Group;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.ListMessagesResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Metadata;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfiguration;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.PageRegion;
import com.hannonhill.www.ws.ns.AssetOperationService.PublishSet;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadAccessRightsResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadAuditsResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadWorkflowInformationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.RecycleBinExpiration;
import com.hannonhill.www.ws.ns.AssetOperationService.ScriptFormat;
import com.hannonhill.www.ws.ns.AssetOperationService.SearchResult;
import com.hannonhill.www.ws.ns.AssetOperationService.SerializationType;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteDestinationContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.Symlink;
import com.hannonhill.www.ws.ns.AssetOperationService.Target;
import com.hannonhill.www.ws.ns.AssetOperationService.Template;
import com.hannonhill.www.ws.ns.AssetOperationService.TextBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.User;
import com.hannonhill.www.ws.ns.AssetOperationService.UserAuthTypes;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinitionContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowNamingBehavior;
import com.hannonhill.www.ws.ns.AssetOperationService.XhtmlDataDefinitionBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.XmlBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.XsltFormat;

/**
 * Base test class for web services tests.
 * 
 * @author Mike Strauch
 * @since 5.5
 */
public abstract class CascadeWebServicesTestCase extends HHTestCase
{
    /*
     * Current assumptions for this test class:
     * 
     *  1. Root containers exist in the cms (this can be remedied by adding a method like generateRootContainers()
     *     that would query the cms and see if a root container exists and then create it if not).
     *  2. 
     */

    protected static Map<Class<? extends OperationResult>, String> OPERATION_CLASS_TO_STRING_MAP = new HashMap<Class<? extends OperationResult>, String>();

    protected static final String SUCCESS = "true";
    protected static final String FAILURE = "false";

    protected AssetOperationHandler client;
    protected Authentication auth;

    // add Identifiers to this list to be deleted on tearDown
    private final List<Identifier> cleanupList = new ArrayList<Identifier>();

    static
    {
        OPERATION_CLASS_TO_STRING_MAP.put(CheckOutResult.class, "check out");
        OPERATION_CLASS_TO_STRING_MAP.put(CreateResult.class, "create");
        OPERATION_CLASS_TO_STRING_MAP.put(ListMessagesResult.class, "list messages");
        OPERATION_CLASS_TO_STRING_MAP.put(ReadAccessRightsResult.class, "read access rights");
        OPERATION_CLASS_TO_STRING_MAP.put(ReadAuditsResult.class, "read audits");
        OPERATION_CLASS_TO_STRING_MAP.put(ReadResult.class, "read");
        OPERATION_CLASS_TO_STRING_MAP.put(ReadWorkflowInformationResult.class, "read workflow information");
        OPERATION_CLASS_TO_STRING_MAP.put(SearchResult.class, "search");
    }

    /**
     * Method to use in place of setUp() so that errors encountered on set up can be trapped.
     */
    @SuppressWarnings("unused")
    protected void onSetUp() throws Exception
    {
        // do nothing
    }

    @Override
    protected final void setUp() throws Exception
    {
        super.setUp();
        AssetOperationHandlerServiceLocator serviceLocator = new AssetOperationHandlerServiceLocator();
        client = serviceLocator.getAssetOperationService();

        auth = new Authentication();
        auth.setPassword("admin");
        auth.setUsername("admin");

        // if anything goes wrong during set up, attempt to delete any assets that were created
        try
        {
            onSetUp();
        }
        catch (Exception e)
        {
            clearCleanupList();
            fail("Setup was not successful, tests aborted.  Exception message is: " + e.getMessage());
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        clearCleanupList();
    }

    /**
     * Gets contents from a file
     * 
     * @param path
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected String getFileContents(Class clazz, String path) throws Exception
    {
        InputStream is = null;
        String contents = "";
        try
        {
            is = ClassUtil.relativeInputStream(clazz, path);
            contents = new StreamReader(is).readAsString();
        }
        catch (IOException e)
        {
            throw new Exception("File " + path + " contents could not be read: " + e.getMessage(), e);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception e)
                {
                    throw new Exception("Error closing InputStream after reading file: " + e.getMessage(), e);
                }
            }
        }

        return contents;
    }

    /**
     * You must be careful when using generate*() methods to create assets because anything created using
     * these
     * methods will be deleted in reverse order (to ensure that dependencies are taken care of "correctly").
     * 
     * Generate*() methods will persist items to the cms via web services and only create semi-generic assets.
     * If
     * you need to create a specialized asset, you can do so by creating your own stub objects manually.
     * 
     * These methods will create any required dependent objects. For example, generating a configuration set
     * will create a configuration set with 1 generated configuration containing a generated template (since
     * a default configuration and a template attached to that configuration are necessary to create a config
     * set).
     */

    /**
     * Creates a new asset factory and returns it after it has been read back from the CMS.
     */
    protected AssetFactory generateAssetFactory(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        AssetFactory assetFactory = generateAssetFactoryObject(name, siteId);
        asset.setAssetFactory(assetFactory);
        return createAsset(asset, EntityTypeString.assetfactory).getAssetFactory();
    }

    /**
     * Generates a valid AssetFactory but does not save it.
     * 
     * @param name
     * @param siteId
     * @return
     * @throws Exception
     */
    protected AssetFactory generateAssetFactoryObject(String name, String siteId) throws Exception
    {
        AssetFactory assetFactory = new AssetFactory();
        assetFactory.setParentContainerId(getRootAssetFactoryContainerId(siteId));
        assetFactory.setAssetType("page");
        assetFactory.setFolderPlacementPosition(new NonNegativeInteger("0"));
        assetFactory.setName(name);
        assetFactory.setWorkflowMode(AssetFactoryWorkflowMode.fromString("none"));
        assetFactory.setApplicableGroups("administration");
        assetFactory.setSiteId(siteId);
        return assetFactory;
    }

    /**
     * Creates a new site and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Site generateSite(String name) throws Exception
    {
        Asset asset = new Asset();
        asset.setSite(generateSiteObject(name));
        return createAsset(asset, EntityTypeString.site).getSite();
    }

    /**
     * Creates a new site object to be used for creation. It contains only the required fields.
     * 
     * @param name
     * @return
     */
    protected Site generateSiteObject(String name)
    {
        Site site = new Site();
        site.setUrl("siteurl");
        site.setName(name);
        site.setRecycleBinExpiration(RecycleBinExpiration.fromString("15"));
        return site;
    }

    /**
     * Creates a new folder and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Folder generateFolder(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setFolder(generateFolderObject(name, siteId));
        return createAsset(asset, EntityTypeString.folder).getFolder();
    }

    /**
     * Creates a new folder object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected Folder generateFolderObject(String name, String siteId) throws Exception
    {
        String parentId = getRootFolderId(siteId);

        Folder folder = new Folder();
        folder.setParentFolderId(parentId);
        folder.setName(name);
        folder.setSiteId(siteId);
        return folder;
    }

    /**
     * Creates a new publish set and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected PublishSet generatePublishSet(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setPublishSet(generatePublishSetObject(name, siteId));
        return createAsset(asset, EntityTypeString.publishset).getPublishSet();
    }

    /**
     * Creates a new publish set object to be used for creation. It contains only the required fileds
     * 
     * @param name
     * @return
     */
    protected PublishSet generatePublishSetObject(String name, String siteId) throws Exception
    {
        PublishSet publishSet = new PublishSet();
        publishSet.setParentContainerId(getRootPublishSetContainerId(siteId));
        publishSet.setName(name);
        return publishSet;
    }

    /**
     * Creates a new file system transport and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected FileSystemTransport generateFileSystemTransport(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setFileSystemTransport(generateFileSystemTransportObject(name, siteId));
        return createAsset(asset, EntityTypeString.transport).getFileSystemTransport();
    }

    /**
     * Creates a new file system transport object to be used for creation. It contains only the required
     * fields
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected FileSystemTransport generateFileSystemTransportObject(String name, String siteId) throws Exception
    {
        FileSystemTransport fileSystemTransport = new FileSystemTransport();
        fileSystemTransport.setName(name);
        fileSystemTransport.setDirectory("/fileSystemTransport");
        fileSystemTransport.setParentContainerId(getRootTransportContainerId(siteId));
        fileSystemTransport.setSiteId(siteId);
        return fileSystemTransport;
    }

    /**
     * Creates a new workflow definition and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected WorkflowDefinition generateWorkflowDefinition(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setWorkflowDefinition(generateWorkflowDefinitionObject(name, siteId));
        return createAsset(asset, EntityTypeString.workflowdefinition).getWorkflowDefinition();
    }

    /**
     * Creates a new workflow definition object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected WorkflowDefinition generateWorkflowDefinitionObject(String name, String siteId) throws Exception
    {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setName(name);
        workflowDefinition.setSiteId(siteId);
        workflowDefinition.setParentContainerId(getRootWorkflowContainerId(siteId));
        ReadResult result = client.read(auth, new Identifier("testgroup", null, EntityTypeString.group, null));
        if (!result.getSuccess().equals("true"))
            generateGroup("testgroup");
        workflowDefinition.setApplicableGroups("testgroup");
        workflowDefinition.setNamingBehavior(WorkflowNamingBehavior.value1);
        workflowDefinition.setCreate(true);
        workflowDefinition.setXml("<system-workflow-definition name=\"Delete\" initial-step=\"initialize\" >"
                + " <triggers> <trigger name=\"delete\" class=\"com.cms.workflow.function.Delete\" />"
                + " </triggers> <steps> <step type=\"system\" identifier=\"initialize\" label=\"Initialization\" >"
                + " <actions> <action identifier=\"initialize\" label=\"n-a\" type=\"auto\" move=\"forward\" />"
                + " </actions> </step> <step type=\"transition\" identifier=\"final-review\" label=\"Final Review\""
                + " restrict-to-type=\"role\" restrict-to-value=\"Publisher\" allow-user-group-change=\"false\""
                + " default-user=\"admin\" > <actions> <action identifier=\"approve\" label=\"Approve\""
                + " move=\"forward\" /> </actions> </step> <step type=\"system\" identifier=\"finalize-delete\""
                + " label=\"Deleting\" > <actions> <action identifier=\"finalize\" label=\"n-a\" type=\"auto\""
                + " move=\"forward\" > <trigger name=\"delete\" /> </action> </actions> </step>"
                + " <step type=\"system\" identifier=\"deleted\" label=\"Deleted\" /> </steps></system-workflow-definition>");
        return workflowDefinition;
    }

    protected void setBasicBlockProperties(Block block, String name, String siteId) throws Exception
    {
        block.setName(name);
        block.setSiteId(siteId);
        block.setParentFolderId(getRootFolderId(siteId));
    }

    /**
     * Creates a new feed block and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected FeedBlock generateFeedBlock(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setFeedBlock(generateFeedBlockObject(name, siteId));
        return createAsset(asset, EntityTypeString.block).getFeedBlock();
    }

    /**
     * Creates a new feed block object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected FeedBlock generateFeedBlockObject(String name, String siteId) throws Exception
    {
        FeedBlock feedBlock = new FeedBlock();
        setBasicBlockProperties(feedBlock, name, siteId);
        feedBlock.setFeedURL(new URI("http://www.yahoo.com"));
        return feedBlock;
    }

    /**
     * Creates a new text block and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected TextBlock generateTextBlock(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setTextBlock(generateTextBlockObject(name, siteId));
        return createAsset(asset, EntityTypeString.block).getTextBlock();
    }

    /**
     * Creates a new text block object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected TextBlock generateTextBlockObject(String name, String siteId) throws Exception
    {
        TextBlock textBlock = new TextBlock();
        setBasicBlockProperties(textBlock, name, siteId);
        textBlock.setText("Some text");
        return textBlock;
    }

    /**
     * Creates a new index block and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected IndexBlock generateIndexBlock(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setIndexBlock(generateIndexBlockObject(name, siteId));
        return createAsset(asset, EntityTypeString.block).getIndexBlock();
    }

    /**
     * Creates a new index block object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @param indexedFolderId
     * @return
     */
    protected IndexBlock generateIndexBlockObject(String name, String siteId) throws Exception
    {
        IndexBlock indexBlock = new IndexBlock();
        setBasicBlockProperties(indexBlock, name, siteId);
        indexBlock.setIndexedFolderPath("");
        return indexBlock;
    }

    /**
     * Creates a new xhtml block and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected XhtmlDataDefinitionBlock generateStructuredDataBlock(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setXhtmlDataDefinitionBlock(generateDataDefinitionBlockObject(name, siteId));
        return createAsset(asset, EntityTypeString.block).getXhtmlDataDefinitionBlock();
    }

    /**
     * Creates a new xhtml block object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected XhtmlDataDefinitionBlock generateDataDefinitionBlockObject(String name, String siteId) throws Exception
    {
        XhtmlDataDefinitionBlock xhtmlBlock = new XhtmlDataDefinitionBlock();
        setBasicBlockProperties(xhtmlBlock, name, siteId);
        xhtmlBlock.setXhtml("<root><element>text</element></root>");
        return xhtmlBlock;
    }

    /**
     * Creates a new xml block and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected XmlBlock generateXmlBlock(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setXmlBlock(generateXmlBlockObject(name, siteId));
        return createAsset(asset, EntityTypeString.block).getXmlBlock();
    }

    /**
     * Creates a new xml block object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected XmlBlock generateXmlBlockObject(String name, String siteId) throws Exception
    {
        XmlBlock block = new XmlBlock();
        setBasicBlockProperties(block, name, siteId);
        block.setXml("<xml></xml>");
        return block;
    }

    /**
     * Creates a new file and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected File generateFile(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setFile(generateFileObject(name, siteId));
        asset.getFile().setSiteId(siteId);
        return createAsset(asset, EntityTypeString.file).getFile();
    }

    /**
     * Creates a new file object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     */
    protected File generateFileObject(String name, String siteId) throws Exception
    {
        File file = new File();
        file.setName(name);
        file.setParentFolderId(getRootFolderId(siteId));
        file.setText("Some text");
        file.setSiteId(siteId);
        return file;
    }

    /**
     * Creates a new page and returns it after reading it back from the CMS
     * It also creates a page configuration set for it
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Page generatePage(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        Page page = generatePageObject(name, generatePageConfigurationSet(name + "_config_set", siteId).getId(), siteId);
        asset.setPage(page);
        return createAsset(asset, EntityTypeString.page).getPage();
    }

    /**
     * Creates a new page object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Page generatePageObject(String name, String pageConfigurationSetId, String siteId) throws Exception
    {
        Page page = new Page();
        page.setName(name);
        page.setConfigurationSetId(pageConfigurationSetId);
        page.setParentFolderId(getRootFolderId(siteId));
        page.setXhtml("some content");
        page.setSiteId(siteId);
        return page;
    }

    /**
     * Creates a new symlink and returns it after it has been read back from CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Symlink generateSymlink(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setSymlink(generateSymlinkObject(name, siteId));
        return createAsset(asset, EntityTypeString.symlink).getSymlink();
    }

    /**
     * Creates a new symlink object to be used for creation. It contains only the required fields
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Symlink generateSymlinkObject(String name, String siteId) throws Exception
    {
        Symlink symlink = new Symlink();
        symlink.setParentFolderId(getRootFolderId(siteId));
        symlink.setName(name);
        symlink.setLinkURL(new URI("http://www.yahoo.com"));
        symlink.setSiteId(siteId);
        return symlink;
    }

    /**
     * Creates a new page configuration set and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     */
    protected PageConfigurationSet generatePageConfigurationSet(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();

        PageConfiguration pc = generatePageConfigurationObject("html", null, siteId);
        pc.setDefaultConfiguration(true);

        PageConfigurationSet configSet = new PageConfigurationSet();
        configSet.setName(name);

        configSet.setPageConfigurations(new PageConfiguration[]
        {
            pc
        });

        configSet.setParentContainerId(getRootPageConfigurationContainerId(siteId));
        configSet.setSiteId(siteId);

        asset.setPageConfigurationSet(configSet);

        return createAsset(asset, EntityTypeString.pageconfigurationset).getPageConfigurationSet();
    }

    /**
     * Creates a new template and returns it after it has been read back from the CMS
     * 
     * @return
     */
    protected Template generateTemplate(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();

        Template template = new Template();
        template.setName(name);
        template.setParentFolderId(getRootFolderId(siteId));
        template.setXml("<system-region name=\"DEFAULT\"></system-region>");
        // only generate target if outside a site
        if (StringUtil.isEmptyTrimmed(siteId))
            template.setTargetId(generateTarget("ws_target").getId());
        template.setPageRegions(new PageRegion[] {});
        template.setSiteId(siteId);
        asset.setTemplate(template);

        return createAsset(asset, EntityTypeString.template).getTemplate();
    }

    /**
     * Creates a new target and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Target generateTarget(String name) throws Exception
    {
        Asset asset = new Asset();

        Target target = new Target();
        target.setName(name);
        target.setBaseFolderId(RootContainerIds.FOLDER_ROOT_ID);
        target.setOutputExtension(".xml");
        target.setParentTargetId(RootContainerIds.TARGET_ROOT_ID);
        target.setSerializationType(SerializationType.XML);
        asset.setTarget(target);

        return createAsset(asset, EntityTypeString.target).getTarget();
    }

    /**
     * Creates a new stylesheet and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected XsltFormat generateXsltFormat(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        XsltFormat xsltFormat = generateXsltFormatObject(name, siteId);
        asset.setXsltFormat(xsltFormat);

        return createAsset(asset, EntityTypeString.format_XSLT).getXsltFormat();
    }

    /**
     * Generates a XsltFormat but does not persist it to Cascade.
     * 
     * @param name
     * @param siteId
     * @return
     * @throws Exception
     */
    protected XsltFormat generateXsltFormatObject(String name, String siteId) throws Exception
    {
        XsltFormat xsltFormat = new XsltFormat();
        xsltFormat.setName(name);
        xsltFormat.setParentFolderId(getRootFolderId(siteId));
        xsltFormat
                .setXml("<?xml version=\"1.0\"?><xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output indent=\"yes\" method=\"xml\"/><xsl:template match=\"@*|node()\" priority=\"-1\"></xsl:template></xsl:stylesheet>");
        xsltFormat.setSiteId(siteId);
        return xsltFormat;
    }

    /**
     * Creates a script (velocity) format and returns it after reading it back from the CMS.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected ScriptFormat generateScriptFormat(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        ScriptFormat scriptFormat = new ScriptFormat();
        scriptFormat.setName(name);
        scriptFormat.setParentFolderId(getRootFolderId(siteId));
        scriptFormat.setScript("some script");
        scriptFormat.setSiteId(siteId);
        asset.setScriptFormat(scriptFormat);

        return createAsset(asset, EntityTypeString.format_SCRIPT).getScriptFormat();
    }

    /**
     * Creates a new metadata set and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected MetadataSet generateMetadataSet(String name, String siteId) throws Exception
    {
        String parentId = getRootMetadataSetContainerId(siteId);

        Asset asset = new Asset();

        MetadataSet metadataSet = new MetadataSet();
        metadataSet.setName(name);
        metadataSet.setParentContainerId(parentId);
        metadataSet.setSiteId(siteId);

        asset.setMetadataSet(metadataSet);
        return createAsset(asset, EntityTypeString.metadataset).getMetadataSet();
    }

    /**
     * Creates a metadata set with the following dynamic metadata fields:
     * 
     * field id | field type
     * ---------+-----------
     * field1 | text
     * field2 | checkbox
     * field3 | dropdown
     * field4 | radio
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected MetadataSet generateMetadataSetWithDynamicFields(String name) throws Exception
    {
        Asset asset = new Asset();

        MetadataSet metadataSet = new MetadataSet();
        metadataSet.setName(name);
        metadataSet.setParentContainerId(RootContainerIds.METADATASET_CONTAINER_ROOT_ID);
        DynamicMetadataFieldDefinition[] dynamicMetadataFieldDefinitions = new DynamicMetadataFieldDefinition[4];
        dynamicMetadataFieldDefinitions[0] = new DynamicMetadataFieldDefinition();
        dynamicMetadataFieldDefinitions[0].setName("field1");
        dynamicMetadataFieldDefinitions[0].setFieldType(DynamicMetadataFieldType.text);
        dynamicMetadataFieldDefinitions[0].setRequired(false);
        dynamicMetadataFieldDefinitions[0].setLabel("Field 1");
        dynamicMetadataFieldDefinitions[0].setConfigurationXML("");
        dynamicMetadataFieldDefinitions[1] = new DynamicMetadataFieldDefinition();
        dynamicMetadataFieldDefinitions[1].setName("field2");
        dynamicMetadataFieldDefinitions[1].setFieldType(DynamicMetadataFieldType.checkbox);
        dynamicMetadataFieldDefinitions[1].setRequired(false);
        dynamicMetadataFieldDefinitions[1]
                .setConfigurationXML("<checkbox><item>Item 1</item><item>Item 2</item><item>Item 3</item><item>Item 4</item><item>Item 5</item><item>Item 6</item></checkbox>");
        dynamicMetadataFieldDefinitions[1].setLabel("Field 2");
        dynamicMetadataFieldDefinitions[2] = new DynamicMetadataFieldDefinition();
        dynamicMetadataFieldDefinitions[2].setName("field3");
        dynamicMetadataFieldDefinitions[2].setFieldType(DynamicMetadataFieldType.dropdown);
        dynamicMetadataFieldDefinitions[2].setRequired(false);
        dynamicMetadataFieldDefinitions[2]
                .setConfigurationXML("<dropdown><item default=\"true\">Item 1</item><item>Item 2</item><item>Item 3</item><item>Item 4</item><item>Item 5</item><item>Item 6</item></dropdown>");
        dynamicMetadataFieldDefinitions[2].setLabel("Field 3");
        dynamicMetadataFieldDefinitions[3] = new DynamicMetadataFieldDefinition();
        dynamicMetadataFieldDefinitions[3].setName("field4");
        dynamicMetadataFieldDefinitions[3].setFieldType(DynamicMetadataFieldType.radio);
        dynamicMetadataFieldDefinitions[3].setRequired(false);
        dynamicMetadataFieldDefinitions[3]
                .setConfigurationXML("<radio><item default=\"true\">Item 1</item><item>Item 2</item><item>Item 3</item><item>Item 4</item><item>Item 5</item><item>Item 6</item></radio>");
        dynamicMetadataFieldDefinitions[3].setLabel("Field 4");

        metadataSet.setDynamicMetadataFieldDefinitions(dynamicMetadataFieldDefinitions);
        asset.setMetadataSet(metadataSet);
        return createAsset(asset, EntityTypeString.metadataset).getMetadataSet();
    }

    /**
     * Creates a new content type and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected ContentType generateContentType(String name, boolean useStructuredData, Site site) throws Exception
    {
        String siteId = site == null ? null : site.getId();
        String parentId = getRootContentTypeContainerId(siteId);

        Asset asset = new Asset();

        ContentType contentType = new ContentType();
        contentType.setPageConfigurationSetId(generatePageConfigurationSet("ws_content_type_config_set", siteId).getId());
        contentType.setMetadataSetId(generateMetadataSet("ws_content_type_metadata_set", siteId).getId());
        contentType.setName(name);
        contentType.setParentContainerId(parentId);
        contentType.setSiteId(siteId);

        if (useStructuredData)
        {
            contentType.setDataDefinitionId(generateDataDefinition("ws_content_type_sdd", siteId).getId());
        }

        asset.setContentType(contentType);
        return createAsset(asset, EntityTypeString.contenttype).getContentType();
    }

    /**
     * Creates a new content type container and returns it after reading it back from the CMS.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected ContentTypeContainer generateContentTypeContainer(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();

        ContentTypeContainer container = new ContentTypeContainer();
        container.setParentContainerId(getRootContentTypeContainerId(siteId));
        container.setName(name);
        container.setSiteId(siteId);

        asset.setContentTypeContainer(container);
        return createAsset(asset, EntityTypeString.contenttypecontainer).getContentTypeContainer();
    }

    /**
     * Creates a new structured data definition and returns it after reading it back from the CMS
     * 
     * @param name
     * @param siteId
     * @return
     * @throws Exception
     */
    protected DataDefinition generateDataDefinition(String name, String siteId) throws Exception
    {
        return generateDataDefinition(name, siteId, "<system-data-structure><text identifier=\"test\"/></system-data-structure>");
    }

    /**
     * Creates a new data definition with provided definitionXml and returns it after reading it back from CMS
     * 
     * @param name
     * @param siteId
     * @param definitionXml
     * @return
     * @throws Exception
     */
    protected DataDefinition generateDataDefinition(String name, String siteId, String definitionXml) throws Exception
    {
        Asset asset = new Asset();

        DataDefinition sdd = new DataDefinition();
        sdd.setName(name);
        sdd.setParentContainerId(getRootDataDefinitionContainerId(siteId));
        sdd.setXml(definitionXml);
        sdd.setSiteId(siteId);

        asset.setDataDefinition(sdd);
        return createAsset(asset, EntityTypeString.datadefinition).getDataDefinition();
    }

    /**
     * Creates a new group and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Group generateGroup(String name) throws Exception
    {
        Asset asset = new Asset();

        Group group = new Group();
        group.setGroupName(name);
        group.setRole("Contributor");

        asset.setGroup(group);
        return createAsset(asset, EntityTypeString.group).getGroup();
    }

    /**
     * Generates a page configuration and returns it. This method does not return an id because stand alone
     * page
     * configurations cannot be created through web services. In the case that the template is null, a new
     * template
     * will be created in the cms to go with the page configuration being generated.
     * 
     * @param name
     * @param template optional template to be set
     * @return
     */
    protected PageConfiguration generatePageConfigurationObject(String name, Template template, String siteId) throws Exception
    {
        String templateId;
        if (template != null)
            templateId = template.getId();
        else
            templateId = generateTemplate("ws_pc_template", siteId).getId();

        PageConfiguration pc = new PageConfiguration();
        pc.setName(name);
        pc.setTemplateId(templateId);
        if (siteId != null)
        {
            pc.setOutputExtension(".html");
            pc.setIncludeXMLDeclaration(false);
            pc.setSerializationType(SerializationType.HTML);
            pc.setPublishable(false);
        }

        return pc;
    }

    /**
     * Generates metadata with some specific values
     * 
     * @return
     */
    protected Metadata generateMetadataObject()
    {
        Metadata metadata = new Metadata();
        metadata.setAuthor("author");
        metadata.setDisplayName("display name");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2008, 02, 02, 10, 50, 40);
        calendar.set(Calendar.ZONE_OFFSET, 0);
        metadata.setEndDate(calendar);
        metadata.setKeywords("keywords");
        metadata.setMetaDescription("description");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2007, 01, 01, 9, 40, 30);
        calendar2.set(Calendar.ZONE_OFFSET, 0);
        metadata.setReviewDate(calendar2);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2007, 02, 02, 8, 30, 20);
        calendar3.set(Calendar.ZONE_OFFSET, 0);
        metadata.setStartDate(calendar3);
        metadata.setSummary("summary");
        metadata.setTeaser("teaser");
        metadata.setTitle("title");
        return metadata;
    }

    /**
     * Generates sample dynamic metadata fields that match the dynamic metadata set created through
     * generateMetadataSetWithDynamicFields() method.
     * 
     * @return
     */
    protected DynamicMetadataField[] generateDynamicMetadataFieldsObject()
    {
        DynamicMetadataField[] meta = new DynamicMetadataField[4];

        meta[0] = new DynamicMetadataField();
        meta[0].setName("field1");
        FieldValue[] fieldValues1 = new FieldValue[1];
        fieldValues1[0] = new FieldValue();
        fieldValues1[0].setValue("value for text");
        meta[0].setFieldValues(fieldValues1);

        meta[1] = new DynamicMetadataField();
        meta[1].setName("field2");
        FieldValue[] fieldValues2 = new FieldValue[2];
        fieldValues2[0] = new FieldValue();
        fieldValues2[0].setValue("Item 1");
        fieldValues2[1] = new FieldValue();
        fieldValues2[1].setValue("Item 2");
        meta[1].setFieldValues(fieldValues2);

        meta[2] = new DynamicMetadataField();
        meta[2].setName("field3");
        FieldValue[] fieldValues3 = new FieldValue[1];
        fieldValues3[0] = new FieldValue();
        fieldValues3[0].setValue("Item 3");
        meta[2].setFieldValues(fieldValues3);

        meta[3] = new DynamicMetadataField();
        meta[3].setName("field4");
        FieldValue[] fieldValues4 = new FieldValue[1];
        fieldValues4[0] = new FieldValue();
        fieldValues4[0].setValue("Item 4");
        meta[3].setFieldValues(fieldValues4);

        return meta;
    }

    /**
     * Generates a page configuration set container and returns it after reading it back from the CMS.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected PageConfigurationSetContainer generatePageConfigurationSetContainer(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();

        PageConfigurationSetContainer pcsc = new PageConfigurationSetContainer();
        pcsc.setName(name);
        pcsc.setParentContainerId(getRootPageConfigurationContainerId(siteId));
        pcsc.setSiteId(siteId);

        asset.setPageConfigurationSetContainer(pcsc);
        return createAsset(asset, EntityTypeString.pageconfigurationsetcontainer).getPageConfigurationSetContainer();
    }

    /**
     * Generates a workflow definition container and returns it after reading it back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected WorkflowDefinitionContainer generateWorkflowDefinitionContainer(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();

        WorkflowDefinitionContainer wfdc = new WorkflowDefinitionContainer();
        wfdc.setName(name);
        wfdc.setParentContainerId(getRootWorkflowContainerId(siteId));
        wfdc.setSiteId(siteId);

        asset.setWorkflowDefinitionContainer(wfdc);
        return createAsset(asset, EntityTypeString.workflowdefinitioncontainer).getWorkflowDefinitionContainer();
    }

    /**
     * Creates a new destination and returns it after it has been read back from the CMS
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected Destination generateDestination(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setDestination(generateDestinationObject(name, siteId));
        return createAsset(asset, EntityTypeString.destination).getDestination();
    }

    protected Destination generateDestinationObject(String name, String siteId) throws Exception
    {
        Destination dest = new Destination();
        dest.setName(name);
        dest.setParentContainerId(getRootDestinationContainerId(siteId));
        dest.setSiteId(siteId);
        FileSystemTransport t = generateFileSystemTransport("transporty", siteId);
        dest.setTransportId(t.getId());
        return dest;
    }

    /**
     * Creates a site destination container and then reads it back from the cMS and returns it.
     * 
     * @param name
     * @param siteId
     * @return
     * @throws Exception
     */
    protected SiteDestinationContainer generateSiteDestinationContainer(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setSiteDestinationContainer(generateSiteDestinationContainerObject(name, siteId));
        return createAsset(asset, EntityTypeString.sitedestinationcontainer).getSiteDestinationContainer();
    }

    protected SiteDestinationContainer generateSiteDestinationContainerObject(String name, String siteId) throws Exception
    {
        SiteDestinationContainer container = new SiteDestinationContainer();
        container.setName(name);
        container.setSiteId(siteId);
        container.setParentContainerId(getRootDestinationContainerId(siteId));
        return container;
    }

    /**
     * Creates a Database Transport and then reads it back from the CMS and returns it.
     * 
     * @param name
     * @param siteId
     * @return
     * @throws Exception
     */
    protected DatabaseTransport generateDatabaseTransport(String name, String siteId) throws Exception
    {
        Asset asset = new Asset();
        asset.setDatabaseTransport(generateDatabaseTransportObject(name, siteId));
        return createAsset(asset, EntityTypeString.transport_db).getDatabaseTransport();

    }

    protected DatabaseTransport generateDatabaseTransportObject(String name, String siteId) throws Exception
    {
        DatabaseTransport transport = new DatabaseTransport();
        transport.setName(name);
        transport.setDatabaseName("blah");
        transport.setParentContainerId(getRootTransportContainerId(siteId));
        transport.setPassword("blah");
        transport.setUsername("blah");
        transport.setServerName("blah");
        transport.setServerPort(new PositiveInteger("90"));
        transport.setTransportSiteId(new NonNegativeInteger("1"));
        transport.setSiteId(siteId);

        return transport;
    }

    protected User generateAdminUser(String name) throws Exception
    {
        User user = new User();
        user.setAuthType(UserAuthTypes.normal);
        user.setUsername(name);
        user.setPassword(name);
        user.setEnabled(true);
        user.setGroups(generateGroup(name + "_group").getGroupName());
        user.setRole("Administrator"); // guaranteed to be in the system

        Asset asset = new Asset();
        asset.setUser(user);
        return createAsset(asset, EntityTypeString.user).getUser();
    }

    /**
     * Creates the given asset, reads it back and returns it
     * 
     * @param asset
     * @param type
     * @return
     * @throws Exception
     */
    protected Asset createAsset(Asset asset, EntityTypeString type) throws Exception
    {
        String assetId = create(asset, type);
        ReadResult result = client.read(auth, new Identifier(assetId, null, type, null));
        assertOperationSuccess(result, type);
        return result.getAsset();
    }

    /**
     * Creates an asset and adds its identifier to the list of items to be cleaned up if creation was
     * successful.
     * 
     * @param asset
     * @return
     */
    protected String create(Asset asset, EntityTypeString type) throws Exception
    {
        CreateResult result = client.create(auth, asset);

        if (success(result))
        {
            Object actualAsset = findAsset(asset);
            String id = result.getCreatedAssetId();

            if (actualAsset != null || asset.getGroup() != null || asset.getUser() != null)
                cleanupList.add(new Identifier(id, null, type, null));

            return result.getCreatedAssetId();
        }

        error("Asset of type '" + type + "' was not created successfully: " + result.getMessage() + "\r\n");
        return null;
    }

    /**
     * Deletes an entity from the cms by first removing it from the cleanup list if it exists in the cleanup
     * list.
     * 
     * @param id
     * @param type
     * @throws Exception
     */
    protected void delete(String id, EntityTypeString type) throws Exception
    {
        Identifier identifier = new Identifier(id, null, type, null);
        if (!cleanupList.remove(identifier))
        {
            info("Entity with id '" + id + "' and type '" + type + "' did not exist in cleanup list");
        }

        client.delete(auth, identifier);
    }

    /**
     * Determines which field is set on the {@link Asset} and returns it.
     * 
     * @param asset
     * @return
     */
    //TODO: move this to the util class?
    private Object findAsset(Asset asset)
    {
        if (asset.getAssetFactory() != null)
            return asset.getAssetFactory();
        else if (asset.getAssetFactoryContainer() != null)
            return asset.getAssetFactoryContainer();
        else if (asset.getContentType() != null)
            return asset.getContentType();
        else if (asset.getContentTypeContainer() != null)
            return asset.getContentTypeContainer();
        else if (asset.getDestination() != null)
            return asset.getDestination();
        else if (asset.getFeedBlock() != null)
            return asset.getFeedBlock();
        else if (asset.getFile() != null)
            return asset.getFile();
        else if (asset.getFileSystemTransport() != null)
            return asset.getFileSystemTransport();
        else if (asset.getFolder() != null)
            return asset.getFolder();
        else if (asset.getFtpTransport() != null)
            return asset.getFtpTransport();
        else if (asset.getDatabaseTransport() != null)
            return asset.getDatabaseTransport();
        else if (asset.getIndexBlock() != null)
            return asset.getIndexBlock();
        else if (asset.getMetadataSet() != null)
            return asset.getMetadataSet();
        else if (asset.getMetadataSetContainer() != null)
            return asset.getMetadataSetContainer();
        else if (asset.getPage() != null)
            return asset.getPage();
        else if (asset.getPageConfigurationSet() != null)
            return asset.getPageConfigurationSet();
        else if (asset.getPageConfigurationSetContainer() != null)
            return asset.getPageConfigurationSetContainer();
        else if (asset.getPublishSet() != null)
            return asset.getPublishSet();
        else if (asset.getPublishSetContainer() != null)
            return asset.getPublishSetContainer();
        else if (asset.getReference() != null)
            return asset.getReference();
        else if (asset.getDataDefinition() != null)
            return asset.getDataDefinition();
        else if (asset.getDataDefinitionContainer() != null)
            return asset.getDataDefinitionContainer();
        else if (asset.getXsltFormat() != null)
            return asset.getXsltFormat();
        else if (asset.getScriptFormat() != null)
            return asset.getScriptFormat();
        else if (asset.getSite() != null)
            return asset.getSite();
        else if (asset.getSymlink() != null)
            return asset.getSymlink();
        else if (asset.getTarget() != null)
            return asset.getTarget();
        else if (asset.getSiteDestinationContainer() != null)
            return asset.getSiteDestinationContainer();
        else if (asset.getTemplate() != null)
            return asset.getTemplate();
        else if (asset.getTextBlock() != null)
            return asset.getTextBlock();
        else if (asset.getTransportContainer() != null)
            return asset.getTransportContainer();
        else if (asset.getWorkflowDefinition() != null)
            return asset.getWorkflowDefinition();
        else if (asset.getWorkflowDefinitionContainer() != null)
            return asset.getWorkflowDefinitionContainer();
        else if (asset.getXhtmlDataDefinitionBlock() != null)
            return asset.getXhtmlDataDefinitionBlock();
        else if (asset.getXmlBlock() != null)
            return asset.getXmlBlock();
        else if (asset.getRole() != null)
            return asset.getRole();
        else if (asset.getConnectorContainer() != null)
            return asset.getConnectorContainer();
        else if (asset.getTwitterConnector() != null)
            return asset.getTwitterConnector();
        else if (asset.getFacebookConnector() != null)
            return asset.getFacebookConnector();
        else if (asset.getWordPressConnector() != null)
            return asset.getWordPressConnector();
        else if (asset.getGoogleAnalyticsConnector() != null)
            return asset.getGoogleAnalyticsConnector();
        else if (asset.getUser() != null)
            return asset.getUser();
        else
            return null;
    }

    /**
     * Determines if an operation was successful or not;
     * 
     * @param result
     * @return
     */
    protected boolean success(OperationResult result)
    {
        if (result != null && SUCCESS.equals(result.getSuccess()))
        {
            return true;
        }
        return false;
    }

    /**
     * Assert that the result of a web services operation is successful.
     * This should only be used when the test can safely be "failed"
     * without leaving assets stranded in cms. This method should not be
     * used in the setUp() or tearDown)( methods of a test case to ensure that
     * all entities created for the test are deleted correctly after the
     * tests have executed.
     * 
     * @param result
     * @param type can be null
     */
    protected void assertOperationSuccess(OperationResult result, EntityTypeString type)
    {
        String typeStr = "";
        String operation = OPERATION_CLASS_TO_STRING_MAP.get(result.getClass());

        if (type != null)
        {
            typeStr = type.getValue();
        }

        if (operation == null)
        {
            operation = result.toString();
        }

        if (!success(result))
        {
            fail(typeStr + " " + operation + " operation failed: " + result.getMessage());
        }
    }

    /**
     * Assert that the result of a web services operation fails.
     * 
     * @param result
     * @param type
     */
    protected void assertOperationFailure(OperationResult result, EntityTypeString type)
    {
        String typeStr = "";
        String operation = OPERATION_CLASS_TO_STRING_MAP.get(result.getClass());

        if (type != null)
        {
            typeStr = type.getValue();
        }

        if (operation == null)
        {
            operation = result.toString();
        }

        if (success(result))
        {
            fail(typeStr + " " + operation + " operation succeeded but should have failed: " + result.getMessage());
        }
    }

    /**
     * Issues web services deletes for any assets that have been created using generate*() methods.
     */
    private void clearCleanupList()
    {
        try
        {
            Collections.reverse(cleanupList);
            for (Identifier id : cleanupList)
            {
                OperationResult result = client.delete(auth, id);
                if (!SUCCESS.equals(result.getSuccess()))
                    error("Error while cleaning up entity (id = " + id.getId() + " , type = " + id.getType() + "): " + result.getMessage() + "\r\n");
            }
        }
        catch (Exception e)
        {
            error("Could not clear the cleanup list.  Extraneous entities may remain in the database.");
        }
    }

    /**
     * Makes sure that a semicolon divided list of elements matches the List of Strings provided. Assumes that
     * the values never repeat.
     * 
     * @param correctValues
     * @param values
     */
    protected static void assertContains(List<String> correctValues, String values)
    {
        List<String> testValues;
        if (StringUtil.isEmpty(values))
        {
            testValues = Collections.emptyList();
        }
        else
        {
            testValues = Arrays.asList(values.split(";"));
        }

        assertEquals(correctValues.size(), testValues.size());
        assertEquals(0, CollectionUtils.disjunction(correctValues, testValues).size());
    }

    /**
     * Returns the root content type container id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootContentTypeContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootContentTypeContainerId();
    }

    /**
     * Returns the root content type container id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootConnectorContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return null;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootConnectorContainerId();
    }

    /**
     * Returns the root asset factory container id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootAssetFactoryContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.ASSET_FACTORY_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootAssetFactoryContainerId();
    }

    /**
     * Returns the root publish set container id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootPublishSetContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.PUBLISH_SET_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootPublishSetContainerId();
    }

    /**
     * Returns the root destination container id
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootDestinationContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.TARGET_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootSiteDestinationContainerId();
    }

    /**
     * Returns the root metadata set container id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootMetadataSetContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.METADATASET_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootMetadataSetContainerId();
    }

    /**
     * Returns the root folder id of given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootFolderId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.FOLDER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootFolderId();
    }

    /**
     * Returns the root container of Page Configurations of a given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootPageConfigurationContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootPageConfigurationSetContainerId();
    }

    /**
     * Returns the root container of Workflows of a given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootWorkflowContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootWorkflowDefinitionContainerId();
    }

    /**
     * Returns the root container of Transports of a given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootTransportContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootTransportContainerId();
    }

    /**
     * Returns the root container of Data Definitions of a given site
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    protected String getRootDataDefinitionContainerId(String siteId) throws Exception
    {
        if (siteId == null)
            return RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID;

        ReadResult result = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        return result.getAsset().getSite().getRootDataDefinitionContainerId();
    }

    /**
     * Sets the appropriate asset type to the Asset object
     * 
     * @param asset
     * @param dublinAwareAsset
     */
    protected void setAppropriateAsset(Asset asset, DublinAwareAsset dublinAwareAsset)
    {
        if (dublinAwareAsset instanceof Folder)
            asset.setFolder((Folder) dublinAwareAsset);
        else if (dublinAwareAsset instanceof FeedBlock)
            asset.setFeedBlock((FeedBlock) dublinAwareAsset);
        else if (dublinAwareAsset instanceof IndexBlock)
            asset.setIndexBlock((IndexBlock) dublinAwareAsset);
        else if (dublinAwareAsset instanceof TextBlock)
            asset.setTextBlock((TextBlock) dublinAwareAsset);
        else if (dublinAwareAsset instanceof XhtmlDataDefinitionBlock)
            asset.setXhtmlDataDefinitionBlock((XhtmlDataDefinitionBlock) dublinAwareAsset);
        else if (dublinAwareAsset instanceof XmlBlock)
            asset.setXmlBlock((XmlBlock) dublinAwareAsset);
        else if (dublinAwareAsset instanceof File)
            asset.setFile((File) dublinAwareAsset);
        else if (dublinAwareAsset instanceof Page)
            asset.setPage((Page) dublinAwareAsset);
        else if (dublinAwareAsset instanceof Symlink)
            asset.setSymlink((Symlink) dublinAwareAsset);
    }

    /**
     * Gets the appropriate type of asset from the Asset object
     * 
     * @param asset
     * @return
     */
    protected DublinAwareAsset getAppropriateAsset(Asset asset)
    {
        if (asset.getFolder() != null)
            return asset.getFolder();
        if (asset.getFeedBlock() != null)
            return asset.getFeedBlock();
        if (asset.getIndexBlock() != null)
            return asset.getIndexBlock();
        if (asset.getTextBlock() != null)
            return asset.getTextBlock();
        if (asset.getXhtmlDataDefinitionBlock() != null)
            return asset.getXhtmlDataDefinitionBlock();
        if (asset.getXmlBlock() != null)
            return asset.getXmlBlock();
        if (asset.getFile() != null)
            return asset.getFile();
        if (asset.getPage() != null)
            return asset.getPage();
        if (asset.getSymlink() != null)
            return asset.getSymlink();
        return null;
    }
}
