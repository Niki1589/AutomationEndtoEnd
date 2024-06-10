package com.rms.automation.exportApi.RDMExport;

import java.util.HashMap;
import java.util.Map;

public enum REX_RDM_LOCATION_ENUM {
    PLATFORM("Platform"),
    DATABRIDGE("DataBridge");

    private String value;
    private static final Map<String, REX_RDM_LOCATION_ENUM> map = new HashMap<>();

    static {
        for (REX_RDM_LOCATION_ENUM format : REX_RDM_LOCATION_ENUM.values()) {
            map.put(format.value, format);
        }
    }

    REX_RDM_LOCATION_ENUM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static REX_RDM_LOCATION_ENUM getByValue(String value) {
        return map.get(value);
    }
}
