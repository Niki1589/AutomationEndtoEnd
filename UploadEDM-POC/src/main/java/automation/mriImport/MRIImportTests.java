package automation.mriImport;

import automation.batch.BatchTests;
import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.edm.MRIImportData;
import automation.merge.jsonMapper.Perils;
import com.rms.core.qe.common.AuthUtil;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static automation.edm.LoadData.MergeHeaders;

public class MRIImportTests {


    @DataProvider(name="loadFromCSV")
    public Object[] provider() throws IOException {
        return LoadData.readMriImportFromLocalCSV();
    }

    @Test(dataProvider = "loadFromCSV")
    public void MRIImport(Map<String, String> tc, Boolean isCreateEDM) throws Exception {

        System.out.println("************** Running MRIImport " );
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");
        String dataSource = null;
        if(isCreateEDM == true) {
            System.out.println("creating edm.");
            dataSource = createEDM(tc, token);
        }
        else {
            System.out.println("using existing edm.");
            dataSource = tc.get("edmDatasourceName");
        }

        MRIImport(token, dataSource, tc, "MRI Import");

    }
    public static void MRIImport(String token, String dataSource, Map<String, String> tc, String jobType) throws Exception {

        if (dataSource == null) {
            throw new Exception("DataSource is null");
        }
        String bucketID = createMriBucket(token);
        if (bucketID == null) {
            throw new Exception("Bucket ID is null");
        }

        String portfolioNumber = tc.get("portfolioNumber");
        String portfolioName = tc.get("portfolioName");
        String description = tc.get("importDescrp");
        System.out.println(" portfolioNumber= "+portfolioNumber);
        System.out.println(" portfolioName= "+portfolioName);
        System.out.println(" description= "+description);

        String portfolioId = null;
        if (tc.get("isCreatePortfolio").contains("YES")) {

            Response portfolioRes = ApiUtil.createPortfolio(token, dataSource, portfolioNumber, portfolioName, description);
            if (portfolioRes.getStatusCode() != 201 && portfolioRes.getStatusCode() != 200) {
                throw new Exception("Failed to create portfolio.");
            }
            String locationHdr = portfolioRes.getHeader("Location");
            portfolioId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
           int portfolioId_created= Integer.parseInt(portfolioId);

           if(portfolioId_created!= -1)
            {
                LoadData.UpdateTCInLocalCSV(tc.get("index"), "isCreatePortfolio", "NO");
                LoadData.UpdateTCInLocalCSV(tc.get("index"), "existingPortfolioId", String.valueOf(portfolioId_created));
            }


        } else {
            portfolioId = tc.get("existingPortfolioId");
        }
        //Upload Account file
        String accountsFile = tc.get("accntFileName");
        String filepath = tc.get("accntFilePath");
        String accountFileID = uploadMriFiles(bucketID, filepath, "account", accountsFile, token);

        // Upload location file
        String locationFile = tc.get("locFileName");
        String locFilepath = tc.get("locFilePath");
        String locationFileID =  uploadMriFiles(bucketID, locFilepath, "location", locationFile, token);

        //Upload Mapping file
        String mapFileName = tc.get("mappingFileName");
        String mapFilePath = tc.get("mappingFilePath");
        String mappingFileID =  uploadMriFiles(bucketID, mapFilePath, "mapping", mapFileName, token);

        Map<String, Object> payload = new HashMap<>();
        payload.put("accountsFileId", accountFileID);
        payload.put("locationsFileId", locationFileID);
        payload.put("reinsuranceFileId", null);
        payload.put("mappingFileId", mappingFileID);
        payload.put("bucketId", bucketID);
        payload.put("delimiter", "Tab");
        payload.put("skipLines", 1);
        payload.put("dataSourceName", dataSource);
        payload.put("geoHaz", true);
        payload.put("appendLocations", false);
        payload.put("currency", "USD");
        payload.put("portfolioId", portfolioId);

        Response submitMRIJobRes = ApiUtil.submitMRIJob(token, payload);
        String mrilocationHdr = submitMRIJobRes.getHeader("Location");
        String mriJobId = mrilocationHdr.substring(mrilocationHdr.lastIndexOf('/') + 1);
        System.out.println(submitMRIJobRes.getStatusCode()+"  :SubmitMriJob Status: mriJobUd:"+mriJobId);

        String msg = ApiUtil.waitForJobToComplete( mriJobId, token, jobType );
        System.out.println("waitforjob msg: "+msg );
        System.out.println("************** Completed MRIImport " );

        MRIImportData mriImportData = new MRIImportData();
        mriImportData.setDataSource(dataSource);
        mriImportData.setPortfolioId(portfolioId);
        LoadData.mriImportData = mriImportData;

        //Making call to Batch API
        BatchTests batchTests = new BatchTests();
        batchTests.batchAPI(tc,portfolioId,dataSource);

    }

    //Create Storage API
    private static String createMriBucket(String token) {

        String bucketID;
        Response response = null;
        try {
            response = ApiUtil.CreateBucketApi(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Create bucket http status code " + response.getStatusCode());
        if (response.getStatusCode() == 201) {
            String locationHdr = response.getHeader("Location");
            bucketID = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("bucketID: "+ bucketID );
            return bucketID;
        }
        return null;

    }

    public String createEDM(Map<String, String> tc, String token) throws Exception {

        String dataSourceName = tc.get("edmDatasourceName");
        String databaseStorage = tc.get("optEdmDatabaseStorage");
        String serverName = tc.get("optServerName");
        String shareWith = tc.get("optShareGroup");

        if (dataSourceName == null || databaseStorage == null) {
            throw new Exception("Test case data is incorrect. Check MRIImport CSV");
        }

        String randmVal = RandomStringUtils.randomNumeric(3);
        dataSourceName = dataSourceName + randmVal;

        List<String> ids = ApiUtil.getGroupIds(token, shareWith);
        System.out.println(dataSourceName+" Group Ids: "+ids.toString());

        Response response = ApiUtil.createEdm(dataSourceName, databaseStorage, serverName, ids, token);
        System.out.println("CreateEDM Status: "+ response.getStatusCode());
        if (response.getStatusCode() == 202) {
            String locationHdr = response.getHeader("Location");
            String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
            System.out.println("createedm_wf_id: "+ jobId );

            if (jobId == null) {
                throw new Exception("JobId is null");
            }

            if (ApiUtil.checkJobStatus(jobId, token)) {
                System.out.println("EDM created successfully");
                return dataSourceName;
            } else {
                throw new Exception("CreateEDM Failed");
            }
        }
        else {
            String msg = response.getBody().jsonPath().get("message");
            System.out.println("CreateEDM Message: "+ msg);
            throw new Exception("CreateEDM Failed");
        }

    }

    private static String uploadMriFiles(String bucketId, String filepath, String fileType, String fileName, String authToken) throws Exception {

        String fileId = null;
        try {
            File file = new File(filepath);
            if (!file.exists() || !file.isFile()) {
                throw new Exception("File error.");
            }
            long fileSize = file.length();
            System.out.println("File size is = "+fileSize);
            fileId = ApiUtil.uploadFile(authToken, bucketId, filepath, fileType, fileName, fileSize);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (fileId != null) {
            System.out.println(fileType + " ID created " + fileId);
            return fileId;
        } else {
            throw new Exception("Error while uploading the file.");
        }

    }


}
