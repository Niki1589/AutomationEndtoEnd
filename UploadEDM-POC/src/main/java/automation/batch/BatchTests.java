package automation.batch;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.edm.ProfileTemplate;
import automation.merge.jsonMapper.Perils;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.rms.core.qe.common.RestApiHelper;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import automation.mriImport.MRIImportTests;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

public class BatchTests {

    @DataProvider(name="loadFromCSV")
    public Object[] provider() throws IOException {
        return LoadData.readModelProfileTCFromLocalCSV();
    }

    @Test(dataProvider = "loadFromCSV", dependsOnMethods = "MRIImport")
    public void batchAPI(Map<String, String> tc,String portfolioId,String dataSourceName) throws Exception {

        System.out.println("***** Running Batch Api Tests ********");
//
        String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

        System.out.println(dataSourceName+" ************* "+portfolioId);
        if (dataSourceName == null) {
            throw new Exception("DataSource is required!");
        }
        if (portfolioId == null) {
            throw new Exception("PortfolioId is required!");
        }

        for(int i = 0 ; i < 2 ; i++) {
            Perils perils = new Perils();
            if (tc.get("peril" + i).length() > 0) {
                perils.setPeril(tc.get("peril" + i));
                perils.setIgnoreContractDates(tc.get("ignoreContractDates" + i).contains("YES"));
                perils.setEngine(tc.get("engine" + i));
                perils.setAlternateVulnCode(tc.get("alternateVulnCode" + i));
                perils.setLabelRegion(tc.get("LabelRegion" + i));
                perils.setNumberOfSamples(tc.get("numberOfSamples" + i));
                perils.setPetName(tc.get("petName" + i));
                perils.setPetDataVersion(tc.get("petDataVersion" + i));
                perils.setNumberOfPeriods(tc.get("numberOfPeriods" + i));
                perils.setInsuranceType(tc.get("insuranceType" + i));
                perils.setAnalysisType(tc.get("analysisType" + i));
                perils.setLocationPerRisk(tc.get("locationPerRisk" + i));
                perils.setVersion(tc.get("version" + i));
                perils.setEndYear(tc.get("endYear" + i));
                perils.setEventRateSchemeId(tc.get("eventRateSchemeId" + i));
                perils.setPolicyPerRisk(tc.get("policyPerRisk" + i));
                perils.setDescription(tc.get("description" + i));
                perils.setModelRegion(tc.get("modelRegion" + i));
                perils.setSubRegions(tc.get("subRegions" + i));
                perils.setAnalysisMode(tc.get("analysisMode" + i));
                perils.setStartYear(tc.get("startYear" + i));
                perils.setGmpeName(tc.get("gmpeName" + i));
                perils.setApplyPLA(tc.get("applyPLA" + i).contains("YES"));
                perils.setGmpeCode(tc.get("gmpeCode" + i));
                perils.setSubPeril(tc.get("subPeril" + i));
                perils.setRegion(tc.get("region" + i));
                perils.setExcludePostalCodes(tc.get("excludePostalCodes" + i));
                perils.setFireOnly(tc.get("fireOnly" + i));
                perils.setPerilOverride(tc.get("perilOverride" + i));
                perils.setDynamicAutomobileModeling(tc.get("dynamicAutomobileModeling" + i).contains("YES"));
                perils.setIncludePluvial(tc.get("includePluvial" + i).contains("YES"));
                perils.setIncludeBespokeDefence(tc.get("includeBespokeDefence" + i).contains("YES"));
                perils.setDefenceOn(tc.get("defenceOn" + i).contains("YES"));
                perils.setSubPerils(List.of(tc.get("subPerils" + i).split(",")));
                perils.setPolicyCoverages(tc.get("policyCoverages" + i));
                perils.setVendor(tc.get("vendor" + i));
                perils.setRun1dOnly(tc.get("run1dOnly" + i).contains(("YES")));
                perils.setSpecialtyModels(List.of(tc.get("specialtyModels" + i).split(",")));
                perils.setFire(tc.get("fire" + i).contains("YES"));
                perils.setCoverage(tc.get("coverage" + i).contains("YES"));
                perils.setProperty(tc.get("property" + i));
                perils.setUnknownForPrimaryCharacteristics(List.of(tc.get("unknownForPrimaryCharacteristics" + i).split(",")));
                perils.setScaleExposureValues(List.of(tc.get("scaleExposureValues" + i).split(",")));
                perils.setSecondaryPerils(List.of(tc.get("secondaryPerils" + i).split(",")));

                String NAEQmodelProfileId = null;
                if (tc.get("ifCreateModelProfile").equals("YES")) {
                    NAEQmodelProfileId = createNAEQProfile(token, tc, true, perils);
                } else {
                    NAEQmodelProfileId = createNAEQProfile(token, tc, false, perils);

                }
                Object payloadObject = getPayloadBatchApi(portfolioId, dataSourceName, NAEQmodelProfileId);

                Response batchResponse = ApiUtil.batchAPI(token, payloadObject);
                String hdr = batchResponse.getHeader("Location");
                String jobId = hdr.substring(hdr.lastIndexOf('/') + 1);
                System.out.println(batchResponse.getStatusCode() + "  :Batch Status: jobId:" + jobId);

                String msg = ApiUtil.waitForJobToComplete(jobId, token);
                System.out.println("waitforjob msg: " + msg);
                System.out.println("***** Finished Batch Api Tests");
                System.out.println("***** Finished till "+perils.getPeril());
            }
        }
    }

