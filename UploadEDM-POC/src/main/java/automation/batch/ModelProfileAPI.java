package automation.batch;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.merge.jsonMapper.Perils;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelProfileAPI {

    public static String getModelProfileApi(Perils perils, Map<String, String> tc, String token) throws Exception {
        try {
            return ApiUtil.createModelProfile(token, tc, perils);
        } catch (Exception e) {
            System.out.println("Error in createModelProfile : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static String getPayloadCreateModelProfileApi(String ModelProfile_Name, Map<String, String> tc, Perils perils) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Gson gson = new Gson();
        // String payloadInString = getPayloadOfEarthquake(NAEQ_ModelProfile_Name, tc);
        String payloadInString="";

        if (perils.getPeril().contains("Earthquake")) {
            payloadInString = getPayloadOfEarthquake(ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Flood"))  {
            payloadInString = getPayloadOfFlood(ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Windstorm"))  {
            payloadInString = getPayloadOfWindstorm(ModelProfile_Name, tc,perils);
        } else if (perils.getPeril().contains("Severe Convective Storm"))  {
            payloadInString = getPayloadOfsevereConvectiveStorm(ModelProfile_Name, perils);
        } else if (perils.getPeril().contains("Terrorism"))  {
            payloadInString = getPayloadOfTerrorism(ModelProfile_Name, tc,perils);
        } else if (perils.getPeril().contains("Wildfire"))  {
            payloadInString = getPayloadOfWildfire(ModelProfile_Name,perils);
        }
        System.out.println("Payload for -------- "+perils.getPeril());
        System.out.println(payloadInString);

//        CreateModelProfileApi createModelProfileApi = new CreateModelProfileApi();
//        try {
//            // Converting json to model
//            createModelProfileApi = gson.fromJson(payloadInString, CreateModelProfileApi.class);
//        } catch (Exception ex) {
//            System.out.print("Error while parsing class from json = "+ex.getMessage());
//
//        }
//
//        // Converting model to HashMap
//        Object mapped = toMap(createModelProfileApi);
//        return mapped;
        return payloadInString;
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
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfWildfire(String NAEQ_ModelProfile_Name, Perils perils) {

        List<String> subPerils = perils.getSubPerils();

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
                "        \"description\": \""+perils.getDescription()+"\",\n" +
                "        \"modelRegion\": \""+perils.getModelRegion()+"\",\n" +
                "        \"subRegions\": \""+perils.getSubRegions()+"\",\n" +
                "        \"analysisMode\": \""+perils.getAnalysisMode()+"\",\n" +
                "        \"startYear\": "+perils.getStartYear()+"\n" +
                "    }\n" +
                "}";
        return payloadInString;
    }

    public static String getPayloadOfTerrorism(String NAEQ_ModelProfile_Name, Map<String, String> tc,Perils perils) {
        List<String> subPerils = perils.getSubPerils();
        //List<String> specialtyModels = perils.getSpecialtyModels();
        //  List<String> scaleExposureValues =perils.getScaleExposureValues();
        //  List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();
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
                "        \"tagIds\": [],\n" +
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
                "            \"Radiological - Dirty Bomb\": "+subPerils.contains("Radiological - Dirty Bomb")+",\n" +
                "            \"Sabotage - Hazmat Transportation\": "+subPerils.contains("Sabotage - Hazmat Transportation")+",\n" +
                "            \"Sabotage - Industrial Plant (vapor release)\": "+subPerils.contains("Sabotage - Industrial Plant (vapor release)")+",\n" +
                "            \"Nuclear Bomb\": "+subPerils.contains("Nuclear Bomb")+",\n" +
                "            \"Chemical - Sarin Gas\": "+subPerils.contains("Chemical - Sarin Gas")+",\n" +
                "            \"Biological - Anthrax\": "+subPerils.contains("Biological - Anthrax")+",\n" +
                "            \"Biological - Smallpox\": "+subPerils.contains("Biological - Smallpox")+",\n" +
                "            \"Sabotage - Nuclear Plant\": "+subPerils.contains("Sabotage - Nuclear Plant")+"\n" +
                "        }\n" +
                "        }\n"+
                "}";
        return payloadInString;
    }

    public static String getPayloadOfsevereConvectiveStorm(String ModelProfile_Name, Perils perils) {
        List<String> subPerils = perils.getSubPerils();
//        List<String> specialtyModels = perils.getSpecialtyModels();
//        List<String> scaleExposureValues = perils.getScaleExposureValues();
//        List<String> unknownForPrimaryCharacteristics = perils.getUnknownForPrimaryCharacteristics();
        String payloadInString = "{\n" +
                "    \"General\": {\n" +
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

        String payloadInString = "{\n" +
                "    \"General\": {\n" +
                "        \"peril\": \""+perils.getPeril()+"\",\n" +
                "        \"ignoreContractDates\": "+perils.getIgnoreContractDates()+",\n" +
                "        \"engine\": \""+perils.getEngine()+"\",\n" +
                //  "        \"alternateVulnCode\": "+perils.getAlternateVulnCode()+",\n" +
                "        \"LabelRegion\": \""+perils.getLabelRegion()+"\",\n" +
                "        \"numberOfSamples\": "+perils.getNumberOfSamples()+",\n" +
                "        \"vulnerabilitySetId\": "+perils.getVulnerabilitySetId()+",\n" +
                "        \"petName\": \""+perils.getPetName()+"\",\n" +
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
                "    }\n" +
                "}";
        return payloadInString;
    }


}
