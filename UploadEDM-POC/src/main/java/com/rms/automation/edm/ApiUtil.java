package com.rms.automation.edm;

import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.bal.EndPointManager;
import com.rms.automation.batchApi.ModelProfileAPI;
import com.rms.automation.constants.AutomationConstants;
import com.rms.automation.merge.jsonMapper.Perils;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.rms.automation.edm.enums.DatabaseStorageType;
import com.rms.core.qe.common.AWSClientUtil;
import com.rms.core.qe.common.RestApiHelper;
import com.rms.core.qe.common.RetryUtil;
import io.restassured.RestAssured;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.restassured.RestAssured.given;

public class ApiUtil {

    private static String rmsUploadId = null;
    public static String getRmsUploadId() {
        return rmsUploadId;
    }

    public static void setRmsUploadId(String rmsUploadId) {
        ApiUtil.rmsUploadId = rmsUploadId;
    }

    private static String createAuthPayload(String userName, String password, String tenantName) {
        String payload = "{ \"password\" : \"" + password + "\", \"tenantName\" : \"" + tenantName + "\", \"username\" :  \"" + userName + "\" }";
        return payload;
    }

    public static String getSmlToken(Map<String, String> tc) throws Exception {
        String user = tc.get("USERNAME");
        String password = tc.get("PASSWORD");
        String tenantName = tc.get("TENANT");
        return getSmlToken(user, password, tenantName);
    }

    public static String getSmlToken(String user, String password, String tenantName) throws Exception {
        RetryUtil retryRequest = new RetryUtil(5);
        String uri = EndPointManager.baseUrl+EndPointManager.apiendpoints.get("authorize");
        String payload = createAuthPayload(user, password, tenantName);
        String tokenType = "accessToken";
        RestApiHelper apiHelper = new RestApiHelper("", uri, "application/json");
        Response response = apiHelper.submitPostWithoutToken(payload);
        if (response.getStatusCode() == AutomationConstants.STATUS_OK) {
            System.out.println("Auth token generated successfully.");
            return (String)response.jsonPath().get(tokenType);
        } else {
            PrintStream var10000 = System.out;
            String var10001 = ((RestAssuredResponseImpl)response).getGroovyResponse().getStatusLine();
            var10000.print("Auth api failed to generate token " + var10001 + " with status code" + response.getStatusCode());
            String var10002 = ((RestAssuredResponseImpl)response).getGroovyResponse().getStatusLine();
            throw new Exception("Auth api failed to generate token " + var10002 + " with status code" + response.getStatusCode());
        }
    }


    public static Boolean fileMultiPartUpload(
            String authToken, String dbType, String filePath, String fileExt, String fileName)
            throws Exception {
        Boolean status = false;
        fileName = UUID.randomUUID().toString() + "." + fileExt.toLowerCase();
        Response response = getS3UploadCredentials(authToken, fileName, dbType, fileExt);
        if (response.getStatusCode() == AutomationConstants.STATUS_OK) {
            try {
                JsonPath jsonPath = response.jsonPath();
                String region = jsonPath.get("awsRegion");
                String accessKey =
                        new String(Base64.getDecoder().decode((String) (jsonPath.get("uploadKey1"))));
                String secretKey =
                        new String(Base64.getDecoder().decode((String) (jsonPath.get("uploadKey2"))));
                setRmsUploadId(jsonPath.get("uploadId"));

                String[] uploadKey3Encoded = ((String) (jsonPath.get("uploadKey3"))).split("\n");
                String sessionToken = "";
                for (int i = 0; i < uploadKey3Encoded.length; i++) {
                    sessionToken += uploadKey3Encoded[i];
                }

                String uploadKey3 = new String(Base64.getDecoder().decode(sessionToken));

                System.out.println("Region: "+region+
                        ", AccessKey: "+accessKey+
                        ". SecretKey: "+secretKey+
                        ", UploadId: "+getRmsUploadId()+
                        ", SessionToken: "+sessionToken);

                BasicSessionCredentials sessionCredentials =
                        new BasicSessionCredentials(accessKey, secretKey, uploadKey3);
                AmazonS3 s3Client =
                        AmazonS3ClientBuilder.standard()
                                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                                .withRegion(region)
                                .build();
                if (filePath != null) {
                    // String filePath = dataPath + fileToUpload;

                    // This will upload the file to S3. Test and see on S3 if the file has been uploaded to the bucket
                    status =
                            uploadToS3UsingClient(
                                    jsonPath.get("bucketPrefix"), fileName, filePath, s3Client);
                    // AWSClient.uploadToS3(s3Client, s3Bucket, key, filePath);
                }
            } catch (Exception ex) {
                System.out.println("Exception in uploading to S3" + ex.getMessage());
                //logger.error("Exception in uploading to S3" + ex.getMessage());
            }

        } else {
            String msg = "Failed to upload file at s3 location due to ";
            try {
                msg += response.jsonPath().get("message") + " with status code " + response.getStatusCode();
            } catch (Exception ex) {
                msg +=
                        ((RestAssuredResponseImpl) response).getGroovyResponse().getStatusLine()
                                + " with status code"
                                + response.getStatusCode();
            }
            System.out.println(msg);
            throw new Exception(msg);
        }
        return status;
    }

