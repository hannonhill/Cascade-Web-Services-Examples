package com.cms.webservices;

/**
 * Constants for system area container database ids
 * 
 * @author  Mike Strauch
 * @version $Id$
 * @since   5.0
 */
public class RootContainerIds
{
    private RootContainerIds()
    {
    }

    public static final String ROOT_PREFIX = "ROOT";
    public static final String FOLDER_ROOT_ID = ROOT_PREFIX;
    public static final String ASSET_FACTORY_CONTAINER_ROOT_ID = ROOT_PREFIX + "_assetfactorycontainer";
    public static final String TARGET_ROOT_ID = ROOT_PREFIX + "_target";
    public static final String METADATASET_CONTAINER_ROOT_ID = ROOT_PREFIX + "_metadatasetcontainer";
    public static final String PAGE_CONFIG_SET_CONT_ROOT_ID = ROOT_PREFIX + "_pageconfigsetcontainer";
    public static final String PUBLISH_SET_CONTAINER_ROOT_ID = ROOT_PREFIX + "_publishsetcontainer";
    public static final String STRUCTURED_DATA_DEF_CONT_ROOT_ID = ROOT_PREFIX + "_structureddatadefcontainer";
    public static final String WORKFLOW_DEFINITION_CONTAINER_ROOT_ID = ROOT_PREFIX + "_workflowdefinitioncontainer";
    public static final String TRANSPORT_CONTAINER_ROOT_ID = ROOT_PREFIX + "_transportcontainer";
    public static final String METADATASET_ROOT_ID = ROOT_PREFIX + "_metadataset";
    public static final String CONTENTTYPE_CONTAINER_ROOT_ID = ROOT_PREFIX + "_contenttypecontainer";
}
