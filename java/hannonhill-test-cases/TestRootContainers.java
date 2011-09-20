/*
 * Created on Sep 18, 2008 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactory;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.ContaineredAsset;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentTypeContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.DataDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.DataDefinitionContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.FileSystemTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSet;
import com.hannonhill.www.ws.ns.AssetOperationService.PageConfigurationSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.PublishSet;
import com.hannonhill.www.ws.ns.AssetOperationService.PublishSetContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Target;
import com.hannonhill.www.ws.ns.AssetOperationService.TransportContainer;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinition;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinitionContainer;

/**
 * Tests reading root system area containers
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   5.7
 */
public class TestRootContainers extends CascadeWebServicesTestCase
{
    /**
     * Reads root asset factory container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootAssetFactoryContainer() throws Exception
    {
        AssetFactory af = generateAssetFactory("assetFactory", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.ASSET_FACTORY_CONTAINER_ROOT_ID, null,
                EntityTypeString.assetfactorycontainer, null));

        assertOperationSuccess(result, EntityTypeString.assetfactorycontainer);
        AssetFactoryContainer afc = result.getAsset().getAssetFactoryContainer();
        checkReturnedParameters(afc, RootContainerIds.ASSET_FACTORY_CONTAINER_ROOT_ID, af.getId(), afc.getChildren());

        Path path = new Path("/", "", null);
        ReadResult result2 = client.read(auth, new Identifier(null, path, EntityTypeString.assetfactorycontainer, null));

        assertOperationSuccess(result2, EntityTypeString.assetfactorycontainer);
        AssetFactoryContainer afc2 = result2.getAsset().getAssetFactoryContainer();
        checkReturnedParameters(afc2, RootContainerIds.ASSET_FACTORY_CONTAINER_ROOT_ID, af.getId(), afc2.getChildren());
    }

    /**
     * Reads root page configuration set container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootPageConfigurationSetContainer() throws Exception
    {
        PageConfigurationSet pcs = generatePageConfigurationSet("pageConfigurationSet", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID, null,
                EntityTypeString.pageconfigurationsetcontainer, null));

        assertOperationSuccess(result, EntityTypeString.pageconfigurationsetcontainer);
        PageConfigurationSetContainer pcsc = result.getAsset().getPageConfigurationSetContainer();
        checkReturnedParameters(pcsc, RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID, pcs.getId(), pcsc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID, null,
                EntityTypeString.pageconfigurationsetcontainer, null));

        assertOperationSuccess(result2, EntityTypeString.pageconfigurationsetcontainer);
        PageConfigurationSetContainer pcsc2 = result2.getAsset().getPageConfigurationSetContainer();
        checkReturnedParameters(pcsc2, RootContainerIds.PAGE_CONFIG_SET_CONT_ROOT_ID, pcs.getId(), pcsc2.getChildren());
    }

    /**
     * Reads root content type container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootContentTypeContainer() throws Exception
    {
        ContentType ct = generateContentType("contentType", false, null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID, null,
                EntityTypeString.contenttypecontainer, null));

        assertOperationSuccess(result, EntityTypeString.contenttypecontainer);
        ContentTypeContainer ctc = result.getAsset().getContentTypeContainer();
        checkReturnedParameters(ctc, RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID, ct.getId(), ctc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID, null,
                EntityTypeString.contenttypecontainer, null));

        assertOperationSuccess(result2, EntityTypeString.contenttypecontainer);
        ContentTypeContainer ctc2 = result2.getAsset().getContentTypeContainer();
        checkReturnedParameters(ctc2, RootContainerIds.CONTENTTYPE_CONTAINER_ROOT_ID, ct.getId(), ctc2.getChildren());
    }

    /**
     * Reads root structured data definition container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootStructuredDataDefinitionContainer() throws Exception
    {
        DataDefinition sdd = generateDataDefinition("structuredDataDefinition", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID, null,
                EntityTypeString.datadefinitioncontainer, null));

        assertOperationSuccess(result, EntityTypeString.datadefinitioncontainer);
        DataDefinitionContainer sddc = result.getAsset().getDataDefinitionContainer();
        checkReturnedParameters(sddc, RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID, sdd.getId(), sddc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID, null,
                EntityTypeString.datadefinitioncontainer, null));

        assertOperationSuccess(result2, EntityTypeString.datadefinitioncontainer);
        DataDefinitionContainer sddc2 = result2.getAsset().getDataDefinitionContainer();
        checkReturnedParameters(sddc2, RootContainerIds.STRUCTURED_DATA_DEF_CONT_ROOT_ID, sdd.getId(), sddc2.getChildren());
    }

    /**
     * Reads root metadata set container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootMetadataSetContainer() throws Exception
    {
        MetadataSet ms = generateMetadataSet("metadataSet", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.METADATASET_CONTAINER_ROOT_ID, null,
                EntityTypeString.metadatasetcontainer, null));

        assertOperationSuccess(result, EntityTypeString.metadatasetcontainer);
        MetadataSetContainer msc = result.getAsset().getMetadataSetContainer();
        checkReturnedParameters(msc, RootContainerIds.METADATASET_CONTAINER_ROOT_ID, ms.getId(), msc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.METADATASET_CONTAINER_ROOT_ID, null,
                EntityTypeString.metadatasetcontainer, null));

        assertOperationSuccess(result2, EntityTypeString.metadatasetcontainer);
        MetadataSetContainer msc2 = result2.getAsset().getMetadataSetContainer();
        checkReturnedParameters(msc2, RootContainerIds.METADATASET_CONTAINER_ROOT_ID, ms.getId(), msc2.getChildren());
    }

    /**
     * Reads root metadata set container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootPublishSetContainer() throws Exception
    {
        PublishSet ps = generatePublishSet("publishSet", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.PUBLISH_SET_CONTAINER_ROOT_ID, null,
                EntityTypeString.publishsetcontainer, null));

        assertOperationSuccess(result, EntityTypeString.publishsetcontainer);
        PublishSetContainer psc = result.getAsset().getPublishSetContainer();
        checkReturnedParameters(psc, RootContainerIds.PUBLISH_SET_CONTAINER_ROOT_ID, ps.getId(), psc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.PUBLISH_SET_CONTAINER_ROOT_ID, null,
                EntityTypeString.publishsetcontainer, null));

        assertOperationSuccess(result2, EntityTypeString.publishsetcontainer);
        PublishSetContainer psc2 = result2.getAsset().getPublishSetContainer();
        checkReturnedParameters(psc2, RootContainerIds.PUBLISH_SET_CONTAINER_ROOT_ID, ps.getId(), psc2.getChildren());
    }

    /**
     * Reads root target using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootTarget() throws Exception
    {
        Target t = generateTarget("target");

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.TARGET_ROOT_ID, null, EntityTypeString.target, null));

        assertOperationSuccess(result, EntityTypeString.target);
        Target rt = result.getAsset().getTarget();
        assertEquals(RootContainerIds.TARGET_ROOT_ID, rt.getId());
        assertEquals("/", rt.getPath());
        assertNotNull(rt.getChildren());
        boolean found = false;
        for (int i = 0; i < rt.getChildren().length; i++)
            if (rt.getChildren()[i].getId().equals(t.getId()))
                found = true;
        assertTrue("The child under the root container not found", found);

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.TARGET_ROOT_ID, null, EntityTypeString.target, null));

        assertOperationSuccess(result, EntityTypeString.target);
        Target rt2 = result2.getAsset().getTarget();
        assertEquals(RootContainerIds.TARGET_ROOT_ID, rt2.getId());
        assertEquals("/", rt2.getPath());
        assertNotNull(rt2.getChildren());
        found = false;
        for (int i = 0; i < rt2.getChildren().length; i++)
            if (rt2.getChildren()[i].getId().equals(t.getId()))
                found = true;
        assertTrue("The child under the root container not found", found);
    }

    /**
     * Reads root transport container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootTransportContainer() throws Exception
    {
        FileSystemTransport fst = generateFileSystemTransport("transport", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID, null, EntityTypeString.transportcontainer,
                null));

        assertOperationSuccess(result, EntityTypeString.transportcontainer);
        TransportContainer tc = result.getAsset().getTransportContainer();
        checkReturnedParameters(tc, RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID, fst.getId(), tc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID, null,
                EntityTypeString.transportcontainer, null));

        assertOperationSuccess(result2, EntityTypeString.transportcontainer);
        TransportContainer tc2 = result2.getAsset().getTransportContainer();
        checkReturnedParameters(tc2, RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID, fst.getId(), tc2.getChildren());
    }

    /**
     * Reads root workflow definition container using id and using path. Creates a child and makes sure
     * it is listed under getChildren().
     * 
     * @throws Exception
     */
    public void testRootWorkflowDefinitionContainer() throws Exception
    {
        WorkflowDefinition wd = generateWorkflowDefinition("workflowDefinition", null);

        ReadResult result = client.read(auth, new Identifier(RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID, null,
                EntityTypeString.workflowdefinitioncontainer, null));

        assertOperationSuccess(result, EntityTypeString.workflowdefinitioncontainer);
        WorkflowDefinitionContainer wdc = result.getAsset().getWorkflowDefinitionContainer();
        checkReturnedParameters(wdc, RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID, wd.getId(), wdc.getChildren());

        ReadResult result2 = client.read(auth, new Identifier(RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID, null,
                EntityTypeString.workflowdefinitioncontainer, null));

        assertOperationSuccess(result2, EntityTypeString.workflowdefinitioncontainer);
        WorkflowDefinitionContainer wdc2 = result2.getAsset().getWorkflowDefinitionContainer();
        checkReturnedParameters(wdc2, RootContainerIds.WORKFLOW_DEFINITION_CONTAINER_ROOT_ID, wd.getId(), wdc2.getChildren());
    }

    /**
     * Checks if the read <em>container</em> has appropriate id (<em>containerId</em>) and 
     * if one of its <em>children</em> is a child with id <em>childId</em>  
     * 
     * @param container
     * @param containerId
     * @param childId
     * @param children
     */
    private void checkReturnedParameters(ContaineredAsset container, String containerId, String childId, Identifier[] children)
    {
        assertEquals(containerId, container.getId());
        assertEquals("/", container.getPath());
        assertNotNull(children);
        boolean found = false;
        for (int i = 0; i < children.length; i++)
            if (children[i].getId().equals(childId))
                found = true;
        assertTrue("The child under the root container not found", found);
    }
}
