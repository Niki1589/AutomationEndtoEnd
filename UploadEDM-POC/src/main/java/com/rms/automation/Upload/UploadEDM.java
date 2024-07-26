package com.rms.automation.Upload;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.LoadData;
import com.rms.automation.edm.TestCase;
import com.rms.automation.edm.ApiUtil;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;

import java.util.*;

public class UploadEDM extends TestCase {

    public void executeUploadEdm(Map<String, String> tc) throws NullPointerException, Exception {

        int actualresponse;
        String fileName = tc.get("EXP_EDM_FILE_NAME");
        String filePath = tc.get("EXP_EDM_FILE_PATH") + "/" + fileName;
        System.out.println("File path is " + filePath);
        String fileExt = tc.get("EXP_FILE_EXT");
        String dbType = tc.get("EXP_DB_TYPE");
        String shareWith = tc.get("EXP_OPT_SHARE_GROUP");


        try {

            String token = ApiUtil.getSmlToken(tc);
            String dataSourceName =tc.get("EXP_EDM_DATASOURCE_NAME");
            //Get the group ids, if any exists.
            List<String> ids = ApiUtil.getGroupIds(token, shareWith);
            System.out.println(dataSourceName + " Group Ids: " + ids.toString());
            if (dataSourceName.isEmpty()) {
                throw new Exception("EDM name not defined, please define the EDM name in the test case sheet");
            }

            Response res = ApiUtil.findEdmByName(token, dataSourceName);
            Boolean isEdmExists = !res.getBody().jsonPath().getMap("$").get("searchTotalMatch").equals(0);

            if (isEdmExists) {
                System.out.println("EDM "+dataSourceName+" already exists on Risk Modeler, using the existing EDM");
                }
            else {

                //Perform the multipart upload
                Boolean status = ApiUtil.fileMultiPartUpload(token, dbType, filePath, fileExt, fileName);
                System.out.println("Status of fileMultiPartUpload: " + status);

                //Upload EDM


                Map<String, Object> payload = new HashMap<>();
                if (ids != null) {
                    payload.put("groups", ids);
                    payload.put("share", (ids.size() > 0));
                } else {
                    payload.put("groups", new ArrayList<String>());
                    payload.put("share", false);
                }

                Response submitJobResponse = ApiUtil.uploadEDM(token, dataSourceName, payload);
                actualresponse = submitJobResponse.getStatusCode();
                System.out.println("UploadEDM Response: " + actualresponse);
                String locationHdr = submitJobResponse.getHeader("Location");
                String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                String workflowId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                System.out.println("workflowId: " + workflowId);

                // Wait for EDM upload job to complete
                String msg = JobsApi.waitForJobToComplete(jobId, token, "Upload EDM API",
                        "UPLOAD_EDM_JOB_STATUS", tc.get("INDEX"));
                System.out.println("wait for job msg: " + msg);

                if (actualresponse == AutomationConstants.STATUS_ACCEPTED && (msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))) {
                    if (dataSourceName != "") {
                        LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "EXP_UPLOAD_EDM_JOBID", jobId);
                    }
                }
            }

            String portfolioId = tc.get("EXP_EXISTING_PORTFOLIO_ID");

            //Batch API call
            BatchTests batchTests = new BatchTests();
            batchTests.batchAPI(tc, portfolioId, dataSourceName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
