/*
 * Created on Mar 23, 2009 by Syl Turner
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteDestinationContainer;

/**
 * Tests operations on {@link Destination}
 * 
 * @author  Syl Turner
 * @since   5.0
 */
public class TestDestination extends CascadeWebServicesTestCase
{
    private Destination destination;
    private String destinationId;

    private Site site;
    private String siteId;
    private Destination destinationSite;
    private String destinationSiteId;

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {

        destination = new Destination();
        destination = generateDestination("desty", null);

        destinationId = destination.getId();

        // create the destination inside a site
        site = generateSite("site");
        siteId = site.getId();

        destinationSite = new Destination();
        destinationSite = generateDestination("desty", siteId);
        destinationSiteId = destinationSite.getId();

    }

    @Test
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(destinationId, null, EntityTypeString.destination, null));
        assertOperationSuccess(result, EntityTypeString.destination);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Destination fetchedDestination = asset.getDestination();

        assertEquals(destination.getName(), fetchedDestination.getName());
        assertEquals(destination.getTransportId(), fetchedDestination.getTransportId());
        assertEquals(destination.getParentContainerId(), fetchedDestination.getParentContainerId());
        assertEquals(destination.getWebUrl(), fetchedDestination.getWebUrl());
    }

    @Test
    public void testSiteRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(destinationSiteId, null, EntityTypeString.destination, null));
        assertOperationSuccess(result, EntityTypeString.destination);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Destination fetchedDestination = asset.getDestination();

        assertEquals(destinationSite.getName(), fetchedDestination.getName());
        assertEquals(destinationSite.getTransportId(), fetchedDestination.getTransportId());
        assertEquals(destinationSite.getParentContainerId(), fetchedDestination.getParentContainerId());
        assertEquals(destinationSite.getSiteId(), fetchedDestination.getSiteId());
        assertEquals(destination.getWebUrl(), fetchedDestination.getWebUrl());
    }

    /**
     * Tests creating a destination in the global area that points to a transport
     * that lives inside a site (which is invalid).
     * 
     * @throws Exception
     */
    @Test
    public void testSiteCreateCrossSite() throws Exception
    {
        Destination destination = generateDestinationObject("desttest", null);
        destination.setTransportId(null);
        destination.setTransportPath(site.getName() + ":" + destinationSite.getTransportPath());
        Asset asset = new Asset();
        asset.setDestination(destination);

        CreateResult result = client.create(auth, asset);
        assertOperationFailure(result, EntityTypeString.destination);
    }

    /**
     * Tests editing of Destination containing:
     * - NULL applicable groups
     * - empty ("") applicable groups
     * - multiple, semicolon separated applicable groups
     * 
     * @throws Exception
     */
    @Test
    public void testEditWithApplicableGroups() throws Exception
    {
        // TEST 1:

        // create two groups
        String group1 = "dest_marketers";
        String group2 = "dest_developers";
        List<String> groups = new ArrayList<String>();
        groups.add(group1);
        groups.add(group2);
        generateGroup(group1);
        generateGroup(group2);

        ReadResult rr = client.read(auth, new Identifier(destinationSiteId, null, EntityTypeString.destination, null));
        assertOperationSuccess(rr, EntityTypeString.destination);

        Asset asset = rr.getAsset();
        assertNotNull(asset);
        Destination dest = asset.getDestination();
        assertNotNull(dest);

        // set the groups to semicolon-separated list of groups
        dest.setApplicableGroups(group1 + ";" + group2);

        // edit the destination
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.destination);

        // read the destination again
        ReadResult readAgain = client.read(auth, new Identifier(destinationSiteId, null, EntityTypeString.destination, null));
        assertOperationSuccess(readAgain, EntityTypeString.destination);

        Destination refetched = readAgain.getAsset().getDestination();
        assertContains(groups, refetched.getApplicableGroups());

        // TEST 2: Edit with empty ("") applicable groups

        // set the groups to empty string
        dest.setApplicableGroups("");

        // edit the destination
        result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.destination);

        // read the destination again
        readAgain = client.read(auth, new Identifier(destinationSiteId, null, EntityTypeString.destination, null));
        assertOperationSuccess(readAgain, EntityTypeString.destination);

        // assert groups are null
        refetched = readAgain.getAsset().getDestination();
        assertTrue(refetched.getApplicableGroups() == null);

        // TEST 3: Edit with empty ("") applicable groups

        // set the groups to NULL
        dest.setApplicableGroups(null);

        // edit the destination
        result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.destination);

        // read the destination again
        readAgain = client.read(auth, new Identifier(destinationSiteId, null, EntityTypeString.destination, null));
        assertOperationSuccess(readAgain, EntityTypeString.destination);

        // assert groups are null
        refetched = readAgain.getAsset().getDestination();
        assertTrue(refetched.getApplicableGroups() == null);
    }

    /**
     * Test that creates a Site Destination but specifies a path for the parent container.  Related to CSCD-7129.
     */
    @Test
    public void testCreateWithContainerPath() throws Exception
    {
        SiteDestinationContainer container = generateSiteDestinationContainer("ws_site_dest_container", site.getId());

        Destination destination = generateDestinationObject("ws_dest_withcontainer_path", site.getId());
        destination.setParentContainerId(null);
        destination.setParentContainerPath(container.getPath());

        Asset asset = new Asset();
        asset.setDestination(destination);
        CreateResult result = client.create(auth, asset);

        assertOperationSuccess(result, EntityTypeString.destination);

        String id = result.getCreatedAssetId();
        ReadResult readResult = client.read(auth, new Identifier(id, null, EntityTypeString.destination, false));

        Destination readDestination = readResult.getAsset().getDestination();
        assertEquals(container.getId(), readDestination.getParentContainerId());
        assertEquals(container.getPath(), readDestination.getParentContainerPath());
        assertEquals(site.getId(), readDestination.getSiteId());
        assertEquals(site.getName(), readDestination.getSiteName());
    }

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // only items that are not created with generate*() methods need to be cleaned up here
        super.tearDown();
    }
}