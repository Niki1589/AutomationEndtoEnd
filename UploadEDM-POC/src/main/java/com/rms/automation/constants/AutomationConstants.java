package com.rms.automation.constants;

public class AutomationConstants {

    public static final String WORKFLOW_STATUS_SUCCEEDED = "FINISHED";
    public static final String WORKFLOW_STATUS_FAILED = "FAILED";
    public static final String WORKFLOW_STATUS_CANCELED = "CANCELLED";

    public static final String WORKFLOW_STATUS_QUEUED = "QUEUED";
    public static final String WORKFLOW_STATUS_RUNNING = "RUNNING";

    // Job Status
    public static final String JOB_STATUS_QUEUED = "Queued";

    public static final String JOB_STATUS_FINISHED = "Finished";

    public static final String JOB_STATUS_RUNNING = "Running";

    public static final String JOB_STATUS_PENDING = "Pending";

    public static final String JOB_STATUS_FAILED = "Failed";

    public static final String JOB_STATUS_CANCELLED = "Cancelled";

    public static final String JOB_STATUS_CREATED = "Created";

    public static final long interval = 5000;
    public static final int timeoutRequests = 17280;

    // testcase statuses
    public static final int TEST_STATUS_PASSED = 1;
    public static final int TEST_STATUS_FAILED = 2;
    public static final int TEST_STATUS_SKIPPED = 0;

    // response statuses
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_ACCEPTED = 202;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_METHOD_NOT_ALLOWED = 405;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int STATUS_UNPROCESSABLE = 422;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_GATEWAY_TIMEOUT = 504;
    public static final int STATUS_BAD_GATEWAY = 502;
}
