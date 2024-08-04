package com.rms.automation.LossValidation.ep.ep_losses;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EPPortfolioLossValidation_SparkOld {

    private static SparkSession spark = SparkSession.builder()
            .appName("EP Portfolio Loss Validation")
            .master("local[*]") // Use local mode for simplicity
            .getOrCreate();


    public static Boolean runPortfolioLossValidationEP(String baselinePathEP, String actualPathEP, String outputPath) throws Exception {

        String baselinePathEPS =  baselinePathEP + "/Portfolio/";
        String actualPathEPS = actualPathEP + "/Portfolio/";
        String outputPathS = String.format(outputPath, "EP_Portfolio_Results");
        // Folders to read from Portf
        //      Dataset<Row> baselineDataset = SparkSQLClient.readHDBaselineData(sparkSession, baselineQuery);
        //      logInfo("After readHDBaselineData");
        //      //get distinct perspcode from baseline so that we can loop through only that no. of times
        //      //One perspcode can return multiple rows because of multiple eventIds
        //      List<Row> arrayList =  baselineDataset.select("perspcode").distinct().collectAsList();
        //
        //      if (baselineDataset.count() > 0) {
        //        for (int i = 0; i < arrayList.size(); i++) {
        //          //get all the perspcode from baseline and based on that get the actual results through API
        //          System.out.println("Baseline Perspcode - " + arrayList.get(i).getAs("perspcode"));
        //          //read HD AAL results by API
        //          Response response = getHDResultsByAPI(hdTestCase, authToken, resultsType,
        //              analysisId, arrayList.get(i).getAs("perspcode"), jobID);
        //          if (response.getStatusCode() == STATUS_OK) {
        //            //convert json response to spark dataset
        //            String json = response.getBody().asString();
        //            List<String> listAALResult = Collections.singletonList(json);
        //            System.out.println("Response body for Testcase "+hdTestCase.getTestCaseNumber()+" and perspcode "+arrayList.get(i).getAs("perspcode")+" : "+json);
        //            logger.info("Response body for Testcase "+hdTestCase.getTestCaseNumber()+" and perspcode "+arrayList.get(i).getAs("perspcode")+" : "+json);
        //            if( response.getBody().asString().equals("[ ]")){
        //              String msg = "Baseline has records but no losses generated for the given Perspcode :  "+arrayList.get(i).getAs("perspcode");
        //              System.out.println(msg);
        //              logger.error(msg);
        //              statusMap.put("status", false);
        //              statusMap.put("message", msg);
        //              return statusMap;
        //            }
        //            Dataset<Row> parquetFileDF = SparkSQLClient
        //                .convertJsonToSpardkDataset(sparkSession, listAALResult);
        //
        //            //calculate and add cv column to actual dataset. cv=stdev/purepremium
        //            if (hdTestCase.getProfileAnalysisType().equalsIgnoreCase("ep")) {
        //              parquetFileDF =
        //                  parquetFileDF.withColumn(
        //                      "actualCV",
        //                      parquetFileDF.col("totalstddev").divide(parquetFileDF.col("purepremium")));
        //            } else {
        //              //column names in Result api are different in case of scenario,footprint and historical analysis
        //              parquetFileDF = parquetFileDF.withColumn("perspcode",
        //                  functions.lit(arrayList.get(i).getAs("perspcode")))
        //                  .withColumnRenamed("stddev", "totalstddev")
        //                  .withColumnRenamed("cv", "actualCV")
        //                  .withColumnRenamed("meanloss", "purepremium");
        //            }
        //
        //            //loss comparison query
        //            String comparisonQuery = objConfig.getHdAALLossComparisonQuery();
        //            boolean comparisonStatus;
        //            //get baseeventid value from hd_baseline_aal baseline table
        //            List<Row> baseEventIdRow = baselineDataset.select("baseeventid").collectAsList();
        //            int baseEventId = Integer.parseInt(baseEventIdRow.get(i).get(0).toString());
        //            //if baseeventid=0 that means there are NOT multiple events for that perspcode,
        //            // so we can join the datasets only on basis of perspcode
        //            if(baseEventId==0){
        //              System.out.println("just 1 event==");
        //               comparisonStatus = SparkSQLClient
        //                  .compareTwoSparkDatasets(sparkSession, parquetFileDF, baselineDataset, "perspcode",
        //                      "perspcode", comparisonQuery,testStatus,jobID);
        //            } else {
        //              System.out.println("Multiple events===");
        //              //if baseeventid is not 0 that means there are multiple events for that perspcode,
        //              //so we have to join the datasets on basis of perspcode and eventIds
        //              //call method to compare two spark datasets
        //               comparisonStatus = SparkSQLClient
        //                  .compareTwoSparkDatasets(sparkSession, parquetFileDF, baselineDataset,
        //                      "perspcode",
        //                      "perspcode", "eventid", "baseeventid", comparisonQuery,testStatus,jobID);
        //            }
        //            if (comparisonStatus) {
        //              statusMap.put("status", true);
        //              statusMap.put("message", "HD AAL Losses are matching with baseline.");
        //            } else {
        //              statusMap.put("status", false);
        //              statusMap.put("message",
        //                  "HD AAL Losses are not matching with baseline for Perspcode: " + arrayList.get(i)
        //                      .getAs("perspcode") + "=" + testStatus.getErrorMessage());
        //              System.out.println(
        //                  "HD AAL Losses are not matching with baseline for Perspcode: " + arrayList.get(i)
        //                      .getAs("perspcode") + "=" + testStatus.getErrorMessage());
        //              ;
        //              return statusMap;
        //            }
        //          } else {
        //            String msg = "HD AAL result api failed to return data: Response Code: " + response
        //                .getStatusCode() + "Response Message: " + response.jsonPath().get("message") + ". ";
        //            System.out.println("message==="+msg);
        //            logger.error("message==="+msg);
        //            statusMap.put("status", false);
        //            statusMap.put("message", msg);
        //            return statusMap;
        //          }
        //        }
        //      } else {
        //        System.out.println(
        //            "No records found in baseline for testcasenumber:" + hdTestCase.getTestCaseNumber());
        //        statusMap.put("status", false);
        //        statusMap.put("message",
        //            "No records found in baseline for testcasenumber:" + hdTestCase.getTestCaseNumber());
        //      }
        //    }catch (Exception ex){
        //      String msg="Exception in validateHDAALLosses: " + ex;
        //      logger.error(msg);
        //      System.out.println(msg);
        //      statusMap.put("status", false);
        //      statusMap.put("message", msg);
        //    }
        //    return statusMap;
        //  }
        //
        //  private Map<String, Object> validateHDEPLosses( HDTestCase hdTestCase,
        //      String authToken, String resultsType,
        //      int analysisId, int jobID) {
        //    System.out.println("--Comparing HD EP losses--");
        //    SparkSession sparkSession = SparkSQLClient
        //        .getSparkSession(SparkSessionValue.THREADS.getValue(),
        //            SparkSessionValue.APPNAME.getValue(),
        //            SparkSessionValue.EXECUTORMEMORY.getValue(),
        //            SparkSessionValue.DRIVERMEMORY.getValue(),
        //            SparkSessionValue.MAXRESULTSIZE.getValue(),
        //            SparkSessionValue.EXECUTORINSTANCES.getValue(),
        //            SparkSessionValue.EXECUTORCORES.getValue(),
        //            SparkSessionValue.MEMORYFRACTION.getValue());
        //    Map<String, Object> statusMap = new HashMap<>();
        //    statusMap.put("status", true);
        //    statusMap.put("message", "HD EP Losses are matching with baseline.");
        //    String message;
        //    try {
        //      //get distinct perspcode from hd_baseline_ep table
        //      String queryToGetDistinctPerspcode = String
        //          .format("select distinct perspcode from hd_baseline_ep where testcasenumber='%s'",
        //              hdTestCase.getTestCaseNumber());
        //      DBConnectionClient dbConnectionClient = new DBConnectionClient();
        //      Handle handle = dbConnectionClient.getDBIConnection(Enums.DBServerTypes.AUTOMATION);
        //      List<Map<String, Object>> perspcodeListMap = DatabseUtils.getQueryResult(handle,queryToGetDistinctPerspcode);
        //
        //      if (perspcodeListMap.size() > 0) {
        //        //loop through each Perspcode from baseline
        //        for (Map<String, Object> perpcodeMap : perspcodeListMap) {
        //          System.out.println(perpcodeMap.get("perspcode"));
        //
        //          //read HD EP baseline by testcasenumber and Perspcode
        //          String baselineQuery = String
        //              .format(objConfig.getReadHDEPLossesBaselineQuery(), hdTestCase.getTestCaseNumber(),
        //                  perpcodeMap.get("perspcode"));
        //
        //          Dataset<Row> baselineDataset = SparkSQLClient
        //              .readHDBaselineData(sparkSession, baselineQuery);olio
        List<String> folders = List.of("FA", "GR", "GU", "QS", "RL", "RP", "SS", "WX");

        List<List<String>> rows = new ArrayList<>();
        Boolean isAllPass = true;

        for (String folder : folders) {
            String baselinePath = baselinePathEPS + "/Portfolio/" + folder;
            String actualPath = actualPathEPS + "/Portfolio/" + folder;

            Dataset<org.apache.spark.sql.Row> baselineDF = spark.read().format("com.crealytics.spark.excel")
                    .option("dataAddress", "'Sheet1'!A1")
                    .option("useHeader", "true")
                    .option("inferSchema", "true")
                    .load(baselinePath);

            Dataset<org.apache.spark.sql.Row> actualDF = spark.read().format("com.crealytics.spark.excel")
                    .option("dataAddress", "'Sheet1'!A1")
                    .option("useHeader", "true")
                    .option("inferSchema", "true")
                    .load(actualPath);

            // Join the DataFrames
            Dataset<org.apache.spark.sql.Row> joinedDF = baselineDF.join(actualDF,
                    baselineDF.col("EPType").equalTo(actualDF.col("EPType"))
                            .and(baselineDF.col("ReturnPeriod").equalTo(actualDF.col("ReturnPeriod"))),
                    "outer"
            ).select(
                    baselineDF.col("EPType").alias("Baseline_EPType"),
                    baselineDF.col("ReturnPeriod").alias("Baseline_ReturnPeriod"),
                    baselineDF.col("Loss").alias("Baseline_Loss"),
                    actualDF.col("EPType").alias("Actual_EPType"),
                    actualDF.col("ReturnPeriod").alias("Actual_ReturnPeriod"),
                    actualDF.col("Loss").alias("Actual_Loss")
            );

            // Collect results and process
            List<org.apache.spark.sql.Row> resultRows = joinedDF.collectAsList();
            for (org.apache.spark.sql.Row row : resultRows) {
                String baselineLoss = row.getAs("Baseline_Loss");
                String actualLoss = row.getAs("Actual_Loss");

                Double baselineLossValue = baselineLoss != null ? Double.valueOf(baselineLoss) : null;
                Double actualLossValue = actualLoss != null ? Double.valueOf(actualLoss) : null;

                Double difference = (baselineLossValue != null && actualLossValue != null) ?
                        Math.abs(baselineLossValue - actualLossValue) : null;

                String resultStatus = (difference != null && difference <= 1) ? "Pass" : "Fail";

                List<String> resultRow = List.of(
                        folder,
                        row.getAs("Baseline_EPType"),
                        row.getAs("Baseline_ReturnPeriod"),
                        baselineLoss,
                        "",
                        "",
                        row.getAs("Actual_EPType"),
                        row.getAs("Actual_ReturnPeriod"),
                        actualLoss,
                        "",
                        "",
                        row.getAs("Actual_EPType"),
                        row.getAs("Actual_ReturnPeriod"),
                        String.valueOf(difference),
                        resultStatus
                );

                rows.add(resultRow);
            }
        }

        writeResultsToExcel(rows, outputPathS);
        return isAllPass;
    }

    private static void writeResultsToExcel(List<List<String>> rows, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");

        List<String> headers = List.of(
                "Folder", "Baseline EPType", "Baseline ReturnPeriod", "Baseline Loss",
                "", "", "Actual EPType", "Actual ReturnPeriod", "Actual Loss",
                "", "", "EPType", "ReturnPeriod", "Difference", "Status"
        );

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        // Write the data rows
        int rowNum = 1;
        for (List<String> resultRow : rows) {
            Row row = sheet.createRow(rowNum++);
            for (int colNum = 0; colNum < resultRow.size(); colNum++) {
                row.createCell(colNum).setCellValue(resultRow.get(colNum));
            }
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }



}
