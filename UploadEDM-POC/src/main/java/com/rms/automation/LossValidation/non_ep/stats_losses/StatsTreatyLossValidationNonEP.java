package com.rms.automation.LossValidation.non_ep.stats_losses;

import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsTreatyLossValidationNonEP {

    public static Boolean runTreatyResults(String baselinePathStats, String actualPathStats, String outputPath) {
        
        List<String> folders = new ArrayList<>();
        folders.add("SU");
        folders.add("TY");

        String baselinStatsathTreatyStats = baselinePathStats + "/Treaty/";
        String actualPathTreatyStats = actualPathStats + "/Treaty/";

        //String actualPathTreatyStats ="/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/ActualResults/25014915_Testing_EDM_E2E_new3__PORTFOLIO__EUWS_01_Losses/STATS/Treaty/";

        String outPathStats = String.format(outputPath, "Stats_Treaty_Results_Non_EP");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinStatsathTreatyStats + folder) && Utils.isDirExists(actualPathTreatyStats + folder) ) {
                    List<Map<String, String>> baselineData = Utils.readCSV(baselinStatsathTreatyStats + folder);
                    List<Map<String, String>> actualData = Utils.readCSV(actualPathTreatyStats + folder);
                    if (baselineData != null && actualData != null) {
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

            writeResultsToExcel(rows, outPathStats);
            return isAllPass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private static ValidationResult compareData(List<Map<String, String>> baselineData, List<Map<String, String>> actualData,String folder) {
        try {
            List<List<String>> results = new ArrayList<>();
            Boolean isAllPass = true;

            for (Map<String, String> baselineRow : baselineData)
            {

                String baselineEventId = baselineRow.get("EventId");
                String baselineTID = baselineRow.get("TreatyId");
                String baselineTNum = baselineRow.get("TreatyNum");
                String baselineMatcher = baselineEventId+baselineTID+"-"+baselineTNum;
                String baselineTName = baselineRow.get("TreatyName");

                for (Map<String, String> actualRow : actualData) {

                    String actualEventId = actualRow.get("EventId");
                    String actualTID = baselineRow.get("TreatyId");
                    String actualTNum = baselineRow.get("TreatyNum");
                    String actualMatcher = actualEventId+actualTID+"-"+actualTNum;
                    String actualTName = baselineRow.get("TreatyName");

                    boolean isMatches = baselineMatcher.equals(actualMatcher);

                    if (isMatches) {
                        List<String> row = new ArrayList<>();

                        String baselineAAL = baselineRow.get("Mean");
                        String baselineStd = baselineRow.get("StdDev");
                        String baselineCV = baselineRow.get("CV");

                        String actualAAL = actualRow.get("Mean");
                        String actualStd = actualRow.get("StdDev");
                        String actualCV = actualRow.get("CV");

                        // Baseline
                        row.add(folder);
                        row.add(baselineEventId);
                        row.add(baselineTID);
                        row.add(baselineTNum);
                        row.add(baselineTName);
                        row.add(baselineAAL);
                        row.add(baselineStd);
                        row.add(baselineCV);

                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualEventId);
                        row.add(actualTID);
                        row.add(actualTNum);
                        row.add(actualTName);
                        row.add(actualAAL);
                        row.add(actualStd);
                        row.add(actualCV);

                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Results
                        row.add(folder);

                        row.add(actualEventId);
                        row.add(actualTID);
                        row.add(actualTNum);
                        row.add(actualTName);

                        List<String> AALRows = checkDiff(baselineAAL, actualAAL, "Mean", folder);
                        List<String> StdRows = checkDiff(baselineAAL, actualAAL, "StdDev", folder);
                        List<String> CVRows = checkDiff(baselineAAL, actualAAL, "CV", folder);

                        row.addAll(AALRows);
                        row.addAll(StdRows);
                        row.addAll(CVRows);

                        if (AALRows.get(1).equals("Fail") || StdRows.get(1).equals("Fail") || CVRows.get(1).equals("Fail"))  {
                            isAllPass = false;
                        }

                        results.add(row);
                        break;
                    }
                }
            }
            ValidationResult validationResult = new ValidationResult();
            validationResult.resultRows = results;
            validationResult.isAllPass = isAllPass;
            return validationResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void writeResultsToExcel(List<List<String>> rows, String filStatsath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");

        List<List<String>> results = new ArrayList<>();
        List<String> sectionNames = new ArrayList<>();

        // Baseline
        sectionNames.add("Baseline Data");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");

        // Two empty cells between Baseline and Actual
        sectionNames.add("");
        sectionNames.add("");

        // Actual
        sectionNames.add("Actual Data");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");

        // Two empty cells between Actual and Results
        sectionNames.add("");
        sectionNames.add("");

        // Actual
        sectionNames.add("Results");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");


        List<String> headers = new ArrayList<>();

        // EventId   TreatyId	TreatyName	TreatyNum Mean	StdDev	CV
        // Baseline
        headers.add("perspcode");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("Mean");
        headers.add("StdDev");
        headers.add("CV");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("Mean");
        headers.add("Std");
        headers.add("CV");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Results
        headers.add("perspcode");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("AAL-Diff");
        headers.add("AAL");
        headers.add("STD-Diff");
        headers.add("STD");
        headers.add("CV-Diff");
        headers.add("CV");

        results.add(sectionNames);
        results.add(headers);

        results.addAll(rows);

        // Write the data rows
        int rowNum = 0;
        for (List<String> resultRow : results) {
            Row row = sheet.createRow(rowNum++);

            // Write the data rows
            int colNum = 0;
            for (String resultCol : resultRow) {
                row.createCell(colNum).setCellValue(resultCol);
                colNum++;
            }
        }

        FileOutputStream fileOut = new FileOutputStream(filStatsath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private  static List<String> checkDiff(String baseline, String actual, String name, String pr) {

        List<String> rows = new ArrayList<>();

        Double baseline_ = Utils.parseToDouble(baseline, name, pr);
        Double actual_ = Utils.parseToDouble(actual, name, pr);

        Double difference = null;
        if (baseline_ != null && actual_ != null) {
            difference = Math.abs(baseline_ - actual_);
        }

        if (difference != null) {
            rows.add(difference+"");
        } else {
            rows.add("");
        }

        if (difference != null && !(difference > 1)) {
            rows.add("Pass");
        } else {
            rows.add("Fail");
        }

        return rows;

    }


}
