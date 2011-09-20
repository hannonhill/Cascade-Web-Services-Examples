/*
 * Created on Feb 18, 2010 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.PageRegion;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Template;

/**
 * Tests Web Services operations for Templates
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.7
 */
public class TestTemplate extends CascadeWebServicesTestCase
{
    /**
     * Tests reading a template that has recycled blocks and formats assigned to page regions.
     *  
     * @throws Exception
     */
    public void testGetRecycledBlockAndFormat() throws Exception
    {
        Template template = generateTemplate("ws_template", null);

        String formatId = generateXsltFormat("ws_stylesheet", null).getId();
        String blockId = generateXmlBlock("ws_block", null).getId();

        PageRegion pageRegion = new PageRegion();
        pageRegion.setBlockId(blockId);
        pageRegion.setFormatId(formatId);
        pageRegion.setName("DEFAULT");
        template.setFormatId(formatId);

        template.setPageRegions(new PageRegion[]
        {
            pageRegion
        });

        Asset asset = new Asset();
        asset.setTemplate(template);
        OperationResult editResult = client.edit(auth, asset);

        assertOperationSuccess(editResult, EntityTypeString.template);

        ReadResult rr = client.read(auth, new Identifier(template.getId(), null, EntityTypeString.template, null));
        assertOperationSuccess(rr, EntityTypeString.template);
        Template fetched = rr.getAsset().getTemplate();
        assertNotNull(fetched);

        assertFalse(fetched.getPageRegions()[0].getBlockRecycled());
        assertFalse(fetched.getPageRegions()[0].getFormatRecycled());
        assertFalse(fetched.getFormatRecycled());

        delete(formatId, EntityTypeString.format);
        delete(blockId, EntityTypeString.block);

        rr = client.read(auth, new Identifier(template.getId(), null, EntityTypeString.template, null));
        assertOperationSuccess(rr, EntityTypeString.template);
        fetched = rr.getAsset().getTemplate();
        assertNotNull(fetched);

        assertTrue(fetched.getPageRegions()[0].getBlockRecycled());
        assertTrue(fetched.getPageRegions()[0].getFormatRecycled());
        assertTrue(fetched.getFormatRecycled());
    }
}
