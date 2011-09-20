/*
 * Created on Feb 19, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Group;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Tests operations on group object
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.7
 */
public class TestGroup extends CascadeWebServicesTestCase
{
    Group group;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        group = generateGroup("ws_group");
    }

    /**
     * Tests reading a group
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult readResult = client.read(auth, new Identifier(group.getGroupName(), null, EntityTypeString.group, null));
        assertOperationSuccess(readResult, EntityTypeString.group);
        Group readGroup = readResult.getAsset().getGroup();
        assertNotNull(readGroup);
        assertEquals(group.getGroupName(), readGroup.getGroupName());
        assertEquals(group.getRole(), readGroup.getRole());
    }

    /**
     * Tests reading a group that has a recycled starting page and base folder
     * 
     * @throws Exception
     */
    public void testGetRecycledAssets() throws Exception
    {
        Page page = generatePage("ws_page", null);
        Folder folder = generateFolder("ws_folder_group", null);
        group.setGroupStartingPageId(page.getId());
        group.setGroupBaseFolderId(folder.getId());
        Asset asset = new Asset();
        asset.setGroup(group);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.group);

        ReadResult result = client.read(auth, new Identifier(group.getGroupName(), null, EntityTypeString.group, null));
        assertOperationSuccess(result, EntityTypeString.group);
        Group fetched = result.getAsset().getGroup();
        assertNotNull(fetched);

        assertFalse(fetched.getGroupStartingPageRecycled());
        assertFalse(fetched.getGroupBaseFolderRecycled());

        delete(page.getId(), EntityTypeString.page);
        delete(folder.getId(), EntityTypeString.folder);

        result = client.read(auth, new Identifier(group.getGroupName(), null, EntityTypeString.group, null));
        assertOperationSuccess(result, EntityTypeString.group);
        fetched = result.getAsset().getGroup();
        assertNotNull(fetched);

        assertTrue(fetched.getGroupStartingPageRecycled());
        assertTrue(fetched.getGroupBaseFolderRecycled());
    }
}
