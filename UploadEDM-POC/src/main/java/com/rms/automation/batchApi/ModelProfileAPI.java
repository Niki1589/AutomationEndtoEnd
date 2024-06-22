package com.rms.automation.batchApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rms.automation.JobsApi.JobsApi;
import com.rms.automation.edm.ApiUtil;
import com.rms.automation.edm.LoadData;
import com.rms.automation.merge.jsonMapper.Perils;
import com.rms.automation.utils.Utils;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelProfileAPI {

    public static String getModelProfileApi(Perils perils, Map<String, String> tc, String token) throws Exception {
        try {
            return createModelProfile(token, tc, perils);
        } catch (Exception e) {
            System.out.println("Error in createModelProfile : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String createModelProfile(String token, Map<String, String> tc, Perils perils) throws Exception {

        //if((perils.getIfModelRun().equalsIgnoreCase("YES"))) {
            if (tc.get("MPF_IF_CREATE_MODEL_PROFILE").equalsIgnoreCase("YES")) {

                String randmVal = RandomStringUtils.randomNumeric(3);
                String ModelProfile_Name = "ModelProfile_" + tc.get("MPF_PERIL") + "_" + tc.get("MPF_MODEL_REGION") + "_" + randmVal;
                System.out.println("Model profile name : " + ModelProfile_Name);
                String TemplateId = null;

                Response res = ApiUtil.getModelProfileTemplate(token, tc, perils);
                TemplateId = res.getBody().jsonPath().get("id") + "";
                System.out.println("createNAEQProfile running: NAEQ_ModelProfile_Name:" + ModelProfile_Name + " .... TemplateId:" + TemplateId);
                String payload = ModelProfileAPI.getPayloadCreateModelProfileApi(ModelProfile_Name, tc, perils);
                System.out.println("Before Calling ModelProfile API");
                Response res1 = ApiUtil.createModelProfile(token, TemplateId, payload);
                System.out.println("After Calling ModelProfile API");

//                String locationHdr = res1.getHeader("Location");
//                String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);

                ArrayList list = res1.getBody().jsonPath().get("links");
                String link = ((String) ((Map) list.get(0)).get("href"));
                String NAEQmodelProfileId = link.substring(link.lastIndexOf('/') + 1);

//                String msg = JobsApi.waitForJobToComplete(jobId, token, "Create Model Profile API", "MPF_JOB_STATUS", tc.get("INDEX"));
//                System.out.println("wait for job msg: " + msg);


                System.out.println("create NAEQModelProfile: Finished " + link + "    and   id is " + NAEQmodelProfileId);
                int NAEQmodelProfileId_created = Integer.parseInt(NAEQmodelProfileId);
                if (NAEQmodelProfileId_created != -1) {
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_IF_CREATE_MODEL_PROFILE", "NO");
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_MFID", String.valueOf(NAEQmodelProfileId_created));
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_CREATED_NAME", ModelProfile_Name);
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_JOB_STATUS", "Model Profile is created Successfully");
                }
                else
                {
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_JOB_STATUS", "Model Profile could not be created,please check the inputs.");

                }
                return NAEQmodelProfileId;
            }
            else {
                return perils.getMfId();
            }
        }
//        else
//        {
//            System.out.println("Model run is set to NO so Model Profile will not be created");
//            return null;
//        }


    public static String getPayloadCreateModelProfileApi(String ModelProfile_Name, Map<String, String> tc, Perils perils) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, JsonProcessingException {



        String payloadInString="";
        switch (perils.getPeril()) {
            case "Earthquake":
                payloadInString = getPayloadOfEarthquake(ModelProfile_Name, perils);
                break;
            case "Flood":
                payloadInString = getPayloadOfFlood(ModelProfile_Name, tc,perils);
                break;
            case "Windstorm":
                payloadInString = getPayloadOfWindstorm(ModelProfile_Name, tc, perils);
                break;
            case "Severe Convective Storm":
                payloadInString = getPayloadOfsevereConvectiveStorm(ModelProfile_Name, perils);
                break;
            case "Terrorism":
                payloadInString = getPayloadOfTerrorism(ModelProfile_Name, tc, perils);
                break;
            case "Wildfire":
                payloadInString = getPayloadOfWildfire(ModelProfile_Name, perils);
                break;
            default:
                // Handle unknown peril
                break;
        }

        System.out.println("Payload for -------- " + perils.getPeril());
        System.out.println(payloadInString);
        return payloadInString;

    }

    public static String getPayloadOfEarthquake(String NAEQ_ModelProfile_Name, Perils perils) {

        List<String> subPerils = perils.getSubPerils();
        List<String> secondaryPerils = perils.getSecondaryPerils();
        List<String> specialtyModels = perils.getSpecialtyModels();

        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

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
                        reportsWindow +
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

    public static String getPayloadOfFlood(String NAEQ_ModelProfile_Name,Map<String, String> tc, Perils perils) throws JsonProcessingException {

        List<String> subPerils = perils.getSubPerils();
        Download_Settings_MP downloadSettings_MP = Download_Settings_MP.parse(tc.get("MPF_DOWNLOAD_SETTINGS"));

        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "     \"subPeril\": \"FL\",\n"+
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"run1dOnly\": "+Utils.isTrue(downloadSettings_MP.getRun1dOnly())+",\n" +
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
                            reportsWindow +
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
                "        \"includePluvial\": "+ Utils.isTrue(downloadSettings_MP.getIncludePluvial())+",\n" +
                "        \"includeBespokeDefence\": "+Utils.isTrue(downloadSettings_MP.getIncludeBespokeDefence())+",\n" +
                "        \"defenceOn\": "+Utils.isTrue(downloadSettings_MP.getDefenceOn())+"\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfWildfire(String NAEQ_ModelProfile_Name, Perils perils) {

        List<String> subPerils = perils.getSubPerils();

        String subPeril = "FR";
       // if (subPerils.contains("smoke")) {
        boolean containsSmoke=subPerils.stream().map(String::toLowerCase).anyMatch(s->s.equals("smoke"));
        if(containsSmoke)
        {
            subPeril += ",SM";
        }

        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "     \"subPeril\": \""+subPeril+"\",\n"+
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"name\": \""+NAEQ_ModelProfile_Name+"\",\n" +
                "        \"smoke\": "+subPerils.contains("smoke")+",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"region\": \""+perils.getRegion()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"fire\": "+perils.getFire()+",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"applyPLA\": "+perils.getApplyPLA()+",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                reportsWindow +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "    \"eventIds\": [" +
                                        perils.getEventIds()+
                "        ], \n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+"\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfTerrorism(String NAEQ_ModelProfile_Name, Map<String, String> tc, Perils perils) throws JsonProcessingException {

        List<String> subPerils = perils.getSubPerils();
        Download_Settings_MP downloadSettings_MP = Download_Settings_MP.parse(tc.get("Download_settings_mp"));
        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

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
                "        \"perilOverride\": \""+downloadSettings_MP.getPerilOverride()+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"tagIds\": [],\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                reportsWindow +
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
                "            \"Conflagration\": "+subPerils.contains("Conflagration")+",\n" +
                "            \"Sabotage - Industrial Plant (explosion only)\": "+subPerils.contains("Sabotage - Industrial Plant (explosion only)")+"\n" +
             //   "            \"Chemical - Sarin Gas\": "+subPerils.contains("Chemical - Sarin Gas")+",\n" +
             //   "            \"Biological - Anthrax\": "+subPerils.contains("Biological - Anthrax")+",\n" +
             //   "            \"Biological - Smallpox\": "+subPerils.contains("Biological - Smallpox")+",\n" +
              //  "            \"Sabotage - Nuclear Plant\": "+subPerils.contains("Sabotage - Nuclear Plant")+"\n" +
                "        }\n" +
                "        }\n"+
                "}";
        return payloadInString;
    }

    public static String getPayloadOfsevereConvectiveStorm(String ModelProfile_Name, Perils perils) {

        List<String> subPerils = perils.getSubPerils();
        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                reportsWindow +
                "        \"startYear\": \""+perils.getStartYear()+"\",\n" +
                "        \"endYear\": \""+perils.getEndYear()+"\",\n" +
                "        \"name\": \""+ModelProfile_Name+"\",\n" +
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
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
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfWindstorm(String ModelProfile_Name, Map<String, String> tc,Perils perils) {

        List<String> subPerils = perils.getSubPerils();
        List<String> policyCoverages = perils.getPolicyCoverages();
        List<String> specialtyModels = perils.getSpecialtyModels();

        String reportsWindow = "";
        if (perils.getApplyContractDatesOn() && perils.getAnalysisType().equalsIgnoreCase("EP")) {
            reportsWindow = "" +
                    "\"reportingWindowStart\": \""+perils.getReportingWindowStart()+"\",\n"+
                    "\"reportingWindowEnd\": \""+perils.getReportingWindowEnd()+"\", \n";
        }

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                    reportsWindow +
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                //  "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"vulnerabilitySetId\": "+perils.getVulnerabilitySetId()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
                "        \"vulnerabilitySetName\": \""+perils.getVulnerabilitySetName()+"\",\n" +
                "        \"name\": \""+ModelProfile_Name+"\",\n" +
                "        \"petDataVersion\": \""+perils.getPetDataVersion()+"\",\n" +
                "        \"numberOfPeriods\": "+perils.getNumberOfPeriods()+",\n" +
                "        \"insuranceType\": \""+perils.getInsuranceType()+"\",\n" +
                "        \"analysisType\": \""+perils.getAnalysisType()+"\",\n" +
                "        \"locationPerRisk\": \""+perils.getLocationPerRisk()+"\",\n" +
                "        \"version\": \""+perils.getVersion()+"\",\n" +
                "        \"endYear\": "+perils.getEndYear()+",\n" +
                "        \"policyCoverages\": {\n" +
                "            \"windstorm\": "+policyCoverages.contains("windstorm")+",\n" +
                "            \"flood\": "+policyCoverages.contains("flood")+"\n" +
                "        },\n" +
                "        \"eventRateSchemeId\": "+perils.getEventRateSchemeId()+",\n" +
                "        \"policyPerRisk\": \""+perils.getPolicyPerRisk()+"\",\n" +
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+"\n" +
                "    },\n" +
                "    \"Windstorm\": {\n" +
                "        \"calculateLossesFrom\": {\n" +
                "            \"coastalFlood\": "+subPerils.contains("coastalFlood")+",\n" +
                "            \"wind\": "+subPerils.contains("wind")+"\n" +
                // "            \"inlandFlood\": "+subPerils.contains("inlandFlood")+"\n" +
                "        },\n" +
                "        \"applyPLA\": "+perils.getApplyPLA()+"\n" +
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


}
