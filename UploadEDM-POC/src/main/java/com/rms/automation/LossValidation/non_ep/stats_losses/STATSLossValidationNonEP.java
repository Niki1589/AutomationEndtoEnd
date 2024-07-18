package com.rms.automation.LossValidation.non_ep.stats_losses;
import com.rms.automation.exportApi.Download_Settings;

import java.io.File;
public class STATSLossValidationNonEP {

    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSetting) throws Exception {

        String baselinePathSTATSNonEP = baselinePath + "/non-EP/EventStats";
        String actualPathSTATSNonEP = actualPath + "/SCENARIO";


        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathSTATSNonEP);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathSTATSNonEP + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathSTATSNonEP);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathSTATSNonEP + "' does not exist or is not a directory.");
        }


        Boolean isPortfolioPass = null;

        if (downloadSetting.getIsStatsMetric() != null && downloadSetting.getIsStatsMetric().equalsIgnoreCase("Portfolio")) {

            isPortfolioPass = STATSPortfolioLossValidation.run(baselinePathSTATSNonEP, actualPathSTATSNonEP, outputPath);

        }
        Boolean isTreatyPass = null;

        if(downloadSetting.getIsStatsMetric() != null && downloadSetting.getIsStatsMetric().equalsIgnoreCase("Treaty")) {

            isTreatyPass = StatsTreatyLossValidation.runTreatyResults(baselinePathSTATSNonEP, actualPathSTATSNonEP, outputPath);
        }

        Boolean isAllPass = (isPortfolioPass==Boolean.TRUE) || (isTreatyPass==Boolean.TRUE);

        System.out.println("Non-EP STATS Comparison completed and results written to Excel.");
        return isAllPass;

    }
}
