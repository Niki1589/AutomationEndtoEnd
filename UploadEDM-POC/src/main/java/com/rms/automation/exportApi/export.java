package com.rms.automation.exportApi;

import java.util.Map;

public class export {

    public static void exportType(Map<String, String> tc, String analysisId) throws Exception {

        if (tc.get("if_rdm_export").equalsIgnoreCase("YES")) {
            RdmExportTests.rdmExport(tc, analysisId);
        }
        if (tc.get("if_file_export").equalsIgnoreCase("YES")) {
            FileExportTests.fileExport(tc, analysisId);
        }
    }
}

