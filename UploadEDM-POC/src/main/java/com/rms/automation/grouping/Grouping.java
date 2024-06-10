package com.rms.automation.grouping;

public class Grouping {
    private String name;
    private String description;
    private String numOfSimulations;
    private String analysisIds;
    private String propagateDetailedLosses;
    private String simulationWindowStart;
    private String  simulationWindowEnd;
    private String reportingWindowStart;
    private String code;
    private String scheme;
    private String vintage;
    private String asOfDate;
    private String regionPerilSimulationSet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumOfSimulations() {
        return numOfSimulations;
    }

    public void setNumOfSimulations(String numOfSimulations) {
        this.numOfSimulations = numOfSimulations;
    }

    public String getAnalysisIds() {
        return analysisIds;
    }

    public void setAnalysisIds(String analysisIds) {
        this.analysisIds = analysisIds;
    }

    public String getPropagateDetailedLosses() {
        return propagateDetailedLosses;
    }

    public void setPropagateDetailedLosses(String propagateDetailedLosses) {
        this.propagateDetailedLosses = propagateDetailedLosses;
    }

    public String getSimulationWindowStart() {
        return simulationWindowStart;
    }

    public void setSimulationWindowStart(String simulationWindowStart) {
        this.simulationWindowStart = simulationWindowStart;
    }

    public String getSimulationWindowEnd() {
        return simulationWindowEnd;
    }

    public void setSimulationWindowEnd(String simulationWindowEnd) {
        this.simulationWindowEnd = simulationWindowEnd;
    }

    public String getReportingWindowStart() {
        return reportingWindowStart;
    }

    public void setReportingWindowStart(String reportingWindowStart) {
        this.reportingWindowStart = reportingWindowStart;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getVintage() {
        return vintage;
    }

    public void setVintage(String vintage) {
        this.vintage = vintage;
    }

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    public String getRegionPerilSimulationSet() {
        return regionPerilSimulationSet;
    }

    public void setRegionPerilSimulationSet(String regionPerilSimulationSet) {
        this.regionPerilSimulationSet = regionPerilSimulationSet;
    }
}
