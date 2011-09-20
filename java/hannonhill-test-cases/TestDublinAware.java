/*
 * Created on Jul 9, 2008 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2008 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import java.util.Calendar;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.DublinAwareAsset;
import com.hannonhill.www.ws.ns.AssetOperationService.DynamicMetadataField;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.FieldValue;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.Metadata;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * This class tests the assignment and persistence of metadata set and metadata values
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   5.5
 */
public class TestDublinAware extends CascadeWebServicesTestCase
{
    private String metadataSetId;
    private String metadataSetPath;
    private String newMetadataSetId;
    private String newMetadataSetPath;
    private String metadataSetWithDynamicFieldsId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#setUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        // Prepare the metadata sets
        MetadataSet metadataSet = generateMetadataSet("ws_foldercontained_test", null);
        MetadataSet newMetadataSet = generateMetadataSet("ws_foldercontained_test2", null);
        metadataSetId = metadataSet.getId();
        metadataSetPath = metadataSet.getPath();
        newMetadataSetId = newMetadataSet.getId();
        newMetadataSetPath = newMetadataSet.getPath();

        // Create a metadata set with dynamic fields
        metadataSetWithDynamicFieldsId = generateMetadataSetWithDynamicFields("ws_foldercontained_test3").getId();
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
     * Tests creation of all dublin aware assets with filling out the dynamic metadata values
     * to see if the values will be persisted
     * 
     * @throws Exception
     */
    public void testCreatePersistDynamicMetadata() throws Exception
    {
        testCreatePersistDynamicMetadata(generateFolderObject("ws_dynamic_metadata_folder", null), EntityTypeString.folder);
        testCreatePersistDynamicMetadata(generateFeedBlockObject("ws_dynamic_metadata_feed_block", null), EntityTypeString.block);
        testCreatePersistDynamicMetadata(generateIndexBlockObject("ws_dynamic_metadata_index_block", null), EntityTypeString.block);
        testCreatePersistDynamicMetadata(generateTextBlockObject("ws_dynamic_metadata_text_block", null), EntityTypeString.block);
        testCreatePersistDynamicMetadata(generateDataDefinitionBlockObject("ws_dynamic_metadata_structured_data_block", null), EntityTypeString.block);
        testCreatePersistDynamicMetadata(generateXmlBlockObject("ws_dynamic_metadata_xml_block", null), EntityTypeString.block);
        testCreatePersistDynamicMetadata(generateFileObject("ws_dynamic_metadata_file", null), EntityTypeString.file);
        testCreatePersistDynamicMetadata(
                generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null), EntityTypeString.page);
        testCreatePersistDynamicMetadata(generateSymlinkObject("ws_dynamic_metadata_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests edition of a folder contained asset with filling out the dynamic metadata values 
     * to see if the values will be persisted
     * 
     * @throws Exception
     */
    public void testEditPersistDynamicMetadata() throws Exception
    {
        testEditPersistDynamicMetadata(generateFolder("test_folder", null), EntityTypeString.folder);
        testEditPersistDynamicMetadata(generateFeedBlock("test_feed_block", null), EntityTypeString.block);
        testEditPersistDynamicMetadata(generateIndexBlock("test_index_block", null), EntityTypeString.block);
        testEditPersistDynamicMetadata(generateTextBlock("test_text_block", null), EntityTypeString.block);
        testEditPersistDynamicMetadata(generateStructuredDataBlock("test_structured_data_block", null), EntityTypeString.block);
        testEditPersistDynamicMetadata(generateXmlBlock("test_xml_block", null), EntityTypeString.block);
        testEditPersistDynamicMetadata(generateFile("test_file", null), EntityTypeString.file);
        testEditPersistDynamicMetadata(generatePage("test_page", null), EntityTypeString.page);
        testEditPersistDynamicMetadata(generateSymlink("test_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests creation of a dublin aware asset with filling out the standard metadata values (not dynamic metadata)
     * to see if the values will be persisted
     * 
     * @throws Exception
     */
    public void testCreatePersistRegularMetadata() throws Exception
    {
        testCreatePersistRegularMetadata(generateFolderObject("ws_dynamic_metadata_folder", null), EntityTypeString.folder);
        testCreatePersistRegularMetadata(generateFeedBlockObject("ws_dynamic_metadata_feed_block", null), EntityTypeString.block);
        testCreatePersistRegularMetadata(generateIndexBlockObject("ws_dynamic_metadata_index_block", null), EntityTypeString.block);
        testCreatePersistRegularMetadata(generateTextBlockObject("ws_dynamic_metadata_text_block", null), EntityTypeString.block);
        testCreatePersistRegularMetadata(generateDataDefinitionBlockObject("ws_dynamic_metadata_structured_data_block", null), EntityTypeString.block);
        testCreatePersistRegularMetadata(generateXmlBlockObject("ws_dynamic_metadata_xml_block", null), EntityTypeString.block);
        testCreatePersistRegularMetadata(generateFileObject("ws_dynamic_metadata_file", null), EntityTypeString.file);
        testCreatePersistRegularMetadata(
                generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null), EntityTypeString.page);
        testCreatePersistRegularMetadata(generateSymlinkObject("ws_dynamic_metadata_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests edition of a dublin aware asset with filling out the standard metadata values (not dynamic metadata)
     * to see if the values will be persisted
     * 
     * @throws Exception
     */
    public void testEditPersistRegularMetadata() throws Exception
    {
        testEditPersistRegularMetadata(generateFolder("test_folder", null), EntityTypeString.folder);
        testEditPersistRegularMetadata(generateFeedBlock("test_feed_block", null), EntityTypeString.block);
        testEditPersistRegularMetadata(generateIndexBlock("test_index_block", null), EntityTypeString.block);
        testEditPersistRegularMetadata(generateTextBlock("test_text_block", null), EntityTypeString.block);
        testEditPersistRegularMetadata(generateStructuredDataBlock("test_structured_data_block", null), EntityTypeString.block);
        testEditPersistRegularMetadata(generateXmlBlock("test_xml_block", null), EntityTypeString.block);
        testEditPersistRegularMetadata(generateFile("test_file", null), EntityTypeString.file);
        testEditPersistRegularMetadata(generatePage("test_page", null), EntityTypeString.page);
        testEditPersistRegularMetadata(generateSymlink("test_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests creation of dublin aware assets with assigning metadata set by path,
     * id and without assigning a metadata set.
     * 
     * @throws Exception
     */
    public void testCreateAssignMetadataSet() throws Exception
    {
        testCreateAssignMetadataSet(generateFolderObject("ws_dynamic_metadata_folder", null), EntityTypeString.folder);
        testCreateAssignMetadataSet(generateFeedBlockObject("ws_dynamic_metadata_feed_block", null), EntityTypeString.block);
        testCreateAssignMetadataSet(generateIndexBlockObject("ws_dynamic_metadata_index_block", null), EntityTypeString.block);
        testCreateAssignMetadataSet(generateTextBlockObject("ws_dynamic_metadata_text_block", null), EntityTypeString.block);
        testCreateAssignMetadataSet(generateDataDefinitionBlockObject("ws_dynamic_metadata_structured_data_block", null), EntityTypeString.block);
        testCreateAssignMetadataSet(generateXmlBlockObject("ws_dynamic_metadata_xml_block", null), EntityTypeString.block);
        testCreateAssignMetadataSet(generateFileObject("ws_dynamic_metadata_file", null), EntityTypeString.file);
        testCreateAssignMetadataSet(generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null),
                EntityTypeString.page);
        testCreateAssignMetadataSet(generateSymlinkObject("ws_dynamic_metadata_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests edition of folder contained assets with assigning metadata set by path,
     * id and without assigning a metadata set.
     * 
     * @throws Exception
     */
    public void testEditAssignMetadataSet() throws Exception
    {
        testEditAssignMetadataSet(generateFolderObject("ws_dynamic_metadata_folder", null), EntityTypeString.folder);
        testEditAssignMetadataSet(generateFeedBlockObject("ws_dynamic_metadata_feed_block", null), EntityTypeString.block);
        testEditAssignMetadataSet(generateIndexBlockObject("ws_dynamic_metadata_index_block", null), EntityTypeString.block);
        testEditAssignMetadataSet(generateTextBlockObject("ws_dynamic_metadata_text_block", null), EntityTypeString.block);
        testEditAssignMetadataSet(generateDataDefinitionBlockObject("ws_dynamic_metadata_structured_data_block", null), EntityTypeString.block);
        testEditAssignMetadataSet(generateXmlBlockObject("ws_dynamic_metadata_xml_block", null), EntityTypeString.block);
        testEditAssignMetadataSet(generateFileObject("ws_dynamic_metadata_file", null), EntityTypeString.file);
        testEditAssignMetadataSet(generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null),
                EntityTypeString.page);
        testEditAssignMetadataSet(generateSymlinkObject("ws_dynamic_metadata_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests edit of dublin aware assets with assigning metdata set from another site by path
     * When asset is created a metadata set is assigned from current site and then when asset is edited, a metadata set is assigned from another site 
     * 
     * @throws Exception
     */
    public void testEditAssignMetadataSetDifferentSite() throws Exception
    {
        Site site = generateSite("asite");
        Site site1 = generateSite("ws_anotherSite");
        MetadataSet metadataSet = generateMetadataSet("ws_metadataset", site.getId());

        newMetadataSetId = metadataSet.getId();
        newMetadataSetPath = "asite:" + metadataSet.getPath();

        Page page = generatePageObject("ws_page", generatePageConfigurationSet("ws_page_config_set", site1.getId()).getId(), site1.getId());
        page.setMetadataSetId(newMetadataSetId);

        Asset asset = new Asset();
        asset.setPage(page);
        CreateResult result = client.create(auth, asset);
        assertOperationSuccess(result, EntityTypeString.page);

        Asset readAsset = client.read(auth, new Identifier(result.getCreatedAssetId(), null, EntityTypeString.page, null)).getAsset();
        Page readPage = readAsset.getPage();

        assertEquals(readPage.getMetadataSetId(), newMetadataSetId);
        assertEquals(readPage.getMetadataSetPath(), newMetadataSetPath);

        readPage.setMetadataSetId(null);
        readPage.setMetadataSetPath(newMetadataSetPath);

        client.edit(auth, readAsset);
        Asset editedAsset = client.read(auth, new Identifier(readPage.getId(), null, EntityTypeString.page, null)).getAsset();
        Page editedPage = editedAsset.getPage();

        assertEquals(editedPage.getMetadataSetId(), newMetadataSetId);
        assertEquals(editedPage.getMetadataSetPath(), newMetadataSetPath);
    }

    /**
     * Tests occurence described in CSCD-6242 where an asset had dynamic metadata field without a single value.
     * 
     * @throws Exception
     */
    public void testReadNullDynamicMetadataValue() throws Exception
    {
        testCreatePersisteDynamicMetadataWithoutValues(generateFolderObject("ws_dynamic_metadata_folder", null), EntityTypeString.folder);
        testCreatePersisteDynamicMetadataWithoutValues(generateFeedBlockObject("ws_dynamic_metadata_feed_block", null), EntityTypeString.block);
        testCreatePersisteDynamicMetadataWithoutValues(generateIndexBlockObject("ws_dynamic_metadata_index_block", null), EntityTypeString.block);
        testCreatePersisteDynamicMetadataWithoutValues(generateTextBlockObject("ws_dynamic_metadata_text_block", null), EntityTypeString.block);
        testCreatePersisteDynamicMetadataWithoutValues(generateDataDefinitionBlockObject("ws_dynamic_metadata_structured_data_block", null),
                EntityTypeString.block);
        testCreatePersisteDynamicMetadataWithoutValues(generateXmlBlockObject("ws_dynamic_metadata_xml_block", null), EntityTypeString.block);
        testCreatePersisteDynamicMetadataWithoutValues(generateFileObject("ws_dynamic_metadata_file", null), EntityTypeString.file);
        testCreatePersisteDynamicMetadataWithoutValues(
                generatePageObject("ws_dynamic_metadata_page", generatePageConfigurationSet("configSet", null).getId(), null), EntityTypeString.page);
        testCreatePersisteDynamicMetadataWithoutValues(generateSymlinkObject("ws_dynamic_metadata_symlink", null), EntityTypeString.symlink);
    }

    /**
     * Tests creation and reading of a deublin aware asset without filling out the dynamic metadata values
     * to see if the asset can be created and then read back
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testCreatePersisteDynamicMetadataWithoutValues(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        // Assign metadata set without assigning metadata
        dublinAwareAsset.setMetadataSetId(metadataSetWithDynamicFieldsId);

        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);

        Asset createdAsset = createAsset(asset, type); // No exception should be thrown here and there should be should be operation success when reading

        DublinAwareAsset readDublinAwareAsset = getAppropriateAsset(createdAsset);
        assertNotNull(readDublinAwareAsset); // make sure asset is there
    }

    /**
     * Tests creation of a dublin aware asset with filling out the dynamic metadata values
     * to see if the values will be persisted
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testCreatePersistDynamicMetadata(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        assignDynamicMetadata(dublinAwareAsset);
        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);

        DublinAwareAsset readDublinAwareAsset = getAppropriateAsset(createAsset(asset, type));
        assertNotNull(readDublinAwareAsset.getMetadata().getDynamicFields());
        compareDynamicMetadata(dublinAwareAsset.getMetadata().getDynamicFields(), readDublinAwareAsset.getMetadata().getDynamicFields());
    }

    /**
     * Tests edition of a dublin aware asset with filling out the dynamic metadata values
     * to see if the values will be persisted
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testEditPersistDynamicMetadata(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        // First, prepare to edit
        assignDynamicMetadata(dublinAwareAsset);

        // Then edit the asset
        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Then read it back and make sure it has correct metadata
        ReadResult result = client.read(auth, new Identifier(dublinAwareAsset.getId(), null, type, null));
        assertOperationSuccess(result, type);
        DublinAwareAsset readDublinAwareAsset = getAppropriateAsset(result.getAsset());
        assertNotNull(readDublinAwareAsset.getMetadata().getDynamicFields());
        compareDynamicMetadata(dublinAwareAsset.getMetadata().getDynamicFields(), readDublinAwareAsset.getMetadata().getDynamicFields());
    }

    /**
     * Tests creation of a dublin aware asset with filling out the standard metadata values (not dynamic metadata)
     * to see if the values will be persisted
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testCreatePersistRegularMetadata(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        Metadata metadata = generateMetadataObject();
        dublinAwareAsset.setMetadata(metadata);
        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);

        DublinAwareAsset readDublinAwareAsset = getAppropriateAsset(createAsset(asset, type));
        assertNotNull(readDublinAwareAsset.getMetadata());
        compareMetadata(dublinAwareAsset.getMetadata(), readDublinAwareAsset.getMetadata());
    }

    /**
     * Tests edition of a dublin aware asset with filling out the standard metadata values (not dynamic metadata)
     * to see if the values will be persisted
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testEditPersistRegularMetadata(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        // First, prepare to edit
        dublinAwareAsset.setMetadata(generateMetadataObject());

        // Then edit the asset
        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        // Then read it back and make sure it has correct metadata
        ReadResult result = client.read(auth, new Identifier(dublinAwareAsset.getId(), null, type, null));
        assertOperationSuccess(result, type);
        DublinAwareAsset readDublinAwareAsset = getAppropriateAsset(result.getAsset());
        assertNotNull(readDublinAwareAsset.getMetadata());
        compareMetadata(dublinAwareAsset.getMetadata(), readDublinAwareAsset.getMetadata());
    }

    /**
     * Tests creation of dublin aware assets with assigning metadata set by path,
     * id and without assigning a metadata set.
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testCreateAssignMetadataSet(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        String name = "" + dublinAwareAsset.getName();
        dublinAwareAsset.setName(name + "1");
        DublinAwareAsset asset1 = createDublinAwareWithMetadataSet(dublinAwareAsset, metadataSetPath, null, type);
        dublinAwareAsset.setName(name + "2");
        DublinAwareAsset asset2 = createDublinAwareWithMetadataSet(dublinAwareAsset, null, metadataSetId, type);
        dublinAwareAsset.setName(name + "3");
        DublinAwareAsset asset3 = createDublinAwareWithMetadataSet(dublinAwareAsset, null, null, type);
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset1.getId(), metadataSetId, type);
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset2.getId(), metadataSetId, type);
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset3.getId(), RootContainerIds.METADATASET_ROOT_ID, type);
    }

    /**
     * Tests edition of folder contained assets with assigning metadata set by path,
     * id and without assigning a metadata set.
     * 
     * @param dublinAwareAsset
     * @param type
     * @throws Exception
     */
    private void testEditAssignMetadataSet(DublinAwareAsset dublinAwareAsset, EntityTypeString type) throws Exception
    {
        // Create the assets
        String name = "" + dublinAwareAsset.getName();
        dublinAwareAsset.setName(name + "1");
        DublinAwareAsset asset1 = createDublinAwareWithMetadataSet(dublinAwareAsset, null, metadataSetId, type);
        dublinAwareAsset.setName(name + "2");
        DublinAwareAsset asset2 = createDublinAwareWithMetadataSet(dublinAwareAsset, null, metadataSetId, type);
        dublinAwareAsset.setName(name + "3");
        DublinAwareAsset asset3 = createDublinAwareWithMetadataSet(dublinAwareAsset, null, metadataSetId, type);

        // Edit asset1 and assign a new metadata set by providing the new metadata set's path
        editDublinAwareWithMetadataSet(asset1, newMetadataSetPath, null, type);

        // Edit asset2 and assign a new metadata set by providing the new metadata set's id
        editDublinAwareWithMetadataSet(asset2, null, newMetadataSetId, type);

        // Edit asset3 and do not assign any metadata set
        editDublinAwareWithMetadataSet(asset3, null, null, type);

        // Make sure folders exists and have correct metadata sets assigned
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset1.getId(), newMetadataSetId, type);
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset2.getId(), newMetadataSetId, type);
        makeSureDublinAwareExistsWithCorrectMetadataSet(asset3.getId(), RootContainerIds.METADATASET_ROOT_ID, type);
    }

    /**
     * Compares two dynamic metadata field arrays to make sure that they have same elements. The fields do not have
     * to be in the right order but the field values have to.
     * 
     * @param originalFields
     * @param newFields
     * @throws Exception
     */
    private void compareDynamicMetadata(DynamicMetadataField[] originalFields, DynamicMetadataField[] newFields) throws Exception
    {
        assertEquals(newFields.length, originalFields.length);
        for (int i = 0; i < originalFields.length; i++)
        {
            boolean found = false;
            for (int j = 0; j < newFields.length; j++)
                if (originalFields[i].getName().equals(newFields[j].getName()))
                {
                    found = true;
                    FieldValue[] originalValues = originalFields[i].getFieldValues();
                    FieldValue[] newValues = newFields[j].getFieldValues();

                    assertEquals(newValues.length, originalValues.length);
                    for (int k = 0; k < newValues.length; k++)
                    {
                        boolean found2 = false;
                        for (int l = 0; l < originalValues.length; l++)
                            if (newValues[k].getValue().equals(originalValues[l].getValue()))
                            {
                                found2 = true;
                                assertEquals(newValues[k].getValue(), originalValues[l].getValue());
                            }
                        if (!found2)
                            throw new Exception("The dynamic metadata value " + newValues[k] + " could not be found");
                    }

                    for (int k = 0; k < newValues.length; k++)
                        if (!newValues[k].getValue().equals(originalValues[k].getValue()))
                            throw new Exception("The dynamic metadata values are not in the right order '" + newValues[k].getValue() + "'!='"
                                    + originalValues[k].getValue() + "'");

                }
            if (!found)
                throw new Exception("Dynamic metadata field not found");
        }
    }

    /**
     * Compares two Metadatas
     * 
     * @param metadata1
     * @param metadata2
     */
    private void compareMetadata(Metadata metadata1, Metadata metadata2)
    {
        assertEquals(metadata1.getAuthor(), metadata2.getAuthor());
        assertEquals(metadata1.getDisplayName(), metadata2.getDisplayName());
        compareCalendar(metadata1.getEndDate(), metadata2.getEndDate());
        assertEquals(metadata1.getKeywords(), metadata2.getKeywords());
        assertEquals(metadata1.getMetaDescription(), metadata2.getMetaDescription());
        compareCalendar(metadata1.getReviewDate(), metadata2.getReviewDate());
        compareCalendar(metadata1.getStartDate(), metadata2.getStartDate());
        assertEquals(metadata1.getSummary(), metadata2.getSummary());
        assertEquals(metadata1.getTeaser(), metadata2.getTeaser());
        assertEquals(metadata1.getTitle(), metadata2.getTitle());
    }

    /**
     * Compares the calendar fields
     * 
     * @param calendar1
     * @param calendar2
     */
    private void compareCalendar(Calendar calendar1, Calendar calendar2)
    {
        assertEquals(calendar1.get(Calendar.SECOND), calendar2.get(Calendar.SECOND));
        assertEquals(calendar1.get(Calendar.MINUTE), calendar2.get(Calendar.MINUTE));
        assertEquals(calendar1.get(Calendar.HOUR), calendar2.get(Calendar.HOUR));
        assertEquals(calendar1.get(Calendar.DATE), calendar2.get(Calendar.DATE));
        assertEquals(calendar1.get(Calendar.MONTH), calendar2.get(Calendar.MONTH));
        assertEquals(calendar1.get(Calendar.YEAR), calendar2.get(Calendar.YEAR));
    }

    /**
     * Reads the dublin aware asset to make sure it exists and that is has correct metadata set id.
     * 
     * @param assetId
     * @param metadataSetId
     * @param type
     * @throws Exception
     */
    private void makeSureDublinAwareExistsWithCorrectMetadataSet(String assetId, String metadataSetId, EntityTypeString type) throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(assetId, null, type, null));
        assertOperationSuccess(result, type);
        DublinAwareAsset dublinAwareAsset = getAppropriateAsset(result.getAsset());
        assertEquals(dublinAwareAsset.getMetadataSetId(), metadataSetId);
    }

    /**
     * Creates a new dublin aware asset and specifies the provided metadataSetPath and metadataSetId. Also, makes sure
     * that the asset got created.
     * 
     * @param dublinAwareAsset
     * @param metadataSetPath
     * @param metadataSetId
     * @return
     * @throws Exception
     */
    private DublinAwareAsset createDublinAwareWithMetadataSet(DublinAwareAsset dublinAwareAsset, String metadataSetPath, String metadataSetId,
            EntityTypeString type) throws Exception
    {
        Asset asset = new Asset();
        dublinAwareAsset.setMetadataSetPath(metadataSetPath);
        dublinAwareAsset.setMetadataSetId(metadataSetId);
        setAppropriateAsset(asset, dublinAwareAsset);
        DublinAwareAsset readAsset = getAppropriateAsset(createAsset(asset, type));
        assertNotNull(readAsset);
        return readAsset;
    }

    /**
     * Edits the asset by assigning the metadata set by path and by id
     * 
     * @param dublinAwareAsset
     * @param metadataSetPath
     * @param metadataSetId
     * @param type
     * @throws Exception
     */
    private void editDublinAwareWithMetadataSet(DublinAwareAsset dublinAwareAsset, String metadataSetPath, String metadataSetId, EntityTypeString type)
            throws Exception
    {
        // First, prepare to edit
        dublinAwareAsset.setMetadataSetPath(metadataSetPath);
        dublinAwareAsset.setMetadataSetId(metadataSetId);

        // Then edit the asset
        Asset asset = new Asset();
        setAppropriateAsset(asset, dublinAwareAsset);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");
    }

    /**
     * Assigns the dynamic metadata and metadata set to the dublin aware asset
     * 
     * @param dublinAwareAsset
     */
    private void assignDynamicMetadata(DublinAwareAsset dublinAwareAsset)
    {
        dublinAwareAsset.setMetadataSetId(metadataSetWithDynamicFieldsId);
        Metadata metadata = new Metadata();
        metadata.setDynamicFields(generateDynamicMetadataFieldsObject());
        dublinAwareAsset.setMetadata(metadata);
    }
}
