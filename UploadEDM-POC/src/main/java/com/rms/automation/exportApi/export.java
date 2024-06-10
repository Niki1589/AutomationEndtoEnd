package com.rms.automation.exportApi;

import com.rms.automation.exportApi.RDMExport.RdmExportTests;

import java.util.Map;

public class export {

    public static void exportType(Map<String, String> tc, String analysisId) throws Exception {

        if (tc.get("REX_IF_RDM_EXPORT").equalsIgnoreCase("YES")) {
            RdmExportTests.rdmExport(tc, analysisId);
        }
        if (tc.get("REX_IF_FILE_EXPORT").equalsIgnoreCase("YES")) {
            FileExportTests.fileExport(tc, analysisId);
        }
    }
}

