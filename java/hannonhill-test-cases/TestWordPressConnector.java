/*
 * Created on Sep 14, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorContentTypeLink;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorContentTypeLinkParam;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.WordPressConnector;

/**
 * Tests for performing web services operations on WordPress connectors
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.4
 */
public class TestWordPressConnector extends CascadeWebServicesTestCase
{
    private String rootConnectorContainerId;
    private Site site;
    private WordPressConnector wordPressConnector;
    private String wordPressConnectorId;
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

        rootConnectorContainerId = getRootConnectorContainerId(siteId);

        wordPressConnector = new WordPressConnector();
        wordPressConnector.setName("ws_wordpress");
        wordPressConnector.setParentContainerId(rootConnectorContainerId);
        wordPressConnector.setSiteId(siteId);
        wordPressConnector.setUrl("http://mywordpress.com");

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

        ConnectorContentTypeLinkParam cctlp1 = new ConnectorContentTypeLinkParam();
        cctlp1.setName("paramName1");
        cctlp1.setValue("paramValue1");
        ConnectorContentTypeLinkParam cctlp2 = new ConnectorContentTypeLinkParam();
        cctlp2.setName("paramName2");
        cctlp2.setValue("paramValue2");
        ConnectorContentTypeLinkParam[] cctlps = new ConnectorContentTypeLinkParam[]
        {
                cctlp1, cctlp2
        };
        connectorContentTypeLink1.setConnectorContentTypeLinkParams(cctlps);

        ConnectorContentTypeLink connectorContentTypeLink2 = new ConnectorContentTypeLink();
        connectorContentTypeLink2.setContentTypeId(contentType2.getId());
        connectorContentTypeLink2.setPageConfigurationId(pageConfigurationId2);

        wordPressConnector.setConnectorContentTypeLinks(new ConnectorContentTypeLink[]
        {
                connectorContentTypeLink1, connectorContentTypeLink2
        });

        Asset asset = new Asset();
        asset.setWordPressConnector(wordPressConnector);
        wordPressConnectorId = create(asset, EntityTypeString.wordpressconnector);
    }

    /**
     * Tests reading a WordPress connector
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(wordPressConnectorId, null, EntityTypeString.wordpressconnector, null));
        assertOperationSuccess(rr, EntityTypeString.wordpressconnector);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        WordPressConnector fetchedConnector = rr.getAsset().getWordPressConnector();
        assertNotNull(fetchedConnector);
        assertEquals(wordPressConnector.getName(), fetchedConnector.getName());
        assertEquals(wordPressConnector.getParentContainerId(), fetchedConnector.getParentContainerId());
        assertEquals(wordPressConnector.getSiteId(), fetchedConnector.getSiteId());
        assertEquals(wordPressConnector.getUrl(), fetchedConnector.getUrl());

        ConnectorContentTypeLink[] fetchedLinks = fetchedConnector.getConnectorContentTypeLinks();
        assertEquals(fetchedLinks.length, wordPressConnector.getConnectorContentTypeLinks().length);
        for (ConnectorContentTypeLink fetchedLink : fetchedLinks)
            if (fetchedLink.getContentTypeId().equals(contentType1.getId()))
            {
                assertEquals(pageConfigurationId1, fetchedLink.getPageConfigurationId());
                assertNotNull(fetchedLink.getConnectorContentTypeLinkParams());
                assertEquals(2, fetchedLink.getConnectorContentTypeLinkParams().length);
                for (ConnectorContentTypeLinkParam param : fetchedLink.getConnectorContentTypeLinkParams())
                {
                    if (param.getName().equals("paramName1"))
                        assertEquals("paramValue1", param.getValue());
                    else if (param.getName().equals("paramName2"))
                        assertEquals("paramValue2", param.getValue());
                    else
                        fail("Param name incorrect " + param.getName());
                }
            }
            else if (fetchedLink.getContentTypeId().equals(contentType2.getId()))
                assertEquals(pageConfigurationId2, fetchedLink.getPageConfigurationId());
            else
                fail("Wrong content type id: " + fetchedLink.getContentTypeId());
    }

    /**
     * Tests editing a WordPress connector
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(wordPressConnectorId, null, EntityTypeString.wordpressconnector, null));
        WordPressConnector fetchedConnector = rr.getAsset().getWordPressConnector();

        fetchedConnector.setUrl("http://newUrl.com");

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
        asset.setWordPressConnector(fetchedConnector);

        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.wordpressconnector);

        rr = client.read(auth, new Identifier(wordPressConnectorId, null, EntityTypeString.wordpressconnector, null));
        Asset reFetchedAsset = rr.getAsset();
        assertNotNull(reFetchedAsset);

        WordPressConnector reFetchedConnector = rr.getAsset().getWordPressConnector();
        assertNotNull(reFetchedConnector);
        assertEquals(fetchedConnector.getParentContainerId(), reFetchedConnector.getParentContainerId());
        assertEquals(fetchedConnector.getSiteId(), reFetchedConnector.getSiteId());
        assertEquals(fetchedConnector.getUrl(), reFetchedConnector.getUrl());

        ConnectorContentTypeLink[] reFetchedLinks = reFetchedConnector.getConnectorContentTypeLinks();
        assertEquals(fetchedConnector.getConnectorContentTypeLinks().length, reFetchedLinks.length);
        for (ConnectorContentTypeLink fetchedLink : fetchedLinks)
            if (fetchedLink.getContentTypeId().equals(contentType1.getId()))
            {
                assertEquals(pageConfigurationId1, fetchedLink.getPageConfigurationId());
                assertNotNull(fetchedLink.getConnectorContentTypeLinkParams());
                assertEquals(2, fetchedLink.getConnectorContentTypeLinkParams().length);
                for (ConnectorContentTypeLinkParam param : fetchedLink.getConnectorContentTypeLinkParams())
                {
                    if (param.getName().equals("paramName1"))
                        assertEquals("paramValue1", param.getValue());
                    else if (param.getName().equals("paramName2"))
                        assertEquals("paramValue2", param.getValue());
                    else
                        fail("Param name incorrect " + param.getName());
                }
            }
            else if (fetchedLink.getContentTypeId().equals(contentType2.getId()))
                assertEquals(pageConfigurationId2, fetchedLink.getPageConfigurationId());
            else if (fetchedLink.getContentTypeId().equals(contentType3.getId()))
                assertEquals(pageConfigurationId3, fetchedLink.getPageConfigurationId());
            else
                fail("Wrong content type id: " + fetchedLink.getContentTypeId());
    }
}
