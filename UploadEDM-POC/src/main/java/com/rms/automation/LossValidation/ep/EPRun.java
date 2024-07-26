package com.rms.automation.LossValidation.ep;

import com.rms.automation.LossValidation.Analysis_Type_ENUM;
import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.LossValidation.ep.ep_losses.EPLossValidation;
import com.rms.automation.LossValidation.ep.plt_losses.PLTLossValidationEP;
import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.utils.Utils;

import java.io.IOException;

public class EPRun {

    public static ValidationResult run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {
        ValidationResult validationResult = new ValidationResult();
        System.out.println("EP Comparison for Analysis Type EP started.");
        try {

            if (Utils.isTrue(downloadSettings.getIsEPMetric()))
            {
                validationResult.isAllEPPass = EPLossValidation.run(baselinePath, actualPath, outputPath, downloadSettings);
            }
            if(Utils.isTrue(downloadSettings.getIsStatsMetric()))
            {
                validationResult.isAllStatsPass = STATSLossValidation.run(baselinePath, actualPath, outputPath,downloadSettings);
            }
            if(Utils.isTrue(downloadSettings.getIsLossTablesMetric())) {

                validationResult.isAllPLTPass = PLTLossValidationEP.run(baselinePath, actualPath, outputPath,downloadSettings);
            }
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

        return validationResult;

    }

}
