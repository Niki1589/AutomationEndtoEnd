package automation.utils;

public class Utils {

    public static Boolean isTrue(String fieldValue) {
        if (fieldValue.equalsIgnoreCase("YES")) {
            return true;
        }
        return false;
    }

}
