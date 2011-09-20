/*
 * Created on Feb 19, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.ExpiringAsset;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * This class tests properties associated with expiring asset
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.7
 */
public class TestExpiring extends CascadeWebServicesTestCase
{
    /**
     * Tests reading an expiration folder from expiring asset
     * 
     * @throws Exception
     */
    public void testGetRecycledExpirationFolder() throws Exception
    {
        testGetRecycledExpirationFolder(generateFolderObject("test_folder", null), EntityTypeString.folder);
        testGetRecycledExpirationFolder(generateFeedBlockObject("test_feed_block", null), EntityTypeString.block);
        testGetRecycledExpirationFolder(generateIndexBlockObject("test_index_block", null), EntityTypeString.block);
        testGetRecycledExpirationFolder(generateTextBlockObject("test_text_block", null), EntityTypeString.block);
        testGetRecycledExpirationFolder(generateDataDefinitionBlockObject("test_structured_data_block", null), EntityTypeString.block);
        testGetRecycledExpirationFolder(generateXmlBlockObject("test_xml_block", null), EntityTypeString.block);
        testGetRecycledExpirationFolder(generateFileObject("test_file", null), EntityTypeString.file);
        testGetRecycledExpirationFolder(
                generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null), EntityTypeString.page);
        testGetRecycledExpirationFolder(generateSymlinkObject("test_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests reading an expiration folder from expiring asset
     * 
     * @param expiringAsset
     * @param type
     */
    private void testGetRecycledExpirationFolder(ExpiringAsset expiringAsset, EntityTypeString type) throws Exception
    {
        // Create asset with expiration folder
        Folder folder = generateFolder("ws_folder_exp", null);
        expiringAsset.setExpirationFolderId(folder.getId());
        Asset asset = new Asset();
        setAppropriateAsset(asset, expiringAsset);
        Asset createdAsset = createAsset(asset, type); // No exception should be thrown here and there should be should be operation success when reading

        // Ensure recycled = false
        ExpiringAsset readExpiringAsset = (ExpiringAsset) getAppropriateAsset(createdAsset);
        assertFalse(readExpiringAsset.getExpirationFolderRecycled());

        // Delete the folder
        delete(folder.getId(), EntityTypeString.folder);

        // Read back the asset an ensure recycled = true
        ReadResult result = client.read(auth, new Identifier(readExpiringAsset.getId(), null, type, null));
        assertOperationSuccess(result, type);
        readExpiringAsset = (ExpiringAsset) getAppropriateAsset(result.getAsset());
        assertTrue(readExpiringAsset.getExpirationFolderRecycled());
    }
}
