package com.rms.automation.grouping;

import com.google.gson.Gson;
import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.apiManager.ApiUtil;
import com.rms.automation.dataProviders.LoadData;
import com.rms.automation.utils.Utils;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.rms.automation.JobsApi.JobsApi.getAnalysisIDByJobId_Pate;

public class AnalysisGroupingTests {

    @Test
    public static void execute() throws Exception {

        List<Map<String, String>> dataList = LoadData.readCaseTCFromLocalExcel_grouping();
        Object[] testCases = LoadData.readCaseTCFromLocalExcel();

        System.out.println("Grouping Test Cases");
        for (Map<String, String> tc : dataList) {

            if (tc.get("is_grouping").equalsIgnoreCase("yes")) {
                String cases = tc.get("CandidateAnalysis_TestCases");
                List<String> listOfCases = Arrays.asList(cases.split(","));

                List<Object> filteredTC = Arrays.stream(testCases).filter(r -> listOfCases.contains((((HashMap) r).get("TEST_CASE_NO")))).collect(Collectors.toList());
                List<Map<String, String>> filteredGroupings = dataList.stream().filter(r -> listOfCases.contains(r.get("Group_TestCase"))).collect(Collectors.toList());

                List<String> analysisIds = new ArrayList<>();

                //check if the test cases have a valid analysis Id's or not.
                filteredTC.forEach(r -> {
                    String analysisId = (String) ((HashMap) r).get("MRN_ANALYSIS_ID");
                    if (Utils.validateAnalysisId(analysisId)) {
                        analysisIds.add(analysisId);
                    }
                });
                filteredGroupings.forEach(r -> {
                    String analysisId = r.get("analysisId_group");
                    if (Utils.validateAnalysisId(analysisId)) {
                        analysisIds.add(analysisId);
                    }
                });

                if(analysisIds.size() > 0) {
                    Object payloadObject = getPayload(tc, analysisIds.toString());
                    System.out.println("Grouping Payload Loaded");
                    runGrouping(tc, payloadObject);
                }
            }

        }
    }

    private static void runGrouping(Map<String, String> tc,Object payloadObject) throws Exception {


        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        try {
                System.out.println("***** Running Analysis Grouping API ********");

                Response batchResponse = ApiUtil.groupingAPI(token, payloadObject);
                String hdr = batchResponse.getHeader("Location");
                String jobId = hdr.substring(hdr.lastIndexOf('/') + 1);
                String msg = null;
                try {
                    msg = JobsApi.waitForJobToComplete(jobId, token, "Analysis Grouping API");
                    System.out.println("wait for job msg: " + msg);
                    if (msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty())) {
                        LoadData.UpdateTCInGroupingExcel(tc.get("index"), "jobId_group", jobId);
                        String analysisId_Grouping = String.valueOf(getAnalysisIDByJobId_Pate(jobId,token));

                        if(analysisId_Grouping!=null)
                        {
                            LoadData.UpdateTCInGroupingExcel(tc.get("index"), "analysisId_group", analysisId_Grouping);

                        }



                    }
                } catch (Exception e) {
                    System.out.println("Error in waitForJobToComplete : " + e.getMessage());
                    throw new RuntimeException(e);
                }

                System.out.println("wait for job msg: " + msg);
                System.out.println("***** Finished Analysis Grouping Tests");




        } catch (Exception e) {
            System.out.println("Error in Code = " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static Object getPayload(Map<String, String> data, String analysisIds) {

        Grouping obj = GroupingMapper.map(data);

        Gson gson = new Gson(); //To convert JSON to Java object, we use GSON library from google
        String payloadInString = "" +
                "{\n" +
                "    \"name\": \""+obj.getName()+"\",\n" +
                "    \"description\": \""+obj.getDescription()+"\",\n" +
                "    \"currency\": {\n" +
                "        \"code\": \""+obj.getCode()+"\",\n" +
                "        \"scheme\": \""+ obj.getScheme()+"\",\n" +
                "        \"vintage\": \""+ obj.getVintage() +"\",\n" +
                "        \"asOfDate\": \""+ obj.getAsOfDate() +"\"\n" +
                "    },\n" +
                "    \"numOfSimulations\": \""+obj.getNumOfSimulations()+"\",\n" +
                "    \"analysisIds\": "+analysisIds+", "+
                "    \"propagateDetailedLosses\": "+obj.getPropagateDetailedLosses()+",\n" +
                "    \"simulationWindowStart\": \""+obj.getSimulationWindowStart()+"\",\n" +
                "    \"simulationWindowEnd\": \""+obj.getSimulationWindowEnd()+"\",\n" +
                "    \"reportingWindowStart\": \""+obj.getReportingWindowStart()+"\",\n" +
                "    \"regionPerilSimulationSet\": [\n" +
                        obj.getRegionPerilSimulationSet() +
                "    ]\n" +
                "}" +
                "";
        return gson.fromJson(payloadInString, Object.class);
    }

}