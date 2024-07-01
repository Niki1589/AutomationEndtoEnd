package com.rms.automation.LossValidation.ep.stats_losses;

public class STATSLossValidation {

    public static Boolean run(String baselinePath, String actualPath, String outputPath) throws Exception {

        String baselinePathSTATS = baselinePath + "/STATS";
        String actualPathSTATS = actualPath + "/STATS";

        Boolean isPortfolioPass = STATSPortfolioLossValidation.run(baselinePathSTATS, actualPathSTATS, outputPath);
        Boolean isTreatyPass = StatsTreatyLossValidation.run(baselinePathSTATS, actualPathSTATS, outputPath);

        Boolean isAllPass = (isPortfolioPass && isTreatyPass);

        System.out.println("STATS Comparison completed and results written to Excel.");
        return isAllPass;

    }
}
