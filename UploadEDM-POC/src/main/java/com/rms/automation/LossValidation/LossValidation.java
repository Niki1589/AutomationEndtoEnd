package com.rms.automation.LossValidation;

import com.rms.automation.LossValidation.ep.ep_losses.EPLossValidation;
import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.utils.Utils;

import java.io.IOException;
import java.util.Map;

public class LossValidation {

    public static void run(Map<String, String> tc) throws Exception {

        Download_Settings downloadSettings = Download_Settings.parse(tc.get("DOWNLOAD_SETTINGS_FILE"));

        String zipFilePath = FileExportTests.localPath;
        String baselinePath = tc.get("BASELINE_PATH");
        String actualPath = zipFilePath.replace(".zip", "");
        String outputPath = tc.get("LOSSVALIDATION_Results_PATH") + "%s.xlsx";

        Boolean isAllEPPass=false;
        Boolean isAllStatsPass=false;
        Boolean isAllPLTPass =false;

        try {
            Utils.unzip(zipFilePath);

            if (Utils.isTrue(downloadSettings.getIsEPMetric()))
            {
                 isAllEPPass = EPLossValidation.run(baselinePath, actualPath, outputPath, downloadSettings);
            }
            if(Utils.isTrue(downloadSettings.getIsStatsMetric()))
            {
                 isAllStatsPass = STATSLossValidation.run(baselinePath, actualPath, outputPath);
            }
            if(Utils.isTrue(downloadSettings.getIsLossTablesMetric())) {

                isAllPLTPass = PLTLossValidation.PLTLossValidation(baselinePath, actualPath, outputPath);
            }

            if (isAllStatsPass && isAllEPPass && isAllPLTPass) {
                //all pass
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "EP, STATS and PLT numbers are matching");
            } else {
                //fail
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "There is a mismatch in numbers,please check comparison sheet");
            }
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

    }

}
