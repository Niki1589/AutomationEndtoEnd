package com.rms.automation.batchApi;
import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.PATEApi.PATETests;
import com.rms.automation.Upload.UploadRDM;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.currencyConverterApi.CurrencyConverter;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.exportApi.RDMExport.RdmExportTests;
import com.rms.automation.merge.jsonMapper.Perils;
import com.rms.automation.renameAnalysisApi.RenameAnalysis;
import com.rms.automation.utils.Utils;
import com.google.gson.*;
import io.restassured.response.Response;
import java.lang.reflect.Field;
import java.util.*;

public class BatchTests {

  // public static String referenceAnalysisId ="";
    public static void batchAPI(Map<String, String> tc,String portfolioId,String dataSourceName) throws Exception {

        String analysisId = "";
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        Perils perils = Perils.extractPerilFromTC(tc);

        try {
            if (perils.getIfModelRun().equalsIgnoreCase("YES")) {

                System.out.println("***** Running Batch Api Tests ********");

                String modelProfileId = ModelProfileAPI.getModelProfileApi(perils, tc, token);

                System.out.println(dataSourceName + " ************* " + portfolioId);
                if (dataSourceName == null) {
                    throw new Exception("DataSourcename is null or empty, please provide a valid dataSourceName");
                }
                if (portfolioId == null) {
                    throw new Exception("PortfolioId is null or empty, please provide a valid portflioId");
                }

            Object payloadObject = getPayloadBatchApi(perils.getPortfolioId(), dataSourceName, modelProfileId, Utils.isTrue(tc.get("GEO_IS_GEOCODE")), perils);
                    System.out.println("After Batch payload");
                    Response batchResponse = ApiUtil.batchAPI(token, payloadObject);
                    String hdr = batchResponse.getHeader("Location");
                    String jobId = hdr.substring(hdr.lastIndexOf('/') + 1);
                    System.out.println(batchResponse.getStatusCode() + "  :Batch Status: jobId:" + jobId);

                String msg = null;
                try {
                    msg = JobsApi.waitForJobToComplete(jobId, token, "Batch API");
                } catch (Exception e) {
                    System.out.println("Error in waitForJobToComplete : " + e.getMessage());
                    throw new RuntimeException(e);
                }
                System.out.println("wait for job msg: " + msg);
                System.out.println("***** Finished Batch Api Tests");
                System.out.println("***** Finished till " + perils.getPeril());
                analysisId = JobsApi.getAnalysisIDByJobId(jobId, token);

                if (!analysisId.isEmpty()) {
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MRN_ANALYSIS_ID", analysisId);
                }
                // Perform downstream workflows - RDM Export, File Export, Convert Currency , Rename Analysis, Pate, Climate Change
                executeDownStreamWorkflows(tc, analysisId);
            }

            else {
                analysisId = tc.get("MRN_ANALYSIS_ID");

                // Perform downstream workflows - RDM Export, File Export, Convert Currency , Rename Analysis, Pate
                executeDownStreamWorkflows(tc, analysisId);
            }
        } catch (Exception e) {
            System.out.println("Error in Code = " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static void executeDownStreamWorkflows(Map<String, String> tc, String analysisId) throws Exception {
      //  String caseNo = tc.get("TEST_CASE_NO");

        if (Utils.isTrue(tc.get("REX_IF_RDM_EXPORT"))) {
            RdmExportTests.rdmExport(tc, analysisId);
        }
        if (Utils.isTrue(tc.get("REX_IF_FILE_EXPORT"))) {
            FileExportTests.fileExport(tc, analysisId);
        }
        if (Utils.isTrue(tc.get("IF_IMPR_ANALYSIS_FROM_RDM"))) {
            UploadRDM.executeUploadRdm(tc);
        }
        if (Utils.isTrue(tc.get("CCU_IS_CONVERT_CURRENCY"))) {
            CurrencyConverter.convert(tc, analysisId);
        }
        if (Utils.isTrue(tc.get("RNM_IS_RENAME_ANALYSIS"))) {
            RenameAnalysis.rename(tc, analysisId);
        }
        if (Utils.isTrue(tc.get("IS_PATE"))) {
            PATETests.executePATETests(tc.get("TEST_CASE_NO"), analysisId);
        }
        if (Utils.isTrue(tc.get("CCG_IS_CLIMATE_CHANGE"))) {
            ClimateChangeTests.climateChange(tc, analysisId);
        }

        //        for (String key : tc.keySet()) {
//            switch (key) {
//                case "REX_IF_RDM_EXPORT":
//                    if (Utils.isTrue(tc.get(key))) {
//                       // export.exportType(tc, analysisId);
//                        RdmExportTests.rdmExport(tc,analysisId);
//                    }
//                    break;
//
//                case "REX_IF_FILE_EXPORT":
//                    if (Utils.isTrue(tc.get(key))) {
//                       // export.exportType(tc, analysisId);
//                        FileExportTests.fileExport(tc,analysisId);
//                    }
//                    break;
//
//                case "IF_IMPR_ANALYSIS_FROM_RDM":
//                    if (Utils.isTrue(tc.get(key))) {
//                        UploadRDM.executeUploadRdm(tc);
//                    }
//                    break;
//
//                case "CCU_IS_CONVERT_CURRENCY":
//                    if (Utils.isTrue(tc.get(key))) {
//                        CurrencyConverter.convert(tc, analysisId);
//                    }
//                    break;
//                case "RNM_IS_RENAME_ANALYSIS":
//                    if (Utils.isTrue(tc.get(key))) {
//                        RenameAnalysis.rename(tc, analysisId);
//                    }
//                    break;
//                case "IS_PATE":
//                    if (Utils.isTrue(tc.get(key))) {
//                        PATETests.executePATETests(caseNo, analysisId);
//                    }
//                    break;
//                case "CCG_IS_CLIMATE_CHANGE":
//                    if (Utils.isTrue(tc.get(key))) {
//                        ClimateChangeTests.climateChange(tc, analysisId);
//                    }
//                    break;
//
//                case "IS_GROUPING":
//                    if (Utils.isTrue(tc.get(key))) {
//
//                        AnalysisGroupingTests.execute();
//                    }
//                    break;
//
//
//
//            }
//        }
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
        String payloadInString="{\n"+
                "\"name\":\""+batchName+"\",\n"+
                "\"operations\":[\n"+
                exposurSummary+
                geoCodedPayload+
                "{\n"+
                "\"continueOnFailure\":true,\n"+
                "\"dependsOn\":[\n"+
                "\"GEOHAZ\"\n"+
                "],\n"+
                "\"input\":{\n"+
                "\"currency\":{\n"+
                "\"asOfDate\":\""+ perils.getAsOfDateProcess() +"\",\n"+
                "\"code\":\""+ perils.getCurrencyCodeProcess() +"\",\n"+
                "\"scheme\":\""+ perils.getCurrencySchemeProcess() +"\",\n"+
                "\"vintage\":\"" + perils.getCurrencyVintageProcess() + "\"\n"+
                "},\n"+
                "\"edm\":\""+dataSourceName+"\",\n"+
                "\"eventRateSchemeId\":0,\n"+
                "\"exposureType\":\"PORTFOLIO\",\n"+
                "\"globalAnalysisSettings\":{\n"+
                "\"franchiseDeductible\":false,\n"+
                "\"minLossThreshold\":\"1.00\",\n"+
                "\"numMaxLossEvent\":\"1\",\n"+
                "\"treatConstructionOccupancyAsUnknown\":true\n"+
                "},\n"+
                "\"id\":"+portfolioId+",\n"+
                "\"modelProfileId\":"+ModelProfileId+",\n"+
                "\"outputProfileId\":"+perils.getOutputProfileId()+",\n"+
                "\"treaties\":["+perils.getTreaties()+"],\n"+
                "\"treatiesName\":["+ perils.getTreatiesName() +"],\n"+
                "\"tagIds\":[]\n"+
                "},\n"+
                "\"label\":\""+profileId+"\",\n"+
                "\"operation\":\"/v2/portfolios/"+portfolioId+"/process\"\n"+
                "}"+
                "]\n"+
                "}";




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