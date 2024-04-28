package com.rms.automation.UploadEdmApi;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.edm.LoadData;
import com.rms.automation.edm.TestCase;
import com.rms.automation.edm.ApiUtil;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

public class UploadEDM extends TestCase {

    public void executeUploadEdm(Map<String, String> tc) throws NullPointerException, Exception {

        int actualresponse;
        String fileName = tc.get("edmFileName");
        String filePath = tc.get("edmFilePath") + "/" + fileName;
        System.out.println("File path is " + filePath);
        String fileExt = tc.get("fileExt");
        String dbType = tc.get("dbType");
        String dataSourceName =
                fileName.substring(0, fileName.indexOf('.')) + "_" + RandomStringUtils.randomNumeric(5);
        try {

            String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
            Boolean status = ApiUtil.fileMultiPartUpload(token, dbType, filePath, fileExt, fileName);
            String payload = "";
            System.out.println("Status of fileMultiPartUpload: " + status);
            Response submitJobResponse = ApiUtil.uploadEDM(token, dataSourceName, payload);
            actualresponse = submitJobResponse.getStatusCode();
            System.out.println("UploadEDM Response: " + actualresponse);
            String locationHdr = submitJobResponse.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            String workflowId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("workflowId: " + workflowId);

            String msg = JobsApi.waitForJobToComplete(workflowId, token,"Upload EDM");
            System.out.println("waitforjob msg: " + msg);

            if(actualresponse== AutomationConstants.STATUS_ACCEPTED &&(msg.equalsIgnoreCase(AutomationConstants.JOB_STATUS_FINISHED ) && (!jobId.isEmpty())))
            {
                if (dataSourceName != "") {
                    LoadData.UpdateTCInLocalExcel(tc.get("index"), "edmDatasourceName", dataSourceName);
                    LoadData.UpdateTCInLocalExcel(tc.get("index"), "UploadedEDMJobId", jobId);
                }
            }

            String portfolioId = null;
           String portfolioNumber = tc.get("portfolioNumber");
            String portfolioName = tc.get("portfolioName");
           String description = tc.get("importDescrp");
                portfolioId = tc.get("existingPortfolioId");


            //Batch API call
            BatchTests batchTests = new BatchTests();
            batchTests.batchAPI(tc, portfolioId, dataSourceName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
