package me.tickey.tickeyboxtest;

import com.android.volley.Request.Method;

import java.util.Locale;

public class ServerApi {

    private static final String HTTPS = "https://";
    private static final String LOCAL_API = "%d";
    private static final String BASE_DNS = ".tickey.me/";
    private static final String BASE = HTTPS + LOCAL_API + BASE_DNS;

    public enum Url {
        OPEN_FAREGATE(BASE + "faregate/demoapp", Method.POST);

        private String path;
        public final int method;
        private String updatedPath = null;

        Url(String path, int method) {
            this.path = path;
            this.method = method;
        }

        public void format(Object... args) {
            updatedPath = String.format(path, args);
        }

        public void format(Locale locale, Object... args) {
            updatedPath = String.format(locale, path, args);
        }

        public String getPath() {
            return updatedPath != null ? updatedPath : path;
        }
    }
}