    private static Response getS3UploadCredentials(
            String authToken, String filename, String dbType, String fileExt) {
        String api = String.format(EndPointManager.apiendpoints.get("s3UploadCredentials"), filename, dbType, fileExt);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper = new RestApiHelper(authToken, url, "application/json", false);
        return restApiHelper.submitGet();
    }

    private static boolean uploadToS3UsingClient(
            String bucketPrefix, String fileName, String filePath, AmazonS3 s3Client) {
        System.out.println("START: multipart upload process");
        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

        List<PartETag> partETags = new ArrayList<>();

        String[] s3FileLocation = bucketPrefix.split("/");
        String bucketName = s3FileLocation[0];
        StringBuilder filePrefix = new StringBuilder();
        for (int i = 1; i < s3FileLocation.length; i++) {
            filePrefix.append(s3FileLocation[i]);
            filePrefix.append("/");
        }

        System.out.println("START: Initiate multipart upload");

        String fileKey = filePrefix.toString() + fileName;
        // Initiate the multipart upload.
        InitiateMultipartUploadRequest initRequest =
                new InitiateMultipartUploadRequest(bucketName, fileKey);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

        System.out.println("END: Initiate multipart upload");
        // Upload the file parts.
        long filePosition = 0;
        for (int i = 1; filePosition < contentLength; i++) {
            long percentageComplete = (filePosition * 100 / contentLength);
            System.out.println(
                    String.format("Uploading in progress... %d%% complete", percentageComplete));
            // Because the last part could be less than 5 MB, adjust the part size as needed.
            partSize = Math.min(partSize, (contentLength - filePosition));

            // Create the request to upload a part.
            UploadPartRequest uploadRequest =
                    new UploadPartRequest()
                            .withBucketName(bucketName)
                            .withKey(fileKey)
                            .withUploadId(initResponse.getUploadId())
                            .withPartNumber(i)
                            .withFileOffset(filePosition)
                            .withFile(file)
                            .withPartSize(partSize);
            int retriesLeft = 5;
            while (retriesLeft > 0) {
                try {
                    // Upload the part and add the response's ETag to our list.
                    UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                    partETags.add(uploadResult.getPartETag());
                    retriesLeft = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("failed to upload file part. retrying... ");
                    if (retriesLeft == 1) {
                        throw new RuntimeException("File upload to S3 failed");
                    }
                    retriesLeft--;
                }
            }
            filePosition += partSize;
        }
        System.out.println("file upload 100% complete");

        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest =
                new CompleteMultipartUploadRequest(
                        bucketName, fileKey, initResponse.getUploadId(), partETags);
        s3Client.completeMultipartUpload(compRequest);
        System.out.println("END: multipart upload process");
        return true;
    }

    public static Response uploadEDM(String authToken, String dataSource,String payload) {
        String api = String.format(EndPointManager.apiendpoints.get("uploadEDM"), getRmsUploadId(), dataSource);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper = new RestApiHelper(authToken, url, "application/json", false);
        return payload.isEmpty() ? restApiHelper.submitPost() : restApiHelper.submitPost(payload);
    }

