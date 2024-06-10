package com.rms.automation.renameAnalysisApi;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RenameAnalysis {

    public static void rename(Map<String, String> tc, String analysisId) throws Exception {
        System.out.println("***** Running Rename Analysis API ********");

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> payload = new HashMap<>();
        payload.put("newAnalysisName", tc.get("RNM_NEW_ANALYSIS_NAME"));

        Response response = ApiUtil.renameAnalysisApi(payload, analysisId, token);
        System.out.println("renameAnalysisApi  Status: " + response.getStatusCode());
        if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("renameAnalysisApi_wp_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = JobsApi.waitForJobToComplete(jobId, token, "Rename Analysis API");
            System.out.println("wait for job msg: " + msg);
            if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) &&(!jobId.isEmpty()))
            {
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "RNM_RENAME_ANALYSIS_JOBID", jobId);

            }
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("rename Analysis Api Message: " + msg);
        }

    }
}