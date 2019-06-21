package android.capsulepharmacy.com;

import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * API client to make HTTP Calls
 */
public class APIClient {

    /**
     * Gets http client.
     *
     * @return the http client
     */
    public static OkHttpClient getHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = httpClient.build();

        return okHttpClient;
    }

    /**
     * Gets request.
     *
     * @param context the context
     * @param url     the url
     * @return the request
     */
    public static Request getRequest(Context context, String url) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .build();
    }
    public static Request getCatRequest(Context context, String url) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .header(CapsuleAPI.ACCEPT, CapsuleAPI.APPLICATION_JSON)
                .url(url)
                .build();
    }
    public static Request getGalleryPostRequest(Context context, String url, RequestBody requestBody) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * Gets kyc request.
     *
     * @param context the context
     * @param url     the url
     * @return the kyc request
     */
    public static Request getKycRequest(Context context, String url) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .url(url)
                .build();
    }

    /**
     * Gets post request.
     *
     * @param context     the context
     * @param url         the url
     * @param requestBody the request body
     * @return the post request
     */
    public static Request getPostRequest(Context context, String url, RequestBody requestBody) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_URL_ENCODED)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .post(requestBody)
                .build();
    }
    public static Request simplePostRequest(Context context, String url, RequestBody requestBody) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.ACCEPT, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .post(requestBody)
                .build();
    }
    public static Request postRequest(Context context, String url, RequestBody requestBody) {
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_URL_ENCODED)
                .header(CapsuleAPI.ACCEPT, CapsuleAPI.APPLICATION_JSON)
                .url(url)
                .post(requestBody)
                .build();
    }
    public static Request getCustomerCreatePostRequest(Context context, String url, RequestBody requestBody) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.ACCEPT, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .post(requestBody)
                .build();
    }
    /**
     * Gets put request.
     *
     * @param context     the context
     * @param url         the url
     * @param requestBody the request body
     * @return the put request
     */
    public static Request getPutRequest(Context context, String url, RequestBody requestBody) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .put(requestBody)
                .build();
    }

    /**
     * Delete request request.
     *
     * @param context the context
     * @param url     the url
     * @return the request
     */
    public static Request deleteRequest(Context context, String url) {
        String authKey = Prefs.getStringPrefs(AppConstants.ACCESS_TOKEN);
        String bearerAuth = "bearer"+" "+authKey;
        return new Request.Builder()
                .header(CapsuleAPI.CONTENT_TYPE, CapsuleAPI.APPLICATION_JSON)
                .header(CapsuleAPI.AUTHORIZATION, bearerAuth)
                .url(url)
                .delete()
                .build();
    }

    /**
     * Description: Converts json params to query string param
     *
     * @param parameters Json Object
     * @return query string param
     */

    public static String buildQS(JSONObject parameters) {
        StringBuffer req = new StringBuffer();
        Iterator<String> iterator = parameters.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String val = parameters.optString(key);
            if (val != null) {
                req.append(key);
                req.append('=');
                req.append(val);
            }
            if (iterator.hasNext()) {
                req.append('&');
            }
        }
        return req.toString();
    }

}
