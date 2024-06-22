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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class PLTLossValidation {
    public static Boolean PLTLossValidation(String baselinePathPortfolio, String actualPathPortfolio, String outputPath) throws Exception {

        ///Folders to read from Portfolio
        List<String> folders = new ArrayList<>();
        folders.add("FA");
        folders.add("GR");
        folders.add("GU");
        folders.add("QS");
        folders.add("RL");
        folders.add("RP");
        folders.add("SS");
        folders.add("WX");

        String baselinePathPortfolioPLT = String.format(baselinePathPortfolio, "PLT");
        String actualPathPortfolioPLT = String.format(actualPathPortfolio, "PLT");
        String outPathPLT = String.format(outputPath, "PLT_Results");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder: folders) {
                if( Utils.isDirExists(baselinePathPortfolioPLT + folder) && Utils.isDirExists(actualPathPortfolioPLT + folder) ) {
                    System.out.println("baselinePathPortfolioPLT + folder Reading");
                    List<Map<String, String>> baselineData = readCSV(baselinePathPortfolioPLT + folder);
                    List<Map<String, String>> actualData = readCSV(actualPathPortfolioPLT + folder);
                    System.out.println("Reading Done + folder Reading");
                    if (baselineData != null && actualData != null) {
                        System.out.println("Comparing");
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        System.out.println("Done COmparing");
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

            System.out.println("Writing to excel");
            writeResultsToExcel(rows, outPathPLT);
            System.out.println("PLT Comparison completed and results written to Excel.");
            return isAllPass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
                        String key = headerss[i];
                        String value = values[i];
                        key = key.replace("\"", "");
                        value = value.replace("\"", "");
                        row.put(key, value);
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

//    private static ValidationResult compareData(List<Map<String, String>> baselineData, List<Map<String, String>> actualData,String folder) {
//        try {
//            List<List<String>> results = new ArrayList<>();
//            Boolean isAllPass = true;
//
//            for (Map<String, String> baselineRow : baselineData)
//            {
//                for (Map<String, String> actualRow : actualData) {
//
//                    String baselineEventId = baselineRow.get("EventId");
//                    String baselinePeriodId = baselineRow.get("PeriodId");
//                    String baselineEIDPID = baselineEventId+"-"+baselinePeriodId;
//
//                    String actualEID = actualRow.get("EventId");
//                    String actualPID = actualRow.get("PeriodId");
//                    String actualEIDPID = actualEID+"-"+actualPID;
//
//                    boolean isMTRPMatches = baselineEIDPID.equals(actualEIDPID);
//
//                    if (isMTRPMatches) {
//                        System.out.println("Comparing "+baselineEIDPID);
//                        List<String> row = new ArrayList<>();
//
//                        String baselineLoss = baselineRow.get("Loss");
//                        String actualLoss = actualRow.get("Loss");
//
//                        // Baseline
//                        row.add(folder);
//                        row.add(baselineEventId);
//                        row.add(baselinePeriodId);
//                        row.add(baselineLoss);
//
//                        // Two empty cells between Baseline and Actual
//                        row.add("");
//                        row.add("");
//
//                        // Actual
//                        row.add(folder);
//                        row.add(actualEID);
//                        row.add(actualPID);
//                        row.add(actualLoss);
//
//                        // Two empty cells between Actual and Results
//                        row.add("");
//                        row.add("");
//
//                        // Result
//                        row.add(folder);
//                        row.add(actualEID);
//                        row.add(actualPID);
//
//                        Double baselineLoss_ = null;
//                        Double actualLoss_ = null;
//
//                        try {
//                            if (baselineLoss != null && !baselineLoss.isEmpty()) {
//                                baselineLoss_ = Double.valueOf(baselineLoss);
//                            } else {
//                                throw new Exception("Error");
//                            }
//                        } catch (Exception ex) {
//                            System.out.println("Wrong baselineLoss_ at "+baselineEIDPID);
//                        }
//
//                        try {
//                            if (actualLoss != null && !actualLoss.isEmpty()) {
//                                actualLoss_ = Double.valueOf(actualLoss);
//                            } else {
//                                throw new Exception("Error");
//                            }
//                        } catch (Exception ex) {
//                            System.out.println("Wrong actualLoss_ at "+actualEIDPID);
//                        }
//
//                        Double difference = null;
//                        if (baselineLoss_ != null && actualLoss_ != null) {
//                            difference = Math.abs(baselineLoss_ - actualLoss_);
//                        }
//
//                        row.add(difference+"");
//
//                        if (difference != null && !(difference > 1)) {
//                            row.add("Pass");
//                        } else {
//                            isAllPass = false;
//                            row.add("Fail");
//                        }
//
//                        results.add(row);
//
//                    }
//                }
//            }
//            System.out.println("Comparing Done");
//
//            ValidationResult validationResult = new ValidationResult();
//            validationResult.resultRows = results;
//            validationResult.isAllPass = isAllPass;
//            return validationResult;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }

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
        headers.add("periodId");
        headers.add("eventId");
        headers.add("loss");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("periodId");
        headers.add("eventId");
        headers.add("loss");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("periodId");
        headers.add("eventId");
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


    private static ValidationResult compareData(List<Map<String, String>> baselineData, List<Map<String, String>> actualData, String folder) {
        try {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            ForkJoinPool forkJoinPool = new ForkJoinPool(availableProcessors);
            CompareTask compareTask = new CompareTask(baselineData, actualData, folder, 0, baselineData.size());
            ValidationResult validationResult = forkJoinPool.invoke(compareTask);
            forkJoinPool.shutdown();
            return validationResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static class CompareTask extends RecursiveTask<ValidationResult> {
        private static final int THRESHOLD = 100;
        private List<Map<String, String>> baselineData;
        private List<Map<String, String>> actualData;
        private String folder;
        private int start;
        private int end;

        public CompareTask(List<Map<String, String>> baselineData, List<Map<String, String>> actualData, String folder, int start, int end) {
            this.baselineData = baselineData;
            this.actualData = actualData;
            this.folder = folder;
            this.start = start;
            this.end = end;
        }

        @Override
        protected ValidationResult compute() {
            if (end - start <= THRESHOLD) {
                return compareDataSegment(baselineData.subList(start, end), actualData, folder);
            } else {
                int mid = (start + end) / 2;
                CompareTask leftTask = new CompareTask(baselineData, actualData, folder, start, mid);
                CompareTask rightTask = new CompareTask(baselineData, actualData, folder, mid, end);
                invokeAll(leftTask, rightTask);
                ValidationResult leftResult = leftTask.join();
                ValidationResult rightResult = rightTask.join();
                return mergeResults(leftResult, rightResult);
            }
        }

        private ValidationResult compareDataSegment(List<Map<String, String>> baselineSegment, List<Map<String, String>> actualData, String folder) {
            List<List<String>> results = new ArrayList<>();
            Boolean isAllPass = true;

            for (Map<String, String> baselineRow : baselineSegment) {
                for (Map<String, String> actualRow : actualData) {
                    String baselineEventId = baselineRow.get("EventId");
                    String baselinePeriodId = baselineRow.get("PeriodId");
                    String baselineEIDPID = baselineEventId + "-" + baselinePeriodId;

                    String actualEID = actualRow.get("EventId");
                    String actualPID = actualRow.get("PeriodId");
                    String actualEIDPID = actualEID + "-" + actualPID;

                    boolean isMTRPMatches = baselineEIDPID.equals(actualEIDPID);

                    if (isMTRPMatches) {
                        System.out.println("Comparing " + baselineEIDPID);
                        List<String> row = new ArrayList<>();

                        String baselineLoss = baselineRow.get("Loss");
                        String actualLoss = actualRow.get("Loss");

                        // Baseline
                        row.add(folder);
                        row.add(baselineEventId);
                        row.add(baselinePeriodId);
                        row.add(baselineLoss);

                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualEID);
                        row.add(actualPID);
                        row.add(actualLoss);

                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Result
                        row.add(folder);
                        row.add(actualEID);
                        row.add(actualPID);

                        Double baselineLoss_ = null;
                        Double actualLoss_ = null;

                        try {
                            if (baselineLoss != null && !baselineLoss.isEmpty()) {
                                baselineLoss_ = Double.valueOf(baselineLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            System.out.println("Wrong baselineLoss_ at " + baselineEIDPID);
                        }

                        try {
                            if (actualLoss != null && !actualLoss.isEmpty()) {
                                actualLoss_ = Double.valueOf(actualLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            System.out.println("Wrong actualLoss_ at " + actualEIDPID);
                        }

                        Double difference = null;
                        if (baselineLoss_ != null && actualLoss_ != null) {
                            difference = Math.abs(baselineLoss_ - actualLoss_);
                        }

                        row.add(difference + "");

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
            System.out.println("Comparing Done");

            ValidationResult validationResult = new ValidationResult();
            validationResult.resultRows = results;
            validationResult.isAllPass = isAllPass;
            return validationResult;
        }

        private ValidationResult mergeResults(ValidationResult leftResult, ValidationResult rightResult) {
            ValidationResult mergedResult = new ValidationResult();
            mergedResult.resultRows = new ArrayList<>();
            mergedResult.resultRows.addAll(leftResult.resultRows);
            mergedResult.resultRows.addAll(rightResult.resultRows);
            mergedResult.isAllPass = leftResult.isAllPass && rightResult.isAllPass;
            return mergedResult;
        }
    }


}
