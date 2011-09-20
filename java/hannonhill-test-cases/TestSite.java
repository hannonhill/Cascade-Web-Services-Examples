/*
 * Created on Feb 27, 2009 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation. All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;

import com.hannonhill.commons.util.StringUtil;
import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.File;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.RecycleBinExpiration;
import com.hannonhill.www.ws.ns.AssetOperationService.Role;
import com.hannonhill.www.ws.ns.AssetOperationService.RoleAssignment;
import com.hannonhill.www.ws.ns.AssetOperationService.RoleTypes;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteAbilities;

/**
 * Tests web services operations for Sites.
 * 
 * @author Mike Strauch
 * @since 6.0
 */
public class TestSite extends CascadeWebServicesTestCase
{
    private String siteRole1Id;
    private String siteRole2Id;

    @Override
    protected void onSetUp() throws Exception
    {
        SiteAbilities sa = new SiteAbilities();
        sa.setBulkChange(true);

        Role siteRole = new Role();
        siteRole.setName("a_site_role");
        siteRole.setRoleType(RoleTypes.site);
        siteRole.setSiteAbilities(sa);

        Asset asset = new Asset();
        asset.setRole(siteRole);

        siteRole1Id = create(asset, EntityTypeString.role);

        Role siteRole2 = new Role();
        siteRole2.setName("another_site_role");
        siteRole2.setRoleType(RoleTypes.site);
        siteRole2.setSiteAbilities(sa);

        asset.setRole(siteRole2);
        siteRole2Id = create(asset, EntityTypeString.role);
    }

    /**
     * Tests reading of a site
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        RoleAssignment[] roleAssignments = new RoleAssignment[2];
        RoleAssignment a1 = new RoleAssignment();
        a1.setRoleId(siteRole1Id);

        RoleAssignment a2 = new RoleAssignment();
        a2.setRoleId(siteRole2Id);

        roleAssignments[0] = a1;
        roleAssignments[1] = a2;

        Site site = new Site();
        site.setUrl("siteurl");
        site.setName("ws_test_site");
        site.setRoleAssignments(roleAssignments);
        site.setRecycleBinExpiration(RecycleBinExpiration.fromString("never"));

        Asset asset = new Asset();
        asset.setSite(site);
        String siteId = create(asset, EntityTypeString.site);

        if (StringUtil.isEmptyTrimmed(siteId))
            fail("Site was not created successfully, test aborted.");

        ReadResult readResult = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        assertOperationSuccess(readResult, EntityTypeString.site);

        Asset readAsset = readResult.getAsset();
        Site readSite = readAsset.getSite();
        assertEquals(readSite.getUrl(), site.getUrl());
        assertEquals(readSite.getName(), site.getName());
        assertNotNull(readSite.getRootFolderId());
        assertNotNull(readSite.getRootAssetFactoryContainerId());
        assertNotNull(readSite.getRootPageConfigurationSetContainerId());
        assertNotNull(readSite.getRootContentTypeContainerId());
        assertNotNull(readSite.getRootDataDefinitionContainerId());
        assertNotNull(readSite.getRootMetadataSetContainerId());
        assertNotNull(readSite.getRootPublishSetContainerId());
        assertNotNull(readSite.getRootSiteDestinationContainerId());
        assertNotNull(readSite.getRootTransportContainerId());
        assertNotNull(readSite.getRootWorkflowDefinitionContainerId());

        RoleAssignment[] readAssignments = readSite.getRoleAssignments();
        assertEquals(readAssignments.length, 2);
        for (RoleAssignment ra : readAssignments)
        {
            if (!ra.getRoleId().equals(siteRole1Id))
                assertTrue(ra.getRoleId().equals(siteRole2Id));
            else
                assertTrue(ra.getRoleId().equals(siteRole1Id));
        }
    }

    /**
     * Tests creating a site via web services.
     * 
     * @throws Exception
     */
    public void testCreate() throws Exception
    {
        // this test will fail when you don't have a site role called "test" created
        // you can't create roles via web services yet
        Site site = new Site();

        site.setDefaultMetadataSetId(generateMetadataSet("ws_site_metadataset", null).getId());
        site.setUrl("siteurl");
        site.setCssFileId(generateFile("ws_test_file", null).getId());
        site.setName("ws_test_site");
        site.setCssClasses("class1, class2, class3");
        site.setUsesScheduledPublishing(Boolean.TRUE);
        site.setPublishIntervalHours(new NonNegativeInteger("1"));
        site.setRecycleBinExpiration(RecycleBinExpiration.fromString("30"));

        RoleAssignment[] ras = new RoleAssignment[1];
        ras[0] = new RoleAssignment();
        ras[0].setUsers("admin");
        ras[0].setRoleName("a_site_role");
        site.setRoleAssignments(ras);

        Asset asset = new Asset();
        asset.setSite(site);
        String siteId = create(asset, EntityTypeString.site);

        if (StringUtil.isEmptyTrimmed(siteId))
            fail("Site was not created successfully, test aborted.");

        ReadResult readResult = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        assertOperationSuccess(readResult, EntityTypeString.site);

        Asset readAsset = readResult.getAsset();
        Site readSite = readAsset.getSite();

        assertEquals(readSite.getUrl(), site.getUrl());
        assertEquals(readSite.getCssFileId(), site.getCssFileId());
        assertEquals(readSite.getDefaultMetadataSetId(), site.getDefaultMetadataSetId());
        assertEquals(readSite.getCssClasses(), site.getCssClasses());
        assertEquals(readSite.getName(), site.getName());
        assertEquals(readSite.getUsesScheduledPublishing(), Boolean.TRUE);
        assertEquals(new NonNegativeInteger("1"), readSite.getPublishIntervalHours());

        RoleAssignment[] readAssignments = readSite.getRoleAssignments();
        assertEquals(readAssignments.length, 1);
        assertEquals(readAssignments[0].getRoleName(), "a_site_role");
        assertEquals(readAssignments[0].getUsers(), "admin");
    }

