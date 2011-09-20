/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ScriptFormat;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for format inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteFormat extends CascadeWebServicesTestCase
{
    private ScriptFormat format;
    private String formatId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        format = new ScriptFormat();
        format.setName("a_format");
        format.setParentFolderId(siteRootFolderId);
        format.setScript("Test");
        format.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setScriptFormat(format);

        formatId = create(asset, EntityTypeString.format_SCRIPT);
    }

    /**
     * Tests reading a format via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(formatId, null, EntityTypeString.format_SCRIPT, null));
        assertOperationSuccess(result, EntityTypeString.format_SCRIPT);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        ScriptFormat fetchedFormat = asset.getScriptFormat();
        assertNotNull(fetchedFormat);
        assertEquals(format.getName(), fetchedFormat.getName());
        assertEquals(formatId, fetchedFormat.getId());
        assertEquals(format.getScript(), fetchedFormat.getScript());
        assertEquals(format.getParentFolderId(), fetchedFormat.getParentFolderId());
        assertEquals(format.getSiteId(), fetchedFormat.getSiteId());
    }

    /**
     * Tests editing a format via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier formatIdentifier = new Identifier(formatId, null, EntityTypeString.format_SCRIPT, null);
        ReadResult result = client.read(auth, formatIdentifier);
        ScriptFormat fetchedFormat = result.getAsset().getScriptFormat();

        fetchedFormat.setScript("New script");

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.format_SCRIPT);

        result = client.read(auth, formatIdentifier);
        assertOperationSuccess(result, EntityTypeString.format_SCRIPT);

        ScriptFormat refetchedFormat = result.getAsset().getScriptFormat();
        assertEquals(fetchedFormat.getScript(), refetchedFormat.getScript());
        assertEquals(fetchedFormat.getSiteId(), refetchedFormat.getSiteId());
    }
}
