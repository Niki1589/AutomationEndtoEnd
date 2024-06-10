package com.rms.automation.exportApi.RDMExport;

import java.util.ArrayList;
import java.util.List;

public class RDMModel {

    private int[] ANALYSIS_ID;
    private String REX_RDM_NAME;
    private List<REX_EXPORT_HD_LOSSES_AS_ENUM> REX_EXPORT_HD_LOSSES_AS = new ArrayList<>();
    private String SQL_VERSION;
    private String EXPORT_FORMAT_RDM;
    private REX_RDM_LOCATION_ENUM REX_RDM_LOCATION;

    private Boolean IS_CREATE_NEW_DATABRIDGE; // REX_DATA_BRIDGE_TYPE
    private String DATABRIDGE_SERVER; //DATABRIDGESERVER

    public int[] getANALYSIS_ID() {
        return ANALYSIS_ID;
    }

    public void setANALYSIS_ID(int[] ANALYSIS_ID) {
        this.ANALYSIS_ID = ANALYSIS_ID;
    }

    public String getREX_RDM_NAME() {
        return REX_RDM_NAME;
    }

    public void setREX_RDM_NAME(String REX_RDM_NAME) {
        this.REX_RDM_NAME = REX_RDM_NAME;
    }

    public List<REX_EXPORT_HD_LOSSES_AS_ENUM> getREX_EXPORT_HD_LOSSES_AS() {
        return REX_EXPORT_HD_LOSSES_AS;
    }

    public void addREX_EXPORT_HD_LOSSES_AS(String v) {
        this.REX_EXPORT_HD_LOSSES_AS.add(REX_EXPORT_HD_LOSSES_AS_ENUM.getByValue(v));
    }

    public String getSQL_VERSION() {
        return SQL_VERSION;
    }

    public void setSQL_VERSION(String SQL_VERSION) {
        this.SQL_VERSION = SQL_VERSION;
    }

    public String getEXPORT_FORMAT_RDM() {
        return EXPORT_FORMAT_RDM;
    }

    public void setEXPORT_FORMAT_RDM(String EXPORT_FORMAT_RDM) {
        this.EXPORT_FORMAT_RDM = EXPORT_FORMAT_RDM;
    }

    public REX_RDM_LOCATION_ENUM getREX_RDM_LOCATION() {
        return REX_RDM_LOCATION;
    }

    public void setREX_RDM_LOCATION(REX_RDM_LOCATION_ENUM REX_RDM_LOCATION) {
        this.REX_RDM_LOCATION = REX_RDM_LOCATION;
    }

    public Boolean getIS_CREATE_NEW_DATABRIDGE() {
        return IS_CREATE_NEW_DATABRIDGE;
    }

    public void setIS_CREATE_NEW_DATABRIDGE(Boolean IS_CREATE_NEW_DATABRIDGE) {
        this.IS_CREATE_NEW_DATABRIDGE = IS_CREATE_NEW_DATABRIDGE;
    }

    public String getDATABRIDGE_SERVER() {
        return DATABRIDGE_SERVER;
    }

    public void setDATABRIDGE_SERVER(String DATABRIDGE_SERVER) {
        this.DATABRIDGE_SERVER = DATABRIDGE_SERVER;
    }
}
