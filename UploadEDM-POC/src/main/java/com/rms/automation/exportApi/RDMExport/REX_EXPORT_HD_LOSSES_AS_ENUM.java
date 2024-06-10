package com.rms.automation.exportApi.RDMExport;

import java.util.HashMap;
import java.util.Map;

public enum REX_EXPORT_HD_LOSSES_AS_ENUM {
    ELT("ELT"),
    PLT("PLT");

    private String value;
    private static final Map<String, REX_EXPORT_HD_LOSSES_AS_ENUM> map = new HashMap<>();

    static {
        for (REX_EXPORT_HD_LOSSES_AS_ENUM format : REX_EXPORT_HD_LOSSES_AS_ENUM.values()) {
            map.put(format.value, format);
        }
    }

    REX_EXPORT_HD_LOSSES_AS_ENUM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static REX_EXPORT_HD_LOSSES_AS_ENUM getByValue(String value) {
        return map.get(value);
    }
}
