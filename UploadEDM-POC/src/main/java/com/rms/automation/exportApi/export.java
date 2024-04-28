package com.rms.automation.exportApi;

import java.util.Map;

public class export {

    public static void exportType(Map<String, String> tc, String analysisId) throws Exception {
        String exportAs = tc.get("exportAs");
        if (exportAs.equalsIgnoreCase("RDM")) {
            RdmExportTests.rdmExport(tc, analysisId);
        }
        else if(exportAs.equalsIgnoreCase("file")) {
            FileExportTests.fileExport(tc, analysisId);
        }
    }
}

