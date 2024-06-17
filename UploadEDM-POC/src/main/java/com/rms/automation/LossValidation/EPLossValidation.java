package com.rms.automation.LossValidation;

import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.util.stream.Stream;

public class EPLossValidation {
    public static void EPLossValidation(Map<String, String> tc) throws Exception {

        String zipFilePath = FileExportTests.localPath; // Path to your zip file
       // String destDir = tc.get("FILE_EXPORT_PATH"); // Destination directory to extract to

        try {
            Utils.unzip(zipFilePath);
            System.out.println("EPLossValidation Unzip complete");
        } catch (IOException e) {
            System.out.println("EPLossValidation Actual Results Unzip Failed");
            e.printStackTrace();
        }

        ///Folders to read from Portfolio
        List<String> folders = new ArrayList<>();
        folders.add("FA");
        folders.add("GR");
        folders.add("GU");
        folders.add("RL");

        String baselinePathPortfolio = tc.get("BASELINE_PATH");

        String actualPathPortfolio = zipFilePath.replace(".zip", "") + File.separator + "EP" +File.separator+"Portfolio"+File.separator;

        String outputPath = tc.get("EP_LOSSVALIDATION_PATH");

        try {
            List<List<String>> rows = new ArrayList<>();
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathPortfolio + folder) && Utils.isDirExists(actualPathPortfolio + folder) ) {
                    List<Map<String, String>> baselineData = readCSV(baselinePathPortfolio + folder);
                    List<Map<String, String>> actualData = readCSV(actualPathPortfolio + folder);
                    if (baselineData != null && actualData != null) {
                        List<List<String>> compareData = compareData(baselineData, actualData, folder);
                        rows.addAll(compareData);
                    }
                }
            }

            writeResultsToExcel(rows, outputPath);
            System.out.println("Comparison completed and results written to Excel.");
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

    private static List<List<String>> compareData(List<Map<String, String>> baselineData, List<Map<String, String>> actualData,String folder) {
        try {
            List<List<String>> results = new ArrayList<>();
            List<String> sectionNames = new ArrayList<>();

            for (Map<String, String> baselineRow : baselineData)
            {
                for (Map<String, String> actualRow : actualData) {

                    String baselineMT = baselineRow.get("EPType");
                    String baselineRP = baselineRow.get("ReturnPeriod");
                    String baselineMTRP = baselineMT+"-"+baselineRP;

                    String actualMT = actualRow.get("EPType");
                    String actualRP = actualRow.get("ReturnPeriod");
                    String actualMTRP = actualMT+"-"+actualRP;

                    boolean isMTRPMatches = baselineMTRP.equals(actualMTRP);

                    if (isMTRPMatches) {
                        List<String> row = new ArrayList<>();

                        String baselineLoss = baselineRow.get("Loss");
                        String actualLoss = actualRow.get("Loss");

                        // Baseline
                        row.add("testcasenumber");
                        row.add(folder);
                        row.add(baselineMT);
                        row.add(baselineRP);
                        row.add(baselineLoss);

                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add("testcasenumber");
                        row.add(folder);
                        row.add(actualMT);
                        row.add(actualRP);
                        row.add(actualLoss);

                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualMT);
                        row.add(actualRP);

                        Double baselineLoss_ = null;
                        Double actualLoss_ = null;

                        try {
                            if (baselineLoss != null && !baselineLoss.isEmpty()) {
                                baselineLoss_ = Double.valueOf(baselineLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            System.out.println("Wrong baselineLoss_ at "+baselineMT);
                        }

                        try {
                            if (actualLoss != null && !actualLoss.isEmpty()) {
                                actualLoss_ = Double.valueOf(actualLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            System.out.println("Wrong actualLoss_ at "+actualMT);
                        }

                        Double difference = null;
                        if (baselineLoss_ != null && actualLoss_ != null) {
                            difference = Math.abs(baselineLoss_ - actualLoss_);
                        }

                        if (difference != null && !(difference > 1)) {
                            row.add("Pass");
                        } else {
                            row.add("Fail");
                        }

                        results.add(row);

                    }
                }
            }
            return results;
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
        headers.add("metrictype");
        headers.add("returnperiod");
        headers.add("loss");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("testcasenumber");
        headers.add("perspcode");
        headers.add("metrictype");
        headers.add("returnperiod");
        headers.add("loss");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("metrictype");
        headers.add("returnperiod");
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
