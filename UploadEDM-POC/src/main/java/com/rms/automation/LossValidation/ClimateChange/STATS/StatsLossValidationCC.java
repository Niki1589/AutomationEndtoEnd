package com.rms.automation.LossValidation.ClimateChange.STATS;

import com.rms.automation.LossValidation.ep.stats_losses.STATSPortfolioLossValidation;

public class StatsLossValidationCC {

    public static Boolean run(String baselinePath, String actualPath, String outputPath) throws Exception {

        String baselinePathSTATS = baselinePath + "/STATS";
        String actualPathSTATS = actualPath + "/STATS";

        Boolean isPortfolioPass = StatsPortfolioLossValidationCC.run(baselinePathSTATS, actualPathSTATS, outputPath);
        Boolean isTreatyPass = StatsTreatyLossValidationCC.run(baselinePathSTATS, actualPathSTATS, outputPath);

        Boolean isAllPass = (isPortfolioPass && isTreatyPass);

        System.out.println("STATS Comparison completed and results written to Excel.");
        return isAllPass;

    }

}