    public static String createNAEQProfile(String token, Map<String, String> tc,Boolean isCreateModelProfile, Perils perils) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {

        String randmVal = RandomStringUtils.randomNumeric(3);
        String NAEQ_ModelProfile_Name = "NAEQ_ModelProfile_Name_" + randmVal;
        String NAEQTemplateId = null;
        if(isCreateModelProfile == true) {
            Response res = ApiUtil.getModelProfileTemplate(token, tc,perils);

            NAEQTemplateId = res.getBody().jsonPath().get("id") + "";
            System.out.println("createNAEQProfile running: NAEQ_ModelProfile_Name:" + NAEQ_ModelProfile_Name + " .... NAEQTemplateId:" + NAEQTemplateId);
        }
        else {
            NAEQTemplateId = tc.get("mfId");
        }
        Object payload = getPayloadCreateModelProfileApi(NAEQ_ModelProfile_Name, tc,perils);
        Response res1 = ApiUtil.createNAEQModelProfile(token, NAEQTemplateId, payload);

        ArrayList list = res1.getBody().jsonPath().get("links");
        String link = ((String) ((Map) list.get(0)).get("href"));
        String NAEQmodelProfileId = link.substring(link.lastIndexOf('/')+1);
        System.out.println("createNAEQModelProfile: Finnished "+link+"    and   id is "+NAEQmodelProfileId);

        return NAEQmodelProfileId;

    }

//    public static Response getModelProfileTemplate(String authToken,Map<String, String> tc) throws IOException {
//        // String perilType = LoadData.config.getPerilType();
//        String perilType=tc.get("peril");
//        Map<String, automation.edm.ProfileTemplate> profileTemplateMap = Map<String, ProfileTemplate>(LoadData.readModelProfileTCFromLocalCSV());
//        ProfileTemplate profileTemplate = profileTemplateMap.get(perilType);
//        String api = String.format(apiendpoints.get("getModelProfileTemplate"), profileTemplate.getAnalysisType(), profileTemplate.getModelRegionCode(), profileTemplate.getSubRegion(),
//                profileTemplate.getPerilName(), profileTemplate.getSoftwareVersion(), profileTemplate.getVendor(), profileTemplate.getInsuranceType(), profileTemplate.getAnalysisMode());
//        String url = baseUrl + api;
//
//        RestApiHelper restApiHelper =
//                new RestApiHelper(
//                        authToken, url, "application/json", false);
//        return restApiHelper.submitGet();
//    }


