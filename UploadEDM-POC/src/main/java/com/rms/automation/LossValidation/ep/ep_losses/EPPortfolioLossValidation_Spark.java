package com.rms.automation.LossValidation.ep.ep_losses;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.rms.automation.LossValidation.ValidationResult;
import com.rms.automation.exportApi.Download_Settings;
import com.rms.automation.utils.Utils;

public class EPPortfolioLossValidation_Spark {

    public static Boolean runPortfolioLossValidationEP(String baselinePathEP, String actualPathEP, String outputPath) throws Exception {

        // Initialize Spark
        SparkSession spark = SparkSession.builder()
                .appName("EPPortfolioLossValidation")
                .master("local[*]")
                .getOrCreate();

        /// Folders to read from Portfolio
        List<String> folders = new ArrayList<>();
        folders.add("FA");
        folders.add("GR");
        folders.add("GU");
        folders.add("QS");
        folders.add("RL");
        folders.add("RP");
        folders.add("SS");
        folders.add("WX");

        String baselinePathPortfolioEP = baselinePathEP + "/Portfolio/";
        String actualPathPortfolioEP = actualPathEP + "/Portfolio/";
        String outPathEP = String.format(outputPath, "EP_Portfolio_Results");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        try {
            for (String folder : folders) {
                if (Utils.isDirExists(baselinePathPortfolioEP + folder) && Utils.isDirExists(actualPathPortfolioEP + folder)) {
                    Dataset<Row> baselineData = readParquet(spark, baselinePathPortfolioEP + folder);
                    Dataset<Row> actualData = readCSV(spark, actualPathPortfolioEP + folder);
                    if (baselineData != null && actualData != null) {
                        ValidationResult validationResult = compareData(baselineData, actualData, folder);
                        rows.addAll(validationResult.resultRows);
                        if (!validationResult.isAllPass) isAllPass = false;
                    }
                }
            }

            writeResultsToExcel(rows, outPathEP);
            spark.stop();
            return isAllPass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static ValidationResult compareData(Dataset<Row> baselineData, Dataset<Row> actualData, String folder) {
        try {
            List<List<String>> results = new ArrayList<>();
            Boolean isAllPass = true;

            List<Row> baselineRows = baselineData.collectAsList();
            List<Row> actualRows = actualData.collectAsList();

            for (Row baselineRow : baselineRows) {
                for (Row actualRow : actualRows) {

                    String baselineMT = baselineRow.getAs("EPType");
                    String baselineRP = baselineRow.getAs("ReturnPeriod").toString();
                    String baselineMTRP = baselineMT + "-" + baselineRP;

                    String actualMT = actualRow.getAs("EPType");
                    String actualRP = actualRow.getAs("ReturnPeriod").toString();
                    String actualMTRP = actualMT + "-" + actualRP;

                    boolean isMTRPMatches = baselineMTRP.equals(actualMTRP);

                    if (isMTRPMatches) {
                        List<String> row = new ArrayList<>();

                        String baselineLoss = baselineRow.getAs("Loss") != null ? baselineRow.getAs("Loss").toString() : "0";
                        String actualLoss = actualRow.getAs("Loss") != null ? actualRow.getAs("Loss").toString() : "0";

                        BigDecimal baselineLoss_ = null;
                        BigDecimal actualLoss_ = null;

                        try {
                            if (baselineLoss != null && !baselineLoss.isEmpty()) {
                                baselineLoss_ = new BigDecimal(baselineLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            //System.out.println("Wrong baselineLoss_ at " + baselineMT);
                        }

                        try {
                            if (actualLoss != null && !actualLoss.isEmpty()) {
                                actualLoss_ = new BigDecimal(actualLoss);
                            } else {
                                throw new Exception("Error");
                            }
                        } catch (Exception ex) {
                            //System.out.println("Wrong actualLoss_ at " + actualMT);
                        }

                        // Baseline
                        row.add(folder);
                        row.add(baselineMT);
                        row.add(baselineRP);
                        row.add(baselineLoss_.toPlainString());

                        // Two empty cells between Baseline and Actual
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualMT);
                        row.add(actualRP);
                        row.add(actualLoss_.toPlainString());

                        // Two empty cells between Actual and Results
                        row.add("");
                        row.add("");

                        // Actual
                        row.add(folder);
                        row.add(actualMT);
                        row.add(actualRP);


                        BigDecimal difference = null;
                        if (baselineLoss_ != null && actualLoss_ != null) {
                            difference = baselineLoss_.subtract(actualLoss_).abs();
                        }

                        row.add(difference.toPlainString());

                        if (difference != null && difference.compareTo(new BigDecimal(1)) <= 0) {
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

    private static void writeResultsToExcel(List<List<String>> rows, String filePath) throws IOException {
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
        headers.add("EPType");
        headers.add("returnperiod");
        headers.add("loss");

        // Two empty cells between Baseline and Actual
        headers.add("");
        headers.add("");

        // Actual
        headers.add("perspcode");
        headers.add("EPType");
        headers.add("returnperiod");
        headers.add("loss");

        // Two empty cells between Actual and Results
        headers.add("");
        headers.add("");

        // Results
        headers.add("perspcode");
        headers.add("EPType");
        headers.add("returnperiod");
        headers.add("difference");
        headers.add("loss");

        results.add(sectionNames);
        results.add(headers);

        results.addAll(rows);

        // Write the data rows
        int rowNum = 0;
        for (List<String> resultRow : results) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);

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

    public static Dataset<Row> readCSV(SparkSession spark, String folderPath) {
        return spark.read().option("header", "true").csv(folderPath + "/*.csv");
    }

    public static Dataset<Row> readParquet(SparkSession spark, String folderPath) {
        return spark.read().parquet(folderPath + "/*.parquet");
    }

}
