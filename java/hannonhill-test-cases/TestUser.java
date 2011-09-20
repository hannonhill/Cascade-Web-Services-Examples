/*
 * Created on Jun 20, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Role;
import com.hannonhill.www.ws.ns.AssetOperationService.RoleTypes;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.SiteAbilities;
import com.hannonhill.www.ws.ns.AssetOperationService.User;
import com.hannonhill.www.ws.ns.AssetOperationService.UserAuthTypes;

/**
 * Tests web services operations for users.
 * 
 * @author  Mike Strauch
 * @since   5.5
 */
public class TestUser extends CascadeWebServicesTestCase
{
    private User user;
    private String userId;
    private Site userSite;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        user = new User();
        user.setUsername("ws_test_user");
        user.setFullName("ws_test_user_full_name");
        user.setEmail("some_email@some_website.com");
        user.setAuthType(UserAuthTypes.normal);
        user.setEnabled(false);
        user.setPassword("ws_test_pass");
        user.setRole("Approver;Administrator");
        user.setGroups(generateGroup("ws_test_user_group").getGroupName() + ";Administrators");
        userSite = generateSite("ws_default_user_site");
        user.setDefaultSiteId(userSite.getId());

        Asset asset = new Asset();
        asset.setUser(user);

        userId = create(asset, EntityTypeString.user);
    }

    /**
     * Tests reading the user created in onSetUp() and verifying its properties.
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals("true", rr.getSuccess());
        User user = rr.getAsset().getUser();

        assertEquals("ws_test_user", user.getUsername());
        assertEquals("ws_test_user_full_name", user.getFullName());
        assertEquals("some_email@some_website.com", user.getEmail());
        assertEquals(UserAuthTypes.normal, user.getAuthType());
        assertEquals(false, user.getEnabled().booleanValue());
        assertEquals(userSite.getName(), user.getDefaultSiteName());
        assertEquals(userSite.getId(), user.getDefaultSiteId());
    }

    /**
     * Test reading a user with multiple groups
     * 
     * @throws Exception
     */
    public void testUserReadMultipleRoles() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals(rr.getSuccess(), "true");
        User user = rr.getAsset().getUser();
        assertNotNull(user);

        // Make sure there are still multiple roles there
        List<String> correctRoles = new ArrayList<String>();
        correctRoles.add("Approver");
        correctRoles.add("Administrator");
        assertContains(correctRoles, user.getRole());
    }

    /**
     * Tests creating a user when the full name and email fields are not supplied.
     * @throws Exception
     */
    public void testCreateNoEmailOrFullName() throws Exception
    {
        User user = new User();
        user.setUsername("ws_test_user2");
        user.setAuthType(UserAuthTypes.normal);
        user.setEnabled(false);
        user.setPassword("ws_test_pass");
        user.setRole("Administrator;Approver");
        user.setGroups(generateGroup("ws_test_user_group2").getGroupName());

        Asset asset = new Asset();
        asset.setUser(user);

        String id = create(asset, EntityTypeString.user);

        ReadResult result = client.read(auth, new Identifier(id, null, EntityTypeString.user, null));
        assertOperationSuccess(result, EntityTypeString.user);

        assertNotNull(result.getAsset());
        User fetchedUser = result.getAsset().getUser();
        assertNotNull(fetchedUser);
        assertEquals(fetchedUser.getUsername(), user.getUsername());
        assertEquals(fetchedUser.getAuthType(), user.getAuthType());
        assertEquals(fetchedUser.getEnabled(), user.getEnabled());
        assertEquals(fetchedUser.getFullName(), user.getFullName());
        assertEquals(fetchedUser.getEmail(), user.getEmail());
        assertNotSame(fetchedUser.getPassword(), user.getPassword());

        List<String> fetchedRoles = Arrays.asList(fetchedUser.getRole().split(";"));
        List<String> originalRoles = Arrays.asList(user.getRole().split(";"));

        for (String fetchedRole : fetchedRoles)
        {
            assertTrue(originalRoles.contains(fetchedRole));
        }

        assertEquals(fetchedUser.getGroups(), user.getGroups());
    }

    /**
     * Tests ability to edit a user.
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals(rr.getSuccess(), "true");
        User user = rr.getAsset().getUser();
        assertNotNull(user);

        Site newUserSite = generateSite("ws_another_default_site");

        String oldPassword = "" + user.getPassword();
        user.setRole("Manager;Contributor");
        user.setFullName("full_name_2");
        user.setEmail("email2@some_website.com");
        user.setEnabled(true);
        user.setPassword("password_2");
        user.setGroups("ws_test_user_group;" + generateGroup("group_2").getGroupName());
        user.setDefaultSiteId(null);
        user.setDefaultSiteName(newUserSite.getName());
        Asset asset = new Asset();
        asset.setUser(user);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals(rr.getSuccess(), "true");
        user = rr.getAsset().getUser();
        assertNotNull(user);

        assertEquals(userId, user.getUsername());
        assertEquals(new Boolean(true), user.getEnabled());
        assertEquals("full_name_2", user.getFullName());
        assertEquals("email2@some_website.com", user.getEmail());
        assertEquals(newUserSite.getName(), user.getDefaultSiteName());
        assertEquals(newUserSite.getId(), user.getDefaultSiteId());
        assertNotSame(oldPassword, user.getPassword());

        List<String> correctRoles = new ArrayList<String>();
        correctRoles.add("Manager");
        correctRoles.add("Contributor");
        assertContains(correctRoles, user.getRole());

        List<String> correctGroups = new ArrayList<String>();
        correctGroups.add("ws_test_user_group");
        correctGroups.add("group_2");
        assertContains(correctGroups, user.getGroups());
    }

    /**
     * Tests nulling out a user's default site.
     * 
     * @throws Exception
     */
    public void testNullDefaultSite() throws Exception
    {
        ReadResult rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals(rr.getSuccess(), "true");
        User user = rr.getAsset().getUser();
        assertNotNull(user);

        user.setDefaultSiteId(null);
        user.setDefaultSiteName(null);

        Asset asset = new Asset();
        asset.setUser(user);
        assertEquals(client.edit(auth, asset).getSuccess(), "true");

        rr = client.read(auth, new Identifier(userId, null, EntityTypeString.user, null));
        assertEquals(rr.getSuccess(), "true");
        user = rr.getAsset().getUser();
        assertNotNull(user);

        // make sure the default site was cleared out
        assertNull(user.getDefaultSiteId());
        assertNull(user.getDefaultSiteName());
    }

    /**
     * Tests creation of a user with role that doesn't exist
     * 
     * @throws Exception
     */
    public void testCreateWithInexistentRole() throws Exception
    {
        User user = new User();
        user.setUsername("ws_test_user2");
        user.setAuthType(UserAuthTypes.normal);
        user.setEnabled(true);
        user.setPassword("ws_test_pass2");
        user.setRole("Approver;Inexistent role;Administrator");
        user.setGroups(generateGroup("ws_test_user_group2").getGroupName() + ";Administrators");

        Asset asset = new Asset();
        asset.setUser(user);

        CreateResult result = client.create(auth, asset);
        assertOperationFailure(result, EntityTypeString.user);
    }

    /**
     * Tests creation of a user with a site role instead of global role
     * 
     * @throws Exception
     */
    public void testCreateWithSiteRole() throws Exception
    {
        Role siteRole = new Role();
        siteRole.setName("a_site_role");
        siteRole.setRoleType(RoleTypes.site);
        siteRole.setSiteAbilities(new SiteAbilities());

        Asset asset = new Asset();
        asset.setRole(siteRole);

        create(asset, EntityTypeString.role);

        User user = new User();
        user.setUsername("ws_test_user2");
        user.setAuthType(UserAuthTypes.normal);
        user.setEnabled(true);
        user.setPassword("ws_test_pass2");
        user.setRole("Approver;a_site_role;Administrator");
        user.setGroups(generateGroup("ws_test_user_group2").getGroupName() + ";Administrators");

        asset = new Asset();
        asset.setUser(user);

        CreateResult result = client.create(auth, asset);
        assertOperationFailure(result, EntityTypeString.user);
    }

    /**
     * Tests creation of a user without any role provided but to avoid wsdl validation error user's role is going to be an
     * empty string instead of null value.
     * 
     * @throws Exception
     */
    public void testCreateNoRoles() throws Exception
    {
        User user = new User();
        user.setUsername("ws_test_user2");
        user.setAuthType(UserAuthTypes.normal);
        user.setEnabled(true);
        user.setPassword("ws_test_pass2");
        user.setRole("");
        user.setGroups(generateGroup("ws_test_user_group2").getGroupName() + ";Administrators");

        Asset asset = new Asset();
        asset.setUser(user);

        CreateResult result = client.create(auth, asset);
        assertOperationFailure(result, EntityTypeString.user);
    }
}
