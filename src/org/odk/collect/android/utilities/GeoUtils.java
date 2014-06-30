package org.odk.collect.android.utilities;

import android.location.Location;

public class GeoUtils {
	public static final double ACCEPTABLE_ACCURACY = 5;
	public static final int MAXIMUM_WAIT = 300 * 1000;	// milliseconds to wait for GPS before giving up

	public static String locationToString(Location location) {
		return location.getLatitude() + " " + location.getLongitude() + " " + location.getAltitude() + " " + location.getAccuracy();
	}
}
