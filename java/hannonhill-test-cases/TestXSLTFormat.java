/*
 * Created on Sep 15, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.XsltFormat;

/**
 * Tests for performing web services operations on xslt formats. 
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.7
 */
public class TestXSLTFormat extends CascadeWebServicesTestCase
{
    XsltFormat format;
    String formatId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        format = new XsltFormat();
        format.setName("ws_xslt_format");
        format.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        format.setXml("<xslt><nested>test</nested></xslt>");

        Asset asset = new Asset();
        asset.setXsltFormat(format);
        formatId = create(asset, EntityTypeString.format_XSLT);
    }

    /**
     * Tests reading a xslt format via web services. 
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(formatId, null, EntityTypeString.format_XSLT, null));
        assertOperationSuccess(result, EntityTypeString.format_XSLT);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        XsltFormat fetched = asset.getXsltFormat();
        assertNotNull(fetched);

        assertEquals(fetched.getName(), format.getName());
        assertEquals(fetched.getXml(), format.getXml());
        assertEquals(fetched.getParentFolderId(), format.getParentFolderId());
    }

    /**
     * Tests editing a xslt format via web services
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        Identifier identifier = new Identifier(formatId, null, EntityTypeString.format_XSLT, null);
        ReadResult result = client.read(auth, identifier);
        Asset asset = result.getAsset();
        XsltFormat fetched = asset.getXsltFormat();

        fetched.setXml("<xslt>test</xslt>");

        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.format_XSLT);

        result = client.read(auth, identifier);
        fetched = result.getAsset().getXsltFormat();

        assertEquals(fetched.getXml(), "<xslt>test</xslt>");
    }
}
