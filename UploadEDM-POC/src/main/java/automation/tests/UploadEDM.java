package automation.tests;

import automation.batch.BatchTests;
import automation.edm.LoadData;
import automation.edm.TestCase;
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

    @Test (dataProvider = "file")
    public void UploadEdm(Map<String, String> tc)throws NullPointerException,Exception {
        executeUploadEdm(tc);
    }
    public void executeUploadEdm(Map<String, String> tc) throws NullPointerException,Exception{

        String fileName = tc.get("edmFileName");
        String filePath = tc.get("edmFilePath") +"/" + fileName;
        System.out.println("File path is " + filePath);
        String fileExt = tc.get("fileExt");
        String dbType = tc.get("dbType");
        String dataSourceName =
                fileName.substring(0, fileName.indexOf('.')) + "_" + RandomStringUtils.randomNumeric(8);
//        try {

            String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
            Boolean status = ApiUtil.fileMultiPartUpload(token, dbType, filePath, fileExt, fileName);
            String payload ="";
            System.out.println("Status of fileMultiPartUpload: "+status);
            Response submitJobResponse = ApiUtil.uploadEDM( token, dataSourceName,payload);
            actualresponse=submitJobResponse.getStatusCode();
            System.out.println("UploadEDM Response: "+actualresponse );

            String locationHdr = submitJobResponse.getHeader("Location");
            String workflowId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("workflowId: "+workflowId );

            String msg = ApiUtil.waitForJobToComplete(workflowId, token);
            System.out.println("waitforjob msg: "+msg );

            String portfolioNumber = tc.get("portfolioNumber");
            String portfolioName = tc.get("portfolioName");
            String description = tc.get("importDescrp");
            System.out.println(" portfolioNumber= "+portfolioNumber);
            System.out.println(" portfolioName= "+portfolioName);
            System.out.println(" description= "+description);

            Response portfolioRes = ApiUtil.createPortfolio(token, dataSourceName, portfolioNumber, portfolioName, description);

            if (portfolioRes.getStatusCode() != 201 && portfolioRes.getStatusCode() != 200) {
                throw new Exception("Failed to create portfolio.");
            }
            String portfolioLocationHdr = portfolioRes.getHeader("Location");
            String portfolioId = portfolioLocationHdr.substring(portfolioLocationHdr.lastIndexOf('/') + 1);

            BatchTests batchTests = new BatchTests();
            batchTests.batchAPI(tc,portfolioId,dataSourceName);

//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

}
