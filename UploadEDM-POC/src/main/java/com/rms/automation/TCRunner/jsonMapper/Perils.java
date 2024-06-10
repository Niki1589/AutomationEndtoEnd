package com.rms.automation.TCRunner.jsonMapper;

import com.rms.automation.dataProviders.LoadData;
import com.rms.automation.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//POJO class Perils
public class Perils {
    String profileName;
    String peril;
    Boolean ignoreContractDates;
    String engine;
    String alternateVulnCode;
    String LabelRegion;
    String numberOfSamples;
    String petName;
    String petDataVersion;
    String numberOfPeriods;
    String insuranceType;
    String analysisType;
    String locationPerRisk;
    String version;
    String endYear;
    String eventRateSchemeId;
    String policyPerRisk;
    String description;
    String modelRegion;
    String subRegions;
    String analysisMode;
    String startYear;
    String gmpeName;
    Boolean applyPLA;
    String gmpeCode;
    String region;
    Boolean excludePostalCodes;
    Boolean fireOnly;
    String perilOverride;
    Boolean dynamicAutomobileModeling;
    Boolean includePluvial;
    Boolean includeBespokeDefence;
    Boolean defenceOn;
    List<String> subPerils;
    List<String> secondaryPerils;
    List<String> policyCoverages;

    public String getEventIds() {
        return eventIds;
    }

    public void setEventIds(String eventIds) {
        this.eventIds = eventIds;
    }
    String eventIds;

    String vendor;
    Boolean run1dOnly;
    List<String> specialtyModels;
    Boolean fire;
    Boolean coverage;
    String property;
    List<String> unknownForPrimaryCharacteristics;
    List<String> scaleExposureValues;
    String portfolioId;
    String mfId;
    String vulnerabilitySetId;

    public String getVulnerabilitySetName() {
        return vulnerabilitySetName;
    }

    public void setVulnerabilitySetName(String vulnerabilitySetName) {
        this.vulnerabilitySetName = vulnerabilitySetName;
    }

    String vulnerabilitySetName;
    String GeocodeVersion;

    String GeoHazVersion;

    List<String> GeoHazLayers;

    String ifModelRun;
    String asOfDateProcess;

    Boolean isApplyContractDatesOn = false;

    public Boolean getApplyContractDatesOn() {
        return isApplyContractDatesOn;
    }

    public void setApplyContractDatesOn(Boolean applyContractDatesOn) {
        isApplyContractDatesOn = applyContractDatesOn;
    }

    public String getReportingWindowStart() {
        return reportingWindowStart;
    }

    public void setReportingWindowStart(String reportingWindowStart) {
        this.reportingWindowStart = reportingWindowStart;
    }

    String reportingWindowStart;

    public String getReportingWindowEnd() {
        return reportingWindowEnd;
    }

    public void setReportingWindowEnd(String reportingWindowEnd) {
        this.reportingWindowEnd = reportingWindowEnd;
    }

    String reportingWindowEnd;

    String currencyCodeProcess;

    String currencySchemeProcess;

    String currencyVintageProcess;

    String outputProfileId;

    String treaties;

    String treatiesName;

    public String getAsOfDateProcess() {
        return asOfDateProcess;
    }

    public void setAsOfDateProcess(String asOfDateProcess) {
        this.asOfDateProcess = asOfDateProcess;
    }

    public String getCurrencySchemeProcess() {
        return currencySchemeProcess;
    }

    public void setCurrencySchemeProcess(String currencySchemeProcess) {
        this.currencySchemeProcess = currencySchemeProcess;
    }

    public String getCurrencyVintageProcess() {
        return currencyVintageProcess;
    }

    public void setCurrencyVintageProcess(String currencyVintageProcess) {
        this.currencyVintageProcess = currencyVintageProcess;
    }

    public String getOutputProfileId() {
        return outputProfileId;
    }

    public void setOutputProfileId(String outputProfileId) {
        this.outputProfileId = outputProfileId;
    }

    public String getCurrencyCodeProcess() {
        return currencyCodeProcess;
    }

    public void setCurrencyCodeProcess(String currencyCodeProcess) {
        this.currencyCodeProcess = currencyCodeProcess;
    }

