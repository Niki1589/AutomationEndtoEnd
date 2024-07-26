package com.rms.automation.LossValidation.ep.stats_losses;

import com.rms.automation.exportApi.Download_Settings;

import java.io.File;

public class STATSLossValidation {

    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathSTATS = baselinePath + "/EP/STATS";
        String actualPathSTATS = actualPath + "/STATS";

        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathSTATS);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathSTATS + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathSTATS);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathSTATS + "' does not exist or is not a directory.");
        }

        Boolean isPortfolioPass = null;
        if (downloadSettings.getOutputLevels_StatesMetric() != null && downloadSettings.getOutputLevels_StatesMetric().toUpperCase().contains("PORTFOLIO")) {

            isPortfolioPass = STATSPortfolioLossValidationEP.run(baselinePathSTATS, actualPathSTATS, outputPath);

        }
        Boolean isTreatyPass = null;

        if (downloadSettings.getOutputLevels_StatesMetric()!= null && downloadSettings.getOutputLevels_EPMetric().toUpperCase().contains("TREATY")) {

            isTreatyPass = StatsTreatyLossValidationEP.run(baselinePathSTATS, actualPathSTATS, outputPath);
        }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE) || (isTreatyPass==Boolean.TRUE);


            System.out.println("STATS Comparison for Analysis type EP completed and results written to Excel.");

        return isAllPass;

    }

}
