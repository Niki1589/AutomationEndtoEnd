package automation.renameAnalysis;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RenameAnalysis {

    public static void rename(Map<String, String> tc, String analysisId) throws Exception {
        System.out.println("***** Running Rename Analysis API ********");

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> payload = new HashMap<>();
        payload.put("newAnalysisName", tc.get("newAnalysisName"));

        Response response = ApiUtil.renameAnalysisApi(payload, analysisId, token);
        System.out.println("renameAnalysisApi  Status: " + response.getStatusCode());
        if (response.getStatusCode() == 202) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("renameAnalysisApi_wp_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = ApiUtil.waitForJobToComplete(jobId, token);
            System.out.println("waitforjob msg: " + msg);
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("renameAnalysisApi Message: " + msg);
        }

    }
}