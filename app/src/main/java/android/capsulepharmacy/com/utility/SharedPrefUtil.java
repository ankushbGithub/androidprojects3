package android.capsulepharmacy.com.utility;

import android.content.Context;
import android.content.SharedPreferences;


//Shared Preference Class for preference file
public final class SharedPrefUtil {
    public static final String KC_PERF = "Party_Planner_perference";
    public static final String DATA_PERF = "Data";

    private SharedPrefUtil(){
        throw new Error("Do n" + "ot need instantiate!");
    }

    public static void setGcmRegistrationID(String key, String defValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defValue);
        editor.commit();
    }

    public static void setStoreID(String key, int value, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getStoreId(String key, int defValue, Context context) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static void setRefreshToken(String key, String defaultValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }

    public static void forgotPasswordToken(String key, String defaultValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }

    public static void setEmail(String key, String defValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defValue);
        editor.commit();
    }

    public static String getGcmRegistrationID(String key, String defValue, Context context){
        return getSharedPreferences(context).getString(key, defValue);
    }

    public static void setOtp(String key, String defaultValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }

    public static void setHubID(String key, String defValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defValue);
        editor.commit();
    }


    public static String getHubID(String key, String defValue, Context context){
        return getSharedPreferences(context).getString(key, defValue);
    }


    public static void setUserId(String key, String defValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defValue);
        editor.commit();
    }


    public static String getUserId(String key, String defValue, Context context){
        return getSharedPreferences(context).getString(key, defValue);
    }


    public static void setGroupAssigned(String key, Boolean defValue, Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, defValue);
        editor.commit();
    }

    public static Boolean getGroupAssigned(String key, Boolean defValue, Context context){
        return getSharedPreferences(context).getBoolean(key, defValue);
    }


    public static String getAccessToken(String key, String defValue, Context context){
        return getSharedPreferences(context).getString(key, defValue);
    }


    public static void setAccessToken(String key, String defaultValue, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }


    public static long getLong(String key, long defValue, Context context) {
        return getSharedPreferences(context).getLong(key, defValue);
    }

    public static String getString(String key, String defValue, Context context) {
        return getSharedPreferences(context).getString(key, defValue);
    }

    public static void putBoolean(String key, boolean value, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static boolean getBoolean(String key, boolean defValue, Context context) {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    public static void putFloat(String key, float value, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getFloat(String key, float defValue, Context context) {
        return getSharedPreferences(context).getFloat(key, defValue);
    }
    public static void putInt(String key, int value, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defValue, Context context) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static void putLong(String key, long value, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putString(String key, String defaultValue, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }
    public static void putFarmerNameString(String key, String defaultValue, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }

    public static void remove(String key, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SharedPrefUtil.KC_PERF, Context.MODE_PRIVATE);
        return pref;
    }
    private static SharedPreferences getFarmerNameSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SharedPrefUtil.DATA_PERF, Context.MODE_PRIVATE);
        return pref;
    }

    public static void clearSharedPreferences(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences(SharedPrefUtil.KC_PERF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    public static void clearFarmerNameSharedPreferences(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences(SharedPrefUtil.DATA_PERF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