    /**
     * Tests creating a site with a global role which is invalid.
     * 
     * @throws Exception
     */
    public void testCreateWithGlobalRole() throws Exception
    {
        Site site = new Site();

        site.setDefaultMetadataSetId(generateMetadataSet("ws_site_metadataset", null).getId());
        site.setUrl("siteurl");
        site.setCssFileId(generateFile("ws_test_file", null).getId());
        site.setName("ws_test_site");
        site.setCssClasses("class1, class2, class3");
        site.setRecycleBinExpiration(RecycleBinExpiration.value1);

        RoleAssignment[] ras = new RoleAssignment[1];
        ras[0] = new RoleAssignment();
        ras[0].setUsers("admin");
        ras[0].setRoleId("5");
        site.setRoleAssignments(ras);

        Asset asset = new Asset();
        asset.setSite(site);
        CreateResult result = client.create(auth, asset);

        assertEquals(result.getSuccess(), "false");
    }

    /**
     * Tests editing a site via web services.
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        Site site = new Site();

        site.setUrl("siteurl");
        site.setCssFileId(generateFile("ws_test_file", null).getId());
        site.setName("ws_test_site");
        site.setCssClasses("class1, class2, class3");
        site.setUsesScheduledPublishing(Boolean.TRUE);
        site.setCronExpression("* 6 * * * ?");
        site.setRecycleBinExpiration(RecycleBinExpiration.fromString("never"));

        RoleAssignment[] ras = new RoleAssignment[1];
        ras[0] = new RoleAssignment();
        ras[0].setUsers("admin");
        ras[0].setRoleName("a_site_role");
        site.setRoleAssignments(ras);

        Asset asset = new Asset();
        asset.setSite(site);
        String siteId = create(asset, EntityTypeString.site);

        ReadResult readResult = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null));
        Asset resultAsset = readResult.getAsset();
        Site readSite = resultAsset.getSite();

        Page page = generatePage("abc", readSite.getId());

        String originalDefaultMetadataSetId = readSite.getDefaultMetadataSetId();

        readSite.setUrl("new_ws_url");
        readSite.setName("new_ws_site_name");
        readSite.setCssClasses("new,cssclasses");
        readSite.setUsesScheduledPublishing(Boolean.FALSE);
        readSite.setCssFileId(generateFile("new_ws_test_file", readSite.getId()).getId());
        readSite.setDefaultMetadataSetId(generateMetadataSet("new_ws_test_def_meta_set", null).getId());
        readSite.getRoleAssignments()[0].setUsers("");
        readSite.setSiteAssetFactoryContainerId(getRootAssetFactoryContainerId(readSite.getId()));
        readSite.setSiteStartingPageId(page.getId());

        OperationResult opResult = client.edit(auth, resultAsset);

        assertOperationSuccess(opResult, EntityTypeString.site);
        Asset afterEditAsset = client.read(auth, new Identifier(siteId, null, EntityTypeString.site, null)).getAsset();
        Site afterEditSite = afterEditAsset.getSite();

        assertEquals(readSite.getUrl(), afterEditSite.getUrl());
        assertEquals(readSite.getCssFileId(), afterEditSite.getCssFileId());
        assertEquals(readSite.getDefaultMetadataSetId(), afterEditSite.getDefaultMetadataSetId());
        assertEquals(readSite.getCssClasses(), afterEditSite.getCssClasses());
        assertEquals(readSite.getName(), afterEditSite.getName());
        assertEquals(readSite.getUsesScheduledPublishing(), afterEditSite.getUsesScheduledPublishing());
        assertTrue(afterEditSite.getRoleAssignments()[0].getUsers() == null || afterEditSite.getRoleAssignments()[0].getUsers().equals(""));
        assertEquals(readSite.getSiteAssetFactoryContainerId(), afterEditSite.getSiteAssetFactoryContainerId());
        assertEquals(readSite.getSiteStartingPageId(), afterEditSite.getSiteStartingPageId());

        // help for cleanup - unassign default metadata set from the site so it can be deleted
        readSite.setDefaultMetadataSetId(originalDefaultMetadataSetId);
        opResult = client.edit(auth, resultAsset);
        assertOperationSuccess(opResult, EntityTypeString.site);
    }

    /**
     * Test reading a site by name for CSCD-5703.
     */
    public void testReadSiteByName() throws Exception
    {
        Site site = generateSite("ws_site_read_by_name");
        ReadResult result = client.read(auth, new Identifier(null, new Path(site.getName(), null, site.getName()), EntityTypeString.site, null));

        assertOperationSuccess(result, EntityTypeString.site);
        assertEquals(site.getId(), result.getAsset().getSite().getId());

        result = client.read(auth, new Identifier(null, new Path(site.getName(), "", null), EntityTypeString.site, null));
        assertOperationSuccess(result, EntityTypeString.site);
        assertEquals(site.getId(), result.getAsset().getSite().getId());
    }

