package me.tickey.tickeyboxtest;

import com.android.volley.VolleyError;

public class TickeyError {

	private String mMessage;
	private VolleyError mError;

	public TickeyError(String message, VolleyError error) {
		mMessage = message;
		mError = error;
	}

	public String getMessage() {
		return mMessage;
	}

	public VolleyError getError() {
		return mError;
	}
}
