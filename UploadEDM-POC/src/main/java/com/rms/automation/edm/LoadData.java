package com.rms.automation.edm;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.rms.automation.utils.Utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.*;
import java.util.*;

public class LoadData {

    private static String configPath;
    public static Config config = new Config();

    public static MRIImportData  mriImportData;

    static {
        configPath = System.getenv("configPath");
        try {
            readConfig();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //Gson provide simple toJson() and fromJson() methods to convert Java objects to / from JSON.
    public static void readConfig() throws FileNotFoundException {
        Gson gson = new Gson();
        System.out.println(configPath);
        JsonReader reader = new JsonReader(new FileReader(configPath));
        //Call the Gson. fromJSon(reader, config.getClass()) to convert the given JSON String to object of the class given as the second argument. This method returns a Java object whose fields are populated using values given in JSON String.
        config = gson.fromJson(reader, config.getClass());
        System.out.println(config.toString());
    }



    public static Object[] readTCFromLocalJson() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = null;

        reader = new JsonReader(new FileReader(config.getTcFile()));

        List<TestCase> listOfTC = new ArrayList<>();
        listOfTC = gson.fromJson(reader, listOfTC.getClass()); //getClass() to get the type of the specific object

        Object[] objectsOfTC = new Object[listOfTC.size()];
        for (int i=0; i < listOfTC.size(); i++) {
            objectsOfTC[i] = listOfTC.get(i);
        }

        return objectsOfTC;
    }

    public static Object[] readCreateEDMFromLocalCSV() throws IOException {
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getCreateEdmFile());

        // create csvReader object and skip first Line ,Builder for creating a CSVReader.CSVReaderBuilder extends Object class
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        //Data Source Name	Database Storage	Server Name	Share With
        List<String> headers = Arrays.asList("dataSourceName", "databaseStorage", "serverName", "shareWith");

        //Read the values from CreateEDM CSV file and put the values in map,and return the key and values from map to the Object array.
        Integer headerNum;
        Integer rowNum = 0;
        Object[] objectsOfTC = new Object[rows.size()];
        for (String[] row : rows) {
            headerNum = 0;
            Map<String, String> rowMap = new HashMap<>();
            for (String cell : row) {
                System.out.println(headers.get(headerNum)+" "+cell);
                rowMap.put(headers.get(headerNum), cell);
                headerNum++;
            }
            objectsOfTC[rowNum] = rowMap;
            rowNum++;
        }
        return objectsOfTC;
    }

