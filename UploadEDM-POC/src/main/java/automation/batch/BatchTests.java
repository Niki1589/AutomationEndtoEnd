package automation.batch;
import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.merge.jsonMapper.Perils;
import automation.utils.Utils;
import com.google.gson.*;
import io.restassured.response.Response;
import java.lang.reflect.Field;
import java.util.*;

public class BatchTests {

    public void batchAPI(Map<String, String> tc,String portfolioId,String dataSourceName) throws Exception {

        System.out.println("***** Running Batch Api Tests ********");
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Perils perils = Perils.extractPerilFromTC(tc);

        String modelProfileId = ModelProfileAPI.getModelProfileApi(perils, tc, token);

        System.out.println(dataSourceName+" ************* "+portfolioId);
        if (dataSourceName == null) {
            throw new Exception("DataSource is required!");
        }
        if (portfolioId == null) {
            throw new Exception("PortfolioId is required!");
        }

        try {
            Object payloadObject = getPayloadBatchApi(
                    perils.getPortfolioId(),
                    dataSourceName,
                    modelProfileId,
                    Utils.isTrue(tc.get("isGeoCoded")),
                    perils
            );
            System.out.println("After  Batch payload");
            Response batchResponse = ApiUtil.batchAPI(token, payloadObject);
            String hdr = batchResponse.getHeader("Location");
            String jobId = hdr.substring(hdr.lastIndexOf('/') + 1);
            System.out.println(batchResponse.getStatusCode() + "  :Batch Status: jobId:" + jobId);

            String msg = null;
            try {
                msg = ApiUtil.waitForJobToComplete(jobId, token, "Batch API");
            } catch (Exception e) {
                System.out.println("Error in waitForJobToComplete : " + e.getMessage());
                throw new RuntimeException(e);
            }
            System.out.println("waitforjob msg: " + msg);
            System.out.println("***** Finished Batch Api Tests");
            System.out.println("***** Finished till " + perils.getPeril());
           int analysisId = ApiUtil.getAnalysisIDByJobId(jobId, token);
            if(tc.get("if_model_run").equals("YES") && analysisId!=-1)
            {
                Integer index=null;
                index=Integer.valueOf(tc.get("index"));
                try{
                    System.out.println("index is="+index);
                }
                catch(Exception e){
                }
                if(index!=null){
                    try{
                        String targetColumn="analysisId";
                        int columnIndex=LoadData.getColumnIndex(targetColumn);
                        if(columnIndex!=-1)
                        {
                            LoadData.UpdateTCInLocalCSV_Analysis(index,columnIndex,String.valueOf(analysisId));
                        }
                    }catch(Exception e){
                        System.out.println("Error on row:"+index+"is"+e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error in Code = " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
}

    public static Object getPayloadBatchApi(
            String portfolioId,
            String dataSourceName,
            String ModelProfileId,
            Boolean isGeoCoded,
            Perils perils
    ) {
        Gson gson = new Gson(); //To convert JSON to Java object, we use GSON library from google

        String perilName = perils.getPeril().replace(" ", "_");
        String batchName = perilName+"_BACTH";
        String profileId = perilName+"_Process_"+ModelProfileId;

        String geoLicenseType = "0";
        if (perils.getGeoHazLayers().contains("geoLicenseType")) {
            geoLicenseType = "1";
        }
        String geoCodedPayload = "";
        if (!isGeoCoded) {
            geoCodedPayload = "{\n" +
                    "\"continueOnFailure\": false,\n" +
                    "\"dependsOn\": [],\n" +
                    "\"input\": [ \n" +
                    "{\n" +
                    "\"name\": \"geocode\",\n" +
                    "\"type\": \"geocode\",\n" +
                    "\"engineType\": \"RL\",\n" +
                    "\"version\": \""+perils.getGeocodeVersion()+"\",\n" +
                    "\"layerOptions\": {\n" +
                    "\"skipPrevGeocoded\": "+ perils.getGeoHazLayers().contains("skipPrevGeocoded") +",\n" +
                    "\"aggregateTriggerEnabled\": \""+ perils.getGeoHazLayers().contains("aggregateTriggerEnabled") +"\",\n" +
                    "\"geoLicenseType\": \""+ geoLicenseType +"\"\n" +
                    "}\n" +
                    "},\n" +
                    "{\n" +
                    "\"name\": \"earthquake\",\n" +
                    "\"type\": \"hazard\",\n" +
                    "\"version\": \""+perils.getGeoHazVersion()+"\",\n" +
                    "\"engineType\": \"RL\",\n" +
                    "\"layerOptions\": {\n" +
                    "\"skipPrevHazard\": "+ perils.getGeoHazLayers().contains("skipPrevHazard") +",\n" +
                    "\"overrideUserDef\": "+ perils.getGeoHazLayers().contains("overrideUserDef") +"\n" +
                    "}\n" +
                    "},\n" +
                    "{\n" +
                    "\"engineType\": \"RL\",\n" +
                    "\"layerOptions\": {\n" +
                    "\"overrideUserDef\": "+ perils.getGeoHazLayers().contains("overrideUserDef") +",\n" +
                    "\"skipPrevHazard\": "+ perils.getGeoHazLayers().contains("skipPrevHazard") +"\n" +
                    "},\n" +
                    "\"type\": \"hazard\",\n" +
                    "\"name\": \"windstorm\",\n" +
                    "\"version\": \""+perils.getGeoHazVersion()+"\"\n" +
                    "}" +
                    "],\n" +
                    "\"label\": \"GEOHAZ\",\n" +
                    "\"operation\": \"/v2/portfolios/"+portfolioId+"/geohaz?datasource="+dataSourceName+"\",\n" +
                    "\"url\": \"/v2/portfolios/"+portfolioId+"/geohaz?datasource="+dataSourceName+"\"\n" +
                    "},\n";
        }

        String exposurSummary = "" +
                "        {\n" +
                "            \"continueOnFailure\": \"true\",\n" +
                "            \"dependsOn\": [\n" +
                "                \"GEOHAZ\"\n" +
                "            ],\n" +
                "            \"input\": {\n" +
                "                \"perilList\": [\n" +
                "                    \"EQ\",\n" +
                "                    \"FL\",\n" +
                "                    \"FR\",\n" +
                "                    \"TR\",\n" +
                "                    \"WS\"\n" +
                "                ],\n" +
                "                \"reportName\": \""+batchName+"\"\n" +
                "            },\n" +
                "            \"label\": \"EXPOSURE_SUMMARY\",\n" +
                "            \"operation\": \"/v2/portfolios/"+portfolioId+"/summary_report?datasource="+dataSourceName+"\"\n" +
                "        },\n";

        String payloadInString = "{\n" +
                "    \"name\": \""+batchName+"\",\n" +
                "    \"operations\": [\n" +
                        exposurSummary +
                        geoCodedPayload +
                "        {\n" +
                "            \"continueOnFailure\": true,\n" +
                "            \"dependsOn\": [\n" +
                "                \"GEOHAZ\"\n" +
                "            ],\n" +
                "            \"input\": {\n" +
                "                \"currency\": {\n" +
                "                    \"asOfDate\": \"2023-06-07\",\n" +
                "                    \"code\": \"USD\",\n" +
                "                    \"scheme\": \"RMS\",\n" +
                "                    \"vintage\": \"RL23\"\n" +
                "                },\n" +
                "                \"edm\": \""+dataSourceName+"\",\n" +
                "                \"eventRateSchemeId\": 163,\n" +
                "                \"exposureType\": \"PORTFOLIO\",\n" +
                "                \"globalAnalysisSettings\": {\n" +
                "                    \"franchiseDeductible\": false,\n" +
                "                    \"minLossThreshold\": \"1.00\",\n" +
                "                    \"numMaxLossEvent\": \"1\",\n" +
                "                    \"treatConstructionOccupancyAsUnknown\": true\n" +
                "                },\n" +
                "                \"id\": "+portfolioId+",\n" +
                "                \"modelProfileId\": "+ModelProfileId+",\n" +
                "                \"outputProfileId\": 1,\n" +
                "                \"treaties\": [],\n" +
                "                \"treatiesName\": [],\n" +
                "                \"tagIds\": []\n" +
                "            },\n" +
                "            \"label\": \""+profileId+"\",\n" +
                "            \"operation\": \"/v2/portfolios/"+portfolioId+"/process\"\n" +
                "        }" +
                "    ]\n" +
                "}";

//        "        {" +
//                "            \"label\": \"rdmExport\",\n" +
//                "            \"operation\": \"/v2/exports\",\n" +
//                "            \"dependsOn\": [\n" +
//                "                "+ profileId +
//                "            ],\n" +
//                "            \"input\": {\n" +
//                "                \"analysisIds\": [\n" +
//                "                    \"{{$."+profileId+".output.analysisId}}\"\n" +
//                "                ],\n" +
//                "                \"rdmName\": \""+dataSourceName+"\",\n" +
//                "                \"type\": \"ResultsExportInputV2\",\n" +
//                "                \"exportType\": \"RDM\",\n" +
//                "                \"createnew\": true,\n" +
//                "                \"exportFormat\": \"BAK\",\n" +
//                "                \"sqlVersion\": \"2019\"\n" +
//                "            }\n" +
//                "        }"+

        System.out.println("Payload Batch API");
                System.out.println(payloadInString);
        return gson.fromJson(payloadInString, Object.class);
    }

    // Custom toMap function to convert model and it's fields to hashmap
    // This will also convert nested fields that are models to hashmap
    public static Object toMap(Object mainObj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        try {
            // Iterating over each field of mainObj
            for (Field field : mainObj.getClass().getDeclaredFields()) {
                if (field != null) {
                    // Accessing value of field from mainObj storing it to Object cause we don't know the type of field
                    Object ob = field.get(mainObj);
                    if (ob != null) {
                        // checking if ob is a model to convert to hashmap
                        if (!ob.getClass().isPrimitive() && !ob.getClass().isArray() && !ob.getClass().getName().startsWith("java")) {
                            ob = toMap(ob);
                        }

                        // stroing values to hashmap
                        map.put( field.getName(), ob );
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception while converting to map = "+ex.getMessage());
        }
        return map;
    }
}