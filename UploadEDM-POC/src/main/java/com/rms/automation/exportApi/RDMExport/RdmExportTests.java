package com.rms.automation.exportApi.RDMExport;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RdmExportTests {

    public static String fileNameRDM;
    public static String localPathRDM;

    public static void rdmExport(Map<String, String> tc, String analysisId) throws Exception {
        RDMModel rdm = RDMMapper.map(tc, analysisId);
        String index = tc.get("INDEX");
        for ( REX_EXPORT_HD_LOSSES_AS_ENUM exportHdLossesAs : rdm.getREX_EXPORT_HD_LOSSES_AS() ) execute(tc,rdm, exportHdLossesAs, index);
    }
    public static void execute(Map<String, String> tc,RDMModel rdm, REX_EXPORT_HD_LOSSES_AS_ENUM exportHdLossesAs, String index) throws Exception {
        System.out.println("***** Running RDM Export API ********");
        String token = ApiUtil.getSmlToken(tc);

        if (REX_RDM_LOCATION_ENUM.PLATFORM.equals(rdm.getREX_RDM_LOCATION())) {
            Response response = ApiUtil.exportRDMToPlatform(
                    rdm.getANALYSIS_ID(), rdm.getREX_RDM_NAME(),
                    exportHdLossesAs.getValue(), rdm.getSQL_VERSION(), rdm.getEXPORT_FORMAT_RDM(), token);
            System.out.println("RDM  Status: " + response.getStatusCode());
            Boolean status = false;
            String jobId = null;
            if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
                String locationHdr = response.getHeader("Location");
                jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                System.out.println("createedm_wf_id: " + jobId);
                status = true;
            }
            else {
                String msg = response.getBody().jsonPath().get("message");
                System.out.println("CreateEDM Message: " + msg);
                status = false;
            }
            if (status) {
                if (jobId == null) {
                    throw new Exception("JobId is null");
                }

                String columnName = "RDM_EXPORT_JOB_STATUS_"+exportHdLossesAs.getValue();
                String msg = JobsApi.waitForJobToComplete(jobId, token, "Export to RDM API",
                        columnName, tc.get("INDEX"));
                System.out.println("wait for job msg: " + msg);
                if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty())) {
                    Response jobDetails = JobsApi.getJobDetailsByJobId(token, jobId);
                    JsonPath jsonPath = jobDetails.jsonPath();
                    Map<String, Object> jobResponseMap = jsonPath.getMap("$");
                    Map<String, Object> summaryMap = (Map<String, Object>) jobResponseMap.get("summary");

                  String rdmLink = String.valueOf(summaryMap.get("downloadLink"));
                    fileNameRDM = rdmLink.substring(rdmLink.lastIndexOf("/") + 1, rdmLink.indexOf("?"));

                //  Create the folder RDM for the RDM files
                    String RDMFolderPath = tc.get("FILE_EXPORT_PATH") + "RDM/";

                    // Create a Path object representing the directory
                    Path RDMFolder = Paths.get(RDMFolderPath);

                    try {
                        // Check if the base folder already exists
                        if (Files.exists(RDMFolder)) {
                            throw new IOException("Comparison folder already exists: " + RDMFolderPath);
                        }

                        // Attempt to create the directory
                        Files.createDirectories(RDMFolder);
                        System.out.println("RDM Folder created successfully.");
                    } catch (IOException e) {
                        System.err.println("Failed to create folder: " + e.getMessage());
                    }

                    localPathRDM = RDMFolderPath + fileNameRDM;
                    Utils.downloadFile(rdmLink, localPathRDM);

                    //Only PLT type losses can be imported as RDM to RM

                    if (tc.get("REX_EXPORT_HD_LOSSES_AS").equalsIgnoreCase(String.valueOf(REX_EXPORT_HD_LOSSES_AS_ENUM.PLT))) {

                        LoadData.UpdateTCInLocalExcel(index, "IMPR_ANALYSIS_FROM_RDM_FILE_NAME", fileNameRDM);
                        LoadData.UpdateTCInLocalExcel(index, "IMPR_ANALYSIS_FROM_RDM_FILE_PATH", localPathRDM);
                        LoadData.UpdateTCInLocalExcel(index, "RDM_EXPORT_JOBID_PLT", jobId);
                    }
                    else if (tc.get("REX_EXPORT_HD_LOSSES_AS").equalsIgnoreCase(String.valueOf(REX_EXPORT_HD_LOSSES_AS_ENUM.ELT)))
                    {
                        LoadData.UpdateTCInLocalExcel(index, "RDM_EXPORT_JOBID_ELT", jobId);


                    }
                }
            }
        }
        else if(REX_RDM_LOCATION_ENUM.DATABRIDGE.equals(rdm.getREX_RDM_LOCATION()))
        {
            Map<String, Object> payload = new HashMap<>();
            payload.put("createnew", rdm.getIS_CREATE_NEW_DATABRIDGE());
            if(!rdm.getIS_CREATE_NEW_DATABRIDGE()) {
                payload.put("exportHdLossesAs", exportHdLossesAs);
            }

            payload.put("analysisIds", rdm.getANALYSIS_ID() );
            payload.put("exportFormat", rdm.getEXPORT_FORMAT_RDM());
            payload.put("exportType","RDM");
            payload.put("rdmName", rdm.getREX_RDM_NAME());
            payload.put("sqlVersion", rdm.getSQL_VERSION());
            payload.put("type", "ResultsExportInputV2");

            Response response = ApiUtil.exportRDMToNewDataBridge(rdm.getDATABRIDGE_SERVER(), payload, token);
            System.out.println("RDM  Status: " + response.getStatusCode());
            if (response.getStatusCode() == 202) {
                String locationHdr = response.getHeader("Location");
                String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                System.out.println("createedm_wf_id: " + jobId);
                if (jobId == null) {
                    throw new Exception("JobId is null");
                }
                String columnName = "RDM_EXPORT_JOB_STATUS_"+exportHdLossesAs.getValue();
                String msg = JobsApi.waitForJobToComplete(jobId, token, "Export to RDM API",
                        columnName, tc.get("INDEX"));
                System.out.println("wait for job msg: " + msg);
                if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))
                {
                    LoadData.UpdateTCInLocalExcel(index,"RDM_EXPORT_JOBID", jobId);

                }
            }
            else {
                String msg = response.getBody().jsonPath().get("message");
                System.out.println("CreateEDM Message: " + msg);
            }

        }
    }
}