    public static Object getPayloadBatchApi(String portfolioId, String dataSourceName, String NAEQmodelProfileId) {
        Gson gson = new Gson();
        String payloadInString = "{\n" +
                "    \"name\": \"NAEQ_BATCH\",\n" +
                "    \"operations\": [\n" +
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
                "                \"reportName\": \"NAEQ_BATCH\"\n" +
                "            },\n" +
                "            \"label\": \"EXPOSURE_SUMMARY\",\n" +
                "            \"operation\": \"/v2/portfolios/"+portfolioId+"/summary_report?datasource="+dataSourceName+"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"continueOnFailure\": false,\n" +
                "            \"dependsOn\": [],\n" +
                "            \"input\": [ \n" +
                "                {\n" +
                "                \"name\": \"geocode\",\n" +
                "                \"type\": \"geocode\",\n" +
                "                \"engineType\": \"RL\",\n" +
                "                \"version\": \"23.0\",\n" +
                "                \"layerOptions\": {\n" +
                "                    \"skipPrevGeocoded\": false,\n" +
                "                    \"aggregateTriggerEnabled\": \"true\",\n" +
                "                    \"geoLicenseType\": \"0\"\n" +
                "                }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"earthquake\",\n" +
                "                    \"type\": \"hazard\",\n" +
                "                    \"version\": \"23.0\",\n" +
                "                    \"engineType\": \"RL\",\n" +
                "                    \"layerOptions\": {\n" +
                "                        \"skipPrevHazard\": false,\n" +
                "                        \"overrideUserDef\": false\n" +
                "                    }\n" +
                "                }\n" +
                "            ],\n" +
                "            \"label\": \"GEOHAZ\",\n" +
                "            \"operation\": \"/v2/portfolios/"+portfolioId+"/geohaz?datasource="+dataSourceName+"\",\n" +
                "            \"url\": \"/v2/portfolios/"+portfolioId+"/geohaz?datasource="+dataSourceName+"\"\n" +
                "        },\n" +
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
                "                \"eventRateSchemeId\": 0,\n" +
                "                \"exposureType\": \"PORTFOLIO\",\n" +
                "                \"globalAnalysisSettings\": {\n" +
                "                    \"franchiseDeductible\": false,\n" +
                "                    \"minLossThreshold\": \"1.00\",\n" +
                "                    \"numMaxLossEvent\": \"1\",\n" +
                "                    \"treatConstructionOccupancyAsUnknown\": true\n" +
                "                },\n" +
                "                \"id\": "+portfolioId+",\n" +
                "                \"modelProfileId\": "+NAEQmodelProfileId+",\n" +
                "                \"outputProfileId\": 1,\n" +
                "                \"treaties\": [],\n" +
                "                \"treatiesName\": [],\n" +
                "                \"tagIds\": []\n" +
                "            },\n" +
                "            \"label\": \"Profile_"+NAEQmodelProfileId+"\",\n" +
                "            \"operation\": \"/v2/portfolios/"+portfolioId+"/process\"\n" +
                "        }" +
                "    ]\n" +
                "}";
        System.out.println(payloadInString);
        return gson.fromJson(payloadInString, Object.class);
    }

    public static Object getPayloadCreateModelProfileApi(String NAEQ_ModelProfile_Name, Map<String, String> tc,Perils perils) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Gson gson = new Gson();
       // String payloadInString = getPayloadOfEarthquake(NAEQ_ModelProfile_Name, tc);
          String payloadInString="";

