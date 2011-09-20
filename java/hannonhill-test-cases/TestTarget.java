/*
 * Created on Feb 19, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Target;

/**
 * Tests operations on a target
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.7
 */
public class TestTarget extends CascadeWebServicesTestCase
{
    Target target;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        target = generateTarget("ws_target");
    }

    /**
     * Tests reading a target
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult readResult = client.read(auth, new Identifier(target.getId(), null, EntityTypeString.target, null));
        assertOperationSuccess(readResult, EntityTypeString.target);
        Target readTarget = readResult.getAsset().getTarget();
        assertNotNull(readTarget);

        assertEquals(target.getName(), readTarget.getName());
        assertEquals(target.getBaseFolderId(), readTarget.getBaseFolderId());
        assertEquals(target.getOutputExtension(), readTarget.getOutputExtension());
        assertEquals(target.getParentTargetId(), readTarget.getParentTargetId());
        assertEquals(target.getSerializationType(), readTarget.getSerializationType());
    }

    /**
     * Tests reading a target that has a recycled css file
     * 
     * @throws Exception
     */
    public void testGetRecycledAssets() throws Exception
    {
        File file = generateFile("ws_file", null);

        target.setCssFileId(file.getId());
        Asset asset = new Asset();
        asset.setTarget(target);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.target);

        ReadResult result = client.read(auth, new Identifier(target.getId(), null, EntityTypeString.target, null));
        assertOperationSuccess(result, EntityTypeString.target);
        Target fetched = result.getAsset().getTarget();
        assertNotNull(fetched);

        assertFalse(fetched.getCssFileRecycled());

        delete(file.getId(), EntityTypeString.file);

        result = client.read(auth, new Identifier(target.getId(), null, EntityTypeString.target, null));
        assertOperationSuccess(result, EntityTypeString.target);
        fetched = result.getAsset().getTarget();
        assertNotNull(fetched);

        assertTrue(fetched.getCssFileRecycled());
    }
}
