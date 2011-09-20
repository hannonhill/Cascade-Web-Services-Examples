/*
 * Created on Aug 18, 2008 by Tim Reilly
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Group;

/**
 * Tests the creation of groups via web services
 * 
 * @author 	Tim Reilly
 * @version $Id$
 * @since   5.0
 */

public class TestCreateGroup extends CascadeWebServicesTestCase
{
    private Group group;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        group = new Group();
        group.setGroupName("web_services_test_group_name");
        group.setRole("Manager");

        Asset asset = new Asset();
        asset.setGroup(group);

        create(asset, EntityTypeString.group);
    }

    public void testNothing() throws Exception
    {
        //do nothing
    }

}
