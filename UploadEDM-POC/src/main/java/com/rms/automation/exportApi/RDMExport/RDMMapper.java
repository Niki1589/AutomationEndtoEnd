package com.rms.automation.exportApi.RDMExport;

import com.rms.automation.utils.Utils;

import java.util.List;
import java.util.Map;

public class RDMMapper {

    public static RDMModel map(Map<String, String> data, String analysisId) {

        RDMModel obj = new RDMModel();
        obj.setANALYSIS_ID((new int[]{(Integer.parseInt(analysisId))}));
        obj.setREX_RDM_NAME(data.get("REX_RDM_NAME"));
        obj.setEXPORT_FORMAT_RDM(data.get("REX_EXPORT_HD_LOSSES_AS"));
        obj.setSQL_VERSION(data.get("SQLVERSION"));
        obj.setEXPORT_FORMAT_RDM(data.get("EXPORT_FORMAT_RDM"));
        obj.setREX_RDM_LOCATION(REX_RDM_LOCATION_ENUM.getByValue(data.get("REX_RDM_LOCATION")));
        for (String v : List.of(data.get("REX_EXPORT_HD_LOSSES_AS").split(","))) obj.addREX_EXPORT_HD_LOSSES_AS(v);

        obj.setIS_CREATE_NEW_DATABRIDGE( (!data.get("REX_DATA_BRIDGE_TYPE").isEmpty() && data.get("REX_DATA_BRIDGE_TYPE").equalsIgnoreCase("createnew")) );

        obj.setDATABRIDGE_SERVER( data.get("DATABRIDGESERVER") );

        return obj;

    }

}
