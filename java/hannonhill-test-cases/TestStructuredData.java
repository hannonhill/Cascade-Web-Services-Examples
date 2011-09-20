/*
 * Created on Nov 24, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.commons.util.StringUtil;
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
 * Tests operations associated with structured data nodes, like assigning assets in asset choosers
 * 
 * @author Artur Tomusiak
 * @since 6.8
 */
public class TestStructuredData extends CascadeWebServicesTestCase
{
    String blockId;
    String dataDefinitionWithAssetChooserId;
    String dataDefinitionWithTextId;

    @Override
    protected void onSetUp() throws Exception
    {
        DataDefinition dataDefinitionWithAssetChooser = new DataDefinition();
        dataDefinitionWithAssetChooser.setName("ws_dataDefinition1");
        dataDefinitionWithAssetChooser
                .setXml("<system-data-structure><asset type=\"file\" identifier=\"file-chooser\"/></system-data-structure>");
        dataDefinitionWithAssetChooser.setParentContainerId(RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID);
        Asset asset = new Asset();
        asset.setDataDefinition(dataDefinitionWithAssetChooser);
        dataDefinitionWithAssetChooserId = create(asset, EntityTypeString.datadefinition);

        DataDefinition dataDefinitionWithText = new DataDefinition();
        dataDefinitionWithText.setName("ws_dataDefinition2");
        dataDefinitionWithText.setXml("<system-data-structure><text identifier=\"content\"/></system-data-structure>");
        dataDefinitionWithText.setParentContainerId(RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID);
        asset = new Asset();
        asset.setDataDefinition(dataDefinitionWithText);
        dataDefinitionWithTextId = create(asset, EntityTypeString.datadefinition);
    }

    /**
     * Tests creating of structured data - ensuring that it is possible to create an empty asset chooser by
     * providing a null path and id.
     * Tests reading of structured data - ensuring that it returns null id and path for empty asset chooser
     * 
     * @throws Exception
     */
    public void testCreateAndReadWithNullChooserIdAndPath() throws Exception
    {
        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithAssetChooserId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode fileChooserNode = new StructuredDataNode();
        fileChooserNode.setIdentifier("file-chooser");
        fileChooserNode.setType(StructuredDataType.asset);
        fileChooserNode.setAssetType(StructuredDataAssetType.file);
        fileChooserNode.setFileId(null);
        fileChooserNode.setFilePath(null);
        nodes[0] = fileChooserNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset2 = new Asset();
        asset2.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset2, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        // Ensure the block has a data definition attached
        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertEquals(dataDefinitionWithAssetChooserId, fetchedBlock.getStructuredData().getDefinitionId());

        // Make sure that the empty file chooser has a null id and path
        fileChooserNode = fetchedBlock.getStructuredData().getStructuredDataNodes()[0];
        assertEquals(null, fileChooserNode.getFileId());
        assertEquals(null, fileChooserNode.getFilePath());
    }

    /**
     * Tests editing of structured data - ensuring that it is possible to edit an asset and provide an empty
     * asset chooser by providing null path and id.
     * 
     * @throws Exception
     */
    public void testEditWithNullChooserIdAndPath() throws Exception
    {
        File file = generateFile("ws_file.txt", null);

        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithAssetChooserId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode fileChooserNode = new StructuredDataNode();
        fileChooserNode.setIdentifier("file-chooser");
        fileChooserNode.setType(StructuredDataType.asset);
        fileChooserNode.setAssetType(StructuredDataAssetType.file);
        fileChooserNode.setFileId(file.getId());
        fileChooserNode.setFilePath(null);
        nodes[0] = fileChooserNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset2 = new Asset();
        asset2.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset2, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        StructuredDataNode readNode = result.getAsset().getXhtmlDataDefinitionBlock().getStructuredData().getStructuredDataNodes()[0];

        // Also make sure the file is still assigned
        assertFalse(StringUtil.isEmpty(readNode.getFileId()));
        assertFalse(StringUtil.isEmpty(readNode.getFilePath()));

        // Edit the block and provide null id and path
        readNode.setFileId(null);
        readNode.setFilePath(null);
        assertEquals(client.edit(auth, result.getAsset()).getSuccess(), "true");

        // Read again and make sure it's blank now
        result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        readNode = result.getAsset().getXhtmlDataDefinitionBlock().getStructuredData().getStructuredDataNodes()[0];
        assertEquals(null, readNode.getFileId());
        assertEquals(null, readNode.getFilePath());
    }

