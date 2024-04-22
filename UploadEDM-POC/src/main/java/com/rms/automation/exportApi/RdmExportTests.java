package com.rms.automation.exportApi;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import io.restassured.response.Response;

import java.util.*;

public class RdmExportTests {

    public static void rdmExport(Map<String, String> tc, String analysisId) throws Exception {
        System.out.println("***** Running RDM Export API ********");
        int anlsId= Integer.parseInt(analysisId);
        int[] analysis_Id = new int[]{(anlsId)};
        String rdmName = tc.get("rdmName");
        String exportHDLossesAs = tc.get("exportHDLossesAs");
        String sqlVersion = tc.get("sqlVersion");
        String exportFormat = tc.get("exportFormat");
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        if (tc.get("rdmLocation").equals("Platform")) {
            Response response = ApiUtil.exportRDMToPlatform(analysis_Id, rdmName, exportHDLossesAs, sqlVersion, exportFormat, token);
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
                String msg =   msg = JobsApi.waitForJobToComplete(jobId, token, "Export to RDM API");
                System.out.println("waitforjob msg: " + msg);
                if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))
                {
                    LoadData.UpdateTCInLocalExcel(tc.get("index"), "ExportJobId", jobId);

                }
            }
        }
        else if(tc.get("rdmLocation").equals("DataBridge"))
        {
            Map<String, Object> payload = new HashMap<>();
            if(tc.get("dataBridgeType").equals("createnew"))
            {
                payload.put("createnew",true);
            } else if(tc.get("dataBridgeType").equals("existing")) {
                payload.put("createnew",false);
                payload.put("exportHdLossesAs",exportHDLossesAs);
            }

            payload.put("analysisIds", analysis_Id );
            payload.put("exportFormat",exportFormat);
            payload.put("exportType","RDM");
            payload.put("rdmName", rdmName);
            payload.put("sqlVersion", sqlVersion);
            payload.put("type", "ResultsExportInputV2");

            String dataBridgeServer=tc.get("dataBridgeServer");
            Response response = ApiUtil.exportRDMToNewDataBridge(dataBridgeServer, payload, token);
            System.out.println("RDM  Status: " + response.getStatusCode());
            if (response.getStatusCode() == 202) {
                String locationHdr = response.getHeader("Location");
                String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                System.out.println("createedm_wf_id: " + jobId);
                if (jobId == null) {
                    throw new Exception("JobId is null");
                }
                String msg = JobsApi.waitForJobToComplete(jobId, token, "Export to RDM API");
                System.out.println("wait for job msg: " + msg);
                if(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED) && (!jobId.isEmpty()))
                {
                    LoadData.UpdateTCInLocalExcel(tc.get("index"),"ExportJobId", jobId);

                }
            }
            else {
                String msg = response.getBody().jsonPath().get("message");
                System.out.println("CreateEDM Message: " + msg);
            }

        }
    }
}

