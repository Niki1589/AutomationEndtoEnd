package com.rms.automation.LossValidation;

import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.LossValidation.non_ep.plt_losses.PLTLossValidationNonEP;
import com.rms.automation.LossValidation.non_ep.stats_losses.NonEPSTATSLossValidation;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LossValidation {

    public static void run(Map<String, String> tc) throws Exception {

        Download_Settings downloadSettings = Download_Settings.parse(tc.get("DOWNLOAD_SETTINGS_FILE"));

        String zipFilePath = FileExportTests.localPath;
        String baselinePath = tc.get("BASELINE_PATH");

        //String baselinePath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/Baselines/non-ep/RM20_FM_EUWS_FP_03/meout/EventStats/";
       // String actualPath = zipFilePath.replace(".zip", "");
        String actualPath ="/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25211050_Testing_EDM_E2E_AllPerils__PORTFOLIO__EUWS_01_Losses/";

        // Specify the path where you want to create the folder
        String comparisonFolderPath = tc.get("FILE_EXPORT_PATH")+"Comparison/";

        // Create a Path object representing the directory
        Path comparisonFolder = Paths.get(comparisonFolderPath);

        try {
            // Check if the base folder already exists
            if (Files.exists(comparisonFolder)) {
                throw new IOException("Comparison folder already exists: " + comparisonFolderPath);
            }

            // Attempt to create the directory
            Files.createDirectories(comparisonFolder);
            System.out.println("Folder created successfully.");
        }
        catch (IOException e) {
            System.err.println("Failed to create folder: " + e.getMessage());
        }

        String outputPath = comparisonFolderPath  + "%s.xlsx";

        Boolean isAllEPPass=false;
        Boolean isAllStatsPass=false;
        Boolean isAllPLTPass =false;

        try {
            Utils.unzip(zipFilePath);
            NonEPSTATSLossValidation.run(baselinePath, actualPath, outputPath);
            PLTLossValidationNonEP.run(baselinePath, actualPath, outputPath);

//            if (Utils.isTrue(downloadSettings.getIsEPMetric()))
//            {
//                 isAllEPPass = EPLossValidation.run(baselinePath, actualPath, outputPath, downloadSettings);
//            }
//            if(Utils.isTrue(downloadSettings.getIsStatsMetric()))
//            {
//                 isAllStatsPass = STATSLossValidation.run(baselinePath, actualPath, outputPath);
//            }
//            if(Utils.isTrue(downloadSettings.getIsLossTablesMetric())) {
//
//                isAllPLTPass = PLTLossValidation.PLTLossValidation(baselinePath, actualPath, outputPath);
//            }
//
//            if (isAllEPPass){
//                //all pass
//                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "EP numbers are matching,please check STATS and PLT");
//            }
//            else if(isAllStatsPass){
//                //fail
//                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "STATS number are matching, please check EP and PLT");
//            }
//            else if (isAllPLTPass)
//            {
//                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "PLT number are matching, please check EP and STATS");
//            }
//
//            else
//            {
//                LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", "Please check EP, STATS and PLT numbers");
//
//            }
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

    }

}
