package com.rms.automation.exportApi;

import java.util.Map;

public class export {

    public static void exportType(Map<String, String> tc, String analysisId) throws Exception {
        String exportAs = tc.get("exportAs");
        if (exportAs.equals("RDM")) {
            RdmExportTests.rdmExport(tc, analysisId);
        }
        else if(exportAs.equals("file")) {
            FileExportTests.fileExport(tc, analysisId);
        }
    }
}

