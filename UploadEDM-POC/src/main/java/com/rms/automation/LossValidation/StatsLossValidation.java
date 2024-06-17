package com.rms.automation.LossValidation;

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

public class StatsLossValidation {

    public static void StatsLossValidation(Map<String, String> tc) throws Exception {

        List<String> folders = new ArrayList<>();
        folders.add("FA");
        folders.add("GR");
        folders.add("GU");
        folders.add("QS");
        folders.add("RL");
        folders.add("RP");
        folders.add("SS");
        folders.add("WX");
//
//        String baselinePathPortfolio = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/Baselines/STATS/Portfolio/";
//        String actualPathPortfolio = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/ActualResults/CSV/ActualResultsData/STATS/Portfolio/";
//        String outputPath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/Comparison/STATSResults.xlsx";


        String baselinePathPortfolio = tc.get("BASELINE_PATH");
        String actualPathPortfolio = tc.get("ACTUALRESULTS_PATH");
        String outputPath = tc.get("STATS_LOSSVALIDATION_PATH");

        try {
            List<List<String>> rows = new ArrayList<>();
            for (String folder: folders) {
                List<Map<String, String>> baselineData = readCSV(baselinePathPortfolio + folder);
                List<Map<String, String>> actualData = readCSV(actualPathPortfolio + folder);

                if (baselineData != null && actualData != null) {
                    List<String> compareData = compareData(baselineData, actualData, folder);
                    rows.add(compareData);
                }
            }

            writeResultsToExcel(rows, outputPath);
            System.out.println("Comparison of Statistics completed and results written to Excel.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> readCSV(String folderPath) throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            Optional<Path> firstCsvFile = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .findFirst();

            if (firstCsvFile.isPresent()) {
                List<Map<String, String>> data = new ArrayList<>();
                BufferedReader br = new BufferedReader(new FileReader(firstCsvFile.get().toFile()));
                String headersLine = br.readLine();
                String[] headerss = headersLine.split(",");
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    Map<String, String> row = new HashMap<>();
                    for (int i = 0; i < headerss.length; i++) {
                        row.put(headerss[i], values[i]);
                    }
                    data.add(row);
                }
                br.close();
                return data;
            } else {
                System.out.println("No CSV files starting with 'csv' found in the folder.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> compareData(List<Map<String, String>> baselineData, List<Map<String, String>> actualData,String folder) {
        try {

            Map<String, String> baselineRow = baselineData.get(0);
            Map<String, String> actualRow = actualData.get(0);

            List<String> row = new ArrayList<>();

            String baselineAAL = baselineRow.get("\"AAL\"");
            String baselineStd = baselineRow.get("\"Std\"");
            String baselineCV = baselineRow.get("\"CV\"");

            String actualAAL = actualRow.get("\"AAL\"");
            String actualStd = actualRow.get("\"Std\"");
            String actualCV = actualRow.get("\"CV\"");

            // Baseline
            row.add("testcasenumber");
            row.add(folder);
            row.add(baselineAAL);
            row.add(baselineStd);
            row.add(baselineCV);

            // Two empty cells between Baseline and Actual
            row.add("");
            row.add("");

            // Actual
            row.add("testcasenumber");
            row.add(folder);
            row.add(actualAAL);
            row.add(actualStd);
            row.add(actualCV);

            // Two empty cells between Actual and Results
            row.add("");
            row.add("");

            // Actual
            row.add(folder);
            row.add(checkDiff(baselineAAL, actualAAL, "AAL", folder));
            row.add(checkDiff(baselineStd, actualStd, "Std", folder));
            row.add(checkDiff(baselineCV, actualCV, "CV", folder));

            return row;
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
        sectionNames.add("Baseline");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");
        sectionNames.add("");

        // Two empty cells between Baseline and Actual
        sectionNames.add("");
        sectionNames.add("");

        // Actual
        sectionNames.add("Actual");
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
        headers.add("testcasenumber");
        headers.add("perspcode");
        headers.add("Pure Premium");
        headers.add("Standard Deviation");
        headers.add("Coefficient of Variation");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("testcasenumber");
        headers.add("perspcode");
        headers.add("Pure Premium");
        headers.add("Standard Deviation");
        headers.add("Coefficient of Variation");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Result
        headers.add("perspcode");
        headers.add("Pure Premium");
        headers.add("Standard Deviation");
        headers.add("Coefficient of Variation");

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

    private  static String checkDiff(String baseline, String actual, String name, String pr) {

        Double baseline_ = Utils.parseToDouble(baseline, name, pr);
        Double actual_ = Utils.parseToDouble(actual, name, pr);

        Double difference = null;
        if (baseline_ != null && actual_ != null) {
            difference = Math.abs(baseline_ - actual_);
        }

        if (difference != null && !(difference > 1)) {
            return "Pass";
        } else {
            return "Fail";
        }

    }


}
