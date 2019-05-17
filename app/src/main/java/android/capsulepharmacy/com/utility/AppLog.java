package android.capsulepharmacy.com.utility;

public class AppLog {
    public static boolean print = true;    //true for printing and false for not

    public AppLog() {
    }

    public static void i(String tag, String message) {
        if (print) {
            android.util.Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (print) {
            android.util.Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
//        if (print) {
        android.util.Log.e(tag, message);
//        }
    }

    public static void v(String tag, String message) {
        if (print) {
            android.util.Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (print) {
            android.util.Log.w(tag, message);
        }
    }
}

