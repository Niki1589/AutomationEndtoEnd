package com.rms.automation.LossValidation;

import com.rms.automation.LossValidation.ClimateChange.CCLossValidation;
import com.rms.automation.LossValidation.ClimateChange.CCRun;
import com.rms.automation.LossValidation.ep.EPRun;
import com.rms.automation.LossValidation.ep.ep_losses.EPLossValidation;
import com.rms.automation.LossValidation.ep.stats_losses.STATSLossValidation;
import com.rms.automation.LossValidation.non_ep.NonEPLossValidation;
import com.rms.automation.LossValidation.non_ep.NonEPRun;
import com.rms.automation.LossValidation.non_ep.plt_losses.PLTLossValidationNonEP;
import com.rms.automation.LossValidation.non_ep.stats_losses.NonEPSTATSLossValidation;
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

        String isCCRun=tc.get("CCG_IS_CLIMATE_CHANGE");

        String zipFilePath = FileExportTests.localPath;
        String baselinePath = tc.get("BASELINE_PATH"); //Baseline path is not genertaed by the code, user has to provide the path

        //String baselinePath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/Baselines/non-ep/RM20_FM_EUWS_FP_03/meout/EventStats/";
       // String actualPath = zipFilePath.replace(".zip", "");
      //  String actualPath ="/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25211050_Testing_EDM_E2E_AllPerils__PORTFOLIO__EUWS_01_Losses/";


        String actualPath ="/Users/Nikita.Arora/Documents/UploadEdmPoc/Results/A002_SMOKE_EUWS/ActualResults/CSV/25118159_Testing_EDM_E2E_new2__PORTFOLIO__EUWS_01_CC_RCP2_6_2075_Losses";

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

        try {
            Utils.unzip(zipFilePath);
          //  NonEPSTATSLossValidation.run(baselinePath, actualPath, outputPath);
           // PLTLossValidationNonEP.run(baselinePath, actualPath, outputPath);

            //Condition to check we are comparing the correct files for Actual and Baseline folders

            ValidationResult EPResults = new ValidationResult();
            ValidationResult nonEPResults = new ValidationResult();
            ValidationResult CCResults = new ValidationResult();

          if(analysisTypeEnum.equals(Analysis_Type_ENUM.EP))
          {
              EPResults = EPRun.run(baselinePath, actualPath, outputPath, downloadSettings);
          }
          else if(analysisTypeEnum.equals(Analysis_Type_ENUM.HISTORICAL) || analysisTypeEnum.equals(Analysis_Type_ENUM.FOOTPRINT) || analysisTypeEnum.equals(Analysis_Type_ENUM.SCENARIO))
          {
              nonEPResults=  NonEPRun.run( baselinePath, actualPath, outputPath, downloadSettings);
          }

          if( isCCRun.equalsIgnoreCase("YES"))
          {
              CCResults= CCRun.run(baselinePath, actualPath, outputPath, downloadSettings);

          }
            String successMsg = "";
            String failMsg = "";

            if (EPResults.isAllEPPass != null) {
                if (EPResults.isAllEPPass){
                    successMsg += "EP loss validation successfull";
                } else {
                    failMsg += "Mismatch in EP results";
                }
            }
            if (EPResults.isAllStatsPass != null) {
                if (EPResults.isAllStatsPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "Stats loss validation successfull";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in STATS results";
                }
            }
            if (EPResults.isAllPLTPass != null) {
                if (EPResults.isAllPLTPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "PLT loss validation successfull";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in PLT results";
                }
            }
            if(nonEPResults.isAllStatsPass != null)
            {
                if (nonEPResults.isAllStatsPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "Stats loss validation successfull for non-EP";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in STATS results for non-EP";
                }
            }

            if(nonEPResults.isAllPLTPass != null)
            {
                if (nonEPResults.isAllPLTPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "PLT loss validation successfull for non-EP";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in PLT results for non-EP";
                }
            }

            if(CCResults.isAllStatsPass != null)
            {
                if (CCResults.isAllStatsPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "Stats loss validation successfull for CC";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in STATS results for CC";
                }
            }

            if(CCResults.isAllPLTPass != null)
            {
                if (CCResults.isAllPLTPass) {
                    if (successMsg != "") successMsg += ", ";
                    successMsg += "PLT loss validation successfull for CC";
                } else {
                    if (failMsg != "") failMsg += ", ";
                    failMsg += "Mismatch in PLT results for CC";
                }
            }

            String msg = "For this EP analysis, ";
            if (successMsg != "") {
                msg += successMsg + "loss validation successful. ";
            }
            if (failMsg != "") {
                msg += "mismatch in "+failMsg;
            }

            LoadData.UpdateTCInLocalExcel(tc.get("INDEX"), "ISLOSSVALIDATION", msg);
        } catch (IOException e) {
            System.out.println("Loss Validation Failed "+e.getMessage());
        }

    }

}
