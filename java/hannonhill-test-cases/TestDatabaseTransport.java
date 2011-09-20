/*
 * Created on Jan 7, 2011 by Mike Strauch
 * 
 * Copyright(c) 2000-2010 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.DatabaseTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * 
 * Test for web service operations performed on DatabaseTransports.
 * 
 * @author  Mike Strauch
 * @since   6.7.5
 */
public class TestDatabaseTransport extends CascadeWebServicesTestCase
{
    private Site site;

    @Override
    protected void onSetUp() throws Exception
    {
        site = generateSite("site");
    }

    /**
     * Tests creating a database transport with a foreign site id of 0.
     * 
     * @throws Exception
     */
    public void testCreateWithZeroSiteId() throws Exception
    {
        DatabaseTransport transport = generateDatabaseTransportObject("ws_db_transport", site.getId());
        transport.setTransportSiteId(new NonNegativeInteger("0"));
        Asset asset = new Asset();
        asset.setDatabaseTransport(transport);

        CreateResult result = client.create(auth, asset);
        assertOperationSuccess(result, EntityTypeString.transport_db);
    }
}
