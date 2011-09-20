/*
 * Created on Mar 2, 2010 by Mike Strauch
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypeContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MoveParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * Test for moving/renaming assets.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   6.7
 */
public class TestMove extends CascadeWebServicesTestCase
{
    File fileToMove;
    ContentType cTypeToMove;
    Site siteToTest;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        siteToTest = generateSite("siteToTest");
        fileToMove = generateFile("ws_test_file", siteToTest.getId());
        cTypeToMove = generateContentType("ws_ctype_to_move", false, siteToTest);
    }

    /**
     * Web Services test for moving a home area asset.
     * 
     * @throws Exception
     */
    public void testMoveFile() throws Exception
    {
        /* test renaming in same site */
        MoveParameters moveParameters = new MoveParameters();
        moveParameters.setDestinationContainerIdentifier(null);
        moveParameters.setNewName("ws_test_file2");
        moveParameters.setDoWorkflow(false);

        Identifier fileId = new Identifier(fileToMove.getId(), null, EntityTypeString.file, false);
        OperationResult result = client.move(auth, fileId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.file);

        ReadResult readResult = client.read(auth, fileId);
        File readFile = readResult.getAsset().getFile();

        assertEquals(readFile.getName(), "ws_test_file2");

        /* test moving into same site */
        Folder toMoveIntoSameSite = generateFolder("ws_to_move_into", siteToTest.getId());
        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoSameSite.getId(), null, EntityTypeString.folder, false));
        moveParameters.setNewName(null);
        result = client.move(auth, fileId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.file);

        readResult = client.read(auth, fileId);
        readFile = readResult.getAsset().getFile();

        assertEquals(readFile.getParentFolderId(), toMoveIntoSameSite.getId());
        assertEquals(readFile.getSiteId(), toMoveIntoSameSite.getSiteId());

        /* test moving into different site */
        Site anotherSite = generateSite("ws_another_site");
        Folder toMoveIntoDifferentSite = generateFolder("ws_to_move_into_different_site", anotherSite.getId());

        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoDifferentSite.getId(), null, EntityTypeString.folder, false));
        result = client.move(auth, fileId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.file);

        readResult = client.read(auth, fileId);
        readFile = readResult.getAsset().getFile();

        assertEquals(readFile.getParentFolderId(), toMoveIntoDifferentSite.getId());
        assertEquals(readFile.getSiteId(), toMoveIntoDifferentSite.getSiteId());

        /* move back to the original site and rename */
        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoSameSite.getId(), null, EntityTypeString.folder, false));
        moveParameters.setNewName("ws_test_file_rename_again");
        result = client.move(auth, fileId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.file);

        readResult = client.read(auth, fileId);
        readFile = readResult.getAsset().getFile();

        assertEquals(readFile.getName(), "ws_test_file_rename_again");
        assertEquals(readFile.getSiteId(), toMoveIntoSameSite.getSiteId());
        assertEquals(readFile.getParentFolderId(), toMoveIntoSameSite.getId());
    }

    /**
     * Web Services test for moving an admin area asset.
     * @throws Exception
     */
    public void testMoveContentType() throws Exception
    {
        /* test renaming in same site */
        MoveParameters moveParameters = new MoveParameters();
        moveParameters.setDestinationContainerIdentifier(null);
        moveParameters.setNewName("ws_test_contenttype2");
        moveParameters.setDoWorkflow(false);

        Identifier cTypeId = new Identifier(cTypeToMove.getId(), null, EntityTypeString.contenttype, false);
        OperationResult result = client.move(auth, cTypeId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.contenttype);

        ReadResult readResult = client.read(auth, cTypeId);
        ContentType readContentType = readResult.getAsset().getContentType();

        assertEquals(readContentType.getName(), "ws_test_contenttype2");

        /* test moving into same site */
        ContentTypeContainer toMoveIntoSameSite = generateContentTypeContainer("ws_to_move_into", siteToTest.getId());
        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoSameSite.getId(), null, EntityTypeString.contenttypecontainer,
                false));
        moveParameters.setNewName(null);
        result = client.move(auth, cTypeId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.contenttype);

        readResult = client.read(auth, cTypeId);
        readContentType = readResult.getAsset().getContentType();

        assertEquals(readContentType.getParentContainerId(), toMoveIntoSameSite.getId());
        assertEquals(readContentType.getSiteId(), toMoveIntoSameSite.getSiteId());

        /* test moving into different site */
        Site anotherSite = generateSite("ws_another_site2");
        ContentTypeContainer toMoveIntoDifferentSite = generateContentTypeContainer("ws_to_move_into_different_site", anotherSite.getId());

        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoDifferentSite.getId(), null, EntityTypeString.contenttypecontainer,
                false));
        result = client.move(auth, cTypeId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.contenttype);

        readResult = client.read(auth, cTypeId);
        readContentType = readResult.getAsset().getContentType();

        assertEquals(readContentType.getParentContainerId(), toMoveIntoDifferentSite.getId());
        assertEquals(readContentType.getSiteId(), toMoveIntoDifferentSite.getSiteId());

        /* move back to the original site and rename */
        moveParameters.setDestinationContainerIdentifier(new Identifier(toMoveIntoSameSite.getId(), null, EntityTypeString.contenttypecontainer,
                false));
        moveParameters.setNewName("ws_test_contenttype_rename_again");
        result = client.move(auth, cTypeId, moveParameters, null);
        assertOperationSuccess(result, EntityTypeString.contenttype);

        readResult = client.read(auth, cTypeId);
        readContentType = readResult.getAsset().getContentType();

        assertEquals(readContentType.getName(), "ws_test_contenttype_rename_again");
        assertEquals(readContentType.getSiteId(), toMoveIntoSameSite.getSiteId());
        assertEquals(readContentType.getParentContainerId(), toMoveIntoSameSite.getId());

        delete(readContentType.getId(), EntityTypeString.contenttype);
    }
}
