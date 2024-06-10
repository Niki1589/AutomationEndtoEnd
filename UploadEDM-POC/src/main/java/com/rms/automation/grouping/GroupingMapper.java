package com.rms.automation.grouping;

import com.rms.automation.utils.Utils;

import java.util.Map;

public class GroupingMapper {

    public static Grouping map(Map<String, String> data) {

        Grouping obj = new Grouping();

        obj.setName(data.get("name"));
        obj.setDescription(data.get("description"));
        obj.setNumOfSimulations(data.get("numOfSimulations"));
        obj.setAnalysisIds(data.get("analysisIds"));
        obj.setPropagateDetailedLosses(data.get("propagateDetailedLosses"));
        obj.setSimulationWindowStart(Utils.formatDate("MM/dd/yyyy", data.get("simulationWindowStart")));//"01/01/2021"
        obj.setSimulationWindowEnd(Utils.formatDate("MM/dd/yyyy", data.get("simulationWindowEnd")));//"12/31/2021"
        obj.setReportingWindowStart(Utils.formatDate("MM/dd/yyyy", data.get("reportingWindowStart")));//"01/01/2021"
        obj.setCode(data.get("code"));
        obj.setScheme(data.get("scheme"));
        obj.setVintage(data.get("vintage"));
        obj.setAsOfDate(Utils.formatDate("yyyy-MM-dd", data.get("asOfDate")));//"2020-03-01"
        obj.setRegionPerilSimulationSet(data.get("regionPerilSimulationSet"));

        return obj;

    }

}
