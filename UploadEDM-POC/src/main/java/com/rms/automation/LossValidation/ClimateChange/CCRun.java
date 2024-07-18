package com.rms.automation.LossValidation.ClimateChange;

import com.rms.automation.LossValidation.ClimateChange.EP.EPLossValidationCC;
import com.rms.automation.LossValidation.ClimateChange.STATS.StatsLossValidationCC;
import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.LossValidation.ep.ep_losses.EPLossValidation;
import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.utils.Utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCRun {

    public static ValidationResult run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {
        ValidationResult validationResult = new ValidationResult();

        try {
            Pattern pattern = Pattern.compile("(\\d+_\\d+_\\d+)");
            Matcher matcher = pattern.matcher(actualPath);
            String ccRcpTime = "";

            if (matcher.find()) {
                String matchedPart = matcher.group(1);
                ccRcpTime = matchedPart.replaceFirst("_", "");
            } else {
                System.out.println("No match found");
                return validationResult; // Return empty ValidationResult
            }

            String baselinePathCC = baselinePath + "/CC/12_R" + ccRcpTime + "/ccout";
            String actualPathCC = actualPath;

            System.out.println("Baseline Path: " + baselinePathCC);
            System.out.println("Actual Path: " + actualPathCC);

            if (Utils.isTrue(downloadSettings.getIsEPMetric())) {
                validationResult.isAllEPPass = EPLossValidationCC.run(baselinePathCC, actualPathCC, outputPath, downloadSettings);
            }

            if (Utils.isTrue(downloadSettings.getIsStatsMetric())) {
                validationResult.isAllStatsPass = StatsLossValidationCC.run(baselinePathCC, actualPathCC, outputPath, downloadSettings);
            }

        } catch (IOException e) {
            System.out.println("Loss Validation Failed " + e.getMessage());
        }

        return validationResult;
    }
}
