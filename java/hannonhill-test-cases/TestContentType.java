/*
 * Created on Jun 17, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypePageConfiguration;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypePageConfigurationPublishMode;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Tests web services operations for content types.
 * 
 * @author Mike Strauch
 * @since 5.5
 */
public class TestContentType extends CascadeWebServicesTestCase
{
    private ContentType contentType;
    private String contentTypeId;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        contentType = new ContentType();
        contentType.setName("ws_content_type");
        contentType.setPageConfigurationSetId(generatePageConfigurationSet("ws_content_type", null).getId());
        contentType.setMetadataSetId(generateMetadataSet("ws_content_type", null).getId());
        contentType.setDataDefinitionId(generateDataDefinition("ws_content_type", null).getId());
        contentType.setParentContainerId(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setContentType(contentType);

        contentTypeId = create(asset, EntityTypeString.contenttype);
    }

    /**
     * Tests reading of a content type via web services
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(contentTypeId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        ContentType fetchedCt = result.getAsset().getContentType();
        assertNotNull(fetchedCt);

        // In global area contentTypePageConfigurations needs to be null
        assertNull(fetchedCt.getContentTypePageConfigurations());
    }

    /**
     * Tests creating a content type via web services
     * 
     * @throws Exception
     */
    public void testCreate() throws Exception
    {
        ContentType contentType = new ContentType();
        contentType.setName("ws_content_type_create");
        contentType.setPageConfigurationSetId(generatePageConfigurationSet("ws_content_type_create", null).getId());
        contentType.setMetadataSetId(generateMetadataSet("ws_content_type_create", null).getId());
        contentType.setDataDefinitionId(generateDataDefinition("ws_content_type_create", null).getId());
        contentType.setParentContainerId(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID);

        Asset asset = new Asset();
        asset.setContentType(contentType);

        String ctId = create(asset, EntityTypeString.contenttype);
        ReadResult result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        ContentType fetchedCt = result.getAsset().getContentType();
        assertNotNull(fetchedCt);
        assertEquals(fetchedCt.getPageConfigurationSetId(), contentType.getPageConfigurationSetId());
        assertEquals(fetchedCt.getMetadataSetId(), contentType.getMetadataSetId());
        assertEquals(fetchedCt.getDataDefinitionId(), contentType.getDataDefinitionId());
    }

    /**
     * Tests reading, creating and editing of content types and makes sure their contentTypePageConfigurations
     * are always stored/returned correctly.
     * 
     * @throws Exception
     */
    public void testContentTypePageConfiguration() throws Exception
    {
        Site site = generateSite("site");

        ContentType contentType = new ContentType();
        contentType.setName("ws_content_type_create");
        contentType.setPageConfigurationSetId(generatePageConfigurationSet("ws_content_type_create", site.getId()).getId());
        contentType.setMetadataSetId(generateMetadataSet("ws_content_type_create", site.getId()).getId());
        contentType.setDataDefinitionId(generateDataDefinition("ws_content_type_create", site.getId()).getId());
        contentType.setParentContainerId(site.getRootContentTypeContainerId());
        contentType.setSiteId(site.getId());

        ContentTypePageConfiguration ctpc = new ContentTypePageConfiguration();
        ctpc.setPageConfigurationName("html");
        ctpc.setPublishMode(ContentTypePageConfigurationPublishMode.fromString("do-not-publish"));

        contentType.setContentTypePageConfigurations(new ContentTypePageConfiguration[]
        {
            ctpc
        });

        Asset asset = new Asset();
        asset.setContentType(contentType);

        String ctId = create(asset, EntityTypeString.contenttype);
        ReadResult result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        ContentType fetchedCt = result.getAsset().getContentType();
        assertNotNull(fetchedCt);
        assertEquals(fetchedCt.getPageConfigurationSetId(), contentType.getPageConfigurationSetId());
        assertEquals(fetchedCt.getMetadataSetId(), contentType.getMetadataSetId());
        assertEquals(fetchedCt.getDataDefinitionId(), contentType.getDataDefinitionId());

        ContentTypePageConfiguration[] contentTypePageConfigurations = fetchedCt.getContentTypePageConfigurations();
        assertNotNull(contentTypePageConfigurations);
        assertEquals(1, contentTypePageConfigurations.length);

        assertEquals("html", contentTypePageConfigurations[0].getPageConfigurationName());
        assertEquals(ContentTypePageConfigurationPublishMode.fromString("do-not-publish"), contentTypePageConfigurations[0].getPublishMode());
        assertNull(contentTypePageConfigurations[0].getDestinations());

        // Try now with selected-destinations
        contentTypePageConfigurations[0].setPublishMode(ContentTypePageConfigurationPublishMode.fromString("selected-destinations"));

        Destination destination = generateDestination("ws_destination", site.getId());
        generateDestination("ws_destination2", site.getId());
        contentTypePageConfigurations[0].setDestinations(new Identifier[]
        {
            new Identifier(destination.getId(), null, EntityTypeString.destination, null)
        });

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.contenttype);

        // read the content type back again
        result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        asset = result.getAsset();
        assertNotNull(asset);

        fetchedCt = asset.getContentType();
        assertNotNull(fetchedCt);

        contentTypePageConfigurations = fetchedCt.getContentTypePageConfigurations();
        assertNotNull(contentTypePageConfigurations);
        assertEquals(1, contentTypePageConfigurations.length);

