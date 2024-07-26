package com.rms.automation.LossValidation.ep.ep_losses;

import com.rms.automation.exportApi.Download_Settings;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

public class EPLossValidation {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        String baselinePathEP = baselinePath + "/EP/EP";
        String actualPathEP = actualPath + "/EP";

        // Check if baselinePathEP directory exists
        File baselineDir = new File(baselinePathEP);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
           // throw new Exception("Baseline directory '" + baselinePathEP + "' does not exist or is not a directory.");

            System.out.println("Baseline directory '" + baselinePathEP + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathEP);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
          //  throw new Exception("Actual directory '" + actualPathEP + "' does not exist or is not a directory.");
            System.out.println("Actual directory '" + actualPathEP + "' does not exist or is not a directory.");
        }

        Boolean isPortfolioPass = null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && (downloadSettings.getOutputLevels_EPMetric().toUpperCase().contains("PORTFOLIO")))
            //||containsIgnoreCase(downloadSettings.getOutputLevels_EPMetric(),"Treaty")))
        {


            isPortfolioPass = EPPortlofioLossValidation.runPortfolioLossValidationEP(baselinePathEP, actualPathEP, outputPath, downloadSettings);
        }

        Boolean isTreatyPass = null;
        if (downloadSettings.getOutputLevels_EPMetric()!=null && ( downloadSettings.getOutputLevels_EPMetric().toUpperCase().contains("TREATY"))) {

            // If both directories exist, proceed with loss validations
            isTreatyPass = EPTreatyLossValidation.runTreatyLossValidationEP(baselinePathEP, actualPathEP, outputPath);
        }
        // Boolean isTreatyPass = EPTreatyLossValidation.runTreatyLossValidationEP(baselinePathEP, actualPathEP, outputPath);

        Boolean isAllPass = (isPortfolioPass ==Boolean.TRUE) || (isTreatyPass==Boolean.TRUE);

        System.out.println("EP Comparison for Analysis Type EP completed and results written to Excel.");
        return isAllPass;

    }


}
