package com.rms.automation.bal;

import com.rms.automation.edm.LoadData;

import java.util.HashMap;
import java.util.Map;

public class EndPointManager {

    public static Map<String, String> apiendpoints = new HashMap<>();
    public static String baseUrl;
    static {
        apiendpoints = LoadData.readApiEndPointsFromLocal();
        baseUrl = apiendpoints.get("baseUrl");
    }

}
