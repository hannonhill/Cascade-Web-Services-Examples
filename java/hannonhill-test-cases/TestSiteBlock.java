/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlock;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlockSortMethod;
import com.hannonhill.www.ws.ns.AssetOperationService.IndexBlockType;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for block inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteBlock extends CascadeWebServicesTestCase
{
    private IndexBlock indexBlock;
    private String indexBlockId;
    private String siteFolderId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String rootFolderId = site.getRootFolderId();

        siteFolderId = generateFolder("folder", siteId).getId();

        indexBlock = new IndexBlock();
        indexBlock.setName("index_block");
        indexBlock.setMetadataSetId(generateMetadataSet("ws_file_metadataset", siteId).getId());
        indexBlock.setParentFolderId(rootFolderId);
        indexBlock.setIndexedFolderId(rootFolderId);
        indexBlock.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setIndexBlock(indexBlock);

        indexBlockId = create(asset, EntityTypeString.block);
    }

    /**
     * Tests reading of a block
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(indexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedBlock = asset.getIndexBlock();
        assertNotNull(fetchedBlock);
        assertEquals(indexBlock.getName(), fetchedBlock.getName());
        assertEquals(indexBlockId, fetchedBlock.getId());
        assertEquals(indexBlock.getIndexedFolderId(), fetchedBlock.getIndexedFolderId());
        assertEquals(indexBlock.getMetadataSetId(), fetchedBlock.getMetadataSetId());
        assertEquals(indexBlock.getParentFolderId(), fetchedBlock.getParentFolderId());
        assertEquals(indexBlock.getSiteId(), fetchedBlock.getSiteId());
        assertEquals(IndexBlockType.fromString("folder"), fetchedBlock.getIndexBlockType());
    }

    /**
     * Tests editing of a block
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        // test site index block
        ReadResult result = client.read(auth, new Identifier(indexBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        IndexBlock fetchedBlock = asset.getIndexBlock();
        fetchedBlock.setDepthOfIndex(new NonNegativeInteger("10"));
        fetchedBlock.setIndexedFolderId(siteFolderId);
        fetchedBlock.setIndexedFolderPath(null);
        fetchedBlock.setIncludeChildrenInHierarchy(true);
        fetchedBlock.setIncludeCurrentPageXML(true);
        fetchedBlock.setIncludePageContent(true);
        fetchedBlock.setIndexBlocks(true);
        fetchedBlock.setIndexFiles(true);
        fetchedBlock.setIndexPages(true);
        fetchedBlock.setIndexLinks(true);
        fetchedBlock.setIndexRegularContent(true);
        fetchedBlock.setIndexSystemMetadata(true);
        fetchedBlock.setIndexUserInfo(true);
        fetchedBlock.setIndexUserMetadata(true);
        fetchedBlock.setIndexWorkflowInfo(true);
        fetchedBlock.setAppendCallingPageData(true);
        fetchedBlock.setMaxRenderedAssets(new NonNegativeInteger("200"));
        fetchedBlock.setSortMethod(IndexBlockSortMethod.fromString("alphabetical"));
        asset = new Asset();
        asset.setIndexBlock(fetchedBlock);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        result = client.read(auth, new Identifier(fetchedBlock.getId(), null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        IndexBlock reFetchedBlock = result.getAsset().getIndexBlock();
        assertNotNull(reFetchedBlock);

        assertEquals(IndexBlockType.fromString("folder"), fetchedBlock.getIndexBlockType());
        assertEquals(fetchedBlock.getDepthOfIndex(), reFetchedBlock.getDepthOfIndex());
        assertEquals(fetchedBlock.getIndexedFolderId(), reFetchedBlock.getIndexedFolderId());
        assertEquals(fetchedBlock.getIncludeChildrenInHierarchy(), reFetchedBlock.getIncludeChildrenInHierarchy());
        assertEquals(fetchedBlock.getIncludeCurrentPageXML(), reFetchedBlock.getIncludeCurrentPageXML());
        assertEquals(fetchedBlock.getIncludePageContent(), reFetchedBlock.getIncludePageContent());
        assertEquals(fetchedBlock.getIndexBlocks(), reFetchedBlock.getIndexBlocks());
        assertEquals(fetchedBlock.getIndexFiles(), reFetchedBlock.getIndexFiles());
        assertEquals(fetchedBlock.getIndexPages(), reFetchedBlock.getIndexPages());
        assertEquals(fetchedBlock.getIndexLinks(), reFetchedBlock.getIndexLinks());
        assertEquals(fetchedBlock.getIndexRegularContent(), reFetchedBlock.getIndexRegularContent());
        assertEquals(fetchedBlock.getIndexSystemMetadata(), reFetchedBlock.getIndexSystemMetadata());
        assertEquals(fetchedBlock.getIndexUserInfo(), reFetchedBlock.getIndexUserInfo());
        assertEquals(fetchedBlock.getIndexUserMetadata(), reFetchedBlock.getIndexUserMetadata());
        assertEquals(fetchedBlock.getIndexWorkflowInfo(), reFetchedBlock.getIndexWorkflowInfo());
        assertEquals(fetchedBlock.getAppendCallingPageData(), reFetchedBlock.getAppendCallingPageData());
        assertEquals(fetchedBlock.getMaxRenderedAssets(), reFetchedBlock.getMaxRenderedAssets());
        assertEquals(fetchedBlock.getSortMethod(), reFetchedBlock.getSortMethod());
        assertEquals(fetchedBlock.getSiteId(), reFetchedBlock.getSiteId());
    }
}
