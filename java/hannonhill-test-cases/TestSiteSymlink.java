/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.URI;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.Symlink;

/**
 * Web Services test for symlink inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteSymlink extends CascadeWebServicesTestCase
{
    private Symlink symlink;
    private String symlinkId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        symlink = new Symlink();
        symlink.setName("a_symlink");
        symlink.setParentFolderId(siteRootFolderId);
        symlink.setLinkURL(new URI("http://yahoo.com"));
        symlink.setSiteId(siteId);
        symlink.setMetadataSetId(generateMetadataSet("ms", siteId).getId());

        Asset asset = new Asset();
        asset.setSymlink(symlink);

        symlinkId = create(asset, EntityTypeString.symlink);
    }

    /**
     * Tests reading a symlink via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(symlinkId, null, EntityTypeString.symlink, null));
        assertOperationSuccess(result, EntityTypeString.symlink);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Symlink fetchedSymlink = asset.getSymlink();
        assertNotNull(fetchedSymlink);
        assertEquals(symlink.getName(), fetchedSymlink.getName());
        assertEquals(symlinkId, fetchedSymlink.getId());
        assertEquals(symlink.getLinkURL(), fetchedSymlink.getLinkURL());
        assertEquals(symlink.getParentFolderId(), fetchedSymlink.getParentFolderId());
        assertEquals(symlink.getSiteId(), fetchedSymlink.getSiteId());
        assertEquals(symlink.getMetadataSetId(), fetchedSymlink.getMetadataSetId());
    }

    /**
     * Tests editing a symlink via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier symlinkIdentifier = new Identifier(symlinkId, null, EntityTypeString.symlink, null);
        ReadResult result = client.read(auth, symlinkIdentifier);
        Symlink fetchedSymlink = result.getAsset().getSymlink();

        fetchedSymlink.setLinkURL(new URI("http://google.com"));

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.symlink);

        result = client.read(auth, symlinkIdentifier);
        assertOperationSuccess(result, EntityTypeString.symlink);

        Symlink refetchedSymlink = result.getAsset().getSymlink();
        assertEquals(fetchedSymlink.getLinkURL(), refetchedSymlink.getLinkURL());
        assertEquals(fetchedSymlink.getSiteId(), refetchedSymlink.getSiteId());
        assertEquals(fetchedSymlink.getMetadataSetId(), refetchedSymlink.getMetadataSetId());
    }
}
