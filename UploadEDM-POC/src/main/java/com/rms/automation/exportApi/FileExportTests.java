package com.rms.automation.exportApi;

import com.rms.automation.LossValidation.LossValidation;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.merge.jsonMapper.Perils;
import com.rms.automation.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExportTests {


    public static String localPath ="";
    private static int[] analysis_Id;
    public static void fileExport(Map<String, String> tc, String analysisId) throws Exception {
        fileExport(tc, analysisId,"","");
    }
    public static void fileExport(Map<String, String> tc, String analysisId,String specificFolder, String specificJobIDColumn) throws Exception {
        System.out.println("***** Running FILE Export API ********");
        if(analysisId!=null && !analysisId.isEmpty())
        {
            try
            {
                int anlsId= Integer.parseInt(analysisId);
                analysis_Id = new int[]{(anlsId)};
                String exportFormat = tc.get("EXPORT_FORMAT_FILE");
                String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> payload = new HashMap<>();
        payload.put("analysisIds", analysis_Id );
        payload.put("exportFormat",exportFormat);
        payload.put("exportType","RDM");
        payload.put("type", "ResultsExportInputV2");
        payload.put("additionalOutputs", new ArrayList<String>());

        Perils perils = Perils.extractPerilFromTC(tc);

        Download_Settings downloadSettings = Download_Settings.parse(tc.get("DOWNLOAD_SETTINGS_FILE"));

        List<Map<String, Object>> lossDetailsList = new ArrayList<>();
        if (Utils.isTrue(downloadSettings.getIsStatsMetric())) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "STATS");
            lossDetails.put("outputLevels",downloadSettings.getOutputLevels_StatesMetric().split(","));
            lossDetails.put("perspectives",downloadSettings.getperspectives_StatsMetric().split(","));
            lossDetailsList.add(lossDetails);

        }
        if ((Utils.isTrue(downloadSettings.getIsEPMetric())) && perils.getAnalysisType().equals("EP")) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "EP");
            lossDetails.put("outputLevels",downloadSettings.getOutputLevels_EPMetric().split(","));
            lossDetails.put("perspectives", downloadSettings.getPerspectives_EPMetric().split(","));
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
            String msg =  JobsApi.waitForJobToComplete(jobId, token, "Export to file API",
                    "FILE_EXPORT_JOB_STATUS", tc.get("INDEX"));
            System.out.println("wait for job msg: " + msg);
            if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))
            {
                Response jobDetails = JobsApi.getJobDetailsByJobId(token, jobId);
                JsonPath jsonPath = jobDetails.jsonPath();
                Map<String, Object> jobResponseMap = jsonPath.getMap("$");
                Map<String, Object> summaryMap = (Map<String, Object>) jobResponseMap.get("summary");

                String downloadLink = String.valueOf(summaryMap.get("downloadLink"));
                String fileName = downloadLink.substring(downloadLink.lastIndexOf("/") + 1, downloadLink.indexOf("?"));


                // Specify the path where you want to create the folder

               // String folderPath = "/Users/YourUsername/Documents/NewFolder";
                String basePath=tc.get("FILE_EXPORT_PATH") + "ActualResults/";

                // Specify the sub-folder name
                String subFolderName = "CSV";

                // Create a Path object representing the base directory
                Path baseFolderPath = Paths.get(basePath);

                // Create a Path object representing the sub-directory
                Path subFolderPath = baseFolderPath.resolve(subFolderName);

                try {

                    // Check if the base folder already exists
                    if (Files.exists(baseFolderPath)) {
                        throw new IOException("Base folder already exists: " + baseFolderPath);
                    }

                    // Check if the sub-folder already exists
                    if (Files.exists(subFolderPath)) {
                        throw new IOException("Sub-folder already exists: " + subFolderPath);
                    }

                    // Attempt to create the base directory and all necessary parent directories
                    Files.createDirectories(baseFolderPath);
                    System.out.println("Base folder created successfully.");

                    // Attempt to create the sub-directory
                    Files.createDirectories(subFolderPath);
                    System.out.println("Sub-folder created successfully.");
                }

                catch (IOException e) {
                    System.err.println("Failed to create folders: " + e.getMessage());
                }

                 localPath = subFolderPath +"/"+ fileName;

                Utils.downloadFile(downloadLink, localPath);

                String fileExportColumnName = "FILE_EXPORT_JOBID";
                if (specificJobIDColumn != null && specificJobIDColumn.length() > 0) {
                    fileExportColumnName = specificJobIDColumn;
                }
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), fileExportColumnName, jobId);

                // To check if actual results file is not empty and has valid data , then populate the complete path of actual results in excel file.
                Path filePath = Paths.get(localPath);
                if (Files.exists(filePath)) {
                    LossValidation.run(tc);
                } else {
                    System.out.println("File does not exist");
                }

            }
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("ExportFile Message: " + msg);
        }

    }
            catch (NumberFormatException e)
            {
                System.out.println("Analysis Id is not a valid Integer");
                e.printStackTrace();
            }
}
        else
        {
            System.out.println("Analysis Id is null or empty");

        }
        }
    }