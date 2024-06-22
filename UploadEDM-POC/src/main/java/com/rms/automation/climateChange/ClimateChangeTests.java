package com.rms.automation.climateChange;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClimateChangeTests {

    public static void climateChange(Map<String, String> tc, String analysisId) throws Exception {

        System.out.println("***** Running Climate Change API ********");
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

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
                            String msg = JobsApi.waitForJobToComplete(jobId, token, "Climate Change API",
                                    "CCG_CLIMATE_CHANGE_JOB_STATUS", tc.get("INDEX"));

                            System.out.println("wait for job msg: " + msg);
                            if (msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty())) {
                                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "CCG_CLIMATE_CHANGE_JOBID", jobId);
                            }
                        } else {
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
            while (completedTasks <= (rcpScenarioList.size() * timeHorizonList.size())) {
                try {
                    Future<String> future = completionService.take(); // Wait for task completion
                    String result = future.get(); // Get task result
                    if (result != null) {
                        completedTasks++;
                        System.out.println("Task" + completedTasks + " :::completed");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // All tasks completed
            System.out.println("All completed");

        } else {

            System.out.println("This Model is not compatible with Climate Change.Please check the model region code and try again.");

        }
    }
    }
