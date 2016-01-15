package me.tickey.tickeyboxtest;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


public class HardwareManager {

    public static Boolean checkRequirements() {
        Boolean result;

        result = isBluetoothEnabled();

        if (result == null) {
            return null;
        } else if (result) {
            result = isNetworkEnabled();

            if (result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = isLocationEnabled(BaseApplication.getInstance().getApplicationContext());
            }
        }

        return result;
    }

    private static Boolean isNetworkEnabled() {
        Boolean result;
        ConnectivityManager cm = (ConnectivityManager) BaseApplication
                .getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static Boolean isBluetoothEnabled() {
        boolean result = true;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return null;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                result = false;
            }
        }
        return result;
    }
}
