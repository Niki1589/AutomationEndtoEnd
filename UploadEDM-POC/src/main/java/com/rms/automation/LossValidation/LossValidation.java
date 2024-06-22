package com.rms.automation.LossValidation;

import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LossValidation {

    public static void run(Map<String, String> tc) throws Exception {

        String baselinePathPortfolio = "";
        String actualPathPortfolio = "";
        String outputPath = "";

        String zipFilePath = FileExportTests.localPath; // Path to your zip file
        baselinePathPortfolio = tc.get("BASELINE_PATH") +"%s"+ File.separator + "Portfolio"+File.separator;
        actualPathPortfolio = zipFilePath.replace(".zip", "") + File.separator + "%s" +File.separator+"Portfolio"+File.separator;
        outputPath = tc.get("LOSSVALIDATION_Results_PATH")+"%s.xlsx";

        try {
            Utils.unzip(zipFilePath);
            Boolean isAllEPPass = EPLossValidation.EPLossValidation(baselinePathPortfolio, actualPathPortfolio, outputPath);
            Boolean isAllStatsPass = StatsLossValidation.StatsLossValidation(baselinePathPortfolio, actualPathPortfolio, outputPath);
            Boolean isAllPLTPass = PLTLossValidation.PLTLossValidation(baselinePathPortfolio, actualPathPortfolio, outputPath);

            if (isAllStatsPass && isAllEPPass && isAllPLTPass) {
                //all pass
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "EP, STATS and PLT numbers are matching");
            } else {
                //fail
                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "There is mismtach in numbers,please check comparison sheet");
            }
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

    }


}
