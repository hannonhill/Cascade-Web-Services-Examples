/*
 * Created on Mar 24, 2009 by syl
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataFieldVisibility;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * 
 * @author  Syl Turner
 * @version $Id$
 * @since   5.0
 */
public class TestMetadataSet extends CascadeWebServicesTestCase
{
    private MetadataSet metadataSet;
    private MetadataSet siteMetadataSet;
    private Site site;
    private String siteId;

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        metadataSet = generateMetadataSet("ws_metadata", null);

        site = generateSite("site");
        siteId = site.getId();
        siteMetadataSet = generateMetadataSet("ws_metadata", siteId);

    }

    /**
     * Test reading the metadata set
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(metadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet ms = asset.getMetadataSet();
        assertNotNull(ms);
        assertEquals(ms.getName(), metadataSet.getName());
        assertEquals(ms.getParentContainerId(), metadataSet.getParentContainerId());
    }

    /**
     * Test reading the metadata set from a site
     * @throws Exception
     */
    public void testSiteRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(siteMetadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet ms = asset.getMetadataSet();
        assertNotNull(ms);
        assertEquals(ms.getName(), siteMetadataSet.getName());
        assertEquals(ms.getParentContainerId(), siteMetadataSet.getParentContainerId());
        assertEquals(ms.getSiteId(), siteMetadataSet.getSiteId());
    }

    /**
     * Test editing the metadata set
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(metadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet ms = asset.getMetadataSet();
        assertNotNull(ms);

        ms.setAuthorFieldRequired(true);
        ms.setAuthorFieldVisibility(MetadataFieldVisibility.hidden);
        ms.setDisplayNameFieldRequired(true);
        ms.setDisplayNameFieldVisibility(MetadataFieldVisibility.inline);
        ms.setEndDateFieldRequired(true);
        ms.setEndDateFieldVisibility(MetadataFieldVisibility.hidden);

        // edit the metadata set
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.metadataset);

        // read the content type back again
        rr = client.read(auth, new Identifier(metadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet fetchedMs = asset.getMetadataSet();

        assertTrue(fetchedMs.getAuthorFieldRequired());
        assertEquals(fetchedMs.getAuthorFieldVisibility(), MetadataFieldVisibility.hidden);
        assertTrue(fetchedMs.getDisplayNameFieldRequired());
        assertEquals(fetchedMs.getDisplayNameFieldVisibility(), MetadataFieldVisibility.inline);
        assertTrue(fetchedMs.getEndDateFieldRequired());
        assertEquals(fetchedMs.getEndDateFieldVisibility(), MetadataFieldVisibility.hidden);

        delete(fetchedMs.getId(), EntityTypeString.metadataset);
    }

    /**
     * Test editing the metadata set from a site
     * @throws Exception
     */
    public void testSiteEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(siteMetadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        Asset asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet ms = asset.getMetadataSet();
        assertNotNull(ms);

        ms.setAuthorFieldRequired(true);
        ms.setAuthorFieldVisibility(MetadataFieldVisibility.hidden);
        ms.setDisplayNameFieldRequired(true);
        ms.setDisplayNameFieldVisibility(MetadataFieldVisibility.inline);
        ms.setEndDateFieldRequired(true);
        ms.setEndDateFieldVisibility(MetadataFieldVisibility.hidden);

        // edit the metadata set
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.metadataset);

        // read the content type back again
        rr = client.read(auth, new Identifier(siteMetadataSet.getId(), null, EntityTypeString.metadataset, null));
        assertOperationSuccess(rr, EntityTypeString.metadataset);

        asset = rr.getAsset();
        assertNotNull(asset);

        MetadataSet fetchedMs = asset.getMetadataSet();

        assertTrue(fetchedMs.getAuthorFieldRequired());
        assertEquals(fetchedMs.getAuthorFieldVisibility(), MetadataFieldVisibility.hidden);
        assertTrue(fetchedMs.getDisplayNameFieldRequired());
        assertEquals(fetchedMs.getDisplayNameFieldVisibility(), MetadataFieldVisibility.inline);
        assertTrue(fetchedMs.getEndDateFieldRequired());
        assertEquals(fetchedMs.getEndDateFieldVisibility(), MetadataFieldVisibility.hidden);

        delete(fetchedMs.getId(), EntityTypeString.metadataset);
    }

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

}
