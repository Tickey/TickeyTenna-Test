package me.tickey.tickeyboxtest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.tickey.tickeyboxtest.ServerApi.Url;

public class GsonRequest<T> extends Request<T> {

    private static final String TAG = GsonRequest.class.getSimpleName();

    public static final int DEFAULT_RESPONSE_TIME = 30000;
    public static final int DEFAULT_MAX_RETRIES = 0;

    private static final String APPLICATION_JSON = "application/json";

    private static final String TYPE_JSON_ARRAY = "class org.json.JsonArray";

    private static final String TYPE_JSON_OBJECT = "class org.json.JSONObject";

    private Priority mPriority = Priority.LOW;

    private final Gson mGson = new Gson();
    private final Type mType;
    private Map<String, String> mHeaders;
    private Map<String, Object> mMapParams;
    private final Listener<T> mListener;

    public GsonRequest(Url url, Type type, Map<String, Object> params,
                       Context context, Listener<T> listener, ErrorListener errorListener) {
        this(url, type, params, listener, errorListener);
    }

    public GsonRequest(Url url, Type type, Context context,
                       Listener<T> listener, ErrorListener errorListener) {
        this(url, type, listener, errorListener);
    }

    public GsonRequest(Url url, Type type, Activity activity,
                       Listener<T> listener) {
        this(url, type, activity, listener, new TickeyErrorListener(activity) {

            @Override
            public void onTickeyErrorResponse(TickeyError error) {
            }
        });
    }

    public GsonRequest(Url url, Type type, Map<String, Object> params,
                       Activity activity, Listener<T> listener) {
        this(url, type, params, activity, listener, new TickeyErrorListener(
                activity) {

            @Override
            public void onTickeyErrorResponse(TickeyError error) {
            }
        });
    }

    public GsonRequest(Url url, Type type, Map<String, Object> params,
                       Context context, Map<String, String> headers, Listener<T> listener,
                       ErrorListener errorListener) {
        this(url, type, context, headers, listener, errorListener);
        this.mMapParams = params;
    }

    public GsonRequest(Url url, Type type, Context context,
                       Map<String, String> headers, Listener<T> listener,
                       ErrorListener errorListener) {
        this(url, type, listener, errorListener);
        this.mHeaders = headers;
    }

    public GsonRequest(Url url, Type type, Map<String, Object> params,
                       Activity activity, Map<String, String> headers, Listener<T> listener) {
        this(url, type, params, activity, headers, listener,
                new TickeyErrorListener(activity) {

                    @Override
                    public void onTickeyErrorResponse(TickeyError error) {
                    }
                });
    }

    public GsonRequest(Url url, Type type, Activity activity,
                       Map<String, String> headers, Listener<T> listener) {
        this(url, type, activity, headers, listener, new TickeyErrorListener(
                activity) {

            @Override
            public void onTickeyErrorResponse(TickeyError error) {
            }
        });
    }

    public GsonRequest(Url url, Type type, Map<String, Object> params,
                       Listener<T> listener, ErrorListener errorListener) {
        this(url, type, listener, errorListener);
        this.mMapParams = params;
    }

    public GsonRequest(Url url, Type type, Listener<T> listener,
                       ErrorListener errorListener) {
        super(url.method, url.getPath(), errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_RESPONSE_TIME,
                DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.v(TAG, url.getPath());
        this.mType = type;
        this.mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = null;

        if (mMapParams != null) {
            JSONObject json = new JSONObject();

            Iterator<Entry<String, Object>> it = mMapParams.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> pairs = (Map.Entry<String, Object>) it
                        .next();
                try {
                    if (pairs.getValue() instanceof ArrayList<?>) {
                        ArrayList<?> array = (ArrayList<?>) pairs.getValue();
                        json.put(pairs.getKey(), new JSONArray(array));
                    } else {
                        json.put(pairs.getKey(), pairs.getValue());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            if (json != null) {
                Log.v(TAG, "json: " + json.toString());
                body = json.toString().getBytes();
            }
        } else {
            Log.v(TAG, "no params presented");
        }

        return body != null ? body : super.getBody();
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return APPLICATION_JSON;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            Log.v(TAG, "response: " + json);

            Object resultObject;

            if (mType.toString().equalsIgnoreCase(TYPE_JSON_OBJECT)) {
                resultObject = new JSONObject(json);
            } else if (mType.toString().equalsIgnoreCase(TYPE_JSON_ARRAY)) {
                resultObject = new JSONArray(json);
            } else {
                resultObject = mGson.fromJson(json, mType);
            }

            return Response.success((T) resultObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
