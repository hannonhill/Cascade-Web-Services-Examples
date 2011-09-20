/*
 * Created on Aug 5, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.BatchResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.Operation;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.Read;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * Tests batch operation
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.2.1
 */
public class TestBatch extends CascadeWebServicesTestCase
{
    /**
     * Tests batch reading of pages
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        Asset asset = new Asset();
        Page page1 = new Page();
        page1.setName("ws_page");
        page1.setConfigurationSetId(generatePageConfigurationSet("ws_page_test", null).getId());
        page1.setMetadataSetId(generateMetadataSet("ws_page_test", null).getId());
        page1.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        page1.setXhtml("some content");
        asset.setPage(page1);
        String page1Id = create(asset, EntityTypeString.page);

        Asset asset2 = new Asset();
        Page page2 = new Page();
        page2.setName("ws_page_content_type");
        page2.setContentTypeId(generateContentType("ws_content_type", false, null).getId());
        page2.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        page2.setXhtml("some content");
        asset2.setPage(page2);
        String page2Id = create(asset2, EntityTypeString.page);

        Operation[] operations = new Operation[2];

        Operation operation1 = new Operation();
        Read read1 = new Read();
        Identifier identifier1 = new Identifier();
        identifier1.setId(page1Id);
        identifier1.setType(EntityTypeString.page);
        read1.setIdentifier(identifier1);
        operation1.setRead(read1);

        Operation operation2 = new Operation();
        Read read2 = new Read();
        Identifier identifier2 = new Identifier();
        identifier2.setId(page2Id);
        identifier2.setType(EntityTypeString.page);
        read2.setIdentifier(identifier2);
        operation2.setRead(read2);

        operations[0] = operation1;
        operations[1] = operation2;

        BatchResult[] results = client.batch(auth, operations);

        assertNotNull(results);
        assertEquals(2, results.length);
        for (int i = 0; i < results.length; i++)
        {
            BatchResult result = results[i];
            ReadResult readResult = result.getReadResult();
            assertNotNull(readResult);
            Page page = readResult.getAsset().getPage();
            assertNotNull(page);
            if (!page.getId().equals(page1Id) && !page.getId().equals(page2Id))
                fail("A wrong page was returned " + page.getId());
        }
    }
}
