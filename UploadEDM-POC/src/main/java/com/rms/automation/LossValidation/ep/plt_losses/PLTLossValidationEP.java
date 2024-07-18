package com.rms.automation.LossValidation.ep.plt_losses;

import com.rms.automation.exportApi.Download_Settings;

import java.io.File;

public class PLTLossValidationEP {

    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathPLT = baselinePath + "/EP/PLT";
        String actualPathPLT = actualPath + "/PLT";

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
        if (downloadSettings.getOutputLevels_LossTablesMetric() !=null && downloadSettings.getOutputLevels_LossTablesMetric().equalsIgnoreCase("Portfolio")) {

            isPortfolioPass = PLTPortfolioLossValidationEP.run(baselinePathPLT, actualPathPLT, outputPath);

        }

//        Boolean isTreatyPass = null;
//        if (downloadSettings.getOutputLevels_EPMetric().equalsIgnoreCase("Treaty")) {
//
//            isTreatyPass = StatsTreatyLossValidation.run(baselinePathPLT, actualPathPLT, outputPath);
//        }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE);

        System.out.println("PLT Comparison for Analysis Type EP completed and results written to Excel.");
        return isAllPass;

    }

}
