package com.rms.automation.LossValidation.non_ep.plt_losses;

import java.io.File;
import com.rms.automation.exportApi.Download_Settings;


public class PLTLossValidationNonEP {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathPLT = baselinePath + "non-EP/SLT";
    //    String actualPathPLT = actualPath + "SAMPLED_PLT";

        String actualPathPLT = "/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/Testing_EDM_E2E_latest_Automation__PORTFOLIO__EUWS_01_Losses/SAMPLED_PLT";

        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathPLT);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathPLT + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathPLT);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathPLT + "' does not exist or is not a directory.");
        }

        Boolean isPortfolioPass = null;
        if (downloadSettings.getIsLossTablesMetric() != null && downloadSettings.getIsLossTablesMetric().equalsIgnoreCase("Portfolio")) {

            isPortfolioPass = PLTPortfolioLossValidationNonEP.run(baselinePathPLT, actualPathPLT, outputPath);
        }
        Boolean isTreatyPass = null;
        if (downloadSettings.getIsLossTablesMetric() != null && downloadSettings.getIsLossTablesMetric().equalsIgnoreCase("Treaty")) {

            isTreatyPass = PLTTreatyLossValidationNonEP.runTreatyResults(baselinePathPLT, actualPathPLT, outputPath);
        }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE) || (isTreatyPass ==Boolean.TRUE);

        System.out.println(" Non-EP PLT Comparison completed and results written to Excel.");
        return isAllPass;

    }
}
