package com.rms.automation.LossValidation.ep.stats_losses;

import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class STATSPortfolioLossValidationEP {

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
        String outPathStats = String.format(outputPath, "Stats_Portfolio_Results_EP");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathPortfolioStats + folder) && Utils.isDirExists(actualPathPortfolioStats + folder) ) {
                    List<Map<String, String>> baselineData = Utils.readParquet(baselinePathPortfolioStats + folder);
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

            Double ALLDiff = Utils.checkDiff(baselineAAL, actualAAL, "AAL", folder);
            Double STDDiff = Utils.checkDiff(baselineAAL, actualAAL, "Std", folder);
            Double CVDiff = Utils.checkDiff(baselineAAL, actualAAL, "CV", folder);

            if (ALLDiff != null) {
                row.add(ALLDiff+"");
            } else {
                row.add("");
            }
            if (STDDiff != null) {
                row.add(STDDiff+"");
            } else {
                row.add("");
            }
            if (CVDiff != null) {
                row.add(CVDiff+"");
            } else {
                row.add("");
            }

            if (ALLDiff != null && !(ALLDiff <=1 )) {
                row.add("Pass");
            } else {
                row.add("Fail");
                isAllPass = false;
            }
            if (STDDiff != null && !(STDDiff <= 1)) {
                row.add("Pass");
            } else {
                row.add("Fail");
                isAllPass = false;
            }
            if (CVDiff != null && !(CVDiff <= 1)) {
                row.add("Pass");
            } else {
                row.add("Fail");
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

    private static void writeResultsToExcel(List<List<String>> rows, String filStatsath) throws IOException, IOException {
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


        //EventId, Mean, Stddev,CV
        // Baseline
        headers.add("perspcode");
        headers.add("AAL");
        headers.add("Std");
        headers.add("CV");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual

        headers.add("perspcode");
        headers.add("AAL");
        headers.add("StdDev");
        headers.add("CV");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Result
        headers.add("perspcode");
        headers.add("AAL-Diff");
        headers.add("STD-Diff");
        headers.add("CV-Diff");
        headers.add("AAL");
        headers.add("STD");
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

}
