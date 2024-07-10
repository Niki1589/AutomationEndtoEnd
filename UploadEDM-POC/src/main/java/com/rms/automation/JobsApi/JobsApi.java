package com.rms.automation.JobsApi;

import com.rms.automation.bal.EndPointManager;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.core.qe.common.RestApiHelper;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JobsApi {

    public static Response getJobDetailsByJobId(String authToken, String jobId) {
        String api = String.format(EndPointManager.apiendpoints.get("detailsByJobID"), jobId);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitGet();
        return response;
    }

    public static String getAnalysisIDByJobId(String workflowId,String authToken)
    {
        String subJobId = "";
        String analysisID = "";
        try
        {
            Response jobGetJobDetailsResponse = getJobDetailsByJobId(authToken, workflowId);
            if(jobGetJobDetailsResponse.getStatusCode()== AutomationConstants.STATUS_OK)
            {
                subJobId= (String) ((LinkedHashMap)(((ArrayList) jobGetJobDetailsResponse.getBody().jsonPath().getMap("$").get("jobs")).stream().filter(j -> ((LinkedHashMap) j).get("name").toString().equals("PROCESS")).findFirst().orElse(null))).get("id");
                System.out.println(subJobId);
            }
            Response subJobGetJobDetailsResponse = getsubJobDetailsByJobId(authToken,subJobId,workflowId);
            if(subJobGetJobDetailsResponse.getStatusCode()==AutomationConstants.STATUS_OK)
            {
                Map<Object, Object> jobResult = subJobGetJobDetailsResponse.jsonPath().getMap("$");
                HashMap<String, Object> summaryMap = (HashMap<String, Object>) jobResult.get("summary");
                analysisID = summaryMap.get("analysisId").toString();
            }
            else {
                System.out.println("Issue with API ");
            }}
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return analysisID;
    }

    public static Response getsubJobDetailsByJobId(String authToken, String subJobId, String workflowId) {
        String api = String.format(EndPointManager.apiendpoints.get("detailsByJobID"), subJobId +"?parent="+workflowId);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitGet();
        return response;
    }

    public static int getAnalysisIDByJobId_Pate(String jobId, String authToken) {
        // common function to get analysisID by JobID
        int analysisID = 0;

        try {

            Response response = getJobDetailsByJobId(authToken, String.valueOf(jobId));
            if (response.getStatusCode() == 200) {
                Map<Object, Object> jobResult = response.jsonPath().getMap("$");
                HashMap<String, Object> summaryMap = (HashMap<String, Object>) jobResult.get("summary");
                analysisID = Integer.parseInt(summaryMap.get("analysisId").toString());

            } else {
                System.out.println("Issue with Job API ");
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return analysisID;
    }

    public static int getnewAnalysisIDByJobId (String jobId, String authToken) {
        int newAnalysisId = 0;
        try {

            Response response = getJobDetailsByJobId(authToken, String.valueOf(jobId));
            if (response.getStatusCode() == 200) {
                Map<Object, Object> jobResult = response.jsonPath().getMap("$");
                HashMap<String, Object> summaryMap = (HashMap<String, Object>) jobResult.get("summary");
                newAnalysisId = Integer.parseInt(summaryMap.get("newAnalysisId").toString());
            } else {
                System.out.println("New Analysis id is null ");
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return newAnalysisId;
    }

    public static JobResult waitForJobToComplete(String workflowId, String authToken, String jobName) throws Exception {
        String status = "";
        String message = "";
        try {
            Response jobGetJobDetailsResponse = JobsApi.getJobDetailsByJobId(authToken, workflowId);
            for (int i = 0; i < AutomationConstants.timeoutRequests * 4; i++) {
                if (jobGetJobDetailsResponse.getStatusCode() == 200) {
                    Map<String, Object> responseMap = jobGetJobDetailsResponse.jsonPath().getMap("$");
                    status = (String) responseMap.get("status");

                    System.out.println(
                            String.format(
                                    "Getting  job status: Response: %s, JobId: %s , JobName: "+jobName+", Status: %s",
                                    jobGetJobDetailsResponse.getStatusCode(), workflowId, status));
                    if ((status.equalsIgnoreCase(AutomationConstants.WORKFLOW_STATUS_CANCELED))) {
                        message = "Job cancelled";
                        break;
                    } else if ( (status.equalsIgnoreCase(AutomationConstants.WORKFLOW_STATUS_FAILED))) {
                        if (responseMap.get("summary") != null) {
                            Map summary = (Map) responseMap.get("summary");
                            message = (String) summary.get("error");
                        }
                        break;
                    } else if ((status.equalsIgnoreCase(AutomationConstants.WORKFLOW_STATUS_SUCCEEDED))) {
                        message = "Success";
                        break;
                    }
                } else if (jobGetJobDetailsResponse.getStatusCode() == 401) {
                    authToken = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
                }
                else {
                    status = String.valueOf(jobGetJobDetailsResponse.getStatusCode());
                    message = "Failed to check Status: "+status;
                    System.out.println("Error while checking job status: " + status);
                }

                Thread.sleep(AutomationConstants.interval);
                jobGetJobDetailsResponse = JobsApi.getJobDetailsByJobId(authToken, workflowId);
            }
            if ((status.equalsIgnoreCase(AutomationConstants.WORKFLOW_STATUS_RUNNING))
                    || (status.equalsIgnoreCase(AutomationConstants.WORKFLOW_STATUS_QUEUED))) {
                Double waitSeconds = Double.valueOf(AutomationConstants.interval) * Double.valueOf(AutomationConstants.timeoutRequests) / 1000;
                message = "Job not finished. Still " + status + " after " + waitSeconds + " seconds.";
            }
        } catch (Exception ex) {
            status = AutomationConstants.WORKFLOW_STATUS_FAILED;
            message = "Failed: " + ex.getMessage();
            System.out.println(message);
        }
        JobResult jobResult = new JobResult();
        jobResult.setMessage(message);
        jobResult.setStatus(status);
        return jobResult;
    }

    public static String waitForJobToComplete(String workflowId, String authToken) throws Exception{
        JobResult jobResult = waitForJobToComplete(workflowId, authToken, "");
        return jobResult.getStatus();
    }

    public static String waitForJobToComplete(String workflowId, String authToken, String jobName, String columnName, String rowIndex) throws Exception {

        JobResult jobResult = new JobResult();
        try {
            jobResult = waitForJobToComplete(workflowId, authToken, jobName);
        } catch (Exception e) {
            LoadData.UpdateTCInLocalExcel(rowIndex, columnName, "Failed: "+e.getMessage());
            System.out.println("Error in waitForJobToComplete : " + e.getMessage());
            return AutomationConstants.WORKFLOW_STATUS_FAILED;
        }

        LoadData.UpdateTCInLocalExcel(rowIndex, columnName, jobResult.getMessage());

        return jobResult.getStatus();
    }

    public static Boolean checkJobStatus(String workflowId, String authToken) {
        String status = "";
        try {
            Boolean isCheckStatus = true;
            while(isCheckStatus) {
                Response jobGetJobDetailsResponse = getJobDetailsByJobId(authToken, workflowId);
                if (jobGetJobDetailsResponse.getStatusCode() == 200) {
                    Map<String, Object> responseMap = jobGetJobDetailsResponse.jsonPath().getMap("$");
                    status = (String) responseMap.get("status");
                    System.out.println(
                            String.format(
                                    "Getting job status: Response: %s, JobId: %s , Status: %s",
                                    jobGetJobDetailsResponse.getStatusCode(), workflowId, status));
                    if ((status.equalsIgnoreCase("CANCELLED"))
                            || (status.equalsIgnoreCase("FAILED"))) {
                        System.out.println("Job has: " + status);
                        isCheckStatus = false;
                        return false;
                    } else if ((status.equalsIgnoreCase("FINISHED"))) {
                        System.out.println("Job has: " + status);
                        isCheckStatus = false;
                        return true;
                    }
                } else {
                    status = String.valueOf(jobGetJobDetailsResponse.getStatusCode());
                    System.out.println("Error while checking job status: " + status);
                }
                Thread.sleep(AutomationConstants.interval);
            }

        } catch (Exception ex) {
            status = "Exception in checkJobStatus() " + ex;
            System.out.println(status);
            return false;
        }
        return false;
    }



}
