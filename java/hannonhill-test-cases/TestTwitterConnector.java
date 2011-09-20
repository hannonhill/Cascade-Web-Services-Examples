/*
 * Created on Sep 11, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorContentTypeLink;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorParameter;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.TwitterConnector;

/**
 * Tests for performing web services operations on twitter connectors
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.4
 */
public class TestTwitterConnector extends CascadeWebServicesTestCase
{
    private String rootConnectorContainerId;
    private Site site;
    private TwitterConnector twitterConnector;
    private String twitterConnectorId;
    private Destination destination;
    private Destination destination2;
    private ContentType contentType1;
    private ContentType contentType2;
    private ContentType contentType3;
    private String pageConfigurationId1;
    private String pageConfigurationId2;
    private String pageConfigurationId3;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        site = generateSite("site");
        String siteId = site.getId();

        destination = generateDestination("ws_destination", siteId);
        destination2 = generateDestination("ws_destination", site.getId());
        rootConnectorContainerId = getRootConnectorContainerId(siteId);

        twitterConnector = new TwitterConnector();
        twitterConnector.setName("ws_twitter");
        twitterConnector.setParentContainerId(rootConnectorContainerId);
        twitterConnector.setSiteId(siteId);
        twitterConnector.setDestinationId(destination.getId());

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

        ConnectorParameter connectorParameter1 = new ConnectorParameter();
        connectorParameter1.setName("Hash Tags");
        connectorParameter1.setValue("value1");
        ConnectorParameter connectorParameter2 = new ConnectorParameter();
        connectorParameter2.setName("Prefix");
        connectorParameter2.setValue("value2");
        twitterConnector.setConnectorParameters(new ConnectorParameter[]
        {
                connectorParameter1, connectorParameter2
        });

        ConnectorContentTypeLink connectorContentTypeLink1 = new ConnectorContentTypeLink();
        connectorContentTypeLink1.setContentTypeId(contentType1.getId());
        connectorContentTypeLink1.setPageConfigurationId(pageConfigurationId1);
        ConnectorContentTypeLink connectorContentTypeLink2 = new ConnectorContentTypeLink();
        connectorContentTypeLink2.setContentTypeId(contentType2.getId());
        connectorContentTypeLink2.setPageConfigurationId(pageConfigurationId2);

        twitterConnector.setConnectorContentTypeLinks(new ConnectorContentTypeLink[]
        {
                connectorContentTypeLink1, connectorContentTypeLink2
        });

        Asset asset = new Asset();
        asset.setTwitterConnector(twitterConnector);
        twitterConnectorId = create(asset, EntityTypeString.twitterconnector);
    }

    /**
     * Tests reading a twitter connector
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(twitterConnectorId, null, EntityTypeString.twitterconnector, null));
        assertOperationSuccess(rr, EntityTypeString.twitterconnector);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        TwitterConnector fetchedConnector = rr.getAsset().getTwitterConnector();
        assertNotNull(fetchedConnector);
        assertEquals(fetchedConnector.getName(), twitterConnector.getName());
        assertEquals(twitterConnector.getParentContainerId(), fetchedConnector.getParentContainerId());
        assertEquals(twitterConnector.getSiteId(), fetchedConnector.getSiteId());
        assertEquals(twitterConnector.getDestinationId(), fetchedConnector.getDestinationId());

        ConnectorParameter[] fetchedParameters = fetchedConnector.getConnectorParameters();
        assertEquals(fetchedParameters.length, twitterConnector.getConnectorParameters().length);
        for (ConnectorParameter fetchedParameter : fetchedParameters)
            if (fetchedParameter.getName().equals("Hash Tags"))
                assertEquals("value1", fetchedParameter.getValue());
            else if (fetchedParameter.getName().equals("Prefix"))
                assertEquals("value2", fetchedParameter.getValue());
            else
                fail("Wrong parameter name: " + fetchedParameter.getName());

        ConnectorContentTypeLink[] fetchedLinks = fetchedConnector.getConnectorContentTypeLinks();
        assertEquals(twitterConnector.getConnectorContentTypeLinks().length, fetchedLinks.length);
        for (ConnectorContentTypeLink fetchedLink : fetchedLinks)
            if (!fetchedLink.getContentTypeId().equals(contentType1.getId()) && !fetchedLink.getContentTypeId().equals(contentType2.getId()))
                fail("Wrong content type id: " + fetchedLink.getPageConfigurationId());
    }

    /**
     * Tests editing a twitter connector
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(twitterConnectorId, null, EntityTypeString.twitterconnector, null));
        TwitterConnector fetchedConnector = rr.getAsset().getTwitterConnector();

        fetchedConnector.setName("ws_twitter_renamed");
        fetchedConnector.setDestinationId(destination2.getId());
        fetchedConnector.setDestinationPath(null);

        ConnectorParameter[] fetchedParameters = fetchedConnector.getConnectorParameters();
        for (ConnectorParameter fetchedParameter : fetchedParameters)
            if (fetchedParameter.getName().equals("Hash Tags"))
                fetchedParameter.setValue("value1changed");
            else if (fetchedParameter.getName().equals("Prefix"))
                fetchedParameter.setValue("value2changed");

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
        asset.setTwitterConnector(fetchedConnector);

        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.twitterconnector);

        Asset reFetchedAsset = rr.getAsset();
        assertNotNull(reFetchedAsset);

        TwitterConnector reFetchedConnector = rr.getAsset().getTwitterConnector();
        assertNotNull(reFetchedConnector);
        assertEquals(fetchedConnector.getName(), reFetchedConnector.getName());
        assertEquals(fetchedConnector.getParentContainerId(), reFetchedConnector.getParentContainerId());
        assertEquals(fetchedConnector.getSiteId(), reFetchedConnector.getSiteId());
        assertEquals(fetchedConnector.getDestinationId(), reFetchedConnector.getDestinationId());

        ConnectorParameter[] reFetchedParameters = reFetchedConnector.getConnectorParameters();
        assertEquals(reFetchedParameters.length, fetchedConnector.getConnectorParameters().length);
        for (ConnectorParameter fetchedParameter : reFetchedParameters)
            if (fetchedParameter.getName().equals("Hash Tags"))
                assertEquals("value1changed", fetchedParameter.getValue());
            else if (fetchedParameter.getName().equals("Prefix"))
                assertEquals("value2changed", fetchedParameter.getValue());
            else
                fail("Wrong parameter name: " + fetchedParameter.getName());

        ConnectorContentTypeLink[] reFetchedLinks = reFetchedConnector.getConnectorContentTypeLinks();
        assertEquals(fetchedConnector.getConnectorContentTypeLinks().length, reFetchedLinks.length);
        for (ConnectorContentTypeLink fetchedLink : reFetchedLinks)
            if (!fetchedLink.getContentTypeId().equals(contentType1.getId()) && !fetchedLink.getContentTypeId().equals(contentType2.getId())
                    && !fetchedLink.getContentTypeId().equals(contentType3.getId()))
                fail("Wrong content type id: " + fetchedLink.getPageConfigurationId());
    }
}
