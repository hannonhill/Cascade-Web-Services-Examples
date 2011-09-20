/*
 * Created on Jan 18, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.DataDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredData;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataAssetType;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataNode;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataType;
import com.hannonhill.www.ws.ns.AssetOperationService.XhtmlDataDefinitionBlock;

/**
 * Tests the XHTML structured data block
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.7
 */
public class TestXhtmlStructuredDataBlock extends CascadeWebServicesTestCase
{
    /**
     * Tests creating a structured data block 
     */
    public void testCreateSDBlock() throws Exception
    {
        String sdBlockId = "";
        Asset asset = new Asset();
        StructuredDataNode node = new StructuredDataNode();
        node.setText("text");
        node.setType(StructuredDataType.text);
        node.setIdentifier("identifier");

        StructuredData sd = new StructuredData();
        sd.setStructuredDataNodes(new StructuredDataNode[]
        {
            node
        });

        DataDefinition sdd = generateDataDefinition("sdd", null);
        sd.setDefinitionId(sdd.getId());

        XhtmlDataDefinitionBlock sdBlock = new XhtmlDataDefinitionBlock();
        sdBlock.setName("ws_block_sd");
        sdBlock.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        sdBlock.setStructuredData(sd);

        asset.setXhtmlDataDefinitionBlock(sdBlock);
        sdBlockId = create(asset, EntityTypeString.block_XHTML_DATADEFINITION);

        ReadResult result = client.read(auth, new Identifier(sdBlockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertNotNull(fetchedBlock);
        assertNotNull(fetchedBlock.getStructuredData());
        assertNotNull(fetchedBlock.getStructuredData().getStructuredDataNodes());

        StructuredDataNode[] nodes = fetchedBlock.getStructuredData().getStructuredDataNodes();
        assertEquals(nodes.length, 1);
        assertEquals(nodes[0].getType(), node.getType());
        assertEquals(nodes[0].getText(), node.getText());
        assertEquals(nodes[0].getIdentifier(), node.getIdentifier());

        assertEquals(sdBlock.getName(), fetchedBlock.getName());
        assertEquals(sdBlock.getStructuredData().getDefinitionId(), fetchedBlock.getStructuredData().getDefinitionId());
        assertEquals(sdBlock.getParentFolderId(), fetchedBlock.getParentFolderId());
    }
    
    /**
     * Tests than an structured data block can be edited without explicitly nulling out the recycled element (CSCD - 6968
     * @throws Exception
     */
    public void testEditSDBlock () throws Exception
    {
        String sdBlockId = "";
        Asset asset = new Asset();
        StructuredDataNode node = new StructuredDataNode();
        node.setText("text");
        node.setType(StructuredDataType.text);
        node.setIdentifier("identifier");

        StructuredData sd = new StructuredData();
        sd.setStructuredDataNodes(new StructuredDataNode[]
        {
            node
        });

        DataDefinition sdd = generateDataDefinition("sdd", null);
        sd.setDefinitionId(sdd.getId());

        XhtmlDataDefinitionBlock sdBlock = new XhtmlDataDefinitionBlock();
        sdBlock.setName("ws_block_sd");
        sdBlock.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        sdBlock.setStructuredData(sd);

        asset.setXhtmlDataDefinitionBlock(sdBlock);
        sdBlockId = create(asset, EntityTypeString.block_XHTML_DATADEFINITION);

        ReadResult result = client.read(auth, new Identifier(sdBlockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        Asset editAsset = result.getAsset();
        XhtmlDataDefinitionBlock fetchedBlock = editAsset.getXhtmlDataDefinitionBlock();
        assertNotNull(fetchedBlock);
        assertNotNull(fetchedBlock.getStructuredData());
        assertNotNull(fetchedBlock.getStructuredData().getStructuredDataNodes());

        StructuredDataNode[] nodes = fetchedBlock.getStructuredData().getStructuredDataNodes();
        assertEquals(nodes.length, 1);
        assertEquals(nodes[0].getType(), node.getType());
        assertEquals(nodes[0].getText(), node.getText());
        assertEquals(nodes[0].getIdentifier(), node.getIdentifier());
        assertFalse(nodes[0].getRecycled());
        
        // now, edit the node and return it without nulling out the recycled flag
        nodes[0].setText("Testing editing");
        client.edit(auth, editAsset);
        
        ReadResult refetchResult = client.read(auth, new Identifier(sdBlockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        Asset refetch = refetchResult.getAsset();
        assertEquals(nodes[0].getText(), editAsset.getXhtmlDataDefinitionBlock().getStructuredData().getStructuredDataNodes()[0].getText());
    }

    /**
     * Tests reading and creating of structured data block that contains structured data with an asset chooser and the selected asset is recycled.
     * 
     * @throws Exception
     */
    public void testSelectRecycled() throws Exception
    {
        // Create a file
        String fileId = createFile("ws_file.txt");

        // then delete it so it's recycled
        delete(fileId, EntityTypeString.file);

        // now create a block and choose the recycled file
        String sdBlockId = createBlock("ws_block_sd", fileId);

        // now read the block and ensure that is says that the selected asset is recycled
        ReadResult result = client.read(auth, new Identifier(sdBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertTrue(fetchedBlock.getStructuredData().getStructuredDataNodes()[0].getRecycled());

        // Now do the same without deleting to make sure that the recycled value is false
        fileId = createFile("ws_file2.txt");
        sdBlockId = createBlock("ws_block_sd2", fileId);
        result = client.read(auth, new Identifier(sdBlockId, null, EntityTypeString.block, null));
        assertOperationSuccess(result, EntityTypeString.block);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());
        fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertFalse(fetchedBlock.getStructuredData().getStructuredDataNodes()[0].getRecycled());
    }

    /**
     * Creates a file
     * 
     * @param name
     * @return file id
     * @throws Exception
     */
    private String createFile(String name) throws Exception
    {
        File file = new File();
        file.setName(name);
        file.setMetadataSetId(generateMetadataSet("ws_file_metadataset", null).getId());
        file.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        file.setText("sample text");

        Asset asset = new Asset();
        asset.setFile(file);

        return create(asset, EntityTypeString.file);
    }

    /**
     * Creates a structured data block and assign the file to the structured data asset chooser
     * 
     * @param name
     * @param fileId
     * @return block id
     * @throws Exception
     */
    private String createBlock(String name, String fileId) throws Exception
    {
        Asset asset = new Asset();
        StructuredDataNode node = new StructuredDataNode();
        node.setFileId(fileId);
        node.setType(StructuredDataType.asset);
        node.setAssetType(StructuredDataAssetType.file);
        node.setIdentifier("identifier");
        StructuredData sd = new StructuredData();
        sd.setStructuredDataNodes(new StructuredDataNode[]
        {
            node
        });
        DataDefinition sdd = generateDataDefinition("sdd", null,
                "<system-data-structure><asset type=\"file\" identifier=\"identifier\"/></system-data-structure>");
        sd.setDefinitionId(sdd.getId());

        XhtmlDataDefinitionBlock sdBlock = new XhtmlDataDefinitionBlock();
        sdBlock.setName(name);
        sdBlock.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        sdBlock.setStructuredData(sd);

        asset.setXhtmlDataDefinitionBlock(sdBlock);
        return create(asset, EntityTypeString.block_XHTML_DATADEFINITION);
    }
}
