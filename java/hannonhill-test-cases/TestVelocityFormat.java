/*
 * Created on Sep 12, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ScriptFormat;

/**
 * Tests for performing web services operations on velocity formats.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.7
 */
public class TestVelocityFormat extends CascadeWebServicesTestCase
{
    ScriptFormat format;
    String formatId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        format = new ScriptFormat();
        format.setName("ws_script_format");
        format.setParentFolderId(RootContainerIds.FOLDER_ROOT_ID);
        format.setScript("Test script");

        Asset asset = new Asset();
        asset.setScriptFormat(format);
        formatId = create(asset, EntityTypeString.format_SCRIPT);
    }

    /**
     * Tests reading a script format via web services. 
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(formatId, null, EntityTypeString.format_SCRIPT, null));
        assertOperationSuccess(result, EntityTypeString.format_SCRIPT);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        ScriptFormat fetched = asset.getScriptFormat();
        assertNotNull(fetched);

        assertEquals(fetched.getName(), format.getName());
        assertEquals(fetched.getScript(), format.getScript());
        assertEquals(fetched.getParentFolderId(), format.getParentFolderId());
    }

    /**
     * Tests editing a script format via web services
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        Identifier identifier = new Identifier(formatId, null, EntityTypeString.format_SCRIPT, null);
        ReadResult result = client.read(auth, identifier);
        Asset asset = result.getAsset();
        ScriptFormat fetched = asset.getScriptFormat();

        fetched.setScript("set new script logic");

        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.format_SCRIPT);

        result = client.read(auth, identifier);
        fetched = result.getAsset().getScriptFormat();

        assertEquals(fetched.getScript(), "set new script logic");
    }
}
