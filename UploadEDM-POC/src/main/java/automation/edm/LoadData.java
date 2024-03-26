package automation.edm;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import automation.edm.enums.TCFileType;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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

    public static Map<String, ProfileTemplate> readProfileTemplateJsonFile() throws FileNotFoundException {
        Gson gson = new Gson();
        System.out.println("ProfileTemplateJsonFile Path: "+LoadData.config.getProfileTemplateJsonFile());
        JsonReader reader = new JsonReader(new FileReader(LoadData.config.getProfileTemplateJsonFile()));

        Type type = new TypeToken<Map<String, ProfileTemplate>>(){}.getType();
        Map<String, ProfileTemplate> profileTemplateMap = gson.fromJson(reader, type);


        return profileTemplateMap;
    }

    //Reading the test case data from environment variables
    public static Object[] readTCFromLocal() throws IOException {
        if (config.getTcFileType().equals(TCFileType.json)) {
            return readTCFromLocalJson();
        } else {
            return readTCFromLocalCSV();
        }
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

    public static Object[] readTCFromLocalCSV() throws IOException {
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getTcFile());

        // create csvReader object and skip first Line
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        List<String> headers = Arrays.asList("filename", "filepath", "fileExt", "dbType");

        Integer headerNum = 0;
        Integer rowNum = 0;
        Object[] objectsOfTC = new Object[rows.size()];
        for (String[] row : rows) {
            headerNum = 0;
            Map<String, String> rowMap = new HashMap<>();
            for (String cell : row) {
                rowMap.put(headers.get(headerNum), cell);
                headerNum++;
            }
            objectsOfTC[rowNum] = rowMap;
            rowNum++;
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

    public static Object[] readMriImportFromLocalCSV() throws IOException {
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getMriImportFile());

        // create csvReader object and skip first Line ,Builder for creating a CSVReader.CSVReaderBuilder extends Object class
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        //Data Source Name	Database Storage	Server Name	Share With
        List<String> headers = Arrays.asList("dataSourceName", "databaseStorage", "serverName","portfolioNumber", "portfolioName", "accountFilePath", "accountFileName", "locationFilePath", "locationFileName", "useDefaultMapping", "mapFilePath", "mapFileName", "delimiter", "currency", "description");

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

    public static Object[] readModelProfileTCFromLocalCSV() throws IOException {
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getSingleCSVFile());

        // create csvReader object and skip first Line ,Builder for creating a CSVReader.CSVReaderBuilder extends Object class
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        //Data Source Name	Database Storage	Server Name	Share With
        List<String> headers = Arrays.asList("peril", "ignoreContractDates", "engine", "alternateVulnCode", "LabelRegion", "numberOfSamples", "petName", "petDataVersion", "numberOfPeriods", "insuranceType", "analysisType", "locationPerRisk", "version", "endYear", "eventRateSchemeId", "policyPerRisk", "description", "modelRegion", "subRegions", "analysisMode", "startYear", "gmpeName", "applyPLA", "gmpeCode", "subPeril", "region", "excludePostalCodes", "fireOnly", "perilOverride", "dynamicAutomobileModeling", "includePluvial", "includeBespokeDefence", "defenceOn", "subPerils", "secondaryPerils", "policyCoverages", "vendor", "run1dOnly", "specialtyModels","unknownForPrimaryCharacteristics","scaleExposureValues","fire","coverage","property");

        //Read the values from Single Input CSV file and put the values in map,and return the key and values from map to the Object array.
        Integer headerNum;
        Integer rowNum = 0;
        Object[] objectsOfTC = new Object[headers.size()];
        for (String[] row : rows) {
            Map<String, String> rowMap = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                String v = "";
                try {
                    if (row[i] != null) {
                        v = row[i];
                    }
                } catch (Exception ex) {
                    System.out.println("Error in field = "+headers.get(i));
                }
                System.out.println(headers.get(i)+" "+v);
                rowMap.put(headers.get(i), v);
            }

            objectsOfTC[rowNum] = rowMap;
            rowNum++;
        }
        System.out.println("CSV Loaded");
        return objectsOfTC;
    }

    public static List<String> MergeHeaders = Arrays.asList("index","caseNo","ifRun", "ifUploadImportExpo", "ifCreateEdm", "edmDatasourceName", "edmFileName","edmFilePath", "fileExt", "dbType", "optEdmDatabaseStorage", "optServerName","optShareGroup","isCreatePortfolio","existingPortfolioId","portfolioNumber","portfolioName","accntFilePath","accntFileName","locFilePath","locFileName","ifDefaultMapping","mappingFilePath","mappingFileName","fileFormat","importCurrency","importDescrp","sysJobIdEdmUpload","sysJobIdMriImport",
            "isGeoCoded","GeocodeVersion","GeoHazVersion","GeoHazLayers", "ifCreateModelProfile", "mfId","peril","ignoreContractDates","engine","alternateVulnCode","LabelRegion","numberOfSamples","petName","petDataVersion","numberOfPeriods","insuranceType","analysisType","locationPerRisk","version","endYear","eventRateSchemeId","policyPerRisk","description","modelRegion","subRegions","analysisMode","startYear","gmpeName","applyPLA","gmpeCode","subPeril","region","excludePostalCodes","fireOnly","perilOverride","dynamicAutomobileModeling","includePluvial","includeBespokeDefence","defenceOn","subPerils","secondaryPerils","policyCoverages","vendor","run1dOnly","specialtyModels","fire","coverage","property","unknownForPrimaryCharacteristics","scaleExposureValues"
            ,"if_model_run","analysisId");



    public static int getColumnIndex(String columnName) {
        return MergeHeaders.indexOf(columnName);
    }
    public static Object[] readCaseTCFromLocalCSV() throws IOException {
        System.out.println("Dataloading");
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getSingleCSVFile());

        // create csvReader object and skip first Line ,Builder for creating a CSVReader.CSVReaderBuilder extends Object class
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        //Data Source Name	Database Storage	Server Name	Share With


        //Read the values from Single CSV file and put the values in map,and return the key and values from map to the Object array.
        Integer headerNum;
        Integer rowNum = 0;
        Object[] objectsOfTC = new Object[MergeHeaders.size()];
        for (String[] row : rows) {
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("rowNumber", rowNum+"");
            for (int i = 0; i < MergeHeaders.size(); i++) {
                String v = "";
                try {
                    if (row[i] != null) {
                        v = row[i];
                    }
                } catch (Exception ex) {
                    System.out.println("Error in field = "+MergeHeaders.get(i));
                }
                rowMap.put(MergeHeaders.get(i), v);
            }

            objectsOfTC[rowNum] = rowMap;
            rowNum++;
        }
        System.out.println("CSV Loaded");
        return objectsOfTC;
    }

    public static Boolean UpdateTCInLocalCSV(int rowIndex, int columnIndex, String newValue) throws IOException {
        FileReader csvFile = new FileReader(config.getSingleCSVFile());

        try (CSVReader reader = new CSVReaderBuilder(csvFile).build()) {

            List<String[]> data = reader.readAll();

            // Modify the data (for example, update a specific value)
            // Let's say we want to update the value in the second row and third column

            if (rowIndex < data.size() && columnIndex < data.get(rowIndex).length) {
                data.get(rowIndex)[columnIndex] = newValue;
                data.get(rowIndex)[columnIndex-1]="NO";
            } else {
                System.out.println("Invalid row or column index.");
                return false;
            }

            // Write back to the CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(config.getSingleCSVFile()))) {
               writer.writeAll(data);

               // writer.writeNext(new String[]{String.valueOf(data)});
                System.out.println("CSV file has been updated.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


    public static Boolean UpdateTCInLocalCSV_Analysis(int rowIndex, int columnIndex, String newValue) throws IOException {
        FileReader csvFile = new FileReader(config.getSingleCSVFile());

        try (CSVReader reader = new CSVReaderBuilder(csvFile).build()) {

            List<String[]> data = reader.readAll();

            // Modify the data (for example, update a specific value)
            // Let's say we want to update the value in the second row and third column

            if (rowIndex < data.size() && columnIndex < data.get(rowIndex).length) {
                data.get(rowIndex)[columnIndex] = newValue;
                //data.get(rowIndex)[columnIndex-1]="NO";
            } else {
                System.out.println("Invalid row or column index.");
                return false;
            }

            // Write back to the CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(config.getSingleCSVFile()))) {
                writer.writeAll(data);

                // writer.writeNext(new String[]{String.valueOf(data)});
                System.out.println("CSV file has been updated.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }
}
