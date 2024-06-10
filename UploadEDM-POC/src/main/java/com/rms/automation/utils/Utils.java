package com.rms.automation.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
                    System.out.println("Downloaded " + bytesReadTotal + " of " + contentLength + " bytes (" + progress + "%)");
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

}
