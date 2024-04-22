package com.rms.automation.utils;

import org.apache.poi.ss.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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

}
