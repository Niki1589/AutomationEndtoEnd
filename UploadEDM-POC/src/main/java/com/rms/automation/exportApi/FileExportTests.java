package com.rms.automation.exportApi;

import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExportTests {

    public static void fileExport(Map<String, String> tc, String analysisId) throws Exception {
        System.out.println("***** Running FILE Export API ********");

        int anlsId= Integer.parseInt(analysisId);
        int[] analysis_Id = new int[]{(anlsId)};

        String exportFormat = tc.get("EXPORT_FORMAT_FILE");
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> payload = new HashMap<>();
        payload.put("analysisIds", analysis_Id );
        payload.put("exportFormat",exportFormat);
        payload.put("exportType","RDM");
        payload.put("type", "ResultsExportInputV2");
        payload.put("additionalOutputs", new ArrayList<String>());

        Download_Settings downloadSettings = Download_Settings.parse(tc.get("DOWNLOAD_SETTINGS_FILE"));

        List<Map<String, Object>> lossDetailsList = new ArrayList<>();
        if (Utils.isTrue(downloadSettings.getIsStatsMetric())) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "STATS");
            lossDetails.put("outputLevels",downloadSettings.getOutputLevels_StatesMetric().split(","));
            lossDetails.put("perspectives",downloadSettings.getperspectives_StatsMetric().split(","));
            lossDetailsList.add(lossDetails);
        }
        if (Utils.isTrue(downloadSettings.getIsEPMetric())) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "EP");
            lossDetails.put("outputLevels",downloadSettings.getOutputLevels_EPMetric().split(","));
            lossDetails.put("perspectives", downloadSettings.getperspectives_StatsMetric().split(","));
            lossDetailsList.add(lossDetails);
        }
        if (Utils.isTrue(downloadSettings.getIsLossTablesMetric())) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "LOSS_TABLES");
            lossDetails.put("outputLevels", downloadSettings.getOutputLevels_LossTablesMetric().split(","));
            lossDetails.put("perspectives", downloadSettings.getPerspectives_LossTablesMetric().split(","));
            lossDetailsList.add(lossDetails);
        }

        payload.put("lossDetails", lossDetailsList);

        Response response = ApiUtil.exportFile(payload, token);
        System.out.println("exportFile  Status: " + response.getStatusCode());
        if (response.getStatusCode() == 202) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("exportFile_wf_id: " + jobId);
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg =  JobsApi.waitForJobToComplete(jobId, token, "Export to file API");
            System.out.println("wait for job msg: " + msg);
            if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))
            {
                Response jobDetails = JobsApi.getJobDetailsByJobId(token, jobId);
                JsonPath jsonPath = jobDetails.jsonPath();
                Map<String, Object> jobResponseMap = jsonPath.getMap("$");
                Map<String, Object> summaryMap = (Map<String, Object>) jobResponseMap.get("summary");

                String s3Link = String.valueOf(summaryMap.get("downloadLink"));
                String fileName = s3Link.substring(s3Link.lastIndexOf("/") + 1, s3Link.indexOf("?"));
                String localPath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/ActualResults/" + fileName;
                Utils.downloadFile(s3Link, localPath);

                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"),"FILE_EXPORT_JOBID", jobId);
            }
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("ExportFile Message: " + msg);
        }

    }
}