    public String getIfModelRun() {
        return ifModelRun;
    }

    public void setIfModelRun(String ifModelRun) {
        this.ifModelRun = ifModelRun;
    }

    public List<String> getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(List<String> analysisId) {
        this.analysisId = analysisId;
    }

    List<String> analysisId;

    public String getGeocodeVersion() {
        return GeocodeVersion;
    }

    public void setGeocodeVersion(String geocodeVersion) {
        GeocodeVersion = geocodeVersion;
    }

    public String getGeoHazVersion() {
        return GeoHazVersion;
    }

    public void setGeoHazVersion(String geoHazVersion) {
        GeoHazVersion = geoHazVersion;
    }

    public List<String> getGeoHazLayers() {
        return GeoHazLayers;
    }

    public void setGeoHazLayers(List<String> geoHazLayers) {
        GeoHazLayers = geoHazLayers;
    }

    public String getMfId() {
        return mfId;
    }

    public void setMfId(String mfId) {
        this.mfId = mfId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPeril() {
        return peril;
    }

    public void setPeril(String peril) {
        this.peril = peril;
    }

    public Boolean getIgnoreContractDates() {
        return ignoreContractDates;
    }

    public void setIgnoreContractDates(Boolean ignoreContractDates) {
        this.ignoreContractDates = ignoreContractDates;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getAlternateVulnCode() {
        return alternateVulnCode;
    }

    public void setAlternateVulnCode(String alternateVulnCode) {
        this.alternateVulnCode = alternateVulnCode;
    }

    public String getLabelRegion() {
        return LabelRegion;
    }

    public void setLabelRegion(String labelRegion) {
        LabelRegion = labelRegion;
    }

    public String getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(String numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetDataVersion() {
        return petDataVersion;
    }

    public void setPetDataVersion(String petDataVersion) {
        this.petDataVersion = petDataVersion;
    }

    public String getNumberOfPeriods() {
        return numberOfPeriods;
    }

    public void setNumberOfPeriods(String numberOfPeriods) {
        if (numberOfPeriods.isEmpty()) {
            this.numberOfPeriods = null;
        } else {
            this.numberOfPeriods = numberOfPeriods;
        }
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getLocationPerRisk() {
        return locationPerRisk;
    }

    public void setLocationPerRisk(String locationPerRisk) {
        this.locationPerRisk = locationPerRisk;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        if (endYear.isEmpty()) {
            this.endYear = null;
        } else {
            this.endYear = endYear;
        }
    }

    public String getEventRateSchemeId() {
        return eventRateSchemeId;
    }

    public void setEventRateSchemeId(String eventRateSchemeId) {

        if(eventRateSchemeId.isEmpty())
        {
            this.eventRateSchemeId=null;
        }
        else
        {
            this.eventRateSchemeId = eventRateSchemeId;
        }

    }

    public String getPolicyPerRisk() {
        return policyPerRisk;
    }

    public void setPolicyPerRisk(String policyPerRisk) {
        this.policyPerRisk = policyPerRisk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelRegion() {
        return modelRegion;
    }

    public void setModelRegion(String modelRegion) {
        this.modelRegion = modelRegion;
    }

    public String getSubRegions() {
        return subRegions;
    }

    public void setSubRegions(String subRegions) {
        this.subRegions = subRegions;
    }

    public String getAnalysisMode() {
        return analysisMode;
    }

    public void setAnalysisMode(String analysisMode) {
        this.analysisMode = analysisMode;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {

        if(startYear.isEmpty())
        {
            this.startYear=null;
        }
        else
        {
            this.startYear = startYear;
        }

    }

    public String getGmpeName() {
        return gmpeName;
    }

    public void setGmpeName(String gmpeName) {
        this.gmpeName = gmpeName;
    }

    public String getGmpeCode() {
        return gmpeCode;
    }

    public void setGmpeCode(String gmpeCode) {
        this.gmpeCode = gmpeCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getExcludePostalCodes() {
        return excludePostalCodes;
    }

    public void setExcludePostalCodes(Boolean excludePostalCodes) {
        this.excludePostalCodes = excludePostalCodes;
    }

    public Boolean getFireOnly() {
        return fireOnly;
    }

    public void setFireOnly(Boolean fireOnly) {
        this.fireOnly = fireOnly;
    }

    public String getPerilOverride() {
        return perilOverride;
    }

    public void setPerilOverride(String perilOverride) {
        this.perilOverride = perilOverride;
    }

    public Boolean getDynamicAutomobileModeling() {
        return dynamicAutomobileModeling;
    }

    public void setDynamicAutomobileModeling(Boolean dynamicAutomobileModeling) {
        this.dynamicAutomobileModeling = dynamicAutomobileModeling;
    }


    public List<String> getSubPerils() {
        return subPerils;
    }

    public void setSubPerils(List<String> subPerils) {
        this.subPerils = subPerils;
    }

    public List<String> getSecondaryPerils() {
        return secondaryPerils;
    }

    public void setSecondaryPerils(List<String> secondaryPerils) {
        this.secondaryPerils = secondaryPerils;
    }

    public List<String> getPolicyCoverages() {
        return policyCoverages;
    }

    public void setPolicyCoverages(List<String> policyCoverages) {
        this.policyCoverages = policyCoverages;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Boolean getRun1dOnly() {
        return run1dOnly;
    }

    public void setRun1dOnly(Boolean run1dOnly) {
        this.run1dOnly = run1dOnly;
    }

    public List<String> getSpecialtyModels() {
        return specialtyModels;
    }

    public void setSpecialtyModels(List<String> specialtyModels) {
        this.specialtyModels = specialtyModels;
    }

    public Boolean getFire() {
        return fire;
    }

    public void setFire(Boolean fire) {
        this.fire = fire;
    }

    public Boolean getCoverage() {
        return coverage;
    }

    public void setCoverage(Boolean coverage) {
        this.coverage = coverage;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<String> getUnknownForPrimaryCharacteristics() {
        return unknownForPrimaryCharacteristics;
    }

    public void setUnknownForPrimaryCharacteristics(List<String> unknownForPrimaryCharacteristics) {
        this.unknownForPrimaryCharacteristics = unknownForPrimaryCharacteristics;
    }

    public String getVulnerabilitySetId() {
        return vulnerabilitySetId;
    }

    public void setVulnerabilitySetId(String vulnerabilitySetId) {
        this.vulnerabilitySetId = vulnerabilitySetId;
    }

    public List<String> getScaleExposureValues() {
        return scaleExposureValues;
    }

    public void setScaleExposureValues(List<String> scaleExposureValues) {
        this.scaleExposureValues = scaleExposureValues;
    }

    public Boolean getApplyPLA() {
        return applyPLA;
    }

    public void setApplyPLA(Boolean applyPLA) {
        this.applyPLA = applyPLA;
    }

    public Boolean getIncludePluvial() {
        return includePluvial;
    }

    public void setIncludePluvial(Boolean includePluvial) {
        this.includePluvial = includePluvial;
    }

    public Boolean getIncludeBespokeDefence() {
        return includeBespokeDefence;
    }

    public void setIncludeBespokeDefence(Boolean includeBespokeDefence) {
        this.includeBespokeDefence = includeBespokeDefence;
    }
    public Boolean getDefenceOn() {
        return defenceOn;
    }

    public void setDefenceOn(Boolean defenceOn) {
        this.defenceOn = defenceOn;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getTreaties() {
        return treaties;
    }

    public void setTreaties(String treaties) {
        this.treaties = treaties;
    }

    public String getTreatiesName() {
        return treatiesName;
    }

    public void setTreatiesName(String treatiesName) {
        this.treatiesName = treatiesName;
    }

    public static Perils extractPerilFromTC(Map<String, String> tc) throws IOException {
        Perils perils = new Perils();
        perils.setPeril(tc.get("MPF_PERIL"));
        perils.setIgnoreContractDates(tc.get("MPF_IGNORE_CONTRACT_DATES").equalsIgnoreCase("YES"));
        perils.setEngine(tc.get("MPF_ENGINE"));
        perils.setAlternateVulnCode(tc.get("MPF_ALTERNATE_VULN_CODE"));
        perils.setLabelRegion(tc.get("MPF_LABEL_REGION"));
        perils.setNumberOfSamples(tc.get("MPF_NUMBER_OF_SAMPLES"));
        perils.setPetName(tc.get("MPF_PET_NAME"));
        perils.setPetDataVersion(tc.get("MPF_PET_DATA_VERSION"));
        perils.setNumberOfPeriods(tc.get("MPF_NUMBER_OF_PERIODS"));
        perils.setInsuranceType(tc.get("MPF_INSURANCE_TYPE"));
        perils.setAnalysisType(tc.get("MPF_ANALYSIS_TYPE"));
        perils.setLocationPerRisk(tc.get("MPF_LOCATION_PER_RISK"));
        perils.setVersion(tc.get("MPF_VERSION"));
        perils.setEndYear(tc.get("MPF_END_YEAR"));
        perils.setEventRateSchemeId(tc.get("MPF_EVENT_RATE_SCHEME_ID"));
        perils.setPolicyPerRisk(tc.get("MPF_POLICY_PER_RISK"));
        perils.setDescription(tc.get("MPF_DESCRIPTION"));
        perils.setModelRegion(tc.get("MPF_MODEL_REGION"));
        perils.setSubRegions(tc.get("MPF_SUB_REGIONS"));
        perils.setAnalysisMode(tc.get("MPF_ANALYSIS_MODE"));
        perils.setStartYear(tc.get("MPF_START_YEAR"));
        perils.setGmpeName(tc.get("MPF_GMPE_NAME"));
        perils.setApplyPLA(tc.get("MPF_APPLY_PLA").equalsIgnoreCase("YES"));
        perils.setGmpeCode(tc.get("MPF_GMPE_CODE"));
        perils.setRegion(tc.get("MPF_REGION"));
     //   perils.setExcludePostalCodes(tc.get("excludePostalCodes").equalsIgnoreCase("YES"));
     //   perils.setFireOnly(tc.get("fireOnly").equalsIgnoreCase("YES"));
     //   perils.setPerilOverride(tc.get("perilOverride"));
    //    perils.setDynamicAutomobileModeling(tc.get("dynamicAutomobileModeling").equalsIgnoreCase("YES"));
    //    perils.setIncludePluvial(tc.get("includePluvial").equalsIgnoreCase("YES"));
    //    perils.setIncludeBespokeDefence(tc.get("includeBespokeDefence").equalsIgnoreCase("YES"));
    //    perils.setDefenceOn(tc.get("defenceOn").equalsIgnoreCase("YES"));
        perils.setSubPerils(List.of(tc.get("MPF_SUB_PERILS").split(",")));
        perils.setSecondaryPerils(List.of(tc.get("MPF_SECONDARY_PERILS").split(",")));
        perils.setPolicyCoverages(List.of(tc.get("MPF_POLICY_COVERAGES").split(",")));
        perils.setVendor(tc.get("MPF_VENDOR"));
     //   perils.setRun1dOnly(tc.get("run1dOnly").equalsIgnoreCase(("YES")));
        perils.setSpecialtyModels(List.of(tc.get("MPF_SPECIALTY_MODELS").split(",")));
       // perils.setFire(tc.get("fire").equalsIgnoreCase("YES"));
       // perils.setCoverage(tc.get("coverage").equalsIgnoreCase("YES"));
        perils.setProperty(tc.get("property"));
       // perils.setUnknownForPrimaryCharacteristics(List.of(tc.get("unknownForPrimaryCharacteristics").split(",")));
        perils.setVulnerabilitySetId(tc.get("MPF_VULNERABILITY_SET_ID"));
        perils.setVulnerabilitySetName(tc.get("MPF_VULNERABILITY_SET_NAME"));
 //       perils.setScaleExposureValues(List.of(tc.get("scaleExposureValues").split(",")));
        perils.setMfId(tc.get("MPF_MFID"));
        perils.setPortfolioId(tc.get("EXP_EXISTING_PORTFOLIO_ID"));

        perils.setGeocodeVersion(tc.get("GEO_GEOCODE_VERSION"));
        perils.setGeoHazVersion(tc.get("GEO_GEOHAZ_VERSION"));
        perils.setIfModelRun(tc.get("MRN_IF_MODEL_RUN"));

        String layer = tc.get("GEO_GEOHAZ_LAYERS");
        if ( layer != null && layer != "" ) {
            String[] layersList = layer.split(",");
            perils.setGeoHazLayers(List.of(layersList));
        } else {
            perils.setGeoHazLayers(List.of());
        }

        String eventIds=tc.get("MPF_EVENT_IDS");
        if(eventIds!=null && eventIds !="")
        {
            perils.setEventIds( eventIds );
        }
        else
        {
            perils.setEventIds("");
        }

        if (tc.get("MRN_AS_OF_DATE_PROCESS") != null && tc.get("MRN_AS_OF_DATE_PROCESS").length() > 0) {
            perils.setAsOfDateProcess(tc.get("MRN_AS_OF_DATE_PROCESS"));
        } else {
            perils.setAsOfDateProcess("");
        }

        if (tc.get("MPF_REPORTING_WINDOW_START_YEAR") != null && tc.get("MPF_REPORTING_WINDOW_START_YEAR").length() > 0) {
            perils.setReportingWindowStart(tc.get("MPF_REPORTING_WINDOW_START_YEAR"));
        } else {
            perils.setReportingWindowStart("");
        }

        if (tc.get("MPF_REPORTING_WINDOW_END_YEAR") != null && tc.get("MPF_REPORTING_WINDOW_END_YEAR").length() > 0) {
            perils.setReportingWindowEnd(tc.get("MPF_REPORTING_WINDOW_END_YEAR"));
        } else {
            perils.setReportingWindowEnd("");
        }



        if (tc.get("MRN_CURRENCY_CODE") != null && tc.get("MRN_CURRENCY_CODE").length() > 0) {
            perils.setCurrencyCodeProcess(tc.get("MRN_CURRENCY_CODE"));
        } else {
            perils.setCurrencyCodeProcess("");
        }

        if (tc.get("MRN_CURRENCY_SCHEME") != null && tc.get("MRN_CURRENCY_SCHEME").length() > 0) {
            perils.setCurrencySchemeProcess(tc.get("MRN_CURRENCY_SCHEME"));
        } else {
            perils.setCurrencySchemeProcess("");
        }

        if (tc.get("MRN_CURRENCY_VINTAGE") != null && tc.get("MRN_CURRENCY_VINTAGE").length() > 0) {
            perils.setCurrencyVintageProcess(tc.get("MRN_CURRENCY_VINTAGE"));
        } else {
            perils.setCurrencyVintageProcess("");
        }

        if (tc.get("MRN_OUTPUT_PROFILE_ID") != null && tc.get("MRN_OUTPUT_PROFILE_ID").length() > 0) {
            perils.setOutputProfileId(tc.get("MRN_OUTPUT_PROFILE_ID"));
        } else {
            perils.setOutputProfileId("");
        }

        if (tc.get("MRN_TREATIES") != null && tc.get("MRN_TREATIES").length() > 0) {
            perils.setTreaties(tc.get("MRN_TREATIES"));
        } else {
            perils.setTreaties("");
        }

        if (tc.get("MRN_TREATIES_NAME") != null && tc.get("MRN_TREATIES_NAME").length() > 0) {
            perils.setTreatiesName(tc.get("MRN_TREATIES_NAME"));
        } else {
            perils.setTreatiesName("");
        }

        perils.setApplyContractDatesOn(Utils.isTrue(tc.get("MPF_IS_APPLY_CONTRACT_DATES_ON")));
        if (perils.getApplyContractDatesOn()) {
            String date = tc.get("MPF_REPORTING_WINDOW_START_YEAR");
            if (date != null && date.length() > 0) {
                Map<String, String> dates = Utils.getEndDateAndFormat(date);
                perils.setReportingWindowStart(dates.get("startDate"));
                perils.setReportingWindowEnd(dates.get("endDate"));

                String reportingWindowEndDate=dates.get("endDate");

                if(!reportingWindowEndDate.isEmpty())
                {
                    LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "MPF_REPORTING_WINDOW_END_YEAR", reportingWindowEndDate);
                }
            }
        }

        return perils;
    }

}
