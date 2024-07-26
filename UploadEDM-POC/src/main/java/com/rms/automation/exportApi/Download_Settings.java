package com.rms.automation.exportApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Download_Settings {

    @JsonProperty("IsStatsMetric")
    String IsStatsMetric;

    @JsonProperty("outputLevels_StatsMetric")
    String outputLevels_StatsMetric;

    @JsonProperty("perspectives_StatsMetric")
    String perspectives_StatsMetric;

    @JsonProperty("IsEPMetric")
    String IsEPMetric;

    @JsonProperty("outputLevels_EPMetric")
    String outputLevels_EPMetric;

    @JsonProperty("perspectives_EPMetric")
    String perspectives_EPMetric;

    @JsonProperty("IsLossTablesMetric")
    String IsLossTablesMetric;

    @JsonProperty("outputLevels_LossTablesMetric")
    String outputLevels_LossTablesMetric;

    @JsonProperty("perspectives_LossTablesMetric")
    String perspectives_LossTablesMetric;

    public String getIsStatsMetric() {
        return IsStatsMetric;
    }

    public void setIsStatsMetric(String isStatesMetric) {
        IsStatsMetric = isStatesMetric;
    }

    public String getOutputLevels_StatesMetric() {
        return outputLevels_StatsMetric;
    }

    public void setOutputLevels_StatesMetric(String outputLevels_StatesMetric) {
        this.outputLevels_StatsMetric = outputLevels_StatesMetric;
    }

    public String getperspectives_StatsMetric() {
        return perspectives_StatsMetric;
    }

    public void setPerspectives_StatsMetric(String perspectives_StatesMetric) {
        this.perspectives_StatsMetric = perspectives_StatesMetric;
    }

    public String getIsEPMetric() {
        return IsEPMetric;
    }

    public void setIsEPMetric(String isEPMetric) {
        IsEPMetric = isEPMetric;
    }

    public String getOutputLevels_EPMetric() {
        return outputLevels_EPMetric;
    }

    // Adjusted method to return a list of values
//    public List<String> getOutputLevels_EPMetric() {
//        // Split the input string by commas and trim any whitespace
//        return Arrays.stream(outputLevels_EPMetric.split(","))
//                .map(String::trim)
//                .collect(Collectors.toList());
//    }

    public void setOutputLevels_EPMetric(String outputLevels_EPMetric) {
        this.outputLevels_EPMetric = outputLevels_EPMetric;
    }

    public String getPerspectives_EPMetric() {
        return perspectives_EPMetric;
    }

    public void setPerspectives_EPMetric(String perspectives_EPMetric) {
        this.perspectives_EPMetric = perspectives_EPMetric;
    }

    public String getIsLossTablesMetric() {
        return IsLossTablesMetric;
    }

    public void setIsLossTablesMetric(String isLossTablesMetric) {
        IsLossTablesMetric = isLossTablesMetric;
    }

    public String getOutputLevels_LossTablesMetric() {
        return outputLevels_LossTablesMetric;
    }

    public void setOutputLevels_LossTablesMetric(String outputLevels_LossTablesMetric) {
        this.outputLevels_LossTablesMetric = outputLevels_LossTablesMetric;
    }

    public String getPerspectives_LossTablesMetric() {
        return perspectives_LossTablesMetric;
    }

    public void setPerspectives_LossTablesMetric(String perspectives_LossTablesMetric) {
        this.perspectives_LossTablesMetric = perspectives_LossTablesMetric;
    }

    public  static Download_Settings parse(String json) throws JsonProcessingException {
        if (json != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Download_Settings obj = objectMapper.readValue(json, Download_Settings.class);

            return  obj;

        }
        return  new Download_Settings();
    }

}
