/*
 * Created on Mar 17, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Site;
import com.hannonhill.www.ws.ns.AssetOperationService.Template;

/**
 * Web Services test for template inside of a site
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.0
 */
public class TestSiteTemplate extends CascadeWebServicesTestCase
{
    private Template template;
    private String templateId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        Site site = generateSite("site");
        String siteId = site.getId();
        String siteRootFolderId = site.getRootFolderId();

        template = new Template();
        template.setName("a_template");
        template.setParentFolderId(siteRootFolderId);
        template.setXml("<xml>\naaa\n</xml>");
        template.setSiteId(siteId);

        Asset asset = new Asset();
        asset.setTemplate(template);

        templateId = create(asset, EntityTypeString.template);
    }

    /**
     * Tests reading a template via web services.
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(templateId, null, EntityTypeString.template, null));
        assertOperationSuccess(result, EntityTypeString.template);

        Asset asset = result.getAsset();
        assertNotNull(asset);

        Template fetchedTemplate = asset.getTemplate();
        assertNotNull(fetchedTemplate);
        assertEquals(template.getName(), fetchedTemplate.getName());
        assertEquals(templateId, fetchedTemplate.getId());
        assertEquals(template.getXml(), fetchedTemplate.getXml());
        assertEquals(template.getParentFolderId(), fetchedTemplate.getParentFolderId());
        assertEquals(template.getSiteId(), fetchedTemplate.getSiteId());
    }

    /**
     * Tests editing a template via web services. 
     */
    public void testEdit() throws Exception
    {
        Identifier templateIdentifier = new Identifier(templateId, null, EntityTypeString.template, null);
        ReadResult result = client.read(auth, templateIdentifier);
        Template fetchedTemplate = result.getAsset().getTemplate();

        fetchedTemplate.setXml("<xml>bbb</xml>");

        OperationResult editResult = client.edit(auth, result.getAsset());
        assertOperationSuccess(editResult, EntityTypeString.template);

        result = client.read(auth, templateIdentifier);
        assertOperationSuccess(result, EntityTypeString.template);

        Template refetchedTemplate = result.getAsset().getTemplate();
        assertEquals(fetchedTemplate.getXml(), refetchedTemplate.getXml());
        assertEquals(fetchedTemplate.getSiteId(), refetchedTemplate.getSiteId());
    }
}
