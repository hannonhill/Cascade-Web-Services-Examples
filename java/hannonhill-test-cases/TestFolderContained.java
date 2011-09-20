/*
 * Created on Jun 26, 2008 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2008 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.Authentication;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.User;

/**
 * Test for performing web services operations on folder contained assets.
 * 
 * @author Artur Tomusiak
 * @version $Id$
 * @since 5.5
 */
public class TestFolderContained extends CascadeWebServicesTestCase
{
    private String parentFolderId;
    private String parentFolderPath;
    private String expirationFolderId;
    private String expirationFolderPath;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#setUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        parentFolderId = createFolderWithParentFolder("parentFolder", null, RootContainerIds.FOLDER_ROOT_ID);
        parentFolderPath = "/parentFolder";
        expirationFolderId = createFolderWithParentFolder("expirationFolder", null, RootContainerIds.FOLDER_ROOT_ID);
        expirationFolderPath = "/expirationFolder";
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // only items that are not created with generate*() methods need to be cleaned up here
        super.tearDown();
    }

    /**
     * Test creation of folder contained assets with assigning parent folder by path and by id
     * 
     * @throws Exception
     */
    public void testCreateAssignParentFolder() throws Exception
    {
        // Create a folder with providing the parent folder path
        String folder1Id = createFolderWithParentFolder("childFolder1", parentFolderPath, null);

        // Create a folder with providing the parent folder id
        String folder2Id = createFolderWithParentFolder("childFolder2", null, parentFolderId);

        // Make sure these folders exist and that they have corrent parent folders assigned
        makeSureFolderExistsWithCorrentParentFolder(folder1Id, parentFolderId);
        makeSureFolderExistsWithCorrentParentFolder(folder2Id, parentFolderId);
    }

    /**
     * Tests creation of folder contained assets with assigning expiration folder set by path,
     * id and without assigning an expiration folder.
     * 
     * @throws Exception
     */
    public void testCreateAssignExpirationFolder() throws Exception
    {
        // Create a folder with providing the expiration folder path
        String folder1Id = createFolderWithExpirationFolder("folder1", expirationFolderPath, null);

        // Create a folder with providing the expiration folder id
        String folder2Id = createFolderWithExpirationFolder("folder2", null, expirationFolderId);

        // Create a folder without providing the expiration folder
        String folder3Id = createFolderWithExpirationFolder("folder3", null, null);

        // Make sure folders exists and have correct expiration folder assigned
        makeSureFolderExistsWithCorrentExpirationFolder(folder1Id, expirationFolderId);
        makeSureFolderExistsWithCorrentExpirationFolder(folder2Id, expirationFolderId);
        makeSureFolderExistsWithCorrentExpirationFolder(folder3Id, null);
    }

    /**
     * Tests edition of folder contained assets with assigning the parent folder by path and by id
     * 
     * @throws Exception
     */
    public void testMoveAssignParentFolderPath() throws Exception
    {
        String folder1Id = createFolderWithParentFolder("tfolder1", null, RootContainerIds.FOLDER_ROOT_ID);
        String folder2Id = createFolderWithParentFolder("tfolder1", null, RootContainerIds.FOLDER_ROOT_ID);

        // move by path
        MoveParameters mp = new MoveParameters();
        mp.setDestinationContainerIdentifier(new Identifier(null, new Path(parentFolderPath, null, "Global"), EntityTypeString.folder, false));
        client.move(auth, new Identifier(folder1Id, null, EntityTypeString.folder, false), mp, null);

        // move by id
        mp.setDestinationContainerIdentifier(new Identifier(parentFolderId, null, EntityTypeString.folder, null));
        client.move(auth, new Identifier(folder2Id, null, EntityTypeString.folder, false), mp, null);

        // Make sure these folders exist and that they have corrent parent folders assigned
        makeSureFolderExistsWithCorrentParentFolder(folder1Id, parentFolderId);
        makeSureFolderExistsWithCorrentParentFolder(folder2Id, parentFolderId);
    }

    /**
     * Tests edition of folder contained assets with assigning the expiration folder by path and by id
     * and not assigning any expiration folder
     * 
     * @throws Exception
     */
    public void testEditAssignExpirationFolder() throws Exception
    {
        String folder1Id = createFolderWithExpirationFolder("tfolder1", null, null);
        String folder2Id = createFolderWithExpirationFolder("tfolder2", null, null);
        String folder3Id = createFolderWithExpirationFolder("tfolder3", null, null);

        // Edit folder1 and assign new expiration folder by providing the new expiration folder's path
        editFolderWithExpirationFolder(folder1Id, expirationFolderPath, null);

        // Edit folder2 and assign new expiration folder by providing the new expiration folder's id
        editFolderWithExpirationFolder(folder2Id, null, expirationFolderId);

        // Edit folder3 without assigning expiration folder
        editFolderWithExpirationFolder(folder3Id, null, null);

        // Make sure these folders exist and that they have correct expiration folders assigned
        makeSureFolderExistsWithCorrentExpirationFolder(folder1Id, expirationFolderId);
        makeSureFolderExistsWithCorrentExpirationFolder(folder2Id, expirationFolderId);
        makeSureFolderExistsWithCorrentExpirationFolder(folder3Id, null);
    }

    /**
     * Test to make sure system metadata can be accessed when reading an asset.
     */
    public void testSystemMetadata() throws Exception
    {
        long now = System.currentTimeMillis();

        User user = generateAdminUser("ws_admin");
        Folder folder = generateFolder("ws_test_folder", null);

        assertEquals("admin", folder.getCreatedBy());
        assertTrue(now < folder.getCreatedDate().getTimeInMillis());
        assertEquals("admin", folder.getLastModifiedBy());
        assertTrue(now < folder.getLastModifiedDate().getTimeInMillis());

        Asset asset = new Asset();
        asset.setFolder(folder);

        Authentication auth = new Authentication();
        auth.setUsername(user.getUsername());
        auth.setPassword(user.getUsername());
        OperationResult result = client.edit(auth, asset);
        assertOperationSuccess(result, EntityTypeString.folder);

        ReadResult readResult = client.read(auth, new Identifier(folder.getId(), null, EntityTypeString.folder, false));
        asset = readResult.getAsset();
        Folder readFolder = asset.getFolder();

        long nowNew = System.currentTimeMillis();

        assertEquals("ws_admin", readFolder.getLastModifiedBy());
        assertTrue(readFolder.getLastModifiedDate().getTimeInMillis() < nowNew);
        assertTrue(now < readFolder.getLastModifiedDate().getTimeInMillis());
    }

    /**
     * Reads the folder to make sure it exists and that it has correct parent folder id.
     * 
     * @param folderId
     * @param parentFolderId
     * @throws Exception
     */
    private void makeSureFolderExistsWithCorrentParentFolder(String folderId, String parentFolderId) throws Exception
    {
        Folder resultFolder = readFolder(folderId);
        assertEquals(resultFolder.getParentFolderId(), parentFolderId);
    }

    /**
     * Reads the folder to make sure it exists and that it has correct expiration folder id.
     * 
     * @param folderId
     * @param parentFolderId
     * @throws Exception
     */
    private void makeSureFolderExistsWithCorrentExpirationFolder(String folderId, String expirationFolderId) throws Exception
    {
        Folder resultFolder = readFolder(folderId);
        assertEquals(resultFolder.getExpirationFolderId(), expirationFolderId);
    }

    /**
     * Creates a new folder with specified parentFolderPath and parentFolderId. Also, makes sure the folder
     * got created.
     * 
     * @param name
     * @param parentFolderPath
     * @param parentFolderId
     * @return
     * @throws Exception
     */
    private String createFolderWithParentFolder(String name, String parentFolderPath, String parentFolderId) throws Exception
    {
        Asset asset = new Asset();
        Folder folder = new Folder();
        folder.setParentFolderId(parentFolderId);
        folder.setParentFolderPath(parentFolderPath);
        folder.setName(name);
        asset.setFolder(folder);
        String folderId = create(asset, EntityTypeString.folder);
        assertNotNull(folderId);
        return folderId;
    }

    /**
     * Creates a new folder with specified expirationFolderPath and expirationFolderId. Also, makes sure the
     * folder got created.
     * 
     * @param name
     * @param parentFolderPath
     * @param parentFolderId
     * @return
     * @throws Exception
     */
    private String createFolderWithExpirationFolder(String name, String expirationFolderPath, String expirationFolderId) throws Exception
    {
        Asset asset = new Asset();
        Folder folder = new Folder();
        folder.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        folder.setExpirationFolderId(expirationFolderId);
        folder.setExpirationFolderPath(expirationFolderPath);
        folder.setName(name);
        asset.setFolder(folder);
        String folderId = create(asset, EntityTypeString.folder);
        assertNotNull(folderId);
        return folderId;
    }

    /**
     * Edits the folder by changing its expirationFolderPath and expirationFolderId. Also, makes sure that the
     * operation result success is "true"
     * 
     * @param folderId
     * @param expirationFolderPath
     * @param expirationFolderId
     * @throws Exception
     */
    private void editFolderWithExpirationFolder(String folderId, String expirationFolderPath, String expirationFolderId) throws Exception
    {
        Folder folder = readFolder(folderId);
        folder.setExpirationFolderId(expirationFolderId);
        folder.setExpirationFolderPath(expirationFolderPath);
        Asset resultAsset = new Asset();
        resultAsset.setFolder(folder);
        assertEquals(client.edit(auth, resultAsset).getSuccess(), "true");
    }

    /**
     * Reads a folder and makes sure the read folder exists
     * 
     * @param folderId
     * @return
     * @throws Exception
     */
    private Folder readFolder(String folderId) throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(folderId, null, EntityTypeString.folder, null));
        assertEquals(result.getSuccess(), "true");
        Folder resultFolder = result.getAsset().getFolder();
        assertNotNull(resultFolder);
        return resultFolder;
    }
}
