package com.rms.automation.climateChange;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ClimateChangeTests {

    public static void climateChange(Map<String, String> tc, String analysisId) throws Exception {

        String analysisName= "";

        System.out.println("***** Running Climate Change API ********");

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

      Response jsonResponse=ApiUtil.getAnalysisNameByAnalysisId(token,analysisId);

      //if(jsonResponse.getStatusCode())
        if (jsonResponse.getStatusCode() == AutomationConstants.STATUS_OK) {

            // Extract the Analysis Name field from the JSON response
            analysisName = jsonResponse.jsonPath().getString("searchItems[0].name");
        }

        else
        {
            String msg = jsonResponse.getBody().jsonPath().get("message");
            System.out.println("Climate Change  Message: " + msg);
        }

        // Print the extracted name
        System.out.println("Analysis Name is : " + "");

        Map<String, Object> climateChangePayload = new HashMap<>();
      //  climateChangePayload.put("analysisName",analysisName);
        climateChangePayload.put("referenceRateSchemeId", tc.get("referenceRateSchemeId"));
        climateChangePayload.put("climateConditionView", "Default");
        climateChangePayload.put("is2CWarmingScenario", tc.get("is2CWarmingScenario"));
        climateChangePayload.put("rcpScenario", tc.get("rcpScenario"));
        climateChangePayload.put("timeHorizon", tc.get("timeHorizon"));

        Response response = ApiUtil.climateChangeApi(climateChangePayload, analysisId, token);
        System.out.println("Climate Change API Status: " + response.getStatusCode());
        if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("exportFile_wf_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = JobsApi.waitForJobToComplete(jobId, token, "Climate Change API");;
            System.out.println("wait for job msg: " + msg);
            //newAnalysisIdConvertCurrency = JobsApi.getnewAnalysisIDByJobId(jobId, token);
//            if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED ) && (!jobId.isEmpty()))
//            {
//                LoadData.UpdateTCInLocalCSV(tc.get("index"), "ConvertCurrencyJobId", jobId);
//                LoadData.UpdateTCInLocalCSV(tc.get("index"), "ConvertCurrencyNewAnalysisId", String.valueOf(newAnalysisIdConvertCurrency));
//
//            }


        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("currencyConverter Message: " + msg);
        }

    }
}
