package com.rms.automation.utils;

import org.apache.poi.ss.usermodel.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


}
