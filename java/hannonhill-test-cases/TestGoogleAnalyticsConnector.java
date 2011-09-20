/*
 * Created on Aug 9, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ConnectorParameter;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.GoogleAnalyticsConnector;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Tests for performing web services operations on Google Analytics connectors
 * 
 * @author Artur Tomusiak
 * @since 6.8
 */
public class TestGoogleAnalyticsConnector extends CascadeWebServicesTestCase
{
    private String rootConnectorContainerId;
    private Site site;
    private GoogleAnalyticsConnector googleAnalyticsConnector;
    private String googleAnalyticsConnectorId;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        site = generateSite("site");
        String siteId = site.getId();

        rootConnectorContainerId = getRootConnectorContainerId(siteId);

        googleAnalyticsConnector = new GoogleAnalyticsConnector();
        googleAnalyticsConnector.setName("ws_ga");
        googleAnalyticsConnector.setParentContainerId(rootConnectorContainerId);
        googleAnalyticsConnector.setSiteId(siteId);

        ConnectorParameter connectorParameter1 = new ConnectorParameter();
        connectorParameter1.setName("Base Path");
        connectorParameter1.setValue("value1");

        ConnectorParameter connectorParameter2 = new ConnectorParameter();
        connectorParameter2.setName("Google Analytics Profile Id");
        connectorParameter2.setValue("value2");
        googleAnalyticsConnector.setConnectorParameters(new ConnectorParameter[]
        {
                connectorParameter1, connectorParameter2
        });

