/*
 * Created on Jul 11, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.XsltFormat;

/**
 * Tests web services operations for Folders.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.5
 */
public class TestFolder extends CascadeWebServicesTestCase
{

    private Folder folder;
    private String folderId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        folder = new Folder();
        folder.setName("ws_folder");
        folder.setMetadataSetId(generateMetadataSet("ws_folder_fold", null).getId());
        folder.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);

        Asset asset = new Asset();
        asset.setFolder(folder);
        folderId = create(asset, EntityTypeString.folder);
    }

    /**
     * Tests reading a folder via web services. 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(folderId, null, EntityTypeString.folder, null));
        assertOperationSuccess(result, EntityTypeString.folder);

        Asset asset = result.getAsset();
        assertNotNull(asset);
        Folder fetchedFolder = asset.getFolder();
        assertNotNull(fetchedFolder);

        assertEquals(fetchedFolder.getName(), folder.getName());
        assertEquals(fetchedFolder.getMetadataSetId(), folder.getMetadataSetId());
        assertEquals(fetchedFolder.getParentFolderId(), folder.getParentFolderId());
        assertEquals(fetchedFolder.getPath(), folder.getName());
    }

    /**
     * Tests editing a folder via web services. 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        Identifier identifier = new Identifier(folderId, null, EntityTypeString.folder, null);
        ReadResult result = client.read(auth, identifier);
        assertOperationSuccess(result, EntityTypeString.folder);

        Asset asset = result.getAsset();
        Folder fetchedFolder = asset.getFolder();

        fetchedFolder.setMetadataSetId(generateMetadataSet("ws_folder_new", null).getId());

        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.folder);

        result = client.read(auth, identifier);
        assertOperationSuccess(result, EntityTypeString.folder);

        Folder editedFolder = result.getAsset().getFolder();
        assertNotNull(editedFolder);
        assertEquals(editedFolder.getMetadataSetId(), fetchedFolder.getMetadataSetId());
        assertEquals(editedFolder.getParentFolderId(), fetchedFolder.getParentFolderId());
        delete(folderId, EntityTypeString.folder);
    }

    /**
     * Test for CSCD-6847, makes sure blocks and formats come back with a valid type string.
     *
     * @throws Exception
     */
    public void testReadFolderWithBlockFormatChildren() throws Exception
    {
        Folder folder = generateFolder("ws_test_folder", null);

        IndexBlock ib = generateIndexBlockObject("ws_ib_in_folder", null);
        ib.setParentFolderId(folder.getId());

        Asset asset = new Asset();
        asset.setIndexBlock(ib);
        createAsset(asset, EntityTypeString.block_INDEX);

        XsltFormat xsltFormat = generateXsltFormatObject("ws_xslt_format_in_folder", null);
        xsltFormat.setParentFolderId(folder.getId());
        asset.setIndexBlock(null);
        asset.setXsltFormat(xsltFormat);
        createAsset(asset, EntityTypeString.format_XSLT);

        ReadResult folderResult = client.read(auth, new Identifier(folder.getId(), null, EntityTypeString.folder, false));
        Folder readFolder = folderResult.getAsset().getFolder();

        for (Identifier id : readFolder.getChildren())
        {
            // none of the type fields should be empty
            if (id.getId().equals(ib.getId()))
                assertEquals(EntityTypeString.block_INDEX, id.getType());
            else if (id.getId().equals(xsltFormat.getId()))
                assertEquals(EntityTypeString.format_XSLT, id.getType());
        }
    }
}
