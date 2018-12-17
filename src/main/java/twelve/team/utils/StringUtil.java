package twelve.team.utils;

public class StringUtil {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNullOrWhitespace(String... args) {
        for (String s : args) {
            if (isNullOrWhitespace(s)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNullOrWhitespace(String s) {
        return s == null || s.equals("") || isWhitespace(s);
    }

    private static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
