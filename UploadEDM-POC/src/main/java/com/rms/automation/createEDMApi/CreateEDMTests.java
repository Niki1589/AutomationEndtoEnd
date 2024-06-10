package com.rms.automation.createEDMApi;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.apiManager.ApiUtil;
import com.rms.automation.dataProviders.LoadData;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CreateEDMTests {

    @DataProvider(name="loadFromCSV")
    public Object[] provider() throws IOException {
        return LoadData.readCreateEDMFromLocalCSV();
    }

    @Test(dataProvider = "loadFromCSV")
    public void CreateEDM(Map<String, String> tc) throws Exception {
        String dataSourceName = tc.get("EXP_EDM_DATASOURCE_NAME");
        String databaseStorage = tc.get("EXP_OPT_EDM_DATABASE_STORAGE");
        String serverName = tc.get("EXP_OPT_SERVER_NAME");
        String shareWith = tc.get("EXP_OPT_SHARE_GROUP");

        if (dataSourceName == null || databaseStorage == null) {
            throw new Exception("Test case data is incorrect. Check CreateEDM CSV");
        }

        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
        List<String> ids = ApiUtil.getGroupIds(token, shareWith);
        System.out.println(dataSourceName+" Group Ids: "+ids.toString());
        createEdm(dataSourceName, databaseStorage, serverName, ids, token);

    }

    public void createEdm(String dataSourceName, String databaseStorage, String serverName, List<String> groupIds,
                                String token) throws Exception {
        Response response = ApiUtil.createEdm(dataSourceName, databaseStorage, serverName, groupIds, token);
        System.out.println("CreateEDM Status: "+ response.getStatusCode());
        Boolean status = false;
        String jobId = null;
        if (response.getStatusCode() == AutomationConstants.STATUS_ACCEPTED) {
            String locationHdr = response.getHeader("Location");
            jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("create edm_wf_id: "+ jobId );
            status = true;
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("CreateEDM Message: "+ msg);
            status = false;
        }

        if (status) {
            if (jobId == null) {
                throw new Exception("JobId is null");
            }
            String msg = JobsApi.waitForJobToComplete(jobId, token);
            System.out.println("wait for job msg: "+msg );
        }

    }

}
