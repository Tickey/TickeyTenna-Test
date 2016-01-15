package me.tickey.tickeyboxtest;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;

public class TickeyBeacons {

    public static final long BACKGROUND_BETWEEN_SAN_PERIOD = 60000;

    public static final long FOREGROUND_BETWEEN_SAN_PERIOD = 0;

    public static final long SAN_PERIOD = 1100;

    public static final String BUS_FOUND_EXTRA = "TICKEY Bus One Door";
    public static final String CONDUCTOR_FOUND_EXTRA = "TICKEY Conductor";
    public static final String UNDERGROUND_FOUND_EXTRA = "TICKEY Faregate";

    public static final String EXTRA_VECHICLE_ID = "vechicleId";

    public static final Region REGION_FAREGATE = new Region(UNDERGROUND_FOUND_EXTRA,
            Identifier.parse("ac2c6632-0a98-11e5-a6c0-1697f925ec7b"), null,
            null);
    public static final Region REGION_CONDUCTOR = new Region(
            CONDUCTOR_FOUND_EXTRA,
            Identifier.parse("a32773da-fd5d-11e4-a322-1697f925ec7b"), null,
            null);
    public static final Region REGION_BUS_ONEDOOR = new Region(
            BUS_FOUND_EXTRA,
            Identifier.parse("b5e343ee-0a98-11e5-a6c0-1697f925ec7b"), null,
            null);
    public static final Region REGION_BUS_ALLDOORS = new Region(
            "Tickey Bus All Doors",
            Identifier.parse("9b2f83f0-0a98-11e5-a6c0-1697f925ec7b"), null,
            null);/*
    public static final Region REGION_BUS_DRIVER = new Region(
			"Tickey Bus Driver",
			Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"), null,
			null);*/

    public static final String BEACONS_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String BEACONS_LAYOUT_CONDUCTOR = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    public static ArrayList<Region> REGIONS = new ArrayList<Region>() {
        /**
         *
         */
        private static final long serialVersionUID = 3291372319074234568L;

        {
            add(REGION_FAREGATE);
            add(REGION_CONDUCTOR);
            add(REGION_BUS_ONEDOOR);
            add(REGION_BUS_ALLDOORS);
            //add(REGION_BUS_DRIVER);
        }
    };
}
