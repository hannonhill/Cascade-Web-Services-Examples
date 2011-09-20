/*
 * Created on Aug 5, 2009 by Artur Tomusiak
 * 
 * Copyright(c) 2000-2009 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import com.hannonhill.www.ws.ns.AssetOperationService.Audit;
import com.hannonhill.www.ws.ns.AssetOperationService.AuditParameters;
import com.hannonhill.www.ws.ns.AssetOperationService.AuditTypes;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadAuditsResult;

/**
 * Tests Audits
 * 
 * @author  Artur Tomusiak
 * @version $Id$
 * @since   6.2.1
 */
public class TestAudit extends CascadeWebServicesTestCase
{
    /**
     * Tests reading edit audits of "admin" user - make sure you have "admin" user in the system and he edited something at least once
     * 
     * @throws Exception
     */
    public void testReadEdits() throws Exception
    {
        AuditParameters auditParameters = new AuditParameters();
        auditParameters.setAuditType(AuditTypes.edit);
        auditParameters.setUsername("admin");
        ReadAuditsResult readAuditsResult = client.readAudits(auth, auditParameters);
        assertNotNull(readAuditsResult);
        assertEquals("true", readAuditsResult.getSuccess());
        Audit[] audits = readAuditsResult.getAudits();
        if (audits.length == 0)
            fail("No audits returned.");
    }

    /**
     * Tests reading login audits - make sure you have "admin" user in the system and he logged in at least once
     * 
     * @throws Exception
     */
    public void testReadLogins() throws Exception
    {
        AuditParameters auditParameters = new AuditParameters();
        auditParameters.setAuditType(AuditTypes.login);
        auditParameters.setUsername("admin");
        ReadAuditsResult readAuditsResult = client.readAudits(auth, auditParameters);
        assertNotNull(readAuditsResult);
        assertEquals("true", readAuditsResult.getSuccess());
        Audit[] audits = readAuditsResult.getAudits();
        if (audits.length == 0)
            fail("No audits returned.");
    }

    /**
     * Tests reading create audits - make sure you have "admin" user in the system and he created something there
     * 
     * @throws Exception
     */
    public void testReadCreates() throws Exception
    {
        AuditParameters auditParameters = new AuditParameters();
        auditParameters.setAuditType(AuditTypes.create);
        auditParameters.setUsername("admin");
        ReadAuditsResult readAuditsResult = client.readAudits(auth, auditParameters);
        assertNotNull(readAuditsResult);
        assertEquals("true", readAuditsResult.getSuccess());
        Audit[] audits = readAuditsResult.getAudits();
        if (audits.length == 0)
            fail("No audits returned.");
    }
}
