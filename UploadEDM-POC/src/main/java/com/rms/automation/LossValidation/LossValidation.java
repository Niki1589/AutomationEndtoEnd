package com.rms.automation.LossValidation;

import com.rms.automation.LossValidation.ClimateChange.CCRun;
import com.rms.automation.LossValidation.ep.EPRun;
import com.rms.automation.LossValidation.non_ep.NonEPRun;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.edm.LoadData;
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

        //MPF_ANALYSIS_TYPE
        Analysis_Type_ENUM analysisTypeEnum = Analysis_Type_ENUM.getByValue(tc.get("MPF_ANALYSIS_TYPE"));

        String isCCRun = tc.get("CCG_IS_CLIMATE_CHANGE");

        String zipFilePath = FileExportTests.localPath;
        String baselinePath = tc.get("BASELINE_PATH"); //Baseline path is not genertaed by the code, user has to provide the path
       // String actualPath = zipFilePath.replace(".zip", "");
        String actualPath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25732868_RM20_FM_EUWS_01_Test_Losses";

        // Specify the path where you want to create the folder
        String comparisonFolderPath = tc.get("FILE_EXPORT_PATH") + "Comparison/";

        // Create a Path object representing the directory
        Path comparisonFolder = Paths.get(comparisonFolderPath);

        try {
            // Check if the base folder already exists
            if (Files.exists(comparisonFolder)) {
                throw new IOException("Comparison folder already exists: " + comparisonFolderPath);
            }

            // Attempt to create the directory
            Files.createDirectories(comparisonFolder);
            System.out.println("Comparison Folder created successfully.");
        } catch (IOException e) {
            System.err.println("Failed to create folder: " + e.getMessage());
        }

        String outputPath = comparisonFolderPath + "%s.xlsx";

        try {
            Utils.unzip(zipFilePath);

            //Condition to check we are comparing the correct files for Actual and Baseline folders

            ValidationResult EPResults = new ValidationResult();
            ValidationResult nonEPResults = new ValidationResult();
            ValidationResult CCResults = new ValidationResult();
            if (analysisTypeEnum.equals(Analysis_Type_ENUM.EP)) {
                EPResults = EPRun.run(baselinePath, actualPath, outputPath, downloadSettings);
            }

            else if (analysisTypeEnum.equals(Analysis_Type_ENUM.HISTORICAL) ||
                    analysisTypeEnum.equals(Analysis_Type_ENUM.FOOTPRINT) ||
                    analysisTypeEnum.equals(Analysis_Type_ENUM.SCENARIO)) {
                nonEPResults = NonEPRun.run(baselinePath, actualPath, outputPath, downloadSettings);
            }


            if (isCCRun != null && isCCRun.equalsIgnoreCase("YES")) {
              //  actualPath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25751853_rename_analysis_WS_CC_RCP4_5_2020_Losses";
                CCResults = CCRun.run(baselinePath, actualPath, outputPath, downloadSettings);
            }

            StringBuilder successMsgBuilder = new StringBuilder();
            StringBuilder failMsgBuilder = new StringBuilder();

            handleValidationResult(EPResults, successMsgBuilder, failMsgBuilder, "EP");
            handleValidationResult(nonEPResults, successMsgBuilder, failMsgBuilder, "non-EP");
            handleValidationResult(CCResults, successMsgBuilder, failMsgBuilder, "CC");

            String successMsg = successMsgBuilder.toString();
            String failMsg = failMsgBuilder.toString();

            String msg = "For this EP analysis, ";
            if (!successMsg.isEmpty()) {
                msg += successMsg + " loss validation successful. ";
            }
            if (!failMsg.isEmpty()) {
                msg += "mismatch in " + failMsg;
            }

            LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", msg);
        } catch (IOException e) {
            System.out.println("Loss Validation Failed " + e.getMessage());
        }
    }

    private static void handleValidationResult(ValidationResult result, StringBuilder successMsgBuilder,
                                               StringBuilder failMsgBuilder, String type) {
        if (result.isAllEPPass != null && result.isAllEPPass) {
            appendToSuccessMsg(successMsgBuilder, "EP loss validation successful for " + type);
        } else if (result.isAllEPPass != null) {
            appendToFailMsg(failMsgBuilder, "Mismatch in EP results for" + type);
        }

        if (result.isAllStatsPass != null && result.isAllStatsPass) {
            appendToSuccessMsg(successMsgBuilder, "Stats loss validation successful for " + type);
        } else if (result.isAllStatsPass != null) {
            appendToFailMsg(failMsgBuilder, "Mismatch in STATS results for " + type);
        }

        if (result.isAllPLTPass != null && result.isAllPLTPass) {
            appendToSuccessMsg(successMsgBuilder, "PLT loss validation successful for " + type);
        } else if (result.isAllPLTPass != null) {
            appendToFailMsg(failMsgBuilder, "Mismatch in PLT results for " + type);
        }
    }

    private static void appendToSuccessMsg(StringBuilder builder, String msg) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(msg);
    }

    private static void appendToFailMsg(StringBuilder builder, String msg) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(msg);
    }
}