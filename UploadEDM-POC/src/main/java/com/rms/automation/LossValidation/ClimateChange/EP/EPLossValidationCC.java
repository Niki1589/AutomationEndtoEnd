package com.rms.automation.LossValidation.ClimateChange.EP;

import com.rms.automation.exportApi.Download_Settings;

import java.io.File;

public class EPLossValidationCC {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathEP = baselinePath + "/EP";
        String actualPathEP = actualPath + "/EP";

        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathEP);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathEP + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathEP);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathEP + "' does not exist or is not a directory.");
        }

        Boolean isPortfolioPass = null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && downloadSettings.getOutputLevels_EPMetric().equalsIgnoreCase("Portfolio"))
        {
            isPortfolioPass = EPPortfolioLossValidationCC.runPortfolioLossValidationEP(baselinePathEP, actualPathEP, outputPath, downloadSettings);
        }
        Boolean isTreatyPass =null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && downloadSettings.getOutputLevels_EPMetric().equalsIgnoreCase("Treaty")) {

                isTreatyPass = EPTreatyLossValidationCC.runTreatyLossValidationEPCC(baselinePathEP, actualPathEP, outputPath);
            }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE )|| (isTreatyPass==Boolean.TRUE);


        System.out.println("EP Comparison for CC completed and results written to Excel.");
        return isAllPass;

    }

}
