package com.rms.automation.LossValidation.ep.stats_losses;

import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class STATSPortfolioLossValidation {

    public static Boolean run(String baselinePathStats, String actualPathStats, String outputPath) throws Exception {

        List<String> folders = new ArrayList<>();
        folders.add("FA");
        folders.add("GR");
        folders.add("GU");
        folders.add("QS");
        folders.add("RL");
        folders.add("RP");
        folders.add("SS");
        folders.add("WX");

        String baselinePathPortfolioStats = baselinePathStats + "/Portfolio/";
        String actualPathPortfolioStats = actualPathStats + "/Portfolio/";
        String outPathStats = String.format(outputPath, "Stats_Portfolio_Results");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathPortfolioStats + folder) && Utils.isDirExists(actualPathPortfolioStats + folder) ) {
                    List<Map<String, String>> baselineData = Utils.readCSV(baselinePathPortfolioStats + folder);
                    List<Map<String, String>> actualData = Utils.readCSV(actualPathPortfolioStats + folder);
                    if (baselineData != null && actualData != null) {
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        rows.add(validationResult.resultRow);
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

            Map<String, String> baselineRow = baselineData.get(0);
            Map<String, String> actualRow = actualData.get(0);

            List<String> row = new ArrayList<>();
            Boolean isAllPass = true;

            String baselineAAL = baselineRow.get("AAL");
            String baselineStd = baselineRow.get("Std");
            String baselineCV = baselineRow.get("CV");

            String actualAAL = actualRow.get("AAL");
            String actualStd = actualRow.get("Std");
            String actualCV = actualRow.get("CV");

            // Baseline
            row.add(folder);
            row.add(baselineAAL);
            row.add(baselineStd);
            row.add(baselineCV);

            // Two empty cells between Baseline and Actual
            row.add("");
            row.add("");

            // Actual
            row.add(folder);
            row.add(actualAAL);
            row.add(actualStd);
            row.add(actualCV);

            // Two empty cells between Actual and Results
            row.add("");
            row.add("");

            // Actual
            row.add(folder);

            List<String> AALRows = checkDiff(baselineAAL, actualAAL, "AAL", folder);
            List<String> StdRows = checkDiff(baselineAAL, actualAAL, "Std", folder);
            List<String> CVRows = checkDiff(baselineAAL, actualAAL, "CV", folder);

            row.addAll(AALRows);
            row.addAll(StdRows);
            row.addAll(CVRows);

            if (AALRows.get(1).equals("Fail") || StdRows.get(1).equals("Fail") || CVRows.get(1).equals("Fail"))  {
                isAllPass = false;
            }

            ValidationResult validationResult = new ValidationResult();
            validationResult.resultRow = row;
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

        // Two empty cells between Baseline and Actual
        sectionNames.add("");
        sectionNames.add("");

        // Actual
        sectionNames.add("Actual Data");
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

        // Baseline
        headers.add("perspcode");
        headers.add("AAL");
        headers.add("STD");
        headers.add("CV");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("Pure Premium");
        headers.add("Standard Deviation");
        headers.add("Coefficient of Variation");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Result
        headers.add("perspcode");
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

        FileOutputStream fileOut = new FileOutputStream(filePath);
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
