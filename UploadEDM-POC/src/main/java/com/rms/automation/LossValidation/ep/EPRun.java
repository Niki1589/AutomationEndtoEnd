package com.rms.automation.LossValidation.ep;

import com.rms.automation.LossValidation.Analysis_Type_ENUM;
import com.rms.automation.LossValidation.ClimateChange.CCLossValidation;
import com.rms.automation.LossValidation.PLTLossValidation;
import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.LossValidation.ep.ep_losses.EPLossValidation;
import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.utils.Utils;

import java.io.IOException;

public class EPRun {

    public static ValidationResult run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {
        ValidationResult validationResult = new ValidationResult();
        try {

            if (Utils.isTrue(downloadSettings.getIsEPMetric()))
            {
                validationResult.isAllEPPass = EPLossValidation.run(baselinePath, actualPath, outputPath, downloadSettings);
            }
            if(Utils.isTrue(downloadSettings.getIsStatsMetric()))
            {
                validationResult.isAllStatsPass = STATSLossValidation.run(baselinePath, actualPath, outputPath);
            }
            if(Utils.isTrue(downloadSettings.getIsLossTablesMetric())) {

                validationResult.isAllPLTPass = PLTLossValidation.PLTLossValidation(baselinePath, actualPath, outputPath);
            }
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

        return validationResult;

    }

}