        Asset asset = new Asset();
        asset.setGoogleAnalyticsConnector(googleAnalyticsConnector);
        googleAnalyticsConnectorId = create(asset, EntityTypeString.googleanalyticsconnector);
    }

    /**
     * Tests reading a Google Analytics connector
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(googleAnalyticsConnectorId, null, EntityTypeString.googleanalyticsconnector, null));
        assertOperationSuccess(rr, EntityTypeString.googleanalyticsconnector);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        GoogleAnalyticsConnector fetchedConnector = rr.getAsset().getGoogleAnalyticsConnector();
        assertNotNull(fetchedConnector);
        assertEquals(fetchedConnector.getName(), googleAnalyticsConnector.getName());
        assertEquals(googleAnalyticsConnector.getParentContainerId(), fetchedConnector.getParentContainerId());
        assertEquals(googleAnalyticsConnector.getSiteId(), fetchedConnector.getSiteId());
    }

    /**
     * Tests editing a Google Analytics connector
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(googleAnalyticsConnectorId, null, EntityTypeString.googleanalyticsconnector, null));
        GoogleAnalyticsConnector fetchedConnector = rr.getAsset().getGoogleAnalyticsConnector();

        fetchedConnector.setName("ws_ga_renamed");

        ConnectorParameter[] fetchedParameters = fetchedConnector.getConnectorParameters();
        for (ConnectorParameter fetchedParameter : fetchedParameters)
            if (fetchedParameter.getName().equals("Base Path"))
                fetchedParameter.setValue("value1changed");
            else if (fetchedParameter.getName().equals("Google Analytics Profile Id"))
                fetchedParameter.setValue("value2changed");

        Asset asset = new Asset();
        asset.setGoogleAnalyticsConnector(fetchedConnector);

        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.googleanalyticsconnector);

        Asset reFetchedAsset = rr.getAsset();
        assertNotNull(reFetchedAsset);

        GoogleAnalyticsConnector reFetchedConnector = rr.getAsset().getGoogleAnalyticsConnector();
        assertNotNull(reFetchedConnector);
        assertEquals(fetchedConnector.getName(), reFetchedConnector.getName());
        assertEquals(fetchedConnector.getParentContainerId(), reFetchedConnector.getParentContainerId());
        assertEquals(fetchedConnector.getSiteId(), reFetchedConnector.getSiteId());

        ConnectorParameter[] reFetchedParameters = reFetchedConnector.getConnectorParameters();
        assertEquals(reFetchedParameters.length, fetchedConnector.getConnectorParameters().length);
        for (ConnectorParameter fetchedParameter : reFetchedParameters)
            if (fetchedParameter.getName().equals("Base Path"))
                assertEquals("value1changed", fetchedParameter.getValue());
            else if (fetchedParameter.getName().equals("Google Analytics Profile Id"))
                assertEquals("value2changed", fetchedParameter.getValue());
            else
                fail("Wrong parameter name: " + fetchedParameter.getName());
    }

    /**
     * Ensures it is impossible to create more than 1 connector per site
     * 
     * @throws Exception
     */
    public void testCreate() throws Exception
    {
        // Try to create 2 connectors in 1 site
        Site site1 = generateSite("site1");
        String site1Id = site1.getId();
        GoogleAnalyticsConnector gaConnector1 = new GoogleAnalyticsConnector();
        gaConnector1.setName("ws_ga1");
        gaConnector1.setParentContainerId(getRootConnectorContainerId(site1Id));
        gaConnector1.setSiteId(site1Id);

        ConnectorParameter connectorParameter1 = new ConnectorParameter();
        connectorParameter1.setName("Google Analytics Profile Id");
        connectorParameter1.setValue("value1");
        gaConnector1.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter1
        });

        Asset asset1 = new Asset();
        asset1.setGoogleAnalyticsConnector(gaConnector1);
        assertNotNull(create(asset1, EntityTypeString.googleanalyticsconnector));

        GoogleAnalyticsConnector gaConnector2 = new GoogleAnalyticsConnector();
        gaConnector2.setName("ws_ga2");
        gaConnector2.setParentContainerId(getRootConnectorContainerId(site1Id));
        gaConnector2.setSiteId(site1Id);
        gaConnector2.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter1
        });

        ConnectorParameter connectorParameter2 = new ConnectorParameter();
        connectorParameter2.setName("Google Analytics Profile Id");
        connectorParameter2.setValue("value2");
        gaConnector2.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter2
        });

        Asset asset2 = new Asset();
        asset2.setGoogleAnalyticsConnector(gaConnector2);
        CreateResult result = client.create(auth, asset2);
        assertFalse(success(result));

        // Try to create a second connector in another site
        Site site2 = generateSite("site2");
        String site2Id = site2.getId();
        GoogleAnalyticsConnector gaConnector3 = new GoogleAnalyticsConnector();
        gaConnector3.setName("ws_ga3");
        gaConnector3.setParentContainerId(getRootConnectorContainerId(site2Id));
        gaConnector3.setSiteId(site2Id);
        gaConnector3.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter1
        });

        ConnectorParameter connectorParameter3 = new ConnectorParameter();
        connectorParameter3.setName("Google Analytics Profile Id");
        connectorParameter3.setValue("value3");
        gaConnector3.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter3
        });

        Asset asset3 = new Asset();
        asset3.setGoogleAnalyticsConnector(gaConnector3);
        assertNotNull(create(asset3, EntityTypeString.googleanalyticsconnector));
    }

    /**
     * Ensures it is not possible to create or edit a {@link GoogleAnalyticsConnector} with an empty profile
     * id
     * 
     * @throws Exception
     */
    public void testRequiredProfileId() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(googleAnalyticsConnectorId, null, EntityTypeString.googleanalyticsconnector, null));
        GoogleAnalyticsConnector fetchedConnector = rr.getAsset().getGoogleAnalyticsConnector();

        // Test no parameters provided
        fetchedConnector.setConnectorParameters(new ConnectorParameter[0]);

        Asset asset = new Asset();
        asset.setGoogleAnalyticsConnector(fetchedConnector);

        assertFalse(success(client.edit(auth, asset)));

        // Test parameter provided but not the right one
        ConnectorParameter connectorParameter1 = new ConnectorParameter();
        connectorParameter1.setName("Base Path");
        connectorParameter1.setValue("value1");
        fetchedConnector.setConnectorParameters(new ConnectorParameter[]
        {
            connectorParameter1
        });

        assertFalse(success(client.edit(auth, asset)));

        // Test right parameter provided but with empty string value
        fetchedConnector.getConnectorParameters()[0].setName("Google Analytics Profile Id");
        fetchedConnector.getConnectorParameters()[0].setValue("");
        assertFalse(success(client.edit(auth, asset)));

        // Test right parameter provided with not empty string value
        fetchedConnector.getConnectorParameters()[0].setValue("value2");
        assertOperationSuccess(client.edit(auth, asset), EntityTypeString.googleanalyticsconnector);
    }
}
