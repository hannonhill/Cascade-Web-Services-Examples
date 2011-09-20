/*
 * Created on Aug 1, 2008 by syl
 * 
 * Copyright(c) 2000-2007 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.Time;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.DayOfWeek;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Folder;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.PublishSet;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;

/**
 * 
 * @author syl
 * @since 5.0
 */
public class TestPublishSet extends CascadeWebServicesTestCase
{
    private PublishSet publishSet;
    private String publishSetId;

    private Site site;
    private String siteId;
    private PublishSet publishSetSite;
    private String publishSetSiteId;

    @Override
    protected void onSetUp() throws Exception
    {
        Asset asset = new Asset();
        publishSet = new PublishSet();

        publishSet.setName("Newsletters");
        publishSet.setTimeToPublish(new Time("12:12:00.000Z"));
        publishSet.setUsesScheduledPublishing(true);

        publishSet.setPublishDaysOfWeek(new DayOfWeek[]
        {
                DayOfWeek.Monday, DayOfWeek.Thursday
        });
        publishSet.setParentContainerId(getRootPublishSetContainerId(null));

        asset.setPublishSet(publishSet);

        publishSetId = create(asset, EntityTypeString.publishset);

        // create the publish set inside a site
        site = generateSite("site");
        siteId = site.getId();

        publishSetSite = new PublishSet();
        publishSetSite.setName("Newsletters");
        publishSetSite.setPublishIntervalHours(new NonNegativeInteger("1"));
        publishSetSite.setTimeToPublish(new Time("12:12:00.000Z"));
        publishSetSite.setUsesScheduledPublishing(true);
        publishSetSite.setParentContainerId(getRootPublishSetContainerId(siteId));
        publishSetSite.setSiteId(site.getId());

        asset.setPublishSet(publishSetSite);
        publishSetSiteId = create(asset, EntityTypeString.publishset);
    }

    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(publishSetId, null, EntityTypeString.publishset, null));
        assertOperationSuccess(result, EntityTypeString.publishset);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        PublishSet fetchedPublishSet = asset.getPublishSet();

        assertEquals(publishSet.getName(), fetchedPublishSet.getName());
        assertEquals(2, fetchedPublishSet.getPublishDaysOfWeek().length);
        assertEquals(DayOfWeek.Monday, fetchedPublishSet.getPublishDaysOfWeek()[0]);
        assertEquals(DayOfWeek.Thursday, fetchedPublishSet.getPublishDaysOfWeek()[1]);
        assertEquals(publishSet.getTimeToPublish(), fetchedPublishSet.getTimeToPublish());
        assertEquals(publishSet.getUsesScheduledPublishing(), fetchedPublishSet.getUsesScheduledPublishing());
    }

    public void testSiteRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(publishSetSiteId, null, EntityTypeString.publishset, null));
        assertOperationSuccess(result, EntityTypeString.publishset);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        PublishSet fetchedPublishSet = asset.getPublishSet();

        assertEquals(publishSetSite.getName(), fetchedPublishSet.getName());
        assertEquals(publishSetSite.getPublishIntervalHours(), fetchedPublishSet.getPublishIntervalHours());
        assertEquals(publishSetSite.getTimeToPublish(), fetchedPublishSet.getTimeToPublish());
        assertEquals(publishSetSite.getUsesScheduledPublishing(), fetchedPublishSet.getUsesScheduledPublishing());
        assertEquals(publishSetSite.getSiteId(), fetchedPublishSet.getSiteId());
    }

    @Override
    protected void tearDown() throws Exception
    {
        // only items that are not created with generate*() methods need to be cleaned up here
        super.tearDown();
    }

    /**
     * Tests reading an asset factory that has a recycled base asset and placement folder
     * 
     * @throws Exception
     */
    public void testGetRecycledAssets() throws Exception
    {
        PublishSet publishSet = generatePublishSet("ws_publishset", null);
        Page page = generatePage("ws_page", null);
        Folder folder = generateFolder("ws_folder_pubset", null);
        File file = generateFile("ws_file", null);
        publishSet.setFiles(new Identifier[]
        {
            new Identifier(file.getId(), null, EntityTypeString.file, null)
        });
        publishSet.setFolders(new Identifier[]
        {
            new Identifier(folder.getId(), null, EntityTypeString.folder, null)
        });
        publishSet.setPages(new Identifier[]
        {
            new Identifier(page.getId(), null, EntityTypeString.page, null)
        });

        Asset asset = new Asset();
        asset.setPublishSet(publishSet);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.publishset);

        ReadResult result = client.read(auth, new Identifier(publishSet.getId(), null, EntityTypeString.publishset, null));
        assertOperationSuccess(result, EntityTypeString.publishset);
        PublishSet fetched = result.getAsset().getPublishSet();
        assertNotNull(fetched);

        assertFalse(fetched.getFiles()[0].getRecycled());
        assertFalse(fetched.getFolders()[0].getRecycled());
        assertFalse(fetched.getPages()[0].getRecycled());

        delete(page.getId(), EntityTypeString.page);
        delete(folder.getId(), EntityTypeString.folder);
        delete(file.getId(), EntityTypeString.file);

        result = client.read(auth, new Identifier(publishSet.getId(), null, EntityTypeString.publishset, null));
        assertOperationSuccess(result, EntityTypeString.publishset);
        fetched = result.getAsset().getPublishSet();
        assertNotNull(fetched);

        assertTrue(fetched.getFiles()[0].getRecycled());
        assertTrue(fetched.getFolders()[0].getRecycled());
        assertTrue(fetched.getPages()[0].getRecycled());
    }
}
