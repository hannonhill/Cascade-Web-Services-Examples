/*
 * Created on Sep 18, 2008 by Mike Strauch
 * 
 * Copyright(c) 2006 Hannon Hill Corporation.  All rights reserved.
 */
package com.cms.webservices;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.DatabaseTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.FileSystemTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.FtpTransport;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;

/**
 * 
 * Tests web services operations for Trasports.
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.7
 */
public class TestTransport extends CascadeWebServicesTestCase
{
    private FtpTransport ftp;
    private String ftpId;
    private FileSystemTransport fs;
    private String fsId;
    private DatabaseTransport db;
    private String dbId;

    /* (non-Javadoc)
     * @see com.cms.webservices.CascadeWebServicesTestCase#onSetUp()
     */
    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();

        ftp = new FtpTransport();
        ftp.setName("ws_ftp");
        ftp.setDirectory("ws_directory");
        ftp.setHostName("ws_hostname");
        ftp.setPort(new PositiveInteger("80"));
        ftp.setUsername("ws_username");
        ftp.setPassword("ws_password");
        ftp.setParentContainerId(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID);
        Asset asset = new Asset();
        asset.setFtpTransport(ftp);
        ftpId = create(asset, EntityTypeString.transport);

        fs = new FileSystemTransport();
        fs.setName("ws_fs");
        fs.setParentContainerId(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID);
        fs.setDirectory("ws_directory");

        asset.setFtpTransport(null);
        asset.setFileSystemTransport(fs);
        fsId = create(asset, EntityTypeString.transport);

        db = new DatabaseTransport();
        db.setName("ws_db");
        db.setParentContainerId(RootContainerIds.TRANSPORT_CONTAINER_ROOT_ID);
        db.setDatabaseName("db_name");
        db.setPassword("pass");
        db.setServerName("server_name");
        db.setServerPort(new PositiveInteger("80"));
        db.setTransportSiteId(new NonNegativeInteger("1"));
        db.setUsername("username");
        asset.setDatabaseTransport(db);
        asset.setFileSystemTransport(null);
        dbId = create(asset, EntityTypeString.transport);
    }

    /**
     * Tests reading a ftp and filesystem transport via web services.
     * 
     * @throws Exception
     */
    public void testRead() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(fsId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        Asset asset = result.getAsset();

        FileSystemTransport fetchedFs = asset.getFileSystemTransport();
        assertEquals(fetchedFs.getName(), fs.getName());
        assertEquals(fetchedFs.getDirectory(), fs.getDirectory());
        assertEquals(fetchedFs.getParentContainerId(), fs.getParentContainerId());

        result = client.read(auth, new Identifier(ftpId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        asset = result.getAsset();

        FtpTransport fetchedFtp = asset.getFtpTransport();
        assertEquals(fetchedFtp.getName(), ftp.getName());
        assertEquals(fetchedFtp.getDirectory(), ftp.getDirectory());
        assertEquals(fetchedFtp.getHostName(), ftp.getHostName());
        assertEquals(fetchedFtp.getUsername(), ftp.getUsername());
        assertEquals(fetchedFtp.getParentContainerId(), ftp.getParentContainerId());
        assertEquals(fetchedFtp.getPort(), ftp.getPort());

        result = client.read(auth, new Identifier(dbId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        asset = result.getAsset();

        DatabaseTransport fetchedDb = asset.getDatabaseTransport();
        assertEquals(fetchedDb.getName(), db.getName());
        assertEquals(fetchedDb.getUsername(), db.getUsername());
        assertEquals(fetchedDb.getDatabaseName(), db.getDatabaseName());
        assertEquals(fetchedDb.getParentContainerId(), db.getParentContainerId());
        assertEquals(fetchedDb.getServerPort(), db.getServerPort());
        assertEquals(fetchedDb.getServerName(), db.getServerName());
        assertEquals(fetchedDb.getTransportSiteId(), db.getTransportSiteId());
    }

    /**
     * Tests editing a ftp and file system transport via web services.
     * 
     * @throws Exception
     */
    public void testEdit() throws Exception
    {
        ReadResult result = client.read(auth, new Identifier(fsId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        Asset asset = result.getAsset();

        FileSystemTransport fetchedFS = asset.getFileSystemTransport();
        fetchedFS.setDirectory("ws_new_dir");

        assertOperationSuccess(client.edit(auth, asset), EntityTypeString.transportcontainer);

        result = client.read(auth, new Identifier(fsId, null, EntityTypeString.transport, null));
        FileSystemTransport fetchedAgain = result.getAsset().getFileSystemTransport();
        assertEquals(fetchedAgain.getDirectory(), fetchedFS.getDirectory());

        result = client.read(auth, new Identifier(ftpId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        asset = result.getAsset();

        FtpTransport fetchedFtp = asset.getFtpTransport();
        fetchedFtp.setDirectory("ws_new_ftp_directory");
        fetchedFtp.setUsername("ws_new_username");

        assertOperationSuccess(client.edit(auth, asset), EntityTypeString.transportcontainer);

        result = client.read(auth, new Identifier(ftpId, null, EntityTypeString.transport, null));
        FtpTransport fetchedAgain2 = result.getAsset().getFtpTransport();
        assertEquals(fetchedAgain2.getDirectory(), fetchedFtp.getDirectory());
        assertEquals(fetchedAgain2.getUsername(), fetchedFtp.getUsername());

        result = client.read(auth, new Identifier(dbId, null, EntityTypeString.transport, null));
        assertOperationSuccess(result, EntityTypeString.transport);
        asset = result.getAsset();

        DatabaseTransport fetchedDb = asset.getDatabaseTransport();
        fetchedDb.setServerName("ws_new_server_name");
        fetchedDb.setUsername("ws_new_username");

        assertOperationSuccess(client.edit(auth, asset), EntityTypeString.transportcontainer);

        result = client.read(auth, new Identifier(dbId, null, EntityTypeString.transport, null));
        DatabaseTransport fetchedAgain3 = result.getAsset().getDatabaseTransport();
        assertEquals(fetchedAgain3.getServerName(), fetchedDb.getServerName());
        assertEquals(fetchedAgain3.getUsername(), fetchedDb.getUsername());
    }
}
