package me.tickey.tickeyboxtest;

/**
 * Created by Nikolay Vasilev on 11/30/2015.
 */

import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

public abstract class BeaconScanner {

    private static final String TAG = BeaconScanner.class.getSimpleName();

    private BeaconManager mBeaconManager;

    private BeaconConsumer mBeaconConsumer;

    public BeaconScanner(BeaconConsumer beaconConsumer) {
        mBeaconConsumer = beaconConsumer;
    }

    public void scan() {
        if (mBeaconManager == null) {
            mBeaconManager = BeaconManager.getInstanceForApplication(BaseApplication.getInstance());

            List<BeaconParser> beaconParsers = mBeaconManager.getBeaconParsers();
            //if (beaconParsers.size() <= 0) {
            beaconParsers.add(new BeaconParser()
                    .setBeaconLayout(TickeyBeacons.BEACONS_LAYOUT_CONDUCTOR));
            //}

            mBeaconManager.setForegroundScanPeriod(150);
        }

        if (!mBeaconManager.isBound(mBeaconConsumer)) {
            Log.v(TAG, "BOUND");
            mBeaconManager.bind(mBeaconConsumer);
        }
    }

    public void unbind() {
        if (mBeaconManager != null && mBeaconManager.isBound(mBeaconConsumer)) {
            Log.v(TAG, "UNBOUND");
            mBeaconManager.unbind(mBeaconConsumer);
        }
    }

    public void onBeaconServiceConnect() {
        Log.v(TAG, "ON BEACON SERVICE CONNECT");
        mBeaconManager.setRangeNotifier(mRangeNotifier);

        try {
            mBeaconManager.startRangingBeaconsInRegion(TickeyBeacons.REGION_FAREGATE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected RangeNotifier mRangeNotifier = new RangeNotifier() {

        private boolean mIsOpened = false;

        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

            if (beacons.size() > 0) {
                Beacon nearestBeacon = beacons.iterator().next();

                if (nearestBeacon != null && !mIsOpened) {
                    mIsOpened = true;
                    mBeaconManager.unbind(mBeaconConsumer);
                    openTurnstyle(nearestBeacon.getId2().toInt(), nearestBeacon.getId3().toInt());
                }

            }
        }
    };

    public abstract void openTurnstyle(int cityId, int posId);
}