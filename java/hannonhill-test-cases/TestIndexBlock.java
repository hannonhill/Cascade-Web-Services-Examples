/*
 * Created on Aug 21, 2008 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2008 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlockSortMethod;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlockSortOrder;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlockType;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for index block
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   5.7
 */
public class TestIndexBlock extends CascadeWebServicesTestCase
{
    private IndexBlock folderIndexBlock;
    private String folderIndexBlockId;
    private IndexBlock contentTypeIndexBlock;
    private String contentTypeIndexBlockId;
    private String folderId;
    private String contentTypeId;
    private ContentType contentType;

    /*
     * (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        folderId = generateFolder("folder", null).getId();
        contentTypeId = generateContentType("content_type_2", false, null).getId();

        folderIndexBlock = new IndexBlock();
        folderIndexBlock.setName("index_block");
        folderIndexBlock.setMetadataSetId(generateMetadataSet("ws_file_metadataset", null).getId());
        folderIndexBlock.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        folderIndexBlock.setIndexedFolderId(RootContainerIds.FOLDER_ROOT_ID);

        Asset asset = new Asset();
        asset.setIndexBlock(folderIndexBlock);

        folderIndexBlockId = create(asset, EntityTypeString.block);

        contentTypeIndexBlock = new IndexBlock();
        contentTypeIndexBlock.setName("index_block_2");
        contentTypeIndexBlock.setMetadataSetId(generateMetadataSet("blah_blah", null).getId());
        contentTypeIndexBlock.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        contentType = generateContentType("content_type", false, null);
        contentTypeIndexBlock.setIndexedContentTypeId(contentType.getId());
        contentTypeIndexBlock.setSortMethod(IndexBlockSortMethod.fromString("alphabetical"));
        contentTypeIndexBlock.setSortOrder(IndexBlockSortOrder.ascending);
        contentTypeIndexBlock.setIndexBlockType(IndexBlockType.fromString("content-type"));

        asset = new Asset();
        asset.setIndexBlock(contentTypeIndexBlock);

        contentTypeIndexBlockId = create(asset, EntityTypeString.block);
    }

    /**
     * Tests reading a folder index block via web services.
     */
    public void testReadFolderIndex() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(folderIndexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedIndexBlock = asset.getIndexBlock();
        assertNotNull(fetchedIndexBlock);
        assertEquals(folderIndexBlock.getName(), fetchedIndexBlock.getName());
        assertEquals(folderIndexBlockId, fetchedIndexBlock.getId());
        assertEquals(folderIndexBlock.getIndexedFolderId(), fetchedIndexBlock.getIndexedFolderId());
        assertEquals(folderIndexBlock.getMetadataSetId(), fetchedIndexBlock.getMetadataSetId());
        assertEquals(folderIndexBlock.getParentFolderId(), fetchedIndexBlock.getParentFolderId());
        assertEquals(IndexBlockType.fromString("folder"), fetchedIndexBlock.getIndexBlockType());
    }

    /**
     * Tests reading a content type index block
     * 
     * @throws Exception
     */
    public void testReadContentTypeIndex() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(contentTypeIndexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedIndexBlock = asset.getIndexBlock();
        assertNotNull(fetchedIndexBlock);
        assertEquals(contentTypeIndexBlock.getName(), fetchedIndexBlock.getName());
        assertEquals(contentTypeIndexBlockId, fetchedIndexBlock.getId());
        assertEquals(contentTypeIndexBlock.getIndexedContentTypeId(), fetchedIndexBlock.getIndexedContentTypeId());
        assertEquals(contentTypeIndexBlock.getMetadataSetId(), fetchedIndexBlock.getMetadataSetId());
        assertEquals(contentTypeIndexBlock.getParentFolderId(), fetchedIndexBlock.getParentFolderId());
        assertEquals(contentTypeIndexBlock.getSortMethod(), fetchedIndexBlock.getSortMethod());
        assertEquals(contentTypeIndexBlock.getSortOrder(), fetchedIndexBlock.getSortOrder());
        assertEquals(IndexBlockType.fromString("content-type"), fetchedIndexBlock.getIndexBlockType());
    }

