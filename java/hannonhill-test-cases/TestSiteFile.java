/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for file inside of a site
 * 
 * @author  Artur
 * @version $Id$
 * @since   6.0
 */
public class TestSiteFile extends CascadeWebServicesTestCase
{
    private File file;
    private String fileId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        file = new File();
        file.setName("ws_file.txt");
        file.setMetadataSetId(generateMetadataSet("ws_file_metadataset", siteId).getId());
        file.setParentFolderId(siteRootFolderId);
        file.setText("sample text");
        file.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setFile(file);

        fileId = create(asset, EntityTypeString.file);
    }

    /**
     * Tests reading a file via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(fileId, null, EntityTypeString.file, null));
        assertOperationSuccess(result, EntityTypeString.file);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        File fetchedFile = asset.getFile();
        assertNotNull(fetchedFile);
        assertEquals(file.getName(), fetchedFile.getName());
        assertEquals(fileId, fetchedFile.getId());
        assertEquals(file.getText(), fetchedFile.getText());
        assertEquals(file.getMetadataSetId(), fetchedFile.getMetadataSetId());
        assertEquals(file.getParentFolderId(), fetchedFile.getParentFolderId());
        assertEquals(file.getSiteId(), fetchedFile.getSiteId());
    }

    /**
     * Tests editing a text file via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier fileIdentifier = new Identifier(fileId, null, EntityTypeString.file, null);
        ReadResult result = client.read(auth, fileIdentifier);
        File fetchedFile = result.getAsset().getFile();

        fetchedFile.setData(null);
        fetchedFile.setText("new text");
        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.file);

        result = client.read(auth, fileIdentifier);
        assertOperationSuccess(result, EntityTypeString.file);

        fetchedFile = result.getAsset().getFile();
        assertEquals("new text", fetchedFile.getText());
    }
}
