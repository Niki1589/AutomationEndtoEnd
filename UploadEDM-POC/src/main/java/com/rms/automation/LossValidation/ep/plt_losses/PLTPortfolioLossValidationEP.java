package com.rms.automation.LossValidation.ep.plt_losses;

import com.rms.automation.LossValidation.ValidationResult;
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
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class PLTPortfolioLossValidationEP {

    // Method to convert List<Map<String, String>> to Map<String, Map<String, String>>
    public static Map<String, Map<String, String>> convertListToMap(List<Map<String, String>> list, String keyField) {
        Map<String, Map<String, String>> resultMap = new HashMap<>();
       // System.out.println("Some texts");
        int i = 0;
        for (Map<String, String> map : list) {
            String key = map.get(keyField); // Replace "uniqueKey" with the actual key field
            System.out.println(i+":"+key + ", ");
            i++;
            resultMap.put(key, map);
        }
        return resultMap;
    }
    public static Map<String, Map<String, String>> convertListToMap(List<Map<String, String>> list, List<String> keyField) {
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        //System.out.println("Some texts");
        int i = 0;
        for (Map<String, String> map : list) {
            String key = "";
            for (String k : keyField) {
                if (!key.isEmpty()) {
                    key += "_";
                }
                key += map.get(k); // Replace "uniqueKey" with the actual key field
            }
          //  System.out.println(i+":"+key + ", ");
            i++;
            resultMap.put(key, map);
        }
        return resultMap;
    }

    public static Boolean run(String baselinePathPortfolio, String actualPathPortfolio, String outputPath) throws Exception {

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


        String baselinePathPortfolioPLT = baselinePathPortfolio + "/Portfolio/";
        String actualPathPortfolioPLT = actualPathPortfolio + "/Portfolio/";
        String outPathPLT = String.format(outputPath, "PLT_Portfolio_Results_EP");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            Instant start;
            Instant end = null;
            Duration duration;
            for (String folder : folders) {
                if (Utils.isDirExists(baselinePathPortfolioPLT + folder) && Utils.isDirExists(actualPathPortfolioPLT + folder)) {
                 //   System.out.println("baselinePathPortfolio PLT + folder Reading");

                    // Construct the full path to the CSV file
                    String csvFolderPathBaseline = baselinePathPortfolioPLT + folder + "/";
                    String csvFolderPathActual = actualPathPortfolioPLT + folder;

                    List<String> listOfKeys = new ArrayList<>();
                    listOfKeys.add( "EventId" );
                    listOfKeys.add(  "PeriodId" );

                    start = Instant.now();
                    List<Map<String, String>> baselineData = Utils.readParquet(csvFolderPathBaseline);
                    // Convert List<Map<String, String>> to Map<String, Map<String, String>>
                    Map<String, Map<String, String>> baselineDataMap = convertListToMap(baselineData, listOfKeys);
                    end = Instant.now();
                    duration = Duration.between(start, end);
                    //  System.out.println("baselineDataDur took " + formatDuration(duration));

                    start = Instant.now();
                    List<Map<String, String>> actualData = Utils.readMultiCSV(csvFolderPathActual);

                    // Convert List<Map<String, String>> to Map<String, Map<String, String>>
                    Map<String, Map<String, String>> actualDataMap = convertListToMap(actualData, listOfKeys);

                    end = Instant.now();
                    duration = Duration.between(start, end);
                    //  System.out.println("actual took " + formatDuration(duration));

                   // System.out.println("Reading Done + folder Reading");
                    if (baselineData != null && actualData != null) {
                    //    System.out.println("Comparing");

                        start = Instant.now();
                        ValidationResult validationResult = compareData(baselineDataMap, actualDataMap, folder);
                        end = Instant.now();
                        duration = Duration.between(start, end);
                        //   System.out.println(folder+" compareData took " + formatDuration(duration));

                    //    System.out.println("Comparison Completed for PLT Portfolio");
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

          //  System.out.println("Writing to excel");
            start = Instant.now();
            writeResultsToExcel(rows, outPathPLT);
            duration = Duration.between(start, end);
          //  System.out.println( " Write results to excel took " + formatDuration(duration));

            // System.out.println("PLT Comparison completed for EP Portfolio and results written to Excel.");
            return isAllPass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Map<String, Map<String, String>> readCSV(String folderPath) throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            Optional<Path> firstCsvFile = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .findFirst();

            if (firstCsvFile.isPresent()) {
                Map<String, Map<String, String>> data = new HashMap<>();
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
                    String eid = row.get("EventId");
                    String pid = row.get("PeriodId");
                    data.put(eid + "-" + pid, row);
                }
                br.close();
                return data;
            } else {
              //  System.out.println("No CSV files starting with 'csv' found in the folder.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private static ValidationResult compareData(Map<String, Map<String, String>> baselineData, Map<String, Map<String, String>> actualData,String folder) {
        try {
            List<List<String>> results = new ArrayList<>();
            Boolean isAllPass = true;

            int index = 1;
            for (Map.Entry<String, Map<String, String>> baselineEntry : baselineData.entrySet())
            {
                // System.out.println("On Index : "+(index++));
                Map<String, String> actualRow = actualData.get(baselineEntry.getKey());
                Map<String, String> baselineRow = baselineEntry.getValue();


                String baselineEventId = "";
                String baselinePeriodId = "";

                String actualEID = "";
                String actualPID = "";

                List<String> row = new ArrayList<>();

                //Baselines Values

                if (baselineRow != null) {

                    baselineEventId = baselineRow.getOrDefault("EventId","");
                    baselinePeriodId = baselineRow.getOrDefault("PeriodId","");
                }

                else {
                  //  System.out.println("Baseline row is null for " + baselineEIDPID);
                }

                if(actualRow!=null)
                {

                    actualEID = actualRow.getOrDefault("EventId","");
                    actualPID = actualRow.getOrDefault("PeriodId","");
                }

              //actualEIDPID = baselineEntry.getKey();
                    else {
               // System.out.println("Baseline row is null for " + baselineEIDPID);
            }


                String baselineLoss = baselineRow != null ? baselineRow.getOrDefault("Loss", "") : "";
                String actualLoss = actualRow != null ? actualRow.getOrDefault("Loss", "") : "";

//                String baselineLoss = baselineRow.get("Loss");
//                String actualLoss = actualRow.get("Loss");

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
                   // System.out.println("Wrong baselineLoss_ at "+baselineEIDPID);
                }

                try {
                    if (actualLoss != null && !actualLoss.isEmpty()) {
                        actualLoss_ = Double.valueOf(actualLoss);
                    } else {
                        throw new Exception("Error");
                    }
                } catch (Exception ex) {
                   // System.out.println("Wrong actualLoss_ at "+actualEIDPID);
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
            //   System.out.println("Comparing Done");

            ValidationResult validationResult = new ValidationResult();
            validationResult.resultRows = results;
            validationResult.isAllPass = isAllPass;
            return validationResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

}
