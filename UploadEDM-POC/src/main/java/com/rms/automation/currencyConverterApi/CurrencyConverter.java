package com.rms.automation.currencyConverterApi;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    public static void convert(Map<String, String> tc, String analysisId) throws Exception {
        int newAnalysisIdConvertCurrency=0;
        System.out.println("***** Running Currency Converter API ********");

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> currency = new HashMap<>();
        currency.put("code", tc.get("CCU_CURRENCY"));
        currency.put("scheme", tc.get("CCU_CURRENCY_SCHEME"));
        currency.put("vintage", tc.get("CCU_CURRENCY_VERSION"));
        currency.put("asOfDate", tc.get("CCU_AS_OF_DATE"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("currency", currency);

        Response response = ApiUtil.convertCurrencyApi(payload, analysisId, token);
        System.out.println("currencyConverter  Status: " + response.getStatusCode());
        if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("exportFile_wf_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = JobsApi.waitForJobToComplete(jobId, token, "Convert Currency API");;
            System.out.println("wait for job msg: " + msg);
            newAnalysisIdConvertCurrency = JobsApi.getnewAnalysisIDByJobId(jobId, token);
            if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED ) && (!jobId.isEmpty()))
            {
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCU_CONVERT_CURRENCY_JOB_ID", jobId);
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCU_CONVERT_CURRENCY_NEW_ANALYSIS_ID", String.valueOf(newAnalysisIdConvertCurrency));

            }


        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("currencyConverter Message: " + msg);
        }

    }
}