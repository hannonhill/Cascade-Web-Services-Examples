/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for folder inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteFolder extends CascadeWebServicesTestCase
{
    private Folder folder;
    private String folderId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        folder = new Folder();
        folder.setName("a_folder");
        folder.setParentFolderId(siteRootFolderId);
        folder.setSiteId(siteId);
        folder.setMetadataSetId(generateMetadataSet("ms", siteId).getId());

        Asset asset = new Asset();
        asset.setFolder(folder);

        folderId = create(asset, EntityTypeString.folder);
    }

    /**
     * Tests reading a folder via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(folderId, null, EntityTypeString.folder, null));
        assertOperationSuccess(result, EntityTypeString.folder);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Folder fetchedFolder = asset.getFolder();
        assertNotNull(fetchedFolder);
        assertEquals(folder.getName(), fetchedFolder.getName());
        assertEquals(folderId, fetchedFolder.getId());
        assertEquals(folder.getParentFolderId(), fetchedFolder.getParentFolderId());
        assertEquals(folder.getSiteId(), fetchedFolder.getSiteId());
        assertEquals(folder.getMetadataSetId(), fetchedFolder.getMetadataSetId());
    }

    /**
     * Tests editing a folder via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier folderIdentifier = new Identifier(folderId, null, EntityTypeString.folder, null);
        ReadResult result = client.read(auth, folderIdentifier);
        Folder fetchedFolder = result.getAsset().getFolder();

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.folder);

        result = client.read(auth, folderIdentifier);
        assertOperationSuccess(result, EntityTypeString.folder);

        Folder refetchedFolder = result.getAsset().getFolder();
        assertEquals(fetchedFolder.getSiteId(), refetchedFolder.getSiteId());
        assertEquals(fetchedFolder.getMetadataSetId(), refetchedFolder.getMetadataSetId());
    }
}
