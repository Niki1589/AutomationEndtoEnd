package com.rms.automation.LossValidation.ClimateChange.EP;

import com.rms.automation.exportApi.Download_Settings;

import java.io.File;

public class EPLossValidationCC {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathEPCC = baselinePath + "/EP";
      String actualPathEPCC = actualPath + "/EP";
        //String actualPathEPCC = "/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25751853_rename_analysis_WS_CC_RCP4_5_2020_Losses" + "/EP";

        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathEPCC);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathEPCC + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathEPCC);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathEPCC + "' does not exist or is not a directory.");
        }

        Boolean isPortfolioPass = null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && downloadSettings.getOutputLevels_EPMetric().toUpperCase().contains("PORTFOLIO"))
        {
            isPortfolioPass = EPPortfolioLossValidationCC.runPortfolioLossValidationEPCC(baselinePathEPCC, actualPathEPCC, outputPath, downloadSettings);
        }
        Boolean isTreatyPass =null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && downloadSettings.getOutputLevels_EPMetric().toUpperCase().contains("TREATY")) {

                isTreatyPass = EPTreatyLossValidationCC.runTreatyLossValidationEPCC(baselinePathEPCC, actualPathEPCC, outputPath);
            }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE )|| (isTreatyPass==Boolean.TRUE);


        System.out.println("EP Comparison for CC completed and results written to Excel.");
        return isAllPass;

    }

}
