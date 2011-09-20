/*
 * Created on Mar 19, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.GlobalAbilities;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Role;
import com.hannonhill.www.ws.ns.AssetOperationService.RoleTypes;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteAbilities;

/**
 * Tests web services operations for Roles.
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestRole extends CascadeWebServicesTestCase
{
    private Role globalRole;
    private Role siteRole;
    private String globalRoleId;
    private String siteRoleId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        globalRole = new Role();
        globalRole.setName("a_global_role");
        globalRole.setRoleType(RoleTypes.global);
        GlobalAbilities globalAbilities = new GlobalAbilities();
        globalAbilities.setAccessAdminArea(true);
        globalAbilities.setAccessAllSites(true);
        globalAbilities.setAccessConfigurationSets(true);
        globalAbilities.setAccessMetadataSets(true);
        globalAbilities.setPublishReadableHomeAssets(true);
        globalAbilities.setPublishWritableHomeAssets(true);
        globalAbilities.setBulkChange(true);
        globalAbilities.setEditAnyUser(true);
        globalAbilities.setRecycleBinDeleteAssets(true);
        globalAbilities.setRecycleBinViewRestoreAllAssets(true);
        globalAbilities.setRecycleBinViewRestoreUserAssets(true);
        globalAbilities.setEditDataDefinition(true);
        globalRole.setGlobalAbilities(globalAbilities);

        Asset asset = new Asset();
        asset.setRole(globalRole);

        globalRoleId = create(asset, EntityTypeString.role);

        siteRole = new Role();
        siteRole.setName("a_site_role");
        siteRole.setRoleType(RoleTypes.site);
        SiteAbilities siteAbilities = new SiteAbilities();
        siteAbilities.setAccessAdminArea(true);
        siteAbilities.setAccessDataDefinitions(true);
        siteAbilities.setAccessConfigurationSets(true);
        siteAbilities.setAccessMetadataSets(true);
        siteAbilities.setPublishReadableHomeAssets(true);
        siteAbilities.setPublishWritableHomeAssets(true);
        siteAbilities.setBulkChange(true);
        siteAbilities.setUploadImagesFromWysiwyg(true);
        siteAbilities.setRecycleBinDeleteAssets(true);
        siteAbilities.setRecycleBinViewRestoreAllAssets(true);
        siteAbilities.setRecycleBinViewRestoreUserAssets(true);
        siteAbilities.setEditDataDefinition(true);
        siteRole.setSiteAbilities(siteAbilities);

        asset = new Asset();
        asset.setRole(siteRole);

        siteRoleId = create(asset, EntityTypeString.role);
    }

    /**
     * Tests reading a role via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(globalRoleId, null, EntityTypeString.role, null));
        assertOperationSuccess(result, EntityTypeString.role);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Role fetchedRole = asset.getRole();
        assertNotNull(fetchedRole);
        assertEquals(globalRole.getName(), fetchedRole.getName());
        assertEquals(globalRoleId, fetchedRole.getId());
        assertEquals(globalRole.getRoleType(), fetchedRole.getRoleType());
        assertGlobalAbilitiesEqual(globalRole.getGlobalAbilities(), fetchedRole.getGlobalAbilities());
        assertNull(fetchedRole.getSiteAbilities());

        result = client.read(auth, new Identifier(siteRoleId, null, EntityTypeString.role, null));
        assertOperationSuccess(result, EntityTypeString.role);

        asset = result.getAsset();
        assertNotNull(asset);

        fetchedRole = asset.getRole();
        assertNotNull(fetchedRole);
        assertEquals(siteRole.getName(), fetchedRole.getName());
        assertEquals(siteRoleId, fetchedRole.getId());
        assertEquals(siteRole.getRoleType(), fetchedRole.getRoleType());
        assertSiteAbilitiesEqual(siteRole.getSiteAbilities(), fetchedRole.getSiteAbilities());
        assertNull(fetchedRole.getGlobalAbilities());
    }

    /**
     * Tests editing a role via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier globalRoleIdentifier = new Identifier(globalRoleId, null, EntityTypeString.role, null);
        ReadResult result = client.read(auth, globalRoleIdentifier);
        Role fetchedGlobalRole = result.getAsset().getRole();
        fetchedGlobalRole.getGlobalAbilities().setAccessSecurityArea(true);
        fetchedGlobalRole.getGlobalAbilities().setAccessConfigurationSets(false);
        fetchedGlobalRole.setName("a_new_global_role_name");

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.role);

        result = client.read(auth, globalRoleIdentifier);
        assertOperationSuccess(result, EntityTypeString.role);

        Role refetchedGlobalRole = result.getAsset().getRole();
        assertNotNull(refetchedGlobalRole);
        assertEquals(fetchedGlobalRole.getName(), refetchedGlobalRole.getName());
        assertEquals(fetchedGlobalRole.getRoleType(), refetchedGlobalRole.getRoleType());
        assertGlobalAbilitiesEqual(fetchedGlobalRole.getGlobalAbilities(), refetchedGlobalRole.getGlobalAbilities());
        assertNull(refetchedGlobalRole.getSiteAbilities());

        Identifier siteRoleIdentifier = new Identifier(siteRoleId, null, EntityTypeString.role, null);
        result = client.read(auth, siteRoleIdentifier);
        Role fetchedSiteRole = result.getAsset().getRole();
        fetchedSiteRole.getSiteAbilities().setViewVersions(true);
        fetchedSiteRole.getSiteAbilities().setAccessConfigurationSets(false);
        fetchedSiteRole.setName("a_new_site_role_name");

        editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.role);

        result = client.read(auth, siteRoleIdentifier);
        assertOperationSuccess(result, EntityTypeString.role);

        Role refetchedSiteRole = result.getAsset().getRole();
        assertNotNull(refetchedSiteRole);
        assertEquals(fetchedSiteRole.getName(), refetchedSiteRole.getName());
        assertEquals(fetchedSiteRole.getRoleType(), refetchedSiteRole.getRoleType());
        assertSiteAbilitiesEqual(fetchedSiteRole.getSiteAbilities(), refetchedSiteRole.getSiteAbilities());
        assertNull(refetchedSiteRole.getGlobalAbilities());
    }

    /**
     * Compares global abilities
     * 
     * @param correctAbilities
     * @param testedAbilities
     */
    private void assertGlobalAbilitiesEqual(GlobalAbilities correctAbilities, GlobalAbilities testedAbilities)
    {
        assertEqualsOrNull(correctAbilities.getBypassAllPermissionsChecks(), testedAbilities.getBypassAllPermissionsChecks());
        assertEqualsOrNull(correctAbilities.getAccessSiteManagement(), testedAbilities.getAccessSiteManagement());
        assertEqualsOrNull(correctAbilities.getCreateSites(), testedAbilities.getCreateSites());
        assertEqualsOrNull(correctAbilities.getUploadImagesFromWysiwyg(), testedAbilities.getUploadImagesFromWysiwyg());
        assertEqualsOrNull(correctAbilities.getMultiSelectCopy(), testedAbilities.getMultiSelectCopy());
        assertEqualsOrNull(correctAbilities.getMultiSelectPublish(), testedAbilities.getMultiSelectPublish());
        assertEqualsOrNull(correctAbilities.getMultiSelectMove(), testedAbilities.getMultiSelectMove());
        assertEqualsOrNull(correctAbilities.getMultiSelectDelete(), testedAbilities.getMultiSelectDelete());
        assertEqualsOrNull(correctAbilities.getEditPageLevelConfigurations(), testedAbilities.getEditPageLevelConfigurations());
        assertEqualsOrNull(correctAbilities.getEditPageContentType(), testedAbilities.getEditPageContentType());
        assertEqualsOrNull(correctAbilities.getPublishReadableHomeAssets(), testedAbilities.getPublishReadableHomeAssets());
        assertEqualsOrNull(correctAbilities.getPublishWritableHomeAssets(), testedAbilities.getPublishWritableHomeAssets());
        assertEqualsOrNull(correctAbilities.getViewPublishQueue(), testedAbilities.getViewPublishQueue());
        assertEqualsOrNull(correctAbilities.getReorderPublishQueue(), testedAbilities.getReorderPublishQueue());
        assertEqualsOrNull(correctAbilities.getCancelPublishJobs(), testedAbilities.getCancelPublishJobs());
        assertEqualsOrNull(correctAbilities.getEditAccessRights(), testedAbilities.getEditAccessRights());
        assertEqualsOrNull(correctAbilities.getViewVersions(), testedAbilities.getViewVersions());
        assertEqualsOrNull(correctAbilities.getActivateDeleteVersions(), testedAbilities.getActivateDeleteVersions());
        assertEqualsOrNull(correctAbilities.getAccessAudits(), testedAbilities.getAccessAudits());
        assertEqualsOrNull(correctAbilities.getBypassWorkflow(), testedAbilities.getBypassWorkflow());
        assertEqualsOrNull(correctAbilities.getAssignApproveWorkflowSteps(), testedAbilities.getAssignApproveWorkflowSteps());
        assertEqualsOrNull(correctAbilities.getDeleteWorkflows(), testedAbilities.getDeleteWorkflows());
        assertEqualsOrNull(correctAbilities.getBreakLocks(), testedAbilities.getBreakLocks());
        assertEqualsOrNull(correctAbilities.getAssignWorkflowsToFolders(), testedAbilities.getAssignWorkflowsToFolders());
        assertEqualsOrNull(correctAbilities.getBypassAssetFactoryGroupsNewMenu(), testedAbilities.getBypassAssetFactoryGroupsNewMenu());
        assertEqualsOrNull(correctAbilities.getBypassDestinationGroupsWhenPublishing(), testedAbilities.getBypassDestinationGroupsWhenPublishing());
        assertEqualsOrNull(correctAbilities.getBypassWorkflowDefintionGroupsForFolders(),
                testedAbilities.getBypassWorkflowDefintionGroupsForFolders());
        assertEqualsOrNull(correctAbilities.getAlwaysAllowedToToggleDataChecks(), testedAbilities.getAlwaysAllowedToToggleDataChecks());
        assertEqualsOrNull(correctAbilities.getAccessAdminArea(), testedAbilities.getAccessAdminArea());
        assertEqualsOrNull(correctAbilities.getAccessAssetFactories(), testedAbilities.getAccessAssetFactories());
        assertEqualsOrNull(correctAbilities.getAccessConfigurationSets(), testedAbilities.getAccessConfigurationSets());
        assertEqualsOrNull(correctAbilities.getAccessDataDefinitions(), testedAbilities.getAccessDataDefinitions());
        assertEqualsOrNull(correctAbilities.getAccessMetadataSets(), testedAbilities.getAccessMetadataSets());
        assertEqualsOrNull(correctAbilities.getAccessPublishSets(), testedAbilities.getAccessPublishSets());
        assertEqualsOrNull(correctAbilities.getAccessTargetsDestinations(), testedAbilities.getAccessTargetsDestinations());
        assertEqualsOrNull(correctAbilities.getAccessTransports(), testedAbilities.getAccessTransports());
        assertEqualsOrNull(correctAbilities.getAccessWorkflowDefinitions(), testedAbilities.getAccessWorkflowDefinitions());
        assertEqualsOrNull(correctAbilities.getAccessContentTypes(), testedAbilities.getAccessContentTypes());
        assertEqualsOrNull(correctAbilities.getAccessAllSites(), testedAbilities.getAccessAllSites());
        assertEqualsOrNull(correctAbilities.getViewSystemInfoAndLogs(), testedAbilities.getViewSystemInfoAndLogs());
        assertEqualsOrNull(correctAbilities.getForceLogout(), testedAbilities.getForceLogout());
        assertEqualsOrNull(correctAbilities.getDiagnosticTests(), testedAbilities.getDiagnosticTests());
        assertEqualsOrNull(correctAbilities.getAccessSecurityArea(), testedAbilities.getAccessSecurityArea());
        assertEqualsOrNull(correctAbilities.getPublishReadableAdminAreaAssets(), testedAbilities.getPublishReadableAdminAreaAssets());
        assertEqualsOrNull(correctAbilities.getPublishWritableAdminAreaAssets(), testedAbilities.getPublishWritableAdminAreaAssets());
        assertEqualsOrNull(correctAbilities.getNewSiteWizard(), testedAbilities.getNewSiteWizard());
        assertEqualsOrNull(correctAbilities.getIntegrateFolder(), testedAbilities.getIntegrateFolder());
        assertEqualsOrNull(correctAbilities.getImportZipArchive(), testedAbilities.getImportZipArchive());
        assertEqualsOrNull(correctAbilities.getOptimizeDatabase(), testedAbilities.getOptimizeDatabase());
        assertEqualsOrNull(correctAbilities.getSyncLdap(), testedAbilities.getSyncLdap());
        assertEqualsOrNull(correctAbilities.getBulkChange(), testedAbilities.getBulkChange());
        assertEqualsOrNull(correctAbilities.getConfigureLogging(), testedAbilities.getConfigureLogging());
        assertEqualsOrNull(correctAbilities.getSearchingIndexing(), testedAbilities.getSearchingIndexing());
        assertEqualsOrNull(correctAbilities.getAccessConfiguration(), testedAbilities.getAccessConfiguration());
        assertEqualsOrNull(correctAbilities.getEditSystemPreferences(), testedAbilities.getEditSystemPreferences());
        assertEqualsOrNull(correctAbilities.getSiteMigration(), testedAbilities.getSiteMigration());
        assertEqualsOrNull(correctAbilities.getViewUsersInMemberGroups(), testedAbilities.getViewUsersInMemberGroups());
        assertEqualsOrNull(correctAbilities.getViewAllUsers(), testedAbilities.getViewAllUsers());
        assertEqualsOrNull(correctAbilities.getCreateUsers(), testedAbilities.getCreateUsers());
        assertEqualsOrNull(correctAbilities.getDeleteUsersInMemberGroups(), testedAbilities.getDeleteUsersInMemberGroups());
        assertEqualsOrNull(correctAbilities.getDeleteAllUsers(), testedAbilities.getDeleteAllUsers());
        assertEqualsOrNull(correctAbilities.getViewMemberGroups(), testedAbilities.getViewMemberGroups());
        assertEqualsOrNull(correctAbilities.getViewAllGroups(), testedAbilities.getViewAllGroups());
        assertEqualsOrNull(correctAbilities.getCreateGroups(), testedAbilities.getCreateGroups());
        assertEqualsOrNull(correctAbilities.getDeleteMemberGroups(), testedAbilities.getDeleteMemberGroups());
        assertEqualsOrNull(correctAbilities.getAccessRoles(), testedAbilities.getAccessRoles());
        assertEqualsOrNull(correctAbilities.getCreateRoles(), testedAbilities.getCreateRoles());
        assertEqualsOrNull(correctAbilities.getDeleteAnyGroup(), testedAbilities.getDeleteAnyGroup());
        assertEqualsOrNull(correctAbilities.getEditAnyUser(), testedAbilities.getEditAnyUser());
        assertEqualsOrNull(correctAbilities.getEditUsersInMemberGroups(), testedAbilities.getEditUsersInMemberGroups());
        assertEqualsOrNull(correctAbilities.getEditAnyGroup(), testedAbilities.getEditAnyGroup());
        assertEqualsOrNull(correctAbilities.getEditMemberGroups(), testedAbilities.getEditMemberGroups());
        assertEqualsOrNull(correctAbilities.getMoveRenameAssets(), testedAbilities.getMoveRenameAssets());
    }

    /**
     * Compares site abilities
     * 
     * @param correctAbilities
     * @param testedAbilities
     */
    private void assertSiteAbilitiesEqual(SiteAbilities correctAbilities, SiteAbilities testedAbilities)
    {
        assertEqualsOrNull(correctAbilities.getBypassAllPermissionsChecks(), testedAbilities.getBypassAllPermissionsChecks());
        assertEqualsOrNull(correctAbilities.getUploadImagesFromWysiwyg(), testedAbilities.getUploadImagesFromWysiwyg());
        assertEqualsOrNull(correctAbilities.getMultiSelectCopy(), testedAbilities.getMultiSelectCopy());
        assertEqualsOrNull(correctAbilities.getMultiSelectPublish(), testedAbilities.getMultiSelectPublish());
        assertEqualsOrNull(correctAbilities.getMultiSelectMove(), testedAbilities.getMultiSelectMove());
        assertEqualsOrNull(correctAbilities.getMultiSelectDelete(), testedAbilities.getMultiSelectDelete());
        assertEqualsOrNull(correctAbilities.getEditPageLevelConfigurations(), testedAbilities.getEditPageLevelConfigurations());
        assertEqualsOrNull(correctAbilities.getEditPageContentType(), testedAbilities.getEditPageContentType());
        assertEqualsOrNull(correctAbilities.getPublishReadableHomeAssets(), testedAbilities.getPublishReadableHomeAssets());
        assertEqualsOrNull(correctAbilities.getPublishWritableHomeAssets(), testedAbilities.getPublishWritableHomeAssets());
        assertEqualsOrNull(correctAbilities.getEditAccessRights(), testedAbilities.getEditAccessRights());
        assertEqualsOrNull(correctAbilities.getViewVersions(), testedAbilities.getViewVersions());
        assertEqualsOrNull(correctAbilities.getActivateDeleteVersions(), testedAbilities.getActivateDeleteVersions());
        assertEqualsOrNull(correctAbilities.getAccessAudits(), testedAbilities.getAccessAudits());
        assertEqualsOrNull(correctAbilities.getBypassWorkflow(), testedAbilities.getBypassWorkflow());
        assertEqualsOrNull(correctAbilities.getAssignApproveWorkflowSteps(), testedAbilities.getAssignApproveWorkflowSteps());
        assertEqualsOrNull(correctAbilities.getDeleteWorkflows(), testedAbilities.getDeleteWorkflows());
        assertEqualsOrNull(correctAbilities.getBreakLocks(), testedAbilities.getBreakLocks());
        assertEqualsOrNull(correctAbilities.getAssignWorkflowsToFolders(), testedAbilities.getAssignWorkflowsToFolders());
        assertEqualsOrNull(correctAbilities.getBypassAssetFactoryGroupsNewMenu(), testedAbilities.getBypassAssetFactoryGroupsNewMenu());
        assertEqualsOrNull(correctAbilities.getBypassDestinationGroupsWhenPublishing(), testedAbilities.getBypassDestinationGroupsWhenPublishing());
        assertEqualsOrNull(correctAbilities.getBypassWorkflowDefintionGroupsForFolders(),
                testedAbilities.getBypassWorkflowDefintionGroupsForFolders());
        assertEqualsOrNull(correctAbilities.getAccessAdminArea(), testedAbilities.getAccessAdminArea());
        assertEqualsOrNull(correctAbilities.getAccessAssetFactories(), testedAbilities.getAccessAssetFactories());
        assertEqualsOrNull(correctAbilities.getAccessConfigurationSets(), testedAbilities.getAccessConfigurationSets());
        assertEqualsOrNull(correctAbilities.getAccessDataDefinitions(), testedAbilities.getAccessDataDefinitions());
        assertEqualsOrNull(correctAbilities.getAccessMetadataSets(), testedAbilities.getAccessMetadataSets());
        assertEqualsOrNull(correctAbilities.getAccessPublishSets(), testedAbilities.getAccessPublishSets());
        assertEqualsOrNull(correctAbilities.getAccessDestinations(), testedAbilities.getAccessDestinations());
        assertEqualsOrNull(correctAbilities.getAccessTransports(), testedAbilities.getAccessTransports());
        assertEqualsOrNull(correctAbilities.getAccessWorkflowDefinitions(), testedAbilities.getAccessWorkflowDefinitions());
        assertEqualsOrNull(correctAbilities.getAccessContentTypes(), testedAbilities.getAccessContentTypes());
        assertEqualsOrNull(correctAbilities.getPublishReadableAdminAreaAssets(), testedAbilities.getPublishReadableAdminAreaAssets());
        assertEqualsOrNull(correctAbilities.getPublishWritableAdminAreaAssets(), testedAbilities.getPublishWritableAdminAreaAssets());
        assertEqualsOrNull(correctAbilities.getIntegrateFolder(), testedAbilities.getIntegrateFolder());
        assertEqualsOrNull(correctAbilities.getImportZipArchive(), testedAbilities.getImportZipArchive());
        assertEqualsOrNull(correctAbilities.getBulkChange(), testedAbilities.getBulkChange());
        assertEqualsOrNull(correctAbilities.getMoveRenameAssets(), testedAbilities.getMoveRenameAssets());
    }

    /**
     * Takes null as false
     * 
     * @param correct
     * @param tested
     */
    private void assertEqualsOrNull(Boolean correct, Boolean tested)
    {
        if (correct == null)
            assertTrue(tested == null || tested == false);
        else
            assertEquals(correct, tested);

    }
}
