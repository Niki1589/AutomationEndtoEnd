package com.rms.automation.Upload;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.edm.TestCase;
import com.rms.automation.exportApi.RDMExport.REX_EXPORT_HD_LOSSES_AS_ENUM;
import com.rms.automation.exportApi.RDMExport.RdmExportTests;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

public class UploadRDM extends TestCase {

    public static void executeUploadRdm(Map<String, String> tc) throws NullPointerException, Exception {

            int actualresponse;
             String fileName = tc.get("IMPR_ANALYSIS_FROM_RDM_FILE_NAME");
           // String fileName = RdmExportTests.fileName;

              String filePath = tc.get("IMPR_ANALYSIS_FROM_RDM_FILE_PATH");
           // String filePath = RdmExportTests.localPath;

            System.out.println("File path is " + filePath);
            String fileExt = tc.get("IMPR_ANALYSIS_FROM_RDM_FILE_EXT");
            String dbType = tc.get("IMPR_ANALYSIS_FROM_RDM_DB_TYPE");
            String rdmName =
                    fileName + "_" + RandomStringUtils.randomNumeric(5) + "." + fileExt;
            try {

                String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
                Boolean status = ApiUtil.fileMultiPartUpload(token, dbType, filePath, fileExt, rdmName);
                String payload = "{\"rdmName\":\"" + rdmName + "\",\"uploadId\":\"" + ApiUtil.getRmsUploadId() + "\",\"share\":\"false\",\"groups\":[]}";
                System.out.println("Status of fileMultiPartUpload: " + status);
                Response submitJobResponse = ApiUtil.uploadRDM(token, fileName, payload);
                actualresponse = submitJobResponse.getStatusCode();
                if (actualresponse == 202) {
                    System.out.println("UploadRDM Response: " + actualresponse);
                    String locationHdr = submitJobResponse.getHeader("Location");
                    String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                    String workflowId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                    System.out.println("workflowId: " + workflowId);

                    String msg = JobsApi.waitForJobToComplete(jobId, token, "Upload RDM API",
                            "IMPR_ANALYSIS_JOB_STATUS", tc.get("INDEX"));
                    System.out.println("wait for job msg: " + msg);
                    if (msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty())) {
                        LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "IMPR_ANALYSIS_FROM_RDM_JOB_ID", jobId);

                    }
                } else {
                    String msg = submitJobResponse.getBody().jsonPath().get("message");
                    System.out.println("Upload RDM To RM: " + msg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