    public static Response uploadRDM(String authToken, String rdmName, String payload) {
        String api = String.format(EndPointManager.apiendpoints.get("uploadRDM"), getRmsUploadId(), getRmsUploadId(), rdmName);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper = new RestApiHelper(authToken, url, "application/json", false);
        return payload.isEmpty() ? restApiHelper.submitPost() : restApiHelper.submitPost(payload);
    }


    public static Response findEdmByName(String authToken, String edmName)
    {
        String api = EndPointManager.apiendpoints.get("findEdmByName") + edmName;
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitGet();
        return response;
    }

    public static Response getTreatyIdByAnalysisId(String authToken, String analysisId_pate)
    {
        String api = String.format(EndPointManager.apiendpoints.get("pate-treaties"), analysisId_pate);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitGet();
        return response;
    }

    public static Response getAnalysisNameByAnalysisId(String authToken, String analysisId)
    {

        String baseUrl = "https://api-euw1.rms-npe.com/riskmodeler/v2/analyses";

        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", authToken)
                .queryParam("q", "id IN (" + analysisId + ")")
                .get();

        return response;
    }
    public static Response getGroups(String authToken) {
        String api = EndPointManager.apiendpoints.get("getGroups");
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitGet();
        return response;
    }

    public static List<String> getGroupIds(String authToken, String groupNames) throws Exception {
        Response groupResponse = getGroups(authToken);
        String[] groupNamesList = groupNames.split(",");
        List<String> guidList = new ArrayList<>();
        if (groupResponse.getStatusCode() == 200) {
            //converting the response into list of groups,Groups.class is a type.
            List<Groups> listOfGroups = groupResponse.jsonPath().getList("$", Groups.class);
            //Looping over list of groups to check if group name exists in the list, then get the Gguid
            listOfGroups.stream().forEach((Groups g) -> {
                if (Arrays.stream(groupNamesList).anyMatch((String n) -> n.equals(g.getGroupName()))) {
                    guidList.add(g.getGroupGuid());
                }
            });

        } else {
            System.out.println("Error while fetching Groups: " + groupResponse.getStatusCode());
        }

        return guidList;
    }



