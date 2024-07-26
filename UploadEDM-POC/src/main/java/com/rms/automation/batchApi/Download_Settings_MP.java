package com.rms.automation.batchApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rms.automation.exportApi.Download_Settings;

public class Download_Settings_MP {

    public String getExcludePostalCodes() {
        return excludePostalCodes;
    }

    public void setExcludePostalCodes(String excludePostalCodes) {
        this.excludePostalCodes = excludePostalCodes;
    }

    @JsonProperty("excludePostalCodes")
    String excludePostalCodes;

    public String getFireOnly() {
        return fireOnly;
    }

    public void setFireOnly(String fireOnly) {
        this.fireOnly = fireOnly;
    }

    @JsonProperty("fireOnly")
    String fireOnly;

    public String getPerilOverride() {
        return perilOverride;
    }

    public void setPerilOverride(String perilOverride) {
        this.perilOverride = perilOverride;
    }

    @JsonProperty("perilOverride")
    String perilOverride;

    public String getDynamicAutomobileModeling() {
        return dynamicAutomobileModeling;
    }

    public void setDynamicAutomobileModeling(String dynamicAutomobileModeling) {
        this.dynamicAutomobileModeling = dynamicAutomobileModeling;
    }

    @JsonProperty("dynamicAutomobileModeling")
    String dynamicAutomobileModeling;

    public String getIncludePluvial() {
        return includePluvial;
    }

    public void setIncludePluvial(String includePluvial) {
        this.includePluvial = includePluvial;
    }

    @JsonProperty("includePluvial")
    String includePluvial;

    public String getIncludeBespokeDefence() {
        return includeBespokeDefence;
    }

    public void setIncludeBespokeDefence(String includeBespokeDefence) {
        this.includeBespokeDefence = includeBespokeDefence;
    }

    @JsonProperty("includeBespokeDefence")
    String includeBespokeDefence;

    public String getDefenceOn() {
        return defenceOn;
    }

    public void setDefenceOn(String defenceOn) {
        this.defenceOn = defenceOn;
    }

    @JsonProperty("defenceOn")
    String defenceOn;

    public String getRun1dOnly() {
        return run1dOnly;
    }

    public void setRun1dOnly(String run1dOnly) {
        this.run1dOnly = run1dOnly;
    }

    @JsonProperty("run1dOnly")
    String run1dOnly;

    public  static Download_Settings_MP parse(String json) throws JsonProcessingException {
        if (json != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Download_Settings_MP obj = objectMapper.readValue(json, Download_Settings_MP.class);

            return  obj;

        }
        return new Download_Settings_MP();
    }

}
