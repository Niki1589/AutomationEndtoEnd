package com.rms.automation.LossValidation.ClimateChange;

import com.rms.automation.LossValidation.ClimateChange.EP.EPLossValidation;
import com.rms.automation.LossValidation.ep.ep_losses.EPPortlofioLossValidation;
import com.rms.automation.LossValidation.ep.ep_losses.EPTreatyLossValidation;
import com.rms.automation.LossValidation.ClimateChange.EP.EPPortfolioLossValidationCC;
import com.rms.automation.LossValidation.ClimateChange.EP.EPTreatyLossValidationCC;
import com.rms.automation.LossValidation.ClimateChange.STATS.StatsLossValidationCC;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.exportApi.Download_Settings;

public class CCLossValidation {
    public static Boolean run(String baselinePath, String actualPath, String outputPath, Download_Settings downloadSettings) throws Exception {

        Pattern pattern = Pattern.compile("(\\d+_\\d+_\\d+)");
        Matcher matcher = pattern.matcher(actualPath);

        String ccRcpTime = "";
        if (matcher.find()) {
            String matchedPart = matcher.group(1);
            ccRcpTime = matchedPart.replaceFirst("_", "");
        } else {
            System.out.println("No match found");
            return false;
        }

        String baselinePathCC = baselinePath + "12_R"+ccRcpTime+"/ccout";
        String actualPathCC = actualPath;

        System.out.println("Baseline Path: " + baselinePathCC);
        System.out.println("Actual Path: " + actualPathCC);

        Boolean isEPPass = EPLossValidation.run(baselinePathCC, actualPathCC, outputPath, downloadSettings);
        Boolean isStatsPass = StatsLossValidationCC.run(baselinePathCC, actualPathCC, outputPath);
        Boolean isAllPass = isEPPass && isStatsPass;

        return isAllPass;

    }
}
