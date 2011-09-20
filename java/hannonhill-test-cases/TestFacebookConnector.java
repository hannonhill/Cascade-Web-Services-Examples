/*
 * Created on Jun 1, 2011 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2011 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorContentTypeLink;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorParameter;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.FacebookConnector;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Tests for performing web services operations on facebook connectors
 * 
 * @author Artur Tomusiak
 * @since 7.0
 */
public class TestFacebookConnector extends CascadeWebServicesTestCase
{
    private String rootConnectorContainerId;
    private Site site;
    private FacebookConnector facebookConnector;
    private String facebookConnectorId;
    private Destination destination;
    private Destination destination2;
    private ContentType contentType1;
    private ContentType contentType2;
    private ContentType contentType3;
    private String pageConfigurationId1;
    private String pageConfigurationId2;
    private String pageConfigurationId3;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        site = generateSite("site");
        String siteId = site.getId();

        destination = generateDestination("ws_destination", siteId);
        destination2 = generateDestination("ws_destination", site.getId());
        rootConnectorContainerId = getRootConnectorContainerId(siteId);

        facebookConnector = new FacebookConnector();
        facebookConnector.setName("ws_facebook");
        facebookConnector.setParentContainerId(rootConnectorContainerId);
        facebookConnector.setSiteId(siteId);
        facebookConnector.setDestinationId(destination.getId());
        ConnectorParameter connectorParameter = new ConnectorParameter();
        connectorParameter.setName("Page Name");
        connectorParameter.setValue("A page name");
        facebookConnector.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter
        });

        contentType1 = generateContentType("ws_contentType1", false, site);
        contentType2 = generateContentType("ws_contentType2", false, site);
        contentType3 = generateContentType("ws_contentType3", false, site);

        PageConfigurationSet pageConfigurationSet1 = client
                .read(auth, new Identifier(contentType1.getPageConfigurationSetId(), null, EntityTypeString.pageconfigurationset, null)).getAsset()
                .getPageConfigurationSet();
        PageConfigurationSet pageConfigurationSet2 = client
                .read(auth, new Identifier(contentType2.getPageConfigurationSetId(), null, EntityTypeString.pageconfigurationset, null)).getAsset()
                .getPageConfigurationSet();
        PageConfigurationSet pageConfigurationSet3 = client
                .read(auth, new Identifier(contentType3.getPageConfigurationSetId(), null, EntityTypeString.pageconfigurationset, null)).getAsset()
                .getPageConfigurationSet();

        pageConfigurationId1 = pageConfigurationSet1.getPageConfigurations()[0].getId();
        pageConfigurationId2 = pageConfigurationSet2.getPageConfigurations()[0].getId();
        pageConfigurationId3 = pageConfigurationSet3.getPageConfigurations()[0].getId();

        ConnectorContentTypeLink connectorContentTypeLink1 = new ConnectorContentTypeLink();
        connectorContentTypeLink1.setContentTypeId(contentType1.getId());
        connectorContentTypeLink1.setPageConfigurationId(pageConfigurationId1);
        ConnectorContentTypeLink connectorContentTypeLink2 = new ConnectorContentTypeLink();
        connectorContentTypeLink2.setContentTypeId(contentType2.getId());
        connectorContentTypeLink2.setPageConfigurationId(pageConfigurationId2);

        facebookConnector.setConnectorContentTypeLinks(new ConnectorContentTypeLink[]
        {
                connectorContentTypeLink1, connectorContentTypeLink2
        });

        Asset asset = new Asset();
        asset.setFacebookConnector(facebookConnector);
        facebookConnectorId = create(asset, EntityTypeString.facebookconnector);
    }

    /**
     * Tests reading a facebook connector
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(facebookConnectorId, null, EntityTypeString.facebookconnector, null));
        assertOperationSuccess(rr, EntityTypeString.facebookconnector);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        FacebookConnector fetchedConnector = rr.getAsset().getFacebookConnector();
        assertNotNull(fetchedConnector);
        assertEquals(fetchedConnector.getName(), facebookConnector.getName());
        assertEquals(facebookConnector.getParentContainerId(), fetchedConnector.getParentContainerId());
        assertEquals(facebookConnector.getSiteId(), fetchedConnector.getSiteId());
        assertEquals(facebookConnector.getDestinationId(), fetchedConnector.getDestinationId());

        ConnectorContentTypeLink[] fetchedLinks = fetchedConnector.getConnectorContentTypeLinks();
        assertEquals(facebookConnector.getConnectorContentTypeLinks().length, fetchedLinks.length);
        for (ConnectorContentTypeLink fetchedLink : fetchedLinks)
            if (!fetchedLink.getContentTypeId().equals(contentType1.getId()) && !fetchedLink.getContentTypeId().equals(contentType2.getId()))
                fail("Wrong content type id: " + fetchedLink.getPageConfigurationId());
    }

    /**
     * Tests editing a facebook connector
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(facebookConnectorId, null, EntityTypeString.facebookconnector, null));
        FacebookConnector fetchedConnector = rr.getAsset().getFacebookConnector();

        fetchedConnector.setName("ws_facebook_renamed");
        fetchedConnector.setDestinationId(destination2.getId());
        fetchedConnector.setDestinationPath(null);

        ConnectorContentTypeLink[] fetchedLinks = fetchedConnector.getConnectorContentTypeLinks();
        ConnectorContentTypeLink connectorContentTypeLink3 = new ConnectorContentTypeLink();
        connectorContentTypeLink3.setContentTypeId(contentType3.getId());
        connectorContentTypeLink3.setPageConfigurationId(pageConfigurationId3);

        ConnectorContentTypeLink[] newFetchedLinks = new ConnectorContentTypeLink[3];
        newFetchedLinks[0] = fetchedLinks[0];
        newFetchedLinks[1] = fetchedLinks[1];
        newFetchedLinks[2] = connectorContentTypeLink3;
        fetchedConnector.setConnectorContentTypeLinks(newFetchedLinks);

        Asset asset = new Asset();
        asset.setFacebookConnector(fetchedConnector);

        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.facebookconnector);

        Asset reFetchedAsset = rr.getAsset();
        assertNotNull(reFetchedAsset);

        FacebookConnector reFetchedConnector = rr.getAsset().getFacebookConnector();
        assertNotNull(reFetchedConnector);
        assertEquals(fetchedConnector.getName(), reFetchedConnector.getName());
        assertEquals(fetchedConnector.getParentContainerId(), reFetchedConnector.getParentContainerId());
        assertEquals(fetchedConnector.getSiteId(), reFetchedConnector.getSiteId());
        assertEquals(fetchedConnector.getDestinationId(), reFetchedConnector.getDestinationId());

        ConnectorContentTypeLink[] reFetchedLinks = reFetchedConnector.getConnectorContentTypeLinks();
        assertEquals(fetchedConnector.getConnectorContentTypeLinks().length, reFetchedLinks.length);
        for (ConnectorContentTypeLink fetchedLink : reFetchedLinks)
            if (!fetchedLink.getContentTypeId().equals(contentType1.getId()) && !fetchedLink.getContentTypeId().equals(contentType2.getId())
                    && !fetchedLink.getContentTypeId().equals(contentType3.getId()))
                fail("Wrong content type id: " + fetchedLink.getPageConfigurationId());
    }
}
