package com.rms.automation.LossValidation.non_ep.stats_losses;

public class NonEPSTATSLossValidation {

    public static Boolean run(String baselinePath, String actualPath, String outputPath) throws Exception {

        String baselinePathSTATS = baselinePath + "/non-ep/RM20_FM_EUWS_FP_03/meout/EventStats";
        String actualPathSTATS = actualPath + "/SCENARIO";

        Boolean isPortfolioPass = STATSPortfolioLossValidation.run(baselinePathSTATS, actualPathSTATS, outputPath);
        Boolean isTreatyPass = StatsTreatyLossValidation.runTreatyResults(baselinePathSTATS, actualPathSTATS, outputPath);

        Boolean isAllPass = (isPortfolioPass && isTreatyPass);

        System.out.println("STATS Comparison completed and results written to Excel.");
        return isAllPass;

    }
}
