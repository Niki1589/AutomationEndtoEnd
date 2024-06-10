package com.rms.automation.edm;

import com.rms.automation.edm.enums.TCFileType;

public class Config {

    private String username;
    private String password;
    private String tenant;
    private String apiendpointsFile;
    private String tcFile;
    private TCFileType tcFileType;
    private String createEdmFile;
    private String mriImportFile;
    private String batchCSVFile;
    private String perilType;
    private String profileTemplateJsonFile;
    private String singleCSVFile;

    public String getSingleExcelFile() {
        return singleExcelFile;
    }

    public void setSingleExcelFile(String singleExcelFile) {
        this.singleExcelFile = singleExcelFile;
    }

    private String singleExcelFile;

    private  String pateFile;

    public String getPateFile() {
        return pateFile;
    }

    public void setPateFile(String pateFile) {
        this.pateFile = pateFile;
    }
    public String getBatchCSVFile() {
        return batchCSVFile;
    }

    public void setBatchCSVFile(String batchCSVFile) {
        this.batchCSVFile = batchCSVFile;
    }

    public String getMriImportFile() {
        return mriImportFile;
    }

    public void setMriImportFile(String mriImportFile) {
        this.mriImportFile = mriImportFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getApiendpointsFile() {
        return apiendpointsFile;
    }

    public void setApiendpointsFile(String apiendpointsFile) {
        this.apiendpointsFile = apiendpointsFile;
    }

    public String getTcFile() {
        return tcFile;
    }

    public void setTcFile(String tcFile) {
        this.tcFile = tcFile;
    }

    public TCFileType getTcFileType() {
        return tcFileType;
    }

    public void setTcFileType(TCFileType tcFileType) {
        this.tcFileType = tcFileType;
    }

    public String getCreateEdmFile() {
        return createEdmFile;
    }

    public void setCreateEdmFile(String createEdmFile) {
        this.createEdmFile = createEdmFile;
    }

    public String getPerilType() {
        return perilType;
    }

    public void setPerilType(String perilType) {
        this.perilType = perilType;
    }

    public String getProfileTemplateJsonFile() {
        return profileTemplateJsonFile;
    }

    public void setProfileTemplateJsonFile(String profileTemplateJsonFile) {
        this.profileTemplateJsonFile = profileTemplateJsonFile;
    }

    public String getSingleCSVFile() {
        return singleCSVFile;
    }

    public void setSingleCSVFile(String singleCSVFile) {
        this.singleCSVFile = singleCSVFile;
    }

    @Override
    public String toString() {
        return "Config{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tenant='" + tenant + '\'' +
                ", apiendpointsFile='" + apiendpointsFile + '\'' +
                '}';
    }
}
