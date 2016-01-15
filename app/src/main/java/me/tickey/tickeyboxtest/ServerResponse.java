package me.tickey.tickeyboxtest;

import com.google.gson.annotations.SerializedName;

public class ServerResponse<T> {

	public static final String PARAM_STATUS = "status";

	public static final String PARAM_RESULT = "result";

	public static final String PARAM_MESSAGE = "message";

	@SerializedName(PARAM_STATUS)
	public int status;

	@SerializedName(PARAM_MESSAGE)
	public String message;

	@SerializedName(PARAM_RESULT)
	public T result;
}
