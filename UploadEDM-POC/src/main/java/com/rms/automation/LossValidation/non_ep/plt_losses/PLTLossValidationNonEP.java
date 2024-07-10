package com.rms.automation.LossValidation.non_ep.plt_losses;

public class PLTLossValidationNonEP {
    public static Boolean run(String baselinePath, String actualPath, String outputPath) throws Exception {

        String baselinePathPLT = baselinePath + "/non-ep/RM20_FM_EUWS_FP_03/meout/SLT";
        String actualPathPLT = actualPath + "/SAMPLED_PLT";

        Boolean isPortfolioPass = PLTPortfolioLossValidation.run(baselinePathPLT, actualPathPLT, outputPath);
        Boolean isTreatyPass = PLTTreatyLossValidation.runTreatyResults(baselinePathPLT, actualPathPLT, outputPath);

        Boolean isAllPass = (isPortfolioPass && isTreatyPass);

        System.out.println("PLT Comparison completed and results written to Excel.");
        return isAllPass;

    }
}
