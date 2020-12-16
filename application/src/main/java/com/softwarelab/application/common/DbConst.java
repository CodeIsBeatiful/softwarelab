package com.softwarelab.application.common;

/**
 * @author black-star
 * <p>
 * status constant
 */
public class DbConst {

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_STATUS = "status";

    public static final String COLUMN_TYPE = "type";

    public static final String COLUMN_USER_ID = "user_id";

    public static final String COLUMN_ADDITIONAL_INFO = "additional_info";

    public static final String COLUMN_RUNNING_STATUS = "running_status";

    public static final String COLUMN_DOWNLOAD_STATUS = "download_status";

    public static final int RUNNING_STATUS_START = 1;

    public static final int RUNNING_STATUS_STOP = 0;

    public static final int STATUS_DELETE = 1;

    public static final int STATUS_NORMAL = 0;

    public static final int APP_SOURCE_RELOAD = 1;

    public static final int APP_SOURCE_UPGRADE = 2;

    public static final int DOWNLOAD_STATUS_FINISH = 2;

    public static final int DOWNLOAD_STATUS_BEGIN = 1;

    public static final int DOWNLOAD_STATUS_INIT = 0;

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String COLUMN_APP = "app";

    public static final String COLUMN_APP_NAME = "app_name";

    public static final String COLUMN_VERSION = "version";
}