    /**
     * Tests reading a content type index block with a content type assigned to another site
     * 
     * @throws Exception
     */
    public void testReadContentTypeIndexCrossSite() throws Exception
    {
        Site site = generateSite("asite");
        contentTypeIndexBlock.setSiteId(site.getId());
        contentTypeIndexBlock.setName("newName");
        contentTypeIndexBlock.setParentFolderId(getRootFolderId(site.getId()));
        Asset asset = new Asset();
        asset.setIndexBlock(contentTypeIndexBlock);
        contentTypeIndexBlock = createAsset(asset, EntityTypeString.block).getIndexBlock();

        ReadResult result = client.read(auth, new Identifier(contentTypeIndexBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedIndexBlock = asset.getIndexBlock();
        assertNotNull(fetchedIndexBlock);
        assertEquals(contentTypeIndexBlock.getName(), fetchedIndexBlock.getName());
        assertEquals(contentTypeIndexBlock.getId(), fetchedIndexBlock.getId());
        assertEquals("Global:" + contentType.getPath(), fetchedIndexBlock.getIndexedContentTypePath());
    }

    /**
     * Tests editing a folder index block via web services
     * 
     * @throws Exception
     */
    public void testEditFolderIndex() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(folderIndexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedIndexBlock = asset.getIndexBlock();
        fetchedIndexBlock.setDepthOfIndex(new NonNegativeInteger("10"));
        fetchedIndexBlock.setIndexedFolderId(folderId);
        fetchedIndexBlock.setIndexedFolderPath(null);
        fetchedIndexBlock.setIncludeChildrenInHierarchy(true);
        fetchedIndexBlock.setIncludeCurrentPageXML(true);
        fetchedIndexBlock.setIncludePageContent(true);
        fetchedIndexBlock.setIndexBlocks(true);
        fetchedIndexBlock.setIndexFiles(true);
        fetchedIndexBlock.setIndexPages(true);
        fetchedIndexBlock.setIndexLinks(true);
        fetchedIndexBlock.setIndexRegularContent(true);
        fetchedIndexBlock.setIndexSystemMetadata(true);
        fetchedIndexBlock.setIndexUserInfo(true);
        fetchedIndexBlock.setIndexUserMetadata(true);
        fetchedIndexBlock.setIndexWorkflowInfo(true);
        fetchedIndexBlock.setAppendCallingPageData(true);
        fetchedIndexBlock.setMaxRenderedAssets(new NonNegativeInteger("200"));
        fetchedIndexBlock.setSortMethod(IndexBlockSortMethod.fromString("alphabetical"));
        asset = new Asset();
        asset.setIndexBlock(fetchedIndexBlock);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Make sure the page has a new content type assigned
        result = client.read(auth, new Identifier(fetchedIndexBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        IndexBlock reFetchedIndexBlock = result.getAsset().getIndexBlock();
        assertNotNull(reFetchedIndexBlock);

        assertEquals(IndexBlockType.fromString("folder"), fetchedIndexBlock.getIndexBlockType());
        assertEquals(fetchedIndexBlock.getDepthOfIndex(), reFetchedIndexBlock.getDepthOfIndex());
        assertEquals(fetchedIndexBlock.getIndexedFolderId(), reFetchedIndexBlock.getIndexedFolderId());
        assertEquals(fetchedIndexBlock.getIncludeChildrenInHierarchy(), reFetchedIndexBlock.getIncludeChildrenInHierarchy());
        assertEquals(fetchedIndexBlock.getIncludeCurrentPageXML(), reFetchedIndexBlock.getIncludeCurrentPageXML());
        assertEquals(fetchedIndexBlock.getIncludePageContent(), reFetchedIndexBlock.getIncludePageContent());
        assertEquals(fetchedIndexBlock.getIndexBlocks(), reFetchedIndexBlock.getIndexBlocks());
        assertEquals(fetchedIndexBlock.getIndexFiles(), reFetchedIndexBlock.getIndexFiles());
        assertEquals(fetchedIndexBlock.getIndexPages(), reFetchedIndexBlock.getIndexPages());
        assertEquals(fetchedIndexBlock.getIndexLinks(), reFetchedIndexBlock.getIndexLinks());
        assertEquals(fetchedIndexBlock.getIndexRegularContent(), reFetchedIndexBlock.getIndexRegularContent());
        assertEquals(fetchedIndexBlock.getIndexSystemMetadata(), reFetchedIndexBlock.getIndexSystemMetadata());
        assertEquals(fetchedIndexBlock.getIndexUserInfo(), reFetchedIndexBlock.getIndexUserInfo());
        assertEquals(fetchedIndexBlock.getIndexUserMetadata(), reFetchedIndexBlock.getIndexUserMetadata());
        assertEquals(fetchedIndexBlock.getIndexWorkflowInfo(), reFetchedIndexBlock.getIndexWorkflowInfo());
        assertEquals(fetchedIndexBlock.getAppendCallingPageData(), reFetchedIndexBlock.getAppendCallingPageData());
        assertEquals(fetchedIndexBlock.getMaxRenderedAssets(), reFetchedIndexBlock.getMaxRenderedAssets());
        assertEquals(fetchedIndexBlock.getSortMethod(), reFetchedIndexBlock.getSortMethod());
    }

    /**
     * Tests editing a content type index block via web services
     * 
     * @throws Exception
     */
    public void testEditContentTypeIndex() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(contentTypeIndexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedIndexBlock = asset.getIndexBlock();
        fetchedIndexBlock.setIndexedContentTypeId(contentTypeId);
        fetchedIndexBlock.setIndexedContentTypePath(null);
        fetchedIndexBlock.setIncludeCurrentPageXML(true);
        fetchedIndexBlock.setIncludePageContent(true);
        fetchedIndexBlock.setIndexRegularContent(true);
        fetchedIndexBlock.setIndexSystemMetadata(true);
        fetchedIndexBlock.setIndexUserInfo(true);
        fetchedIndexBlock.setIndexUserMetadata(true);
        fetchedIndexBlock.setIndexWorkflowInfo(true);
        fetchedIndexBlock.setAppendCallingPageData(true);
        fetchedIndexBlock.setMaxRenderedAssets(new NonNegativeInteger("200"));
        fetchedIndexBlock.setSortMethod(IndexBlockSortMethod.fromString("created-date"));
        fetchedIndexBlock.setSortOrder(IndexBlockSortOrder.descending);
        asset = new Asset();
        asset.setIndexBlock(fetchedIndexBlock);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Make sure the page has a new content type assigned
        result = client.read(auth, new Identifier(fetchedIndexBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        IndexBlock reFetchedIndexBlock = result.getAsset().getIndexBlock();
        assertNotNull(reFetchedIndexBlock);

        assertEquals(IndexBlockType.fromString("content-type"), fetchedIndexBlock.getIndexBlockType());
        assertEquals(fetchedIndexBlock.getIndexedContentTypeId(), reFetchedIndexBlock.getIndexedContentTypeId());
        assertEquals(fetchedIndexBlock.getIncludeCurrentPageXML(), reFetchedIndexBlock.getIncludeCurrentPageXML());
        assertEquals(fetchedIndexBlock.getIncludePageContent(), reFetchedIndexBlock.getIncludePageContent());
        assertEquals(fetchedIndexBlock.getIndexRegularContent(), reFetchedIndexBlock.getIndexRegularContent());
        assertEquals(fetchedIndexBlock.getIndexSystemMetadata(), reFetchedIndexBlock.getIndexSystemMetadata());
        assertEquals(fetchedIndexBlock.getIndexUserInfo(), reFetchedIndexBlock.getIndexUserInfo());
        assertEquals(fetchedIndexBlock.getIndexUserMetadata(), reFetchedIndexBlock.getIndexUserMetadata());
        assertEquals(fetchedIndexBlock.getIndexWorkflowInfo(), reFetchedIndexBlock.getIndexWorkflowInfo());
        assertEquals(fetchedIndexBlock.getAppendCallingPageData(), reFetchedIndexBlock.getAppendCallingPageData());
        assertEquals(fetchedIndexBlock.getMaxRenderedAssets(), reFetchedIndexBlock.getMaxRenderedAssets());
        assertEquals(fetchedIndexBlock.getSortMethod(), reFetchedIndexBlock.getSortMethod());
        assertEquals(fetchedIndexBlock.getSortOrder(), reFetchedIndexBlock.getSortOrder());
    }

    /**
     * Tests reading a folder index block that has a recycled folder assigned to it
     * 
     * @throws Exception
     */
    public void testGetRecycledIndexedFolder() throws Exception
    {
        Folder folder = generateFolder("ws_folder_block", null);

        ReadResult result = client.read(auth, new Identifier(folderIndexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getIndexBlock());

        folderIndexBlock = result.getAsset().getIndexBlock();
        folderIndexBlock.setIndexedFolderId(folder.getId());
        Asset asset = new Asset();
        asset.setIndexBlock(folderIndexBlock);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.block);

        result = client.read(auth, new Identifier(folderIndexBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getIndexBlock());

        IndexBlock fetched = result.getAsset().getIndexBlock();
        assertFalse(fetched.getIndexedFolderRecycled());

        delete(folder.getId(), EntityTypeString.folder);

        result = client.read(auth, new Identifier(folderIndexBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getIndexBlock());

        fetched = result.getAsset().getIndexBlock();
        assertTrue(fetched.getIndexedFolderRecycled());
    }
}
