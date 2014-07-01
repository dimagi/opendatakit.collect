package org.odk.collect.android.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.location.LocationManager;

/**
 * Static functions for dealing with GPS data, specifically Location and LocationManager objects.
 * @author jschweers
 *
 */
public class GeoUtils {
	public static final double ACCEPTABLE_ACCURACY = 5;	// Good enough accuracy to stop pinging the GPS
	public static final int MAXIMUM_WAIT = 300 * 1000;	// For passive collection, milliseconds to wait for GPS before giving up

	/**
	 * Format location in a string for user display.
	 * @param location
	 * @return String in format "<latitude> <longitude> <altitude> <accuracy>"
	 */
	public static String locationToString(Location location) {
		return location.getLatitude() + " " + location.getLongitude() + " " + location.getAltitude() + " " + location.getAccuracy();
	}
	
	/**
	 * Get a LocationManager's providers, and trim the list down to providers we care about: GPS and network.
	 * @param manager
	 * @return Set of String objects that may contain LocationManager.GPS_PROVDER and/or LocationManager.NETWORK_PROVIDER
	 */
	public static Set<String> evaluateProviders(LocationManager manager) {
		HashSet<String> set = new HashSet<String>();
		
		List<String> providers = manager.getProviders(true);
		for (String provider : providers) {
			if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
				set.add(LocationManager.GPS_PROVIDER);
			}
			if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
				set.add(LocationManager.NETWORK_PROVIDER);
			}
		}
				
		return set;
	}
}
