package automation.export;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.utils.Utils;
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

        String exportFormat = tc.get("exportFormat");
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Map<String, Object> payload = new HashMap<>();
        payload.put("analysisIds", analysis_Id );
        payload.put("exportFormat",exportFormat);
        payload.put("exportType","RDM");
        payload.put("type", "ResultsExportInputV2");
        payload.put("additionalOutputs", new ArrayList<String>());

        List<Map<String, Object>> lossDetailsList = new ArrayList<>();
        if (Utils.isTrue(tc.get("IsStatesMetric"))) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "STATS");
            lossDetails.put("outputLevels", tc.get("outputLevels_StatesMetric").split(","));
            lossDetails.put("perspectives", tc.get("perspectives_StatesMetric").split(","));
            lossDetailsList.add(lossDetails);
        }
        if (Utils.isTrue(tc.get("IsEPMetric"))) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "EP");
            lossDetails.put("outputLevels", tc.get("outputLevels_EPMetric").split(","));
            lossDetails.put("perspectives", tc.get("perspectives_EPMetric").split(","));
            lossDetailsList.add(lossDetails);
        }
        if (Utils.isTrue(tc.get("IsLossTablesMetric"))) {
            Map<String, Object> lossDetails = new HashMap<>();
            lossDetails.put("lossType", "LOSS_TABLES");
            lossDetails.put("outputLevels", tc.get("outputLevels_LossTablesMetric").split(","));
            lossDetails.put("perspectives", tc.get("perspectives_LossTablesMetric").split(","));
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
            String msg = ApiUtil.waitForJobToComplete(jobId, token);
            System.out.println("waitforjob msg: " + msg);
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("ExportFile Message: " + msg);
        }

    }
}