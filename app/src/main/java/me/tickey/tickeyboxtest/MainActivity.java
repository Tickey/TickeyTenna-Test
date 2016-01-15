package me.tickey.tickeyboxtest;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.BeaconConsumer;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity
        implements BeaconConsumer {

    private static final int REQUEST_LOCATION = 1;
    private BeaconScanner mBeaconScanner;
    private TextView mTextView;
    private FloatingActionButton mFab;
    private Handler mHandler;
    private Runnable mStopScanRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(android.R.id.text1);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanForBeacons();
            }
        });

        checkRequirements();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkRequirements();
    }

    private void checkRequirements() {
        Boolean requirements = HardwareManager.checkRequirements();

        if (requirements == null || !requirements) {
            mFab.hide();
            String label;
            if (requirements == null) {
                label = "Your device is incompatible";
            } else {
                label = "TURN ON: Bluetooth, Network";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    label += "\nand Location Services";
                }
            }
            mTextView.setText(label);
        }
    }

    private void scanForBeacons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);
                return;
            }
        }

        mBeaconScanner = new BeaconScanner(this) {
            @Override
            public void openTurnstyle(int cityId, int poiId) {
                Type responseType = new TypeToken<ServerResponse<Boolean>>() {
                }.getType();

                ServerApi.Url url = ServerApi.Url.OPEN_FAREGATE;

                url.format(cityId);

                GsonRequest<ServerResponse<Boolean>> request = new GsonRequest<>(
                        url, responseType, getApplicationContext(),
                        paymentSuccessListener(), paymentErrorListener());

                request.setPriority(Request.Priority.IMMEDIATE);
                request.setShouldCache(false);

                BaseApplication.getInstance().getRequestQueue().add(request);
            }
        };
        mBeaconScanner.scan();
        mFab.hide();

        mTextView.setText("Searching TICKEY box");

        if (mHandler == null) {
            mHandler = new Handler();
        }

        if (mStopScanRunnable == null) {
            mStopScanRunnable = new Runnable() {

                @Override
                public void run() {
                    mTextView.setText("Not Found\nClick on the button to try again");
                    mBeaconScanner.unbind();
                    mFab.show();
                }
            };
        }
        mHandler.postDelayed(mStopScanRunnable, 5000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanForBeacons();
                } else {
                    mTextView.setText("cant scan for beacons\nwithout access to the location");
                }
                return;
            }
        }
    }

    private Response.Listener<ServerResponse<Boolean>> paymentSuccessListener() {
        return new Response.Listener<ServerResponse<Boolean>>() {
            @Override
            public void onResponse(ServerResponse<Boolean> response) {
                if (response != null && response.result != null && response.result) {
                    onResult("GO");
                } else {
                    onResult("Ooops error");
                }
            }
        };
    }

    private TickeyErrorListener paymentErrorListener() {
        return new TickeyErrorListener(this) {

            @Override
            public void onTickeyErrorResponse(TickeyError error) {
                onResult("Ooops error");
            }
        };
    }

    private void onResult(String message) {
        mTextView.setText(message);
        mFab.show();
        mBeaconScanner.unbind();
        mHandler.removeCallbacks(mStopScanRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBeaconScanner != null) {
            mBeaconScanner.unbind();
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconScanner.onBeaconServiceConnect();
    }
}