    /**
     * Verifies that providing an empty string for path works exactly the same as providing null path in asset
     * structured data node when id provided is null
     * 
     * @throws Exception
     */
    public void testProvideEmptyPath() throws Exception
    {
        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithAssetChooserId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode fileChooserNode = new StructuredDataNode();
        fileChooserNode.setIdentifier("file-chooser");
        fileChooserNode.setType(StructuredDataType.asset);
        fileChooserNode.setAssetType(StructuredDataAssetType.file);
        fileChooserNode.setFileId(null);
        fileChooserNode.setFilePath(""); // providing empty path
        nodes[0] = fileChooserNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset2 = new Asset();
        asset2.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset2, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        // Ensure the block has a data definition attached
        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertEquals(dataDefinitionWithAssetChooserId, fetchedBlock.getStructuredData().getDefinitionId());

        // Make sure that the empty file chooser has a null id and "/" path
        fileChooserNode = fetchedBlock.getStructuredData().getStructuredDataNodes()[0];
        assertEquals(null, fileChooserNode.getFileId());
        assertEquals(null, fileChooserNode.getFilePath());
    }

    /**
     * Tests creating of structured data - ensuring that it is possible to create an empty text node by
     * providing a null text.
     * Tests reading of structured data - ensuring that it returns empty string content for text node
     * 
     * @throws Exception
     */
    public void testCreateAndReadWithNullText() throws Exception
    {
        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithTextId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode textNode = new StructuredDataNode();
        textNode.setIdentifier("content");
        textNode.setType(StructuredDataType.text);
        textNode.setText(null);
        nodes[0] = textNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset2 = new Asset();
        asset2.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset2, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        // Ensure the block has a data definition attached
        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertEquals(dataDefinitionWithTextId, fetchedBlock.getStructuredData().getDefinitionId());

        // Make sure that the node has empty text
        textNode = fetchedBlock.getStructuredData().getStructuredDataNodes()[0];
        assertEquals("", textNode.getText());
    }

    /**
     * Tests editing of structured data - ensuring that it is possible to edit an asset and provide a null
     * text.
     * 
     * @throws Exception
     */
    public void testEditWithNullText() throws Exception
    {
        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithTextId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode textNode = new StructuredDataNode();
        textNode.setIdentifier("content");
        textNode.setType(StructuredDataType.text);
        textNode.setText("Text");
        nodes[0] = textNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset2 = new Asset();
        asset2.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset2, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        StructuredDataNode readNode = result.getAsset().getXhtmlDataDefinitionBlock().getStructuredData().getStructuredDataNodes()[0];

        // Also make sure the file is still assigned
        assertNotNull(readNode.getText());
        assertFalse(readNode.getText().equals(""));

        // Edit the block and provide null id and path
        readNode.setText(null);
        assertEquals(client.edit(auth, result.getAsset()).getSuccess(), "true");

        // Read again and make sure it's empty string now
        result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        readNode = result.getAsset().getXhtmlDataDefinitionBlock().getStructuredData().getStructuredDataNodes()[0];
        assertEquals("", readNode.getText());
    }

    /**
     * Verifies that providing an empty string for text works exactly the same as providing null text in text
     * structured data node
     * 
     * @throws Exception
     */
    public void testProvideEmptyText() throws Exception
    {
        XhtmlDataDefinitionBlock block = new XhtmlDataDefinitionBlock();
        block.setName("ws_block");
        block.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        StructuredData structuredData = new StructuredData();
        structuredData.setDefinitionId(dataDefinitionWithTextId);
        StructuredDataNode[] nodes = new StructuredDataNode[1];
        StructuredDataNode textNode = new StructuredDataNode();
        textNode.setIdentifier("content");
        textNode.setType(StructuredDataType.text);
        textNode.setText("");
        nodes[0] = textNode;
        structuredData.setStructuredDataNodes(nodes);
        block.setStructuredData(structuredData);
        Asset asset = new Asset();
        asset.setXhtmlDataDefinitionBlock(block);
        String blockId = create(asset, EntityTypeString.block_XHTML_DATADEFINITION);

        // Read the block and ensure the block got created
        ReadResult result = client.read(auth, new Identifier(blockId, null, EntityTypeString.block_XHTML_DATADEFINITION, null));
        assertOperationSuccess(result, EntityTypeString.block_XHTML_DATADEFINITION);
        assertNotNull(result.getAsset().getXhtmlDataDefinitionBlock());

        // Ensure the block has a data definition attached
        XhtmlDataDefinitionBlock fetchedBlock = result.getAsset().getXhtmlDataDefinitionBlock();
        assertEquals(dataDefinitionWithTextId, fetchedBlock.getStructuredData().getDefinitionId());

        // Make sure that the node has empty text
        textNode = fetchedBlock.getStructuredData().getStructuredDataNodes()[0];
        assertEquals("", textNode.getText());
    }
}
