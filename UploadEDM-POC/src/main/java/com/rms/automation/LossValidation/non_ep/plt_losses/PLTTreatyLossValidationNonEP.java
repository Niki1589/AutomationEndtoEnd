package com.rms.automation.LossValidation.non_ep.plt_losses;

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

public class PLTTreatyLossValidationNonEP {

    public static Boolean runTreatyResults(String baselinePathPLT, String actualPathPLT, String outputPath) {

        List<String> folders = new ArrayList<>();
        folders.add("SU");
        folders.add("TY");

        String baselinePathPLTTreaty = baselinePathPLT + "/Treaty/";
        String actualPathPLTTreaty = actualPathPLT + "/Treaty/";
        String outPathPLTTreaty = String.format(outputPath, "PLT_Treaty_Results_non_ep");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathPLTTreaty + folder) && Utils.isDirExists(actualPathPLTTreaty + folder) ) {
                    List<Map<String, String>> baselineData = Utils.readParquet(baselinePathPLTTreaty + folder);
                    List<Map<String, String>> actualData = Utils.readParquet(actualPathPLTTreaty + folder);
                    if (baselineData != null && actualData != null) {
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

            writeResultsToExcel(rows, outPathPLTTreaty);
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
                String baselineSampleId = baselineRow.get("SampleId");
                String baselineEventId = baselineRow.get("EventId");
                String baselineTID = baselineRow.get("TreatyId");
                String baselineMatcher = baselineSampleId+"-"+baselineEventId+"-"+baselineTID;
                String baselineTName = baselineRow.get("TreatyName");

                for (Map<String, String> actualRow : actualData) {

                    String actualSampleId = actualRow.get("SampleId");
                    String actualEventId = actualRow.get("EventId");
                    String actualTID = baselineRow.get("TreatyId");
                    String actualMatcher = actualSampleId +"-"+actualEventId+"-"+actualTID;
                    String actualTName = baselineRow.get("TreatyName");

                    boolean isMatches = baselineMatcher.equals(actualMatcher);

                    if (isMatches) {
                        List<String> row = new ArrayList<>();

                        String baselineLoss = baselineRow.get("Loss");
                        String actualLoss = actualRow.get("Loss");

                        // Baseline
                        row.add(folder);
                        row.add(baselineSampleId);
                        row.add(baselineEventId);
                        row.add(baselineTID);
                        row.add(baselineTName);
                        row.add(baselineLoss);


                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualSampleId);
                        row.add(actualEventId);
                        row.add(actualTID);
                        row.add(actualTName);
                        row.add(actualLoss);


                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Results
                        row.add(folder);
                        row.add(actualSampleId);
                        row.add(actualEventId);
                        row.add(actualTID);
                        row.add(actualTName);

                        Double baselineLoss_ = null;
                        Double actualLoss_ = null;

                        try {
                            if (baselineLoss != null && !baselineLoss.isEmpty()) {
                                baselineLoss_ = Double.valueOf(baselineLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                         //   System.out.println("Wrong baselineLoss_ at "+baselineEventId);
                        }

                        try {
                            if (actualLoss != null && !actualLoss.isEmpty()) {
                                actualLoss_ = Double.valueOf(actualLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                        //    System.out.println("Wrong actualLoss_ at "+actualEventId);
                        }

                        Double difference = null;
                        if (baselineLoss_ != null && actualLoss_ != null) {
                            difference = Math.abs(baselineLoss_ - actualLoss_);
                        }

                        row.add(difference+"");

                        if (difference != null && !(difference > 1)) {
                            row.add("Pass");
                        } else {
                            isAllPass = false;
                            row.add("Fail");
                        }

                        results.add(row);
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



        List<String> headers = new ArrayList<>();

        // EventId   TreatyId	TreatyName	TreatyNum Mean	StdDev	CV
        // Baseline
        headers.add("perspcode");
        headers.add("SampleId");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyName");
        headers.add("Loss");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("SampleId");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyName");
        headers.add("Loss");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Results
        headers.add("perspcode");
        headers.add("SampleId");
        headers.add("EventId");
        headers.add("TreatyId");
        headers.add("TreatyName");
        headers.add("difference");
        headers.add("Result");


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