    public static Map<String, String> readApiEndPointsFromLocal() {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(config.getApiendpointsFile()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> apiendpoints = new HashMap<>();
        return gson.fromJson(reader, apiendpoints.getClass());
    }

    public static List<String> MergeHeaders= Arrays.asList(
            "INDEX","TEST_CASE_NO","TEST_CASE_DESCRIP","USERNAME","PASSWORD","TENANT","IF_TEST_CASE_RUN","EXP_IS_RUN_UPLOAD_IMPORT","EXP_IF_UPLOAD_OR_IMPORT","EXP_IF_CREATE_EDM","EXP_EDM_DATASOURCE_NAME","EXP_EDM_FILE_NAME",
            "EXP_EDM_FILE_PATH","EXP_FILE_EXT","EXP_DB_TYPE","EXP_OPT_EDM_DATABASE_STORAGE","EXP_TAGIDS","EXP_UPLOAD_EDM_JOBID","EXP_OPT_SERVER_NAME","EXP_OPT_SHARE_GROUP","EXP_IS_CREATE_PORTFOLIO","EXP_EXISTING_PORTFOLIO_ID","EXP_PORTFOLIO_NUMBER",
            "EXP_PORTFOLIO_NAME","EXP_ACCNT_FILE_PATH","EXP_ACCNT_FILE_NAME","EXP_LOC_FILE_PATH","EXP_LOC_FILE_NAME","EXP_IF_DEFAULT_MAPPING","EXP_MAPPING_FILE_PATH","EXP_MAPPING_FILE_NAME","EXP_IMPORT_DESCRP","EXP_MRI_IMPORT_JOBID","UPLOAD_EDM_JOB_STATUS","CREATEEDM_JOB_STATUS","MRIIMPORT_JOB_STATUS",
            "GEO_IS_GEOCODE","GEO_GEOCODE_VERSION","GEO_GEOHAZ_VERSION","GEO_GEOHAZ_LAYERS","MPF_IF_CREATE_MODEL_PROFILE","MPF_MFID","MPF_CREATED_NAME","MPF_DESCRIPTION","MPF_PERIL",
            "MPF_LABEL_REGION","MPF_MODEL_REGION","MPF_REGION","MPF_SUB_REGIONS","MPF_VERSION","MPF_SUB_PERILS","MPF_SECONDARY_PERILS","MPF_ANALYSIS_TYPE","MPF_ANALYSIS_MODE",
            "MPF_PET_NAME","MPF_PET_DATA_VERSION","MPF_NUMBER_OF_PERIODS","MPF_EVENT_IDS","MPF_NUMBER_OF_SAMPLES","MPF_GMPE_NAME","MPF_GMPE_CODE","MPF_IGNORE_CONTRACT_DATES","MPF_START_YEAR",
            "MPF_END_YEAR","MPF_REPORTING_WINDOW_START_YEAR","MPF_REPORTING_WINDOW_END_YEAR","MPF_APPLY_PLA","MPF_ALTERNATE_VULN_CODE","MPF_VULNERABILITY_SET_ID","MPF_VULNERABILITY_SET_NAME","MPF_SPECIALTY_MODELS","MPF_INSURANCE_TYPE","MPF_LOCATION_PER_RISK","MPF_POLICY_PER_RISK","MPF_POLICY_COVERAGES","MPF_ENGINE","MPF_VENDOR","MPF_DOWNLOAD_SETTINGS","MPF_EVENT_RATE_SCHEME_ID","MPF_JOB_STATUS",
            "MRN_IF_MODEL_RUN","MRN_AS_OF_DATE_PROCESS","MRN_CURRENCY_CODE","MRN_CURRENCY_SCHEME","MRN_CURRENCY_VINTAGE","MRN_OUTPUT_PROFILE_ID","MRN_TREATIES","MRN_TREATIES_NAME","MRN_ANALYSIS_ID","MRN_BATCH_JOB_ID","MRN_JOB_STATUS","REX_IF_RDM_EXPORT","REX_IF_FILE_EXPORT","REX_RDM_LOCATION",
            "REX_DATA_BRIDGE_TYPE","REX_RDM_NAME","REX_EXPORT_HD_LOSSES_AS","SQLVERSION","EXPORT_FORMAT_RDM","EXPORT_FORMAT_FILE","DATABRIDGESERVER","DOWNLOAD_SETTINGS_FILE","FILE_EXPORT_PATH","RDM_EXPORT_JOBID_ELT","RDM_EXPORT_JOBID_PLT","FILE_EXPORT_JOBID","BASELINE_PATH","ISLOSSVALIDATION","RDM_EXPORT_FILE_LOCATION","IF_IMPR_ANALYSIS_FROM_RDM","IMPR_ANALYSIS_FROM_RDM_FILE_NAME", "IMPR_ANALYSIS_FROM_RDM_FILE_PATH", "IMPR_ANALYSIS_FROM_RDM_FILE_EXT", "IMPR_ANALYSIS_FROM_RDM_DB_TYPE","IMPR_ANALYSIS_FROM_RDM_JOB_ID","RDM_EXPORT_JOB_STATUS_ELT","RDM_EXPORT_JOB_STATUS_PLT","FILE_EXPORT_JOB_STATUS","IMPR_ANALYSIS_JOB_STATUS","CCU_IS_CONVERT_CURRENCY","CCU_CURRENCY","CCU_CURRENCY_SCHEME","CCU_CURRENCY_VERSION",
            "CCU_AS_OF_DATE","CCU_CONVERT_CURRENCY_JOB_ID","CCU_CONVERT_CURRENCY_NEW_ANALYSIS_ID","CCU_CONVERT_CURRENCY_JOB_STATUS",
            "RNM_IS_RENAME_ANALYSIS","RNM_NEW_ANALYSIS_NAME","RNM_RENAME_ANALYSIS_JOBID","RNM_RENAME_ANALYSIS_JOB_STATUS","IS_PATE","IS_PATE_JOB_STATUS","CCG_IS_CLIMATE_CHANGE","CCG_REFERENCE_RATE_SCHEMEID","CCG_CLIMATE_CONDITION_VIEW",
            "CCG_IS_2C_WARMING_SCENARIO","CCG_RCP_SCENARIO","CCG_TIME_HORIZON","CCG_CLIMATE_CHANGE_JOBID","CCG_CLIMATE_CHANGE_JOB_STATUS","CCG_FILE_EXPORT_JOB_ID");

    public static int getColumnIndex(String columnName) {
        return MergeHeaders.indexOf(columnName);
    }

    public static int getColumnIndex_Pate(String columnName) {
        return PateHeaders.indexOf(columnName);
    }

    public static int getColumnIndex_Grouping(String columnName) {
        return GroupingColumns.indexOf(columnName);
    }

    public static Object[] readCaseTCFromLocalExcel() throws Exception {
        System.out.println("Dataloading");
        List<Map<String, String>> dataList = new ArrayList<>();

        try {
            String excelFilePath = config.getSingleExcelFile();
            System.out.println("Excel Fetching main "+excelFilePath);

            // Load Excel file
            FileInputStream file = new FileInputStream(excelFilePath);
            //Workbook workbook = WorkbookFactory.create(file);
            Workbook workbook = new XSSFWorkbook(file);

                // Get the first sheet
                Sheet sheet = workbook.getSheetAt(0);

                // Iterate through rows and cells
                int rowCount = 0;
                for (Row row : sheet) {
                    if (rowCount <= 1) {  // Skip the first two rows
                        rowCount++;
                        continue; // Skip the header row
                    }
                 //   System.out.println("Columns-------------------");
                    Map<String, String> rowData = new HashMap<>();
                    for (int cn = 0; cn < MergeHeaders.size(); cn++) {
                        Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = Utils.getCellValue(cell);
                        String header = MergeHeaders.get(cn);
                     //    System.out.println(header+":"+cn+",");
                        rowData.put(header, cellValue);
                    }
                    dataList.add(rowData);
                    rowCount++;
                }
                workbook.close();
                System.out.println("Excel Loaded");
            }
        catch (Exception e) {
            System.out.println("Error in Loading excel file: "+e.getMessage());
            e.printStackTrace();
        }

        return dataList.toArray();
    }

    public static List<String> PateHeaders =Arrays.asList( "caseNo","index","ifRun","analysisId_pate","operationType","treatyId","updatedJobId","treatyNumber","treatyName","treatyType","occurLimit","attachPt","cedant","effectDate","expireDate","pcntCovered","pcntPlaced","pcntRiShare","pcntRetent","Premium","numOfReinst","reinstCharge","aggregateLimit","aggregateDeductible","priority");

    public static List<Map<String, String>> readCaseTCFromLocalExcel_pate(String caseNo) throws Exception {
        System.out.println("Dataloading");
        List<Map<String, String>> dataList = new ArrayList<>();
        boolean caseFound = false; // Flag to track if the case is found

        try {
            String excelFilePath = config.getSingleExcelFile();
            System.out.println("Excel Fetching "+excelFilePath);

            // Load Excel file
            FileInputStream file = new FileInputStream(excelFilePath);
            //Workbook workbook = WorkbookFactory.create(file);
            Workbook workbook = new XSSFWorkbook(file);
//            WorkbookFactory.addProvider(new HSSFWorkbookFactory());
//            WorkbookFactory.addProvider(new XSSFWorkbookFactory());
//
//            try (InputStream fileInputStream = new FileInputStream(excelFilePath);
//                 BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
//                Workbook workbook = WorkbookFactory.create(bufferedInputStream);
                // Get the first sheet
                Sheet sheet = workbook.getSheetAt(1);

                // Iterate through rows and cells
                int rowCount = 0;

                for (Row row : sheet) {
                    if (rowCount == 0) {
                        rowCount++;
                        continue; // Skip the header row
                    }
                    Map<String, String> rowData = new HashMap<>();
                    String caseNumber = null; // Define caseNumber variable
                    for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                        Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = Utils.getCellValue(cell);
                        String header = PateHeaders.get(cn);
                        // System.out.println("Column name: "+header+" at "+cn);
                        // Check if the current header is "caseNo"
                        if (header.equalsIgnoreCase("caseNo")) {
                            // If the case number matches, set caseNumber variable
                            if (cellValue.equalsIgnoreCase(caseNo)) {
                                caseNumber = cellValue;
                                caseFound = true; // Mark case as found
                            }
                        }
                        if (caseNumber != null) {
                            rowData.put(header, cellValue); // Put rowData only if caseNumber is not null
                        }

                    }
                    // If caseNumber is not null, it means a match is found, add rowData to dataList
                    if (caseNumber != null) {
                        dataList.add(rowData);
                    }
                    rowCount++;
                }
                workbook.close();
                if (!caseFound) {
                    throw new Exception("Case number not found in the input file: " + caseNo);
                } else {
                    System.out.println("Excel Loaded");
                }
            }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return dataList;
    }

