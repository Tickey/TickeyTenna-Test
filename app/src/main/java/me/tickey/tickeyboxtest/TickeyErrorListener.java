package me.tickey.tickeyboxtest;

import android.app.Activity;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class TickeyErrorListener implements ErrorListener {

    private static final String TAG = TickeyErrorListener.class.getSimpleName();

    private Activity mActivity;

    public TickeyErrorListener(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onErrorResponse(VolleyError error) {
        String message = log(error);
        TickeyError tickeyError = new TickeyError(message, error);
        onTickeyErrorResponse(tickeyError);
    }

    public abstract void onTickeyErrorResponse(TickeyError error);

    public static String log(VolleyError error) {
        String json = null;
        String message = null;

        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            json = new String(response.data);
            message = trimMessage(json, ServerResponse.PARAM_MESSAGE);
            if (message != null) {
                displayMessage(message);
                /*
                 * logProperty = trimMessage(json, ServerResponse.PARAM_STATUS);
				 * if (logProperty != null) { displayMessage(logProperty); }
				 */
            } else {
                Log.v(TAG, "message == null");
                Log.v(TAG, json);
            }
        } else {
            Log.v(TAG, "Localized Message: " + error.getLocalizedMessage());
            if (response == null) {
                Log.v(TAG, "response == null");
            } else {
                Log.v(TAG, "response.data == null");
            }
        }

        return message;
    }

    private static String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    // Somewhere that has access to a context
    private static void displayMessage(String message) {
        Log.v(TAG, message);
    }

}
