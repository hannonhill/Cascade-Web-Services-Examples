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
import com.hannonhill.www.ws.ns.AssetOperationService.Reference;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Web Services test for reference inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteReference extends CascadeWebServicesTestCase
{
    private Reference reference;
    private String referenceId;

    private String folder1Id;
    private String folder2Id;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        Folder folder1 = new Folder();
        folder1.setMetadataSetId(generateMetadataSet("ms1", siteId).getId());
        folder1.setName("folder1");
        folder1.setParentFolderId(siteRootFolderId);
        folder1.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setFolder(folder1);

        folder1Id = create(asset, EntityTypeString.folder);

        Folder folder2 = new Folder();
        folder2.setMetadataSetId(generateMetadataSet("ms2", siteId).getId());
        folder2.setName("folder2");
        folder2.setParentFolderId(siteRootFolderId);
        folder2.setSiteId(siteId);

        asset = new Asset();
        asset.setFolder(folder2);

        folder2Id = create(asset, EntityTypeString.folder);

        reference = new Reference();
        reference.setName("a_reference");
        reference.setParentFolderId(siteRootFolderId);
        reference.setReferencedAssetId(folder1Id);
        reference.setReferencedAssetType(EntityTypeString.folder);
        reference.setSiteId(siteId);

        asset = new Asset();
        asset.setReference(reference);

        referenceId = create(asset, EntityTypeString.reference);
    }

    /**
     * Tests reading a reference via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(referenceId, null, EntityTypeString.reference, null));
        assertOperationSuccess(result, EntityTypeString.reference);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Reference fetchedReference = asset.getReference();
        assertNotNull(fetchedReference);
        assertEquals(reference.getName(), fetchedReference.getName());
        assertEquals(referenceId, fetchedReference.getId());
        assertEquals(reference.getReferencedAssetId(), fetchedReference.getReferencedAssetId());
        assertEquals(reference.getReferencedAssetType(), fetchedReference.getReferencedAssetType());
        assertEquals(reference.getParentFolderId(), fetchedReference.getParentFolderId());
        assertEquals(reference.getSiteId(), fetchedReference.getSiteId());
    }

    /**
     * Tests editing a reference via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier referenceIdentifier = new Identifier(referenceId, null, EntityTypeString.reference, null);
        ReadResult result = client.read(auth, referenceIdentifier);
        Reference fetchedReference = result.getAsset().getReference();

        fetchedReference.setReferencedAssetId(folder2Id);
        fetchedReference.setReferencedAssetType(EntityTypeString.folder);

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.reference);

        result = client.read(auth, referenceIdentifier);
        assertOperationSuccess(result, EntityTypeString.reference);

        Reference refetchedReference = result.getAsset().getReference();
        assertEquals(fetchedReference.getReferencedAssetId(), refetchedReference.getReferencedAssetId());
        assertEquals(fetchedReference.getReferencedAssetType(), refetchedReference.getReferencedAssetType());
        assertEquals(fetchedReference.getSiteId(), refetchedReference.getSiteId());
    }
}
