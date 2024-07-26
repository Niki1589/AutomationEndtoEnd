package com.rms.automation.LossValidation;

import java.util.HashMap;
import java.util.Map;

public enum Analysis_Type_ENUM {
    EP("EP"),
    HISTORICAL("Historical"),
    FOOTPRINT("Footprint"),
    SCENARIO("Scenario"),;

    private String value;
    private static final Map<String, Analysis_Type_ENUM> map = new HashMap<>();

    static {
        for (Analysis_Type_ENUM format : Analysis_Type_ENUM.values()) {
            map.put(format.value, format);
        }
    }

    Analysis_Type_ENUM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Analysis_Type_ENUM getByValue(String value) {
        return map.get(value);
    }
}
