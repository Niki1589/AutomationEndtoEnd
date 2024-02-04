package automation.edm;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import automation.edm.enums.TCFileType;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

        //Read the values from CreateEDM CSV file and put the values in map,and return the key and values from map to the Object array.
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

    public static Object[] readCaseTCFromLocalCSV() throws IOException {
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(config.getSingleCSVFile());

        // create csvReader object and skip first Line ,Builder for creating a CSVReader.CSVReaderBuilder extends Object class
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> rows = csvReader.readAll();
        //Data Source Name	Database Storage	Server Name	Share With
        List<String> headers = Arrays.asList("caseNo","ifRun", "ifUploadImportExpo", "ifCreateEdm", "edmDatasourceName", "edmFileName","edmFilePath", "fileExt", "dbType", "optEdmDatabaseStorage", "optServerName","optShareGroup","portfolioNumber","portfolioName","accntFilePath","accntFileName","locFilePath","locFileName","ifDefaultMapping","mappingFilePath","mappingFileName","fileFormat","importCurrency","importDescrp","sysJobIdEdmUpload","sysJobIdMriImport","ifCreateModelProfile","mfId",
                "peril0","ignoreContractDates0","engine0","alternateVulnCode0","LabelRegion0","numberOfSamples0","petName0","petDataVersion0","numberOfPeriods0","insuranceType0","analysisType0","locationPerRisk0","version0","endYear0","eventRateSchemeId0","policyPerRisk0","description0","modelRegion0","subRegions0","analysisMode0","startYear0","gmpeName0","applyPLA0","gmpeCode0","subPeril0","region0","excludePostalCodes0","fireOnly0","perilOverride0","dynamicAutomobileModeling0","includePluvial0","includeBespokeDefence0","defenceOn0","subPerils0","secondaryPerils0","policyCoverages0","vendor0","run1dOnly0","specialtyModels0","fire0","coverage0","property0","unknownForPrimaryCharacteristics0","scaleExposureValues0",
                "peril1","ignoreContractDates1","engine1","alternateVulnCode1","LabelRegion1","numberOfSamples1","petName1","petDataVersion1","numberOfPeriods1","insuranceType1","analysisType1","locationPerRisk1","version1","endYear1","eventRateSchemeId1","policyPerRisk1","description1","modelRegion1","subRegions1","analysisMode1","startYear1","gmpeName1","applyPLA1","gmpeCode1","subPeril1","region1","excludePostalCodes1","fireOnly1","perilOverride1","dynamicAutomobileModeling1","includePluvial1","includeBespokeDefence1","defenceOn1","subPerils1","secondaryPerils1","policyCoverages1","vendor1","run1dOnly1","specialtyModels1","fire1","coverage1","property1","unknownForPrimaryCharacteristics1","scaleExposureValues1"
        );
        //Read the values from Single CSV file and put the values in map,and return the key and values from map to the Object array.
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
}