        assertEquals("html", contentTypePageConfigurations[0].getPageConfigurationName());
        assertEquals(ContentTypePageConfigurationPublishMode.fromString("selected-destinations"), contentTypePageConfigurations[0].getPublishMode());
        assertNotNull(contentTypePageConfigurations[0].getDestinations());
        assertEquals(1, contentTypePageConfigurations[0].getDestinations().length);
        assertEquals(destination.getId(), contentTypePageConfigurations[0].getDestinations()[0].getId());

        // Try now with all-destinations
        contentTypePageConfigurations[0].setPublishMode(ContentTypePageConfigurationPublishMode.fromString("all-destinations"));

        editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.contenttype);

        // read the content type back again
        result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        asset = result.getAsset();
        assertNotNull(asset);

        fetchedCt = asset.getContentType();
        assertNotNull(fetchedCt);

        contentTypePageConfigurations = fetchedCt.getContentTypePageConfigurations();
        assertNotNull(contentTypePageConfigurations);
        assertEquals(1, contentTypePageConfigurations.length);

        assertEquals("html", contentTypePageConfigurations[0].getPageConfigurationName());
        assertEquals(ContentTypePageConfigurationPublishMode.fromString("all-destinations"), contentTypePageConfigurations[0].getPublishMode());
        assertNull(contentTypePageConfigurations[0].getDestinations());

        // Try now without providing it
        fetchedCt.setContentTypePageConfigurations(null);

        editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.contenttype);

        // read the content type back again
        result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        asset = result.getAsset();
        assertNotNull(asset);

        fetchedCt = asset.getContentType();
        assertNotNull(fetchedCt);

        contentTypePageConfigurations = fetchedCt.getContentTypePageConfigurations();
        assertNotNull(contentTypePageConfigurations);
        assertEquals(1, contentTypePageConfigurations.length);

        assertEquals("html", contentTypePageConfigurations[0].getPageConfigurationName());
        assertEquals(ContentTypePageConfigurationPublishMode.fromString("all-destinations"), contentTypePageConfigurations[0].getPublishMode());
        assertNull(contentTypePageConfigurations[0].getDestinations());
    }

    /**
     * Tests creating a content type via web services with assets assigned that belong to the global area
     * 
     * @throws Exception
     */
    public void testCreateCrossSite() throws Exception
    {
        Site site = generateSite("asite");
        String siteId = site.getId();

        ContentType contentType = new ContentType();
        contentType.setName("ws_content_type_create");
        contentType.setPageConfigurationSetPath(generatePageConfigurationSet("ws_content_type_create", siteId).getPath());
        contentType.setMetadataSetPath("Global:" + generateMetadataSet("ws_content_type_create", null).getPath());
        contentType.setDataDefinitionId(generateDataDefinition("ws_content_type_create", null).getId());
        contentType.setParentContainerId(getRootContentTypeContainerId(siteId));
        contentType.setSiteName(site.getName());

        Asset asset = new Asset();
        asset.setContentType(contentType);

        String ctId = create(asset, EntityTypeString.contenttype);
        ReadResult result = client.read(auth, new Identifier(ctId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(result, EntityTypeString.contenttype);

        ContentType fetchedCt = result.getAsset().getContentType();
        assertNotNull(fetchedCt);
        assertEquals(fetchedCt.getPageConfigurationSetPath(), contentType.getPageConfigurationSetPath());
        assertEquals(fetchedCt.getMetadataSetPath(), contentType.getMetadataSetPath());
        assertEquals(fetchedCt.getDataDefinitionId(), contentType.getDataDefinitionId());
    }

    /**
     * Tests editing a content type via web services
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        // read the existing content type
        ReadResult readResult = client.read(auth, new Identifier(contentTypeId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(readResult, EntityTypeString.contenttype);

        Asset asset = readResult.getAsset();
        assertNotNull(asset);

        ContentType toEdit = asset.getContentType();
        assertNotNull(toEdit);

        toEdit.setPageConfigurationSetId(generatePageConfigurationSet("ws_content_type_edit", null).getId());
        toEdit.setMetadataSetId(generateMetadataSet("ws_content_type_edit", null).getId());
        toEdit.setDataDefinitionId(generateDataDefinition("ws_content_type_edit", null).getId());

        // silly web services nulling nonsense
        toEdit.setPageConfigurationSetPath(null);
        toEdit.setMetadataSetPath(null);
        toEdit.setDataDefinitionPath(null);
        toEdit.setParentContainerPath(null);

        // edit the content type
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.contenttype);

        // read the content type back again
        readResult = client.read(auth, new Identifier(contentTypeId, null, EntityTypeString.contenttype, null));
        assertOperationSuccess(readResult, EntityTypeString.contenttype);

        asset = readResult.getAsset();
        assertNotNull(asset);

        ContentType fetchedCt = asset.getContentType();
        assertNotNull(fetchedCt);
        assertEquals(fetchedCt.getPageConfigurationSetId(), toEdit.getPageConfigurationSetId());
        assertEquals(fetchedCt.getMetadataSetId(), toEdit.getMetadataSetId());
        assertEquals(fetchedCt.getDataDefinitionId(), toEdit.getDataDefinitionId());

        // must delete this here since the content type was created before its new config set, metadata set,
        // and structured data def
        // were created
        delete(fetchedCt.getId(), EntityTypeString.contenttype);
    }
}
