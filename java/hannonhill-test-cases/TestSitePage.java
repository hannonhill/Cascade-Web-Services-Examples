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
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for page inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSitePage extends CascadeWebServicesTestCase
{
    private Page page;
    private String pageId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        page = new Page();
        page.setName("a_page");
        page.setParentFolderId(siteRootFolderId);
        page.setContentTypeId(generateContentType("ct_type", false, site).getId());
        page.setXhtml("<xml>aaa</xml>");
        page.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setPage(page);

        pageId = create(asset, EntityTypeString.page);
    }

    /**
     * Tests reading a page via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(pageId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Page fetchedPage = asset.getPage();
        assertNotNull(fetchedPage);
        assertEquals(page.getName(), fetchedPage.getName());
        assertEquals(pageId, fetchedPage.getId());
        assertEquals(page.getXhtml(), fetchedPage.getXhtml());
        assertEquals(page.getContentTypeId(), fetchedPage.getContentTypeId());
        assertEquals(page.getParentFolderId(), fetchedPage.getParentFolderId());
        assertEquals(page.getSiteId(), fetchedPage.getSiteId());
    }

    /**
     * Tests editing a text page via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier pageIdentifier = new Identifier(pageId, null, EntityTypeString.page, null);
        ReadResult result = client.read(auth, pageIdentifier);
        Page fetchedPage = result.getAsset().getPage();

        fetchedPage.setXhtml("<xml>bbb</xml>");

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.page);

        result = client.read(auth, pageIdentifier);
        assertOperationSuccess(result, EntityTypeString.page);

        Page refetchedPage = result.getAsset().getPage();
        assertEquals("<xml>bbb</xml>", refetchedPage.getXhtml());
        assertEquals(fetchedPage.getSiteId(), refetchedPage.getSiteId());
    }
}