        if (perils.getPeril().contains("Earthquake")) {
            payloadInString = getPayloadOfEarthquake(NAEQ_ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Flood"))  {
            payloadInString = getPayloadOfFlood(NAEQ_ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Windstorm"))  {
            payloadInString = getPayloadOfWindstorm(NAEQ_ModelProfile_Name, tc);
        } else if (perils.getPeril().contains("Severe Convective Storm"))  {
            payloadInString = getPayloadOfsevereConvectiveStorm(NAEQ_ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Terrorism"))  {
            payloadInString = getPayloadOfTerrorism(NAEQ_ModelProfile_Name, tc,perils);
        } else if (perils.getPeril().contains("Wildfire"))  {
            payloadInString = getPayloadOfWildfire(NAEQ_ModelProfile_Name, tc);
        }
        System.out.println("Payload for -------- "+perils.getPeril());
        System.out.println(payloadInString);

        CreateModelProfileApi createModelProfileApi = new CreateModelProfileApi();
        try {
            // Converting json to model
            createModelProfileApi = gson.fromJson(payloadInString, CreateModelProfileApi.class);
        } catch (Exception ex) {
            System.out.print("Error while parsing class from json = "+ex.getMessage());

        }

        // Converting model to HashMap
        Object mapped = toMap(createModelProfileApi);
        return mapped;
    }
    public static String getPayloadOfEarthquake(String NAEQ_ModelProfile_Name, Perils perils) {

        List<String> subPerils = perils.getSubPerils();
        List<String> secondaryPerils = perils.getSecondaryPerils();
        List<String> specialtyModels = perils.getSpecialtyModels();
        List<String> scaleExposureValues = perils.getScaleExposureValues();
        List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+"\n" +
                "    },\n" +
                "    \"Earthquake\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"tsunami\": "+subPerils.contains("tsunami")+",\n" +
                "            \"shake\": "+subPerils.contains("shake")+",\n" +
                "            \"fire\": "+subPerils.contains("fire")+"\n" +
                "        },\n" +
                "        \"secondaryPerils\": {\n" +
                "            \"landslide\": "+secondaryPerils.contains("landslide")+",\n" +
                "            \"liquefaction\": "+secondaryPerils.contains("liquefaction")+"\n" +
                "        },\n" +
                "        \"gmpeName\": \""+perils.getGmpeName()+"\",\n" +
                "        \"applyPLA\": "+perils.getApplyPLA()+",\n" +
                "        \"gmpeCode\": \""+perils.getGmpeCode()+"\"\n" +
                "    },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfFlood(String NAEQ_ModelProfile_Name, Perils perils){

        List<String> subPerils = perils.getSubPerils();
        List<String> specialtyModels = perils.getSpecialtyModels();
        List<String> scaleExposureValues = perils.getScaleExposureValues();
        List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "     \"subPeril\": \""+perils.getSubPeril()+"\",\n"+
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"run1dOnly\": "+perils.getRun1dOnly()+",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+",\n" +
                "        \"tagIds\": "+new ArrayList<>()+"\n" +
                "    },\n" +
                "    \"Flood\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"flood\": "+subPerils.contains("flood")+"\n" +
                "        },\n" +
                "        \"applyPLA\": "+perils.getApplyPLA()+",\n" +
                "        \"includePluvial\": "+perils.getIncludePluvial()+",\n" +
                "        \"includeBespokeDefence\": "+perils.getIncludeBespokeDefence()+",\n" +
                "        \"defenceOn\": "+perils.getDefenceOn()+"\n" +
                "    },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        },\n" +
                "        \"scaleExposureValues\": {\n" +
                "            \"building\": "+scaleExposureValues.contains("building")+",\n" +
                "            \"businessInterruption\": "+scaleExposureValues.contains("businessInterruption")+",\n" +
                "            \"content\": "+scaleExposureValues.contains("content")+"\n" +
                "        },\n" +
                "        \"unknownForPrimaryCharacteristics\": {\n" +
                "            \"constructionClass\": "+unknownForPrimaryCharacteristics.contains("constructionClass")+",\n" +
                "            \"floorArea\": "+unknownForPrimaryCharacteristics.contains("floorArea")+",\n" +
                "            \"floorsOccupied\": "+unknownForPrimaryCharacteristics.contains("floorsOccupied")+",\n" +
                "            \"numberOfStories\": "+unknownForPrimaryCharacteristics.contains("numberOfStories")+",\n" +
                "            \"occupancyClass\": "+unknownForPrimaryCharacteristics.contains("occupancyClass")+",\n" +
                "            \"yearBuilt\": "+unknownForPrimaryCharacteristics.contains("yearBuilt")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfWildfire(String NAEQ_ModelProfile_Name, Map<String, String> tc) {

        List<String> subPerils = List.of(tc.get("subPerils").split(","));
        List<String> specialtyModels = List.of(tc.get("specialtyModels").split(","));
        List<String> scaleExposureValues = List.of(tc.get("scaleExposureValues").split(","));
        List<String> unknownForPrimaryCharacteristics = List.of(tc.get("unknownForPrimaryCharacteristics").split(","));

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "     \"subPeril\": \""+tc.get("subPeril")+"\",\n"+
                "        \"peril\": \""+tc.get("peril")+"\",\n" +
                "        \"ignoreContractDates\": "+tc.get("ignoreContractDates").contains("YES")+",\n" +
                "        \"engine\": \""+tc.get("engine")+"\",\n" +
                "        \"alternateVulnCode\": "+tc.get("alternateVulnCode")+",\n" +
                "        \"LabelRegion\": \""+tc.get("LabelRegion")+"\",\n" +
                "        \"numberOfSamples\": "+tc.get("numberOfSamples")+",\n" +
                "        \"petName\": \""+tc.get("petName")+"\",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"smoke\": "+subPerils.contains("smoke")+",\n" +
                "        \"petDataVersion\": \""+tc.get("petDataVersion")+"\",\n" +
                "        \"numberOfPeriods\": "+tc.get("numberOfPeriods")+",\n" +
                "        \"insuranceType\": \""+tc.get("insuranceType")+"\",\n" +
                "        \"region\": \""+tc.get("region")+"\",\n" +
                "        \"analysisType\": \""+tc.get("analysisType")+"\",\n" +
                "        \"fire\": "+subPerils.contains("fire")+",\n" +
                "        \"locationPerRisk\": \""+tc.get("locationPerRisk")+"\",\n" +
                "        \"applyPLA\": "+tc.get("applyPLA")+",\n" +
                "        \"version\": \""+tc.get("version")+"\",\n" +
                "        \"endYear\": "+tc.get("endYear")+",\n" +
                "        \"eventRateSchemeId\": "+tc.get("eventRateSchemeId")+",\n" +
                "        \"policyPerRisk\": \""+tc.get("policyPerRisk")+"\",\n" +
                "        \"description\": \""+tc.get("description")+"\",\n" +
                "        \"modelRegion\": \""+tc.get("modelRegion")+"\",\n" +
                "        \"subRegions\": \""+tc.get("subRegions")+"\",\n" +
                "        \"analysisMode\": \""+tc.get("analysisMode")+"\",\n" +
                "        \"startYear\": "+tc.get("startYear")+"\n" +
                "       },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        },\n" +
                "        \"scaleExposureValues\": {\n" +
                "            \"building\": "+scaleExposureValues.contains("building")+",\n" +
                "            \"businessInterruption\": "+scaleExposureValues.contains("businessInterruption")+",\n" +
                "            \"content\": "+scaleExposureValues.contains("content")+"\n" +
                "        },\n" +
                "        \"unknownForPrimaryCharacteristics\": {\n" +
                "            \"constructionClass\": "+unknownForPrimaryCharacteristics.contains("constructionClass")+",\n" +
                "            \"floorArea\": "+unknownForPrimaryCharacteristics.contains("floorArea")+",\n" +
                "            \"floorsOccupied\": "+unknownForPrimaryCharacteristics.contains("floorsOccupied")+",\n" +
                "            \"numberOfStories\": "+unknownForPrimaryCharacteristics.contains("numberOfStories")+",\n" +
                "            \"occupancyClass\": "+unknownForPrimaryCharacteristics.contains("occupancyClass")+",\n" +
                "            \"yearBuilt\": "+unknownForPrimaryCharacteristics.contains("yearBuilt")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfTerrorism(String NAEQ_ModelProfile_Name, Map<String, String> tc,Perils perils) {
        List<String> subPerils = perils.getSubPerils();
        List<String> specialtyModels = perils.getSpecialtyModels();
        List<String> scaleExposureValues =perils.getScaleExposureValues();
        List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();
        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"excludePostalCodes\": "+perils.getExcludePostalCodes()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"fireOnly\": \""+perils.getFireOnly()+"\",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"perilOverride\": \""+perils.getPerilOverride()+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"region\": \""+perils.getRegion()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+"\n" +
                "    },\n" +
                "    \"Terrorism\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"Aircraft Impact\": "+subPerils.contains("Aircraft Impact")+",\n" +
                "            \"Bombs\": "+subPerils.contains("Bombs")+",\n" +
                "            \"Radiological - Dirty Bomb\": "+subPerils.contains("Radiological - Dirty Bomb")+",\n" +
                "            \"Sabotage - Hazmat Transportation\": "+subPerils.contains("Sabotage - Hazmat Transportation")+",\n" +
                "            \"Sabotage - Nuclear Plant,Sabotage - Industrial Plant (vapor release)\": "+subPerils.contains("Sabotage - Nuclear Plant,Sabotage - Industrial Plant (vapor release)")+",\n" +
                "            \"Radiological - Dirty Bomb\": "+subPerils.contains("Radiological - Dirty Bomb")+",\n" +
                "            \"Nuclear Bomb\": "+subPerils.contains("Nuclear Bomb")+",\n" +
                "            \"Conflagration\": "+subPerils.contains("Conflagration")+",\n" +
                "            \"Sabotage - Industrial Plant (explosion only)\": "+subPerils.contains("Sabotage - Industrial Plant (explosion only)")+"\n" +
                "        }\n" +
                "    },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        },\n" +
                "        \"scaleExposureValues\": {\n" +
                "            \"building\": "+scaleExposureValues.contains("building")+",\n" +
                "            \"businessInterruption\": "+scaleExposureValues.contains("businessInterruption")+",\n" +
                "            \"content\": "+scaleExposureValues.contains("content")+"\n" +
                "        },\n" +
                "        \"unknownForPrimaryCharacteristics\": {\n" +
                "            \"constructionClass\": "+unknownForPrimaryCharacteristics.contains("constructionClass")+",\n" +
                "            \"floorArea\": "+unknownForPrimaryCharacteristics.contains("floorArea")+",\n" +
                "            \"floorsOccupied\": "+unknownForPrimaryCharacteristics.contains("floorsOccupied")+",\n" +
                "            \"numberOfStories\": "+unknownForPrimaryCharacteristics.contains("numberOfStories")+",\n" +
                "            \"occupancyClass\": "+unknownForPrimaryCharacteristics.contains("occupancyClass")+",\n" +
                "            \"yearBuilt\": "+unknownForPrimaryCharacteristics.contains("yearBuilt")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfsevereConvectiveStorm(String NAEQ_ModelProfile_Name, Perils perils) {
        List<String> subPerils = perils.getSubPerils();
        List<String> specialtyModels = perils.getSpecialtyModels();
        List<String> scaleExposureValues = perils.getScaleExposureValues();
        List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();
        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "        \"Peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"ProfileName\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"Version\": \""+perils.getVersion()+"\",\n" +
                "        \"Region\": \""+perils.getRegion()+"\",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"tagIds\": "+new ArrayList<>()+"\n" +
                "    },\n" +
                "    \"SevereConvectiveStorm\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"tornado\": "+subPerils.contains("tornado")+",\n" +
                "            \"hail\": "+subPerils.contains("hail")+",\n" +
                "            \"straightLineWind\": "+subPerils.contains("straightLineWind")+"\n" +
                "        },\n" +
                "        \"dynamicAutomobileModeling\": "+perils.getDynamicAutomobileModeling()+"\n" +
                "    },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        },\n" +
                "        \"scaleExposureValues\": {\n" +
                "            \"building\": "+scaleExposureValues.contains("building")+",\n" +
                "            \"businessInterruption\": "+scaleExposureValues.contains("businessInterruption")+",\n" +
                "            \"content\": "+scaleExposureValues.contains("content")+"\n" +
                "        },\n" +
                "        \"unknownForPrimaryCharacteristics\": {\n" +
                "            \"constructionClass\": "+unknownForPrimaryCharacteristics.contains("constructionClass")+",\n" +
                "            \"floorArea\": "+unknownForPrimaryCharacteristics.contains("floorArea")+",\n" +
                "            \"floorsOccupied\": "+unknownForPrimaryCharacteristics.contains("floorsOccupied")+",\n" +
                "            \"numberOfStories\": "+unknownForPrimaryCharacteristics.contains("numberOfStories")+",\n" +
                "            \"occupancyClass\": "+unknownForPrimaryCharacteristics.contains("occupancyClass")+",\n" +
                "            \"yearBuilt\": "+unknownForPrimaryCharacteristics.contains("yearBuilt")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfWindstorm(String NAEQ_ModelProfile_Name, Map<String, String> tc) {

        List<String> subPerils = List.of(tc.get("subPerils").split(","));
        List<String> specialtyModels = List.of(tc.get("specialtyModels").split(","));
        List<String> scaleExposureValues = List.of(tc.get("scaleExposureValues").split(","));
        List<String> unknownForPrimaryCharacteristics = List.of(tc.get("unknownForPrimaryCharacteristics").split(","));

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "        \"peril\": \""+tc.get("peril")+"\",\n" +
                "        \"ignoreContractDates\": "+tc.get("ignoreContractDates").contains("YES")+",\n" +
                "        \"engine\": \""+tc.get("engine")+"\",\n" +
                "        \"alternateVulnCode\": "+tc.get("alternateVulnCode")+",\n" +
                "        \"LabelRegion\": \""+tc.get("LabelRegion")+"\",\n" +
                "        \"numberOfSamples\": "+tc.get("numberOfSamples")+",\n" +
                "        \"petName\": \""+tc.get("petName")+"\",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"petDataVersion\": \""+tc.get("petDataVersion")+"\",\n" +
                "        \"numberOfPeriods\": "+tc.get("numberOfPeriods")+",\n" +
                "        \"insuranceType\": \""+tc.get("insuranceType")+"\",\n" +
                "        \"analysisType\": \""+tc.get("analysisType")+"\",\n" +
                "        \"locationPerRisk\": \""+tc.get("locationPerRisk")+"\",\n" +
                "        \"version\": \""+tc.get("version")+"\",\n" +
                "        \"endYear\": "+tc.get("endYear")+",\n" +
                "        \"policyCoverages\": {\n" +
                "            \"windstorm\": "+tc.get("windstorm")+",\n" +
                "            \"flood\": "+tc.get("flood")+"\n" +
                "        },\n" +
                "        \"eventRateSchemeId\": "+tc.get("eventRateSchemeId")+",\n" +
                "        \"policyPerRisk\": \""+tc.get("policyPerRisk")+"\",\n" +
                "        \"description\": \""+tc.get("description")+"\",\n" +
                "        \"modelRegion\": \""+tc.get("modelRegion")+"\",\n" +
                "        \"subRegions\": \""+tc.get("subRegions")+"\",\n" +
                "        \"analysisMode\": \""+tc.get("analysisMode")+"\",\n" +
                "        \"startYear\": "+tc.get("startYear")+"\n" +
                "    },\n" +
                "    \"Windstorm\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"coastalFlood\": "+subPerils.contains("coastalFlood")+",\n" +
                "            \"wind\": "+subPerils.contains("wind")+",\n" +
                "            \"inlandFlood\": "+subPerils.contains("inlandFlood")+"\n" +
                "        },\n" +
                "        \"applyPLA\": "+tc.get("applyPLA")+"\n" +
                "    },\n" +
                "    \"ExposureModifications\": {\n" +
                "        \"specialtyModels\": {\n" +
                "            \"mapBrChecked\": "+specialtyModels.contains("mapBrChecked")+",\n" +
                "            \"mapIFMChecked\": "+specialtyModels.contains("mapIFMChecked")+",\n" +
                "            \"mapMarineChecked\": "+specialtyModels.contains("mapMarineChecked")+"\n" +
                "        },\n" +
                "        \"scaleExposureValues\": {\n" +
                "            \"building\": "+scaleExposureValues.contains("building")+",\n" +
                "            \"businessInterruption\": "+scaleExposureValues.contains("businessInterruption")+",\n" +
                "            \"content\": "+scaleExposureValues.contains("content")+"\n" +
                "        },\n" +
                "        \"unknownForPrimaryCharacteristics\": {\n" +
                "            \"constructionClass\": "+unknownForPrimaryCharacteristics.contains("constructionClass")+",\n" +
                "            \"floorArea\": "+unknownForPrimaryCharacteristics.contains("floorArea")+",\n" +
                "            \"floorsOccupied\": "+unknownForPrimaryCharacteristics.contains("floorsOccupied")+",\n" +
                "            \"numberOfStories\": "+unknownForPrimaryCharacteristics.contains("numberOfStories")+",\n" +
                "            \"occupancyClass\": "+unknownForPrimaryCharacteristics.contains("occupancyClass")+",\n" +
                "            \"yearBuilt\": "+unknownForPrimaryCharacteristics.contains("yearBuilt")+"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return payloadInString;
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
