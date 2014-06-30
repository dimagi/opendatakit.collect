package org.odk.collect.android.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.location.LocationManager;

public class GeoUtils {
	public static final double ACCEPTABLE_ACCURACY = 5;
	public static final int MAXIMUM_WAIT = 300 * 1000;	// milliseconds to wait for GPS before giving up

	public static String locationToString(Location location) {
		return location.getLatitude() + " " + location.getLongitude() + " " + location.getAltitude() + " " + location.getAccuracy();
	}
	
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