    public static Boolean UpdateTCInGroupingExcel(String rowIndex, String columName, String newValue) throws IOException {
        Integer index = 1 + Integer.valueOf(rowIndex);
        System.out.println("index is  = " + index);
        int columnIndex = getColumnIndex_Grouping(columName);

        if (index != null && columnIndex !=-1) {
            try {
                return LoadData.UpdateTCInLocalExcel_column(index, columnIndex, newValue, 2);
            } catch (Exception e) {
                System.out.println("Error on row : " + index + " is " + e.getMessage());
            }
        }
        return null;
    }

    public static Boolean UpdateTCInLocalExcel(String rowIndex, String columName, String newValue) throws IOException {
        Integer index = 1 + Integer.valueOf(rowIndex);
        System.out.println("index is  = " + index);
        int columnIndex = LoadData.getColumnIndex(columName);

        if (index != null && columnIndex !=-1) {
            try {
                return LoadData.UpdateTCInLocalExcel_column(index, columnIndex, newValue, 0);
            } catch (Exception e) {
                System.out.println("Error on row : " + index + " is " + e.getMessage());
            }
        }
        return null;
    }
    public static Boolean UpdateTCInLocalExcel_column(int rowIndex, int columnIndex, String newValue) throws IOException {
        return UpdateTCInLocalExcel_column(rowIndex, columnIndex, newValue, 0);
    }
    public static Boolean UpdateTCInLocalExcel_column(int rowIndex, int columnIndex, String newValue, Integer sheetNo) throws IOException {

        try {
            FileInputStream excelFile = new FileInputStream(config.getSingleExcelFile());
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheet = workbook.getSheetAt(sheetNo); // Assuming you're working with the first sheet

                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex);
                    if (cell == null) {
                        cell = row.createCell(columnIndex);
                    }
                    cell.setCellValue(newValue);

                    FileOutputStream outFile = new FileOutputStream(config.getPateFile());
                    workbook.write(outFile);
                    outFile.close();
                    System.out.println("Excel file has been updated.");
                    return true;
                } else {
                    System.out.println("Invalid row index.");
                    return false;
                }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Boolean UpdateTCInLocalExcel_Pate(int rowIndex, String columnName, String newValue) throws IOException {
        try {
            FileInputStream excelFile = new FileInputStream(config.getPateFile());
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheet = workbook.getSheetAt(1); // Assuming you're working with the first sheet

            int columnIndex_pate = LoadData.getColumnIndex_Pate(columnName);

            if (columnIndex_pate != -1) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex_pate);
                    if (cell == null) {
                        cell = row.createCell(columnIndex_pate);
                    }
                    cell.setCellValue(newValue);

                    FileOutputStream outFile = new FileOutputStream(config.getPateFile());
                    workbook.write(outFile);
                    outFile.close();
                    System.out.println("Excel file has been updated.");
                    return true;
                } else {
                    System.out.println("Invalid row index.");
                    return false;
                }
            } else {
                System.out.println("Column '" + columnName + "' not found.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static List<String> GroupingColumns = Arrays.asList("index","is_grouping","Group_TestCase","CandidateAnalysis_TestCases","name", "description", "numOfSimulations", "propagateDetailedLosses", "simulationWindowStart", "simulationWindowEnd", "reportingWindowStart", "code", "scheme", "vintage", "asOfDate", "regionPerilSimulationSet","jobId_group","analysisId_group","jobstatus_group");
    public static List<Map<String, String>> readCaseTCFromLocalExcel_grouping() throws Exception {
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            String excelFilePath = config.getSingleExcelFile();
            // Load Excel file
            FileInputStream file = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(file);

            // Get the Grouping sheet number 3rd
            Sheet sheet = workbook.getSheetAt(2);

            // Iterate through rows and cells
            int rowCount = 0;
            for (Row row : sheet) {
                if (rowCount <= 1) {  // Skip the first two rows
                    rowCount++;
                    continue; // Skip the header row
                }
                Map<String, String> rowData = new HashMap<>();
                for (int cn = 0; cn < GroupingColumns.size(); cn++) {
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = Utils.getCellValue(cell);
                    String header = GroupingColumns.get(cn);
                    rowData.put(header, cellValue);
                }
                dataList.add(rowData);
                rowCount++;
            }
            workbook.close();
            System.out.println("Grouping Excel Loaded");
        }
        catch (Exception e) {
            System.out.println("Error in Loading Grouping excel file: "+e.getMessage());
            e.printStackTrace();
        }

        return dataList;
    }


}
