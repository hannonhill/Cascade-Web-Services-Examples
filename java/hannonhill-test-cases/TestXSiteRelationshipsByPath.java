/*
 * Created on Jul 2, 2009 by Mike Strauch
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactory;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetFactoryWorkflowMode;
import com.hannonhill.www.ws.ns.AssetOperationService.ContentType;
import com.hannonhill.www.ws.ns.AssetOperationService.Destination;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.FileSystemTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.MetadataSet;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.WorkflowDefinition;

/**
 * Tests populating various cross-site relationships using paths rather than ids.  Add tests as needed (obviously).
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   6.2
 */
public class TestXSiteRelationshipsByPath extends CascadeWebServicesTestCase
{
    /**
     * Tests setting a content type on a page using the path to a content type on a different site
     * @throws Exception
     */
    public void testSetPageContentType() throws Exception
    {
        String cTypeSiteName = "ws_page_site";
        ContentType cType = generateContentType("ws_content_type", false, generateSite(cTypeSiteName));

        Page page = generatePageObject("ws_page_test_ctype_by_path", null, generateSite("ws_page_site_name").getId());
        page.setContentTypePath(cTypeSiteName + ":" + cType.getPath());

        Asset asset = new Asset();
        asset.setPage(page);

        String pageId = create(asset, EntityTypeString.page);
        ReadResult result = client.read(auth, new Identifier(pageId, null, EntityTypeString.page, null));
        assertOperationSuccess(result, EntityTypeString.page);

        Page readPage = result.getAsset().getPage();
        assertEquals(readPage.getContentTypeId(), cType.getId());
    }

    /**
     * Tests setting a metadata set on a file using the path to a metadata set on a different site
     * @throws Exception
     */
    public void testSetMetadataSetOnFile() throws Exception
    {
        String metaSetSiteName = "ws_file_site";
        MetadataSet mSet = generateMetadataSet("ws_meta_set", generateSite(metaSetSiteName).getId());

        File file = generateFileObject("ws_file_test_mset_by_path", generateSite("ws_file_site_name").getId());
        file.setMetadataSetPath(metaSetSiteName + ":" + mSet.getPath());

        Asset asset = new Asset();
        asset.setFile(file);

        String fileId = create(asset, EntityTypeString.file);
        ReadResult result = client.read(auth, new Identifier(fileId, null, EntityTypeString.file, null));
        assertOperationSuccess(result, EntityTypeString.file);

        File readFile = result.getAsset().getFile();
        assertEquals(readFile.getMetadataSetId(), mSet.getId());
    }

    /**
     * Tests setting a workflow definition on an asset factory using the path to a workflow def
     * on a different site.
     * @throws Exception
     */
    public void testWFDefOnAssetFactory() throws Exception
    {
        String wfDefSiteName = "ws_wf_def_site";
        WorkflowDefinition wfDef = generateWorkflowDefinition("ws_wf_def", generateSite(wfDefSiteName).getId());

        AssetFactory af = generateAssetFactoryObject("ws_af_test_wfdef_path", generateSite("ws_af_site").getId());
        af.setWorkflowDefinitionPath(wfDefSiteName + ":" + wfDef.getPath());

        // have to set this or else workflow def will not be returned
        af.setWorkflowMode(AssetFactoryWorkflowMode.value2);

        Asset asset = new Asset();
        asset.setAssetFactory(af);

        String afId = create(asset, EntityTypeString.assetfactory);
        ReadResult result = client.read(auth, new Identifier(afId, null, EntityTypeString.assetfactory, null));
        assertOperationSuccess(result, EntityTypeString.assetfactory);

        AssetFactory readFactory = result.getAsset().getAssetFactory();
        assertEquals(readFactory.getWorkflowDefinitionId(), wfDef.getId());
    }

    /**
     * Tests setting a transport on a destination using the path to the transport on a different site.
     * 
     * @throws Exception
     */
    public void testTransportOnDestination() throws Exception
    {
        String transportSiteName = "ws_trans_site";
        FileSystemTransport trans = generateFileSystemTransport("ws_trans", generateSite(transportSiteName).getId());

        Destination dest = generateDestinationObject("ws_trans_dest", generateSite("ws_dest_site").getId());
        dest.setTransportPath(transportSiteName + ":" + trans.getPath());
        // null this out because it's set in the generateDestinationObject method
        dest.setTransportId(null);

        Asset asset = new Asset();
        asset.setDestination(dest);

        String destId = create(asset, EntityTypeString.destination);
        ReadResult result = client.read(auth, new Identifier(destId, null, EntityTypeString.destination, null));
        assertOperationSuccess(result, EntityTypeString.destination);

        Destination readDest = result.getAsset().getDestination();
        assertEquals(readDest.getTransportId(), trans.getId());
    }

}
