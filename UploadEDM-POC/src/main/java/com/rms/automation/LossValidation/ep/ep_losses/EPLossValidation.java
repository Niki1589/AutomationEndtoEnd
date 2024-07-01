package com.rms.automation.LossValidation.ep.ep_losses;

import com.rms.automation.exportApi.Download_Settings;

public class EPLossValidation {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathEP = baselinePath + "/EP";
        String actualPathEP = actualPath + "/EP";

        Boolean isPortfolioPass = EPPortlofioLossValidation.runPortfolioLossValidationEP(baselinePathEP, actualPathEP, outputPath, downloadSettings);
        Boolean isTreatyPass = EPTreatyLossValidation.runTreatyLossValidationEP(baselinePathEP, actualPathEP, outputPath);

        Boolean isAllPass = (isPortfolioPass && isTreatyPass);

        System.out.println("EP Comparison completed and results written to Excel.");
        return isAllPass;

    }

}
