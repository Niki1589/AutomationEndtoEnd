package automation.tests;

import automation.batch.BatchTests;
import automation.edm.LoadData;
import automation.edm.TestCase;
import automation.merge.jsonMapper.Perils;
import automation.mriImport.MRIImportTests;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

import automation.edm.ApiUtil;

public class UploadEDM extends TestCase {

    public int actualresponse;
    public static final int STATUS_ACCEPTED = 202;

    @DataProvider(name = "file")
    public Object[] provider() throws IOException {
        return LoadData.readTCFromLocal();
    }

    @Test(dataProvider = "file")
    public void UploadEdm(Map<String, String> tc) throws NullPointerException, Exception {
        executeUploadEdm(tc);
    }

    public void executeUploadEdm(Map<String, String> tc) throws NullPointerException, Exception {

        String fileName = tc.get("edmFileName");
        String filePath = tc.get("edmFilePath") + "/" + fileName;
        System.out.println("File path is " + filePath);
        String fileExt = tc.get("fileExt");
        String dbType = tc.get("dbType");
        String dataSourceName =
                fileName.substring(0, fileName.indexOf('.')) + "_" + RandomStringUtils.randomNumeric(8);

        //    String dataSourceName ="Testing_EDM_E2E";
        try {

            String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
            Boolean status = ApiUtil.fileMultiPartUpload(token, dbType, filePath, fileExt, fileName);
            String payload = "";
            System.out.println("Status of fileMultiPartUpload: " + status);
            Response submitJobResponse = ApiUtil.uploadEDM(token, dataSourceName, payload);
            actualresponse = submitJobResponse.getStatusCode();
            System.out.println("UploadEDM Response: " + actualresponse);

            String locationHdr = submitJobResponse.getHeader("Location");
            String workflowId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("workflowId: " + workflowId);

            String msg = ApiUtil.waitForJobToComplete(workflowId, token,"Upload EDM");
            System.out.println("waitforjob msg: " + msg);

//            MRIImportTests.MRIImport(token, dataSourceName, tc, "Create Portfolio");

            String portfolioId = null;
           String portfolioNumber = tc.get("portfolioNumber");
            String portfolioName = tc.get("portfolioName");
           String description = tc.get("importDescrp");
//            if (tc.get("isCreatePortfolio").contains("YES")) {
//                Response portfolioRes = ApiUtil.createPortfolio(token, dataSourceName, portfolioNumber, portfolioName, description);
//                if (portfolioRes.getStatusCode() != 201 && portfolioRes.getStatusCode() != 200) {
//                    throw new Exception("Failed to create portfolio.");
//                }
//                String locationHdrPr = portfolioRes.getHeader("Location");
//                portfolioId = locationHdrPr.substring(locationHdrPr.lastIndexOf('/') + 1);
//
//                ApiUtil.accountsUploadToPortfolio(token, dataSourceName, portfolioId);
//
//            } else {
                portfolioId = tc.get("existingPortfolioId");


            //Batch API call
            BatchTests batchTests = new BatchTests();
            // batchTests.batchAPI(tc,"",dataSourceName);
            batchTests.batchAPI(tc, portfolioId, dataSourceName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
