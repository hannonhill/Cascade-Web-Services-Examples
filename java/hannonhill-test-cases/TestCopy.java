/*
 * Created on Jun 4, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CopyParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Tests the copy operation
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.2
 */
public class TestCopy extends CascadeWebServicesTestCase
{
    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
    }

    /**
     * Tests copying of a folder and makes sure that the relative links between assets in that folder get updated.
     * 
     * @throws Exception
     */
    public void testCopyFolder() throws Exception
    {
        Asset asset;
        ReadResult result;

        Folder folder = generateFolder("testfolder", null);
        PageConfigurationSet pageConfigurationSet = generatePageConfigurationSet("testconfigset", null);
        Folder destinationFolder = generateFolder("destinationfolder", null);

        Page page1 = generatePageObject("page1", pageConfigurationSet.getId(), null);
        page1.setParentFolderId(folder.getId());
        asset = new Asset();
        asset.setPage(page1);
        String page1Id = create(asset, EntityTypeString.page);
        result = client.read(auth, new Identifier(page1Id, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        page1 = result.getAsset().getPage();

        Page page2 = generatePageObject("page2", pageConfigurationSet.getId(), null);
        page2.setXhtml("<a href=\"/" + page1.getPath() + "\">test</a>");
        page2.setParentFolderId(folder.getId());
        asset = new Asset();
        asset.setPage(page2);
        create(asset, EntityTypeString.page);

        Identifier identifier = new Identifier();
        identifier.setId(folder.getId());
        identifier.setType(EntityTypeString.folder);

        Identifier destinationContainerIdentifier = new Identifier();
        destinationContainerIdentifier.setId(destinationFolder.getId());
        destinationContainerIdentifier.setType(EntityTypeString.folder);

        CopyParameters copyParameters = new CopyParameters();
        copyParameters.setDestinationContainerIdentifier(destinationContainerIdentifier);
        copyParameters.setDoWorkflow(false);
        copyParameters.setNewName("newtestfolder");

        OperationResult operationResult = client.copy(auth, identifier, copyParameters, null);

        assertOperationSuccess(operationResult, EntityTypeString.folder);

        String newPage2Path = "/" + destinationFolder.getPath() + "/newtestfolder/" + page2.getName();

        result = client.read(auth, new Identifier(null, new Path(newPage2Path, "", null), EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);
        Page newPage2 = result.getAsset().getPage();
        assertEquals("<a href=\"/" + destinationFolder.getPath() + "/newtestfolder/" + page1.getName() + "\">test</a>", newPage2.getXhtml());
    }
}
