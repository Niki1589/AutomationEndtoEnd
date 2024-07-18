package com.rms.automation.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.schema.Type;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Utils {

    public static Boolean isTrue(String fieldValue) {
        if (fieldValue.equalsIgnoreCase("YES")) {
            return true;
        }
        return false;
    }

    public static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    cell.setCellType(CellType.STRING);
                    return cell.getStringCellValue();
                }
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return Boolean.valueOf(cell.getBooleanCellValue()).toString();
            case BLANK:
                return ""; // or return null if you prefer
            default:
                return "";
        }
    }

    public static Map<String, String> getEndDateAndFormat(String rawStartDate) {

        DateTimeFormatter rawStartDateParser = DateTimeFormatter.ofPattern("yyyy-dd-MM");
        DateTimeFormatter startDateParser = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");
        DateTimeFormatter endDateParser = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate date = LocalDate.parse(rawStartDate, rawStartDateParser);
        LocalDate minusOneDay = date.minusDays(1);
        LocalDate newDate = minusOneDay.plusYears(1);

        Map<String, String> mapList= new HashMap<>();
        mapList.put("startDate", date.format(startDateParser));
        mapList.put("endDate", newDate.format(endDateParser));
        return  mapList;
    }

    public static String formatDate(String pattern, String rawDate) {
        if (pattern != null && !pattern.isEmpty() && rawDate != null && !rawDate.isEmpty()) {
            LocalDate date = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return  date.format(DateTimeFormatter.ofPattern(pattern));
        }
        return "";
    }

    public static void downloadFile(String url, String savePath) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Response body is null");
            }

            long contentLength = body.contentLength();
            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(new File(savePath));

            byte[] buffer = new byte[4096];
            long bytesReadTotal = 0;
            int bytesRead;

            long startTime = System.currentTimeMillis();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesReadTotal += bytesRead;

                // Calculate and display the progress
                int progress = (int) ((bytesReadTotal * 100) / contentLength);
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;

                // Print progress every second
                if (elapsedTime > 1000) {
                 //   System.out.println("Downloaded " + bytesReadTotal + " of " + contentLength + " bytes (" + progress + "%)");
                    startTime = currentTime;
                }
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateAnalysisId(String str) {
        String regex = "^[0-9]+$";

        if (str == null) {
            return false;
        }
        if (str.trim().isEmpty()) {
            return false;
        }
        if (!Pattern.matches(regex, str)) {
            return false;
        }
        return true; // The string is valid
    }

    public static Double parseToDouble(String str, String name, String pr) {
        try {
            if (str != null && !str.isEmpty()) {
                return Double.valueOf(str);
            } else {
                throw new Exception("Error");
            }
        } catch (Exception ex) {
            System.out.println("Wrong "+name+" at "+pr);
        }
        return null;
    }

    public static void unzip(String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);
        String destDir = zipFile.getParent(); // Extract to the same directory as the zip file

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zipIn.getNextEntry();

            // Iterate over all entries in the zip file
            while (entry != null) {
                String filePath = destDir + File.separator + entry.getName();

                // Handle directories separately
                if (entry.isDirectory()) {
                    File dir = new File(filePath);
                    dir.mkdirs(); // Create directory if not exists
                } else {
                    // Ensure parent directories exist
                    File parentDir = new File(filePath).getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // Extract file
                    extractFile(zipIn, filePath);
                }

                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }

    }
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public static Boolean isDirExists(String dir) {
        try {
            Path dirPath = Paths.get(dir);
            if (Files.exists(dirPath) && Files.isDirectory(dirPath)) return true;
           // System.out.println("Dir "+dir+" Does not exists");
        } catch (Exception ex) {
            System.out.println("Dir "+dir+" Does not exists");
        }
        return false;
    }


    public static List<Map<String, String>> readCSV(String folderPath) throws IOException {
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
    public static List<Map<String, String>> readParquet(String folderPath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        List<String> listOfFields = new ArrayList<>();

        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".parquet"))
                    .forEach(file -> {
                        Configuration conf = new Configuration();
                        org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(file.toFile().getPath());

                        try (ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), path)
                                .withConf(conf)
                                .build()) {

                            // Read and write rows
                            Group group;
                            while ((group = reader.read()) != null) {

                                if (group != null && listOfFields.size() == 0) {
                                    List<Type> types = group.asGroup().getType().getFields();
                                    for (Type type : types) {
                                        listOfFields.add(type.getName());
                                    }
                                }

                                int colIndex = 0;
                                Map<String, String> row = new HashMap<>();
                                for (String field : listOfFields) {
                                    String value = group.getValueToString(colIndex++, 0);
                                    row.put(field, value);
                                }
                                data.add(row);

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        return data;
    }
//    public static List<Map<String, String>> readParquet(String folderPath) throws IOException {
//        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
//            Optional<Path> firstFile = files
//                    .filter(Files::isRegularFile)
//                    .filter(path -> path.getFileName().toString().endsWith(".parquet"))
//                    .findFirst();
//
//            if (firstFile.isPresent()) {
//
//                Configuration conf = new Configuration();
//                org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(firstFile.get().toFile().getPath());
//
//                List<Map<String, String>> data = new ArrayList<>();
//                List<String> listOfFields = new ArrayList<>();
//
//                try (ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), path)
//                        .withConf(conf)
//                        .build()) {
//
//                    // Read and write rows
//                    Group group;
//                    while ((group = reader.read()) != null) {
//
//                        if (group != null && listOfFields.size() == 0) {
//                            List<Type> types = group.asGroup().getType().getFields();
//                            for (Type type : types) {
//                                listOfFields.add(type.getName());
//                            }
//                        }
//
//                        int colIndex = 0;
//                        Map<String, String> row = new HashMap<>();
//                        for (String field : listOfFields) {
//                            String value = group.getValueToString(colIndex++, 0);
//                            row.put(field, value);
//                        }
//                        data.add(row);
//
//                    }
//
//                }
//
//                return data;
//            }
//        }
//        return null;
//    }

}
