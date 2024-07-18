package com.rms.automation.LossValidation.ClimateChange.EP;

import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EPTreatyLossValidationCC {
    public static Boolean runTreatyLossValidationEPCC(String baselinePathEP, String actualPathEP, String outputPath) throws Exception {

        ///Folders to read from Portfolio
        List<String> folders = new ArrayList<>();
        folders.add("SU");
        folders.add("TY");

        String baselinePathTreatyEP = baselinePathEP + "/Treaty/";
        String actualPathTreatyEP = actualPathEP + "/Treaty/";
        String outPathEP = String.format(outputPath, "EP_Treaty_Results_CC");

        File baselineDir = new File(baselinePathTreatyEP);
        if (!baselineDir.exists() || !baselineDir.isDirectory()) {
            throw new Exception("Baseline directory '" + baselinePathTreatyEP + "' does not exist or is not a directory.");
        }

        // Check if actualPathEP directory exists
        File actualDir = new File(actualPathTreatyEP);
        if (!actualDir.exists() || !actualDir.isDirectory()) {
            throw new Exception("Actual directory '" + actualPathTreatyEP + "' does not exist or is not a directory.");
        }

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathTreatyEP + folder) && Utils.isDirExists(actualPathTreatyEP + folder) ) {
                    List<Map<String, String>> baselineData = Utils.readCSV(baselinePathTreatyEP + folder);
                    List<Map<String, String>> actualData = Utils.readCSV(actualPathTreatyEP + folder);
                    if (baselineData != null && actualData != null) {
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

            writeResultsToExcel(rows, outPathEP);
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

                String baselineET = baselineRow.get("EPType");
                String baselineRP = baselineRow.get("ReturnPeriod");
                String baselineTID = baselineRow.get("TreatyId");
                String baselineTNum = baselineRow.get("TreatyNum");
                String baselineMatcher = baselineET+"-"+baselineRP+"-"+baselineTID+"-"+baselineTNum;
                String baselineTName = baselineRow.get("TreatyName");

                for (Map<String, String> actualRow : actualData) {

                    String actualET = baselineRow.get("EPType");
                    String actualRP = baselineRow.get("ReturnPeriod");
                    String actualTID = baselineRow.get("TreatyId");
                    String actualTNum = baselineRow.get("TreatyNum");
                    String actualMatcher = actualET+"-"+actualRP+"-"+actualTID+"-"+actualTNum;
                    String actualTName = baselineRow.get("TreatyName");

                    boolean isMatches = baselineMatcher.equals(actualMatcher);

                    if (isMatches) {
                        List<String> row = new ArrayList<>();

                        String baselineLoss = baselineRow.get("Loss");
                        String actualLoss = actualRow.get("Loss");

                        // Baseline
                        row.add(folder);
                        row.add(baselineET);
                        row.add(baselineRP);
                        row.add(baselineTID);
                        row.add(baselineTNum);
                        row.add(baselineTName);
                        row.add(baselineLoss);

                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualET);
                        row.add(actualRP);
                        row.add(actualTID);
                        row.add(actualTNum);
                        row.add(actualTName);
                        row.add(actualLoss);

                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Results
                        row.add(folder);
                        row.add(actualET);
                        row.add(actualRP);
                        row.add(actualTID);
                        row.add(actualTNum);
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
                            System.out.println("Wrong baselineLoss_ at "+baselineMatcher);
                        }

                        try {
                            if (actualLoss != null && !actualLoss.isEmpty()) {
                                actualLoss_ = Double.valueOf(actualLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            System.out.println("Wrong actualLoss_ at "+actualMatcher);
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

    private static void writeResultsToExcel(List<List<String>> rows, String filePath) throws IOException, IOException {
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

        // Two empty cells between Actual and Results
        sectionNames.add("");
        sectionNames.add("");

        // Actual
        sectionNames.add("Results");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");

        List<String> headers = new ArrayList<>();

        //EPType	Loss	ReturnPeriod	TreatyId	TreatyName	TreatyNum
        // Baseline
        headers.add("perspcode");
        headers.add("EPType");
        headers.add("ReturnPeriod");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("Loss");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("EPType");
        headers.add("ReturnPeriod");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("Loss");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Results
        headers.add("perspcode");
        headers.add("EPType");
        headers.add("ReturnPeriod");
        headers.add("TreatyId");
        headers.add("TreatyNum");
        headers.add("TreatyName");
        headers.add("difference");
        headers.add("loss");

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

        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

}
