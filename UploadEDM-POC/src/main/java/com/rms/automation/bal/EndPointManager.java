package com.rms.automation.bal;

import com.rms.automation.edm.LoadData;

import java.util.HashMap;
import java.util.Map;

public class EndPointManager {

    public static Map<String, String> apiendpoints = new HashMap<>();
    public static String baseUrl;

//    static {
//        apiendpoints = LoadData.readApiEndPointsFromLocal();
//        baseUrl = apiendpoints.get("baseUrl");
//    }


    static {
        apiendpoints = LoadData.readApiEndPointsFromLocal();
    }

    public static void setBaseUrlByTenant(String tenant) throws Exception {
        baseUrl = apiendpoints.get(tenant);
        if (baseUrl == null) {
            throw new Exception("Tenant not found in API Endpoints");
        }
    }

}