    /**
     * Tests reading a site that has a recycled starting page and css file
     * 
     * @throws Exception
     */
    public void testGetRecycledAssets() throws Exception
    {
        Site site = generateSite("ws_site");
        Page page = generatePage("ws_page", site.getId());
        File file = generateFile("ws_file", site.getId());

        site.setSiteStartingPageId(page.getId());
        site.setCssFileId(file.getId());
        Asset asset = new Asset();
        asset.setSite(site);
        OperationResult editResult = client.edit(auth, asset);
        assertOperationSuccess(editResult, EntityTypeString.site);

        ReadResult result = client.read(auth, new Identifier(site.getId(), null, EntityTypeString.site, null));
        assertOperationSuccess(result, EntityTypeString.site);
        Site fetched = result.getAsset().getSite();
        assertNotNull(fetched);

        assertFalse(fetched.getSiteStartingPageRecycled());
        assertFalse(fetched.getCssFileRecycled());

        delete(page.getId(), EntityTypeString.page);
        delete(file.getId(), EntityTypeString.file);

        result = client.read(auth, new Identifier(site.getId(), null, EntityTypeString.site, null));
        assertOperationSuccess(result, EntityTypeString.site);
        fetched = result.getAsset().getSite();
        assertNotNull(fetched);

        assertTrue(fetched.getSiteStartingPageRecycled());
        assertTrue(fetched.getCssFileRecycled());
    }
}