    public static Response createEdm(String dataSource, String databaseStorage, String serverName, List<String> groupIds, String token) throws Exception {
        String api = EndPointManager.apiendpoints.get("createEDM");
        String url = EndPointManager.baseUrl + api;
        System.out.println(" Creating Edm " + dataSource);
        Map<String, String> params = new HashMap<>();
        params.put("datasourcename", dataSource);
        params.put("operation", "CREATE");
        if (databaseStorage.equals(DatabaseStorageType.DataBridge.name()))
            params.put("servername", serverName);

        Map<String, Object> payload = new HashMap<>();
        if (groupIds != null) {
            payload.put("groups", groupIds);
            payload.put("share", (groupIds.size() > 0));
            payload.put("tagIds", new ArrayList<String>());
        } else {
            payload.put("groups", new ArrayList<String>());
            payload.put("share", false);
            payload.put("tagIds",new ArrayList<String>());
        }

        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", params, false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }

    public static Response exportRDMToPlatform(int[] analysisIds, String rdmName, String exportHDLossesAs, String sqlVersion, String exportFormat, String token) throws Exception {
        String api = EndPointManager.apiendpoints.get("exportRDMBAK");
        String url = EndPointManager.baseUrl + api;
        Map<String, Object> payload = new HashMap<>();
        payload.put("analysisIds",analysisIds );
        payload.put("createnew",true);
        payload.put("exportFormat",exportFormat);
        payload.put("exportType","RDM");
        payload.put("rdmName", rdmName);
        payload.put("sqlVersion", sqlVersion);
        payload.put("type", "ResultsExportInputV2");
        payload.put("exportHdLossesAs",exportHDLossesAs);

        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }

    public static Response exportRDMToNewDataBridge(String dataBridgeServer, Map<String, Object> payload, String token) throws Exception {
        String api = EndPointManager.apiendpoints.get("exportRDMBAK") + "?servername="+dataBridgeServer;
        String url = EndPointManager.baseUrl + api;


        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }

    public static Response exportFile(Map<String, Object> payload, String token) throws Exception {
        String api = EndPointManager.apiendpoints.get("exportFile");
        String url = EndPointManager.baseUrl + api;


        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }

    public static Response convertCurrencyApi(Map<String, Object> payload, String analysisId, String token) throws Exception {
        String api = String.format(EndPointManager.apiendpoints.get("convertCurrency"), analysisId);
        String url = EndPointManager.baseUrl + api;


        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }
    public static Response climateChangeApi(Map<String, Object> payload, String analysisId, String token) throws Exception {
        String api = String.format(EndPointManager.apiendpoints.get("climateChange"), analysisId);
        String url = EndPointManager.baseUrl + api;


        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPost(payload);
        return response;
    }



    public static Response pateApi(String pateOperationType,Map<String, List<Map<String, Object>>> payload, int analysisId, String token) throws Exception {
        String api = String.format(EndPointManager.apiendpoints.get("pate"), analysisId);
        String url = EndPointManager.baseUrl + api;

            RestApiHelper apiHelper =
                    new RestApiHelper(token, url, "application/json", false);
           Response response = apiHelper.submitPost(payload);
            return response;
    }




    public static Response renameAnalysisApi(Map<String, Object> payload, String analysisId, String token) throws Exception {
        String api = String.format(EndPointManager.apiendpoints.get("renameAnalysis"), analysisId);
        String url = EndPointManager.baseUrl + api;


        RestApiHelper apiHelper =
                new RestApiHelper(token, url, "application/json", false);
        Response response = apiHelper.submitPut(payload);
        return response;
    }

    public static Response CreateBucketApi(String authToken) {
        String api = EndPointManager.apiendpoints.get("createBucket");
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        Response response = restApiHelper.submitPost();
        return response;
    }

    public static Response getBucketCredentials(String authToken, Object payload, String bucketId) {
        String api = String.format(EndPointManager.apiendpoints.get("getBucketCredentials"), bucketId);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitPost(payload);
    }

    public static String uploadFile(String authToken, String bucketID, String filePath, String fileType,
                                    String fileName, long fileSize) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", fileName);
        payload.put("fileSize", fileSize);
        payload.put("fileType", fileType);
        Response response = getBucketCredentials(authToken, payload, bucketID);
        System.out.println("Create bucket http status code " + response.getStatusCode());

        String fileId = null;
        if (response.getStatusCode() == 201) {
            try {
                JsonPath jsonPath = response.jsonPath();
                String accessKey = new String(
                        Base64.getDecoder().decode((String) jsonPath.get("accessKeyId")));
                String secretKey = new String(
                        Base64.getDecoder().decode((String) (jsonPath.get("secretAccessKey"))));
                String region = new String(
                        Base64.getDecoder().decode((String) jsonPath.get("s3Region")));

                System.out.println("Region : "+region);

                String[] sessionTokenssplit = ((String) (jsonPath.get("sessionToken"))).split("\n");
                String sessionToken = "";
                for (int i = 0; i < sessionTokenssplit.length; i++) {
                    sessionToken += sessionTokenssplit[i];
                }
                String sessionTokenDecoded = new String(Base64.getDecoder().decode(sessionToken));
                String s3Path = new String(Base64.getDecoder().decode((String) (jsonPath.get("s3Path"))));
                System.out.println("s3Path: "+s3Path);
                String[] s3FileLocation = s3Path.split("/");
                String s3Bucket = s3FileLocation[0];
                StringBuilder key = new StringBuilder();
                for (int i = 1; i < s3FileLocation.length; i++) {
                    key.append(s3FileLocation[i]);
                    key.append("/");
                }
                String locationHdr = response.getHeader("Location");
                fileId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);

                fileName = fileId + "-" + fileName;
                System.out.println("Uploading file = "+fileName);
                AmazonS3 s3Client = AWSClientUtil
                        .getS3ClientSessionCredentials(accessKey, secretKey, sessionTokenDecoded, region);
                if (filePath != null) {
                    uploadToS3UsingClient(s3Client, s3Bucket, key + fileName, filePath);
                    AWSClientUtil.uploadToS3(s3Client, s3Bucket, key.toString(), filePath);
                }
            } catch (Exception ex) {
                System.out.println("Exception in uploading to S3" + ex.getMessage());
                throw ex;
            }

        }
        return fileId;
    }

    private static boolean uploadToS3UsingClient(
            AmazonS3 s3Client, String bucketName, String fileKey, String filePath) {
        System.out.println("START: multipart upload process");

        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

        List<PartETag> partETags = new ArrayList<>();
        System.out.println("START: Initiate multipart upload");
        // Initiate the multipart upload.
        InitiateMultipartUploadRequest initRequest =
                new InitiateMultipartUploadRequest(bucketName, fileKey);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
        System.out.println("END: Initiate multipart upload");
        // Upload the file parts.
        long filePosition = 0;
        for (int i = 1; filePosition < contentLength; i++) {
            long percentageComplete = (filePosition * 100 / contentLength);
            System.out.println(
                    String.format("Uploading in progress... %d%% complete", percentageComplete));
            // Because the last part could be less than 5 MB, adjust the part size as needed.
            partSize = Math.min(partSize, (contentLength - filePosition));

            // Create the request to upload a part.
            UploadPartRequest uploadRequest =
                    new UploadPartRequest()
                            .withBucketName(bucketName)
                            .withKey(fileKey)
                            .withUploadId(initResponse.getUploadId())
                            .withPartNumber(i)
                            .withFileOffset(filePosition)
                            .withFile(file)
                            .withPartSize(partSize);
            int retriesLeft = 5;
            while (retriesLeft > 0) {
                try {
                    // Upload the part and add the response's ETag to our list.
                    UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                    partETags.add(uploadResult.getPartETag());
                    retriesLeft = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("failed to upload file part. retrying... ");
                    if (retriesLeft == 1) {
                        throw new RuntimeException("File upload to S3 failed");
                    }
                    retriesLeft--;
                }
            }
            filePosition += partSize;
        }
        System.out.println("file upload 100% complete");
        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest =
                new CompleteMultipartUploadRequest(
                        bucketName, fileKey, initResponse.getUploadId(), partETags);
        s3Client.completeMultipartUpload(compRequest);
        System.out.println("END: multipart upload process");
        return true;
    }

    public static Response createPortfolio(String authToken, String datasource, String portfolioNumber, String portfolioName, String portfolioDescription) {

        String api = EndPointManager.apiendpoints.get("createPortfolio")+datasource;
        String url = EndPointManager.baseUrl + api;

        Map<String, Object> payload = new HashMap<>();
        payload.put("number", portfolioNumber);
        payload.put("name", portfolioName);
        payload.put("description", portfolioDescription);

        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitPost(payload);
    }


    public static Response submitMRIJob(String authToken, Map<String, Object> payload) {

        String api = EndPointManager.apiendpoints.get("submitMRIJob");
        String url = EndPointManager.baseUrl + api;

        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitPost(payload);
    }

    public static Response batchAPI(String authToken, Object payload) {

        String api = EndPointManager.apiendpoints.get("batchAPI");
        String url = EndPointManager.baseUrl + api;

        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitPost(payload);
    }

    public static Response groupingAPI(String authToken, Object payload) {

        String api = EndPointManager.apiendpoints.get("analysis-groups");
        String url = EndPointManager.baseUrl + api;

        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitPost(payload);
    }
    public static Response getModelProfileTemplate(String authToken, Map<String, String> tc, Perils perils) throws IOException {
        String api = String.format(EndPointManager.apiendpoints.get("getModelProfileTemplate"),perils.getAnalysisType(), perils.getModelRegion(), perils.getSubRegions(),
                perils.getPeril(), perils.getVersion(), perils.getVendor(), perils.getInsuranceType(), perils.getAnalysisMode(),perils.getFire(),
                perils.getCoverage(),perils.getProperty());
        System.out.println("---------ProfileTemplate payload for " + perils.getPeril() + " = " + api);
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitGet();
    }

    public static Response getAllHDModelProfiles(String authToken) {

        String api = String.format(EndPointManager.apiendpoints.get("getHDModelProfile"));
        String url = EndPointManager.baseUrl + api;
        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        return restApiHelper.submitGet();
    }

    public static Response createModelProfile(String authToken, String modelProfileTemplateId, Object payload) {
        String api = String.format(EndPointManager.apiendpoints.get("createNAEQModelProfile"), modelProfileTemplateId);
        String url = EndPointManager.baseUrl + api;

        RestApiHelper restApiHelper =
                new RestApiHelper(
                        authToken, url, "application/json", false);
        System.out.println("Calling API Just before Post");
        return restApiHelper.submitPost(payload);
    }


}
