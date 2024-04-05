package automation.currencyConverter;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.utils.Utils;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyConverter {

    public static void convert(Map<String, String> tc, String analysisId) throws Exception {
        System.out.println("***** Running Currency Converter API ********");

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> currency = new HashMap<>();
        currency.put("code", tc.get("Currency"));
        currency.put("scheme", tc.get("Currency Scheme"));
        currency.put("vintage", tc.get("Currency Version"));
        currency.put("asOfDate", tc.get("asOfDate"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("currency", currency);

        Response response = ApiUtil.convertCurrencyApi(payload, analysisId, token);
        System.out.println("currencyConverter  Status: " + response.getStatusCode());
        if (response.getStatusCode() == 202) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("exportFile_wf_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = ApiUtil.waitForJobToComplete(jobId, token);
            System.out.println("waitforjob msg: " + msg);
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("currencyConverter Message: " + msg);
        }

    }
}