/*
 * Created on Aug 21, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.AccessRightsInformation;
import com.hannonhill.www.ws.ns.AssetOperationService.AclEntry;
import com.hannonhill.www.ws.ns.AssetOperationService.AclEntryLevel;
import com.hannonhill.www.ws.ns.AssetOperationService.AclEntryType;
import com.hannonhill.www.ws.ns.AssetOperationService.AllLevel;
import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadAccessRightsResult;

/**
 * Tests Access Rights and Permissions
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.2.1
 */
public class TestAccessRights extends CascadeWebServicesTestCase
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
        folder.setName("ws_folder_ar");
        folder.setMetadataSetId(generateMetadataSet("ws_folder_set", null).getId());
        folder.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);

        Asset asset = new Asset();
        asset.setFolder(folder);
        folderId = create(asset, EntityTypeString.folder);
    }

    /**
     * Tests reading access rights
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        Identifier identifier = new Identifier();
        identifier.setId(folderId);
        identifier.setType(EntityTypeString.folder);

        ReadAccessRightsResult readResult = client.readAccessRights(auth, identifier);
        assertOperationSuccess(readResult, null);

        AccessRightsInformation readAccessRightsInformation = readResult.getAccessRightsInformation();
        assertNotNull(readAccessRightsInformation.getAllLevel());
        AclEntry[] readAclEntries = readAccessRightsInformation.getAclEntries();
        assertNotNull(readAclEntries);
        assertEquals(0, readAclEntries.length);

        // you must have a user called "admin" in the system
        AccessRightsInformation accessRightsInformation = new AccessRightsInformation();
        accessRightsInformation.setAllLevel(AllLevel.none);
        accessRightsInformation.setIdentifier(identifier);

        AclEntry[] aclEntries = new AclEntry[1];
        AclEntry aclEntry = new AclEntry();
        aclEntry.setLevel(AclEntryLevel.read);
        aclEntry.setName("admin");
        aclEntry.setType(AclEntryType.user);
        aclEntries[0] = aclEntry;
        accessRightsInformation.setAclEntries(aclEntries);

        OperationResult result = client.editAccessRights(auth, accessRightsInformation, false);
        assertOperationSuccess(result, null);

        readResult = client.readAccessRights(auth, identifier);
        assertOperationSuccess(readResult, null);

        readAccessRightsInformation = readResult.getAccessRightsInformation();
        assertEquals(AllLevel.none, readAccessRightsInformation.getAllLevel());
        readAclEntries = readAccessRightsInformation.getAclEntries();
        assertNotNull(readAclEntries);
        assertEquals(1, readAclEntries.length);
        assertEquals("admin", readAclEntries[0].getName());
        assertEquals(AclEntryType.user, readAclEntries[0].getType());
        assertEquals(AclEntryLevel.read, readAclEntries[0].getLevel());
    }
}
