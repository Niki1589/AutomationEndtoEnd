package com.rms.automation.climateChange;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.LossValidation.LossValidation;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.FileExportTests;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class ClimateChangeTests {

    public static String[] ccAnalysisIdArray = {""};
    public static List<String> jobIds = new ArrayList<>();
    public static Map<String, String> analysisIdMap = new HashMap<>();
    public static void climateChange(Map<String, String> tc, String analysisId) throws Exception {

        System.out.println("***** Running Climate Change API ********");
        String token = ApiUtil.getSmlToken(tc);
        if ((tc.get("MPF_MODEL_REGION").equalsIgnoreCase("EUWS") || tc.get("MPF_MODEL_REGION").equalsIgnoreCase("EUFL") || (tc.get("MPF_MODEL_REGION").equalsIgnoreCase("USFL")) || tc.get("MPF_MODEL_REGION").equalsIgnoreCase("NAWF") || tc.get("MPF_MODEL_REGION").equalsIgnoreCase("JPWS"))) {
            String analysisName = "";

            Response jsonResponse = ApiUtil.getAnalysisNameByAnalysisId(token, analysisId);

            if (jsonResponse.getStatusCode() == AutomationConstants.STATUS_OK) {
                // Extract the Analysis Name field from the JSON response
                analysisName = jsonResponse.jsonPath().getString("searchItems[0].name");
            } else {
                String msg = jsonResponse.getBody().jsonPath().get("message");
                System.out.println("Climate Change  Message: " + msg);
            }
            List<String> rcpScenarioList = List.of(tc.get("CCG_RCP_SCENARIO").split(","));
            List<String> timeHorizonList = List.of(tc.get("CCG_TIME_HORIZON").split(","));

            // Executor service for asynchronous tasks
            ExecutorService executor = Executors.newFixedThreadPool((rcpScenarioList.size() * timeHorizonList.size()));
            // Completion service for handling completed tasks
            CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

            for (String rcpScenario : rcpScenarioList) {
                for (String timeHorizon : timeHorizonList) {
                    Map<String, Object> climateChangePayload = new HashMap<>();
                    //  climateChangePayload.put("analysisName",analysisName);
                    climateChangePayload.put("referenceRateSchemeId",tc.get("CCG_REFERENCE_RATE_SCHEMEID"));
                    climateChangePayload.put("climateConditionView", tc.get("CCG_CLIMATE_CONDITION_VIEW"));
                    climateChangePayload.put("is2CWarmingScenario", tc.get("CCG_CLIMATE_CONDITION_VIEW").equalsIgnoreCase("YES"));
                    climateChangePayload.put("rcpScenario", rcpScenario);
                    climateChangePayload.put("timeHorizon", Integer.parseInt(timeHorizon));
                    completionService.submit(() -> {
                        System.out.println("Started: Climate Change with rcpScenario: " + rcpScenario + " CCG_TIME_HORIZON: " + timeHorizon);
                        Response response = ApiUtil.climateChangeApi(climateChangePayload, analysisId, token);
                        System.out.println("Climate Change API Status: " + response.getStatusCode());
                        if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
                            String locationHdr = response.getHeader("Location");
                            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                            System.out.println("exportFile_wf_id: " + jobId);
                            if (jobId == null) {
                                throw new Exception("JobId is null");
                            }
                            jobIds.add(jobId);
                            String msg = JobsApi.waitForJobToComplete(jobId, token, "Climate Change API",
                                    "CCG_CLIMATE_CHANGE_JOB_STATUS", tc.get("INDEX"));

                            System.out.println("wait for job msg: " + msg);
                            if (msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty())) {
                                Response jobDetails = JobsApi.getJobDetailsByJobId(token, jobId);
                                JsonPath jsonPath = jobDetails.jsonPath();
                                Map<String, Object> jobResponseMap = jsonPath.getMap("$");
                                Map<String, Object> summaryMap = (Map<String, Object>) jobResponseMap.get("summary");
                                //LossValidation.run(tc);

                                //ccAnalysisId = String.valueOf(summaryMap.get("climateChangeAnalysisId"));

//                                String[] ccAnalysisIdArray = { String.valueOf(summaryMap.get("climateChangeAnalysisId")) };
//
//
//                                for (int i = 0; i < ccAnalysisIdArray.length; i++) {
//                                    analysisIdMap.put("climateChangeAnalysisId", ccAnalysisIdArray[i]);
//                                }                                // FileExportTests.fileExport(tc, ccAnalysisId,"CCG_FILE_EXPORT_JOB_ID");
//
                                //  LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCG_CLIMATE_CHANGE_JOBID", id);

                                if (jobIds.size() > 1) {
                                    // Create a StringBuilder to concatenate IDs
                                    StringBuilder sb = new StringBuilder();

                                    // Iterate through jobIds and append each ID to the StringBuilder
                                    for (String id : jobIds) {
                                        sb.append(id).append(",");
                                    }

                                    // Remove the trailing comma if there are IDs present
                                    if (sb.length() > 0) {
                                        sb.deleteCharAt(sb.length() - 1);
                                    }

                                    // Convert StringBuilder to a comma-separated string of IDs
                                    String commaSeparatedIds = sb.toString();

                                    // Call LoadData.UpdateTCInLocalExcel with the comma-separated IDs
                                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCG_CLIMATE_CHANGE_JOBID", commaSeparatedIds);
                                } else if (jobIds.size() == 1) {
                                    // Only one ID, directly pass it to LoadData.UpdateTCInLocalExcel
                                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCG_CLIMATE_CHANGE_JOBID", jobIds.get(0));
                                } else {
                                    System.out.println("Please check the Job Id and try again");
                                }


                                Object climateChangeAnalysisIdObj = summaryMap.get("climateChangeAnalysisId");

                                if (climateChangeAnalysisIdObj != null) {
                                    String[] ccAnalysisIdArray;

                                    if (climateChangeAnalysisIdObj instanceof String) {
                                        ccAnalysisIdArray = ((String) climateChangeAnalysisIdObj).split(",");
                                    } else if (climateChangeAnalysisIdObj instanceof Object[]) {
                                        ccAnalysisIdArray = Arrays.stream((Object[]) climateChangeAnalysisIdObj)
                                                .map(Object::toString)
                                                .toArray(String[]::new);
                                    } else {
                                        ccAnalysisIdArray = new String[0];
                                    }

                                    for (int i = 0; i < ccAnalysisIdArray.length; i++) {
                                        // Store each analysis ID with a unique key based on rcpScenario and timeHorizon
                                        analysisIdMap.put("climateChangeAnalysisId_" + rcpScenario + "_" + timeHorizon + "_" + i, ccAnalysisIdArray[i]);
                                    }
                                }

                            }
                        }
                        else {
                            String msg = response.getBody().jsonPath().get("message");
                            System.out.println("ClimateChange Message: " + msg);
                        }
                        return "FINISHED: Climate Change with rcpScenario: " + rcpScenario + " timeHorizon: " + timeHorizon;
                    });
                }
            }

            System.out.println("Climate Change APi with Its Combinations is running in Background");

            // Handle completed tasks
            int completedTasks = 0;
            while (completedTasks < (rcpScenarioList.size() * timeHorizonList.size())) {
                try {
                    Future<String> future = completionService.take(); // Wait for task completion
                    String result = future.get(); // Get task result
                    if (result != null) {
                        completedTasks++;
                        System.out.println("Task" + completedTasks + " :::completed");
                        System.out.println("Result: "+ result);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // All tasks completed
            System.out.println("All completed");

            // Export files for all analysis ids
            FileExportTests.jobIds = new ArrayList<>();
            for (Map.Entry<String, String> entry : analysisIdMap.entrySet()) {
                String key = entry.getKey();       // This will be "climateChangeAnalysisId"
                String ccAnalysisId = entry.getValue();  // This will be the value associated with "climateChangeAnalysisId"

                FileExportTests.fileExport(tc, ccAnalysisId, "CCG_FILE_EXPORT_JOB_ID");
                //LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCG_FILE_EXPORT_JOB_ID", jobId);

            }
        } else {

            System.out.println("This Model is not compatible with Climate Change.Please check the model region code and try again.");

        }
    }
    }
