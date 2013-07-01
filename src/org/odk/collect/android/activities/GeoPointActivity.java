/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.activities;

import java.text.DecimalFormat;
import java.util.List;

import org.odk.collect.android.R;
import org.odk.collect.android.application.GeoProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GeoPointActivity extends Activity implements LocationListener {
    private GeoProgressDialog mLocationDialog;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean mGPSOn = false;
    private boolean mNetworkOn = false;
    
    private int acceptableThreshold = 1600;

    // default location accuracy
    private static double LOCATION_ACCURACY = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.app_name) + " > " + getString(R.string.get_location));

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        evalProviders();
        
    	setupLocationDialog();

    }


	private void evalProviders() {

        // make sure we have a good location provider before continuing
        List<String> providers = mLocationManager.getProviders(true);
        for (String provider : providers) {
            if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
                mGPSOn = true;
            }
            if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
                mNetworkOn = true;
            }
        }

	}


	private void showNoGpsDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.no_gps_title));
		dialog.setMessage(getString(R.string.no_gps_message));
        DialogInterface.OnClickListener changeSettingsListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON1: //Yes, get settings 
                		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                		startActivity(intent);
                        break;
                    case DialogInterface.BUTTON2: //No, bail
                    	mLocation = null;
                    	GeoPointActivity.this.finish();
                    	break;
                }
            }
        };
        

        DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
            	mLocation = null;
            	GeoPointActivity.this.finish();
			}
        };
        
        dialog.setCancelable(true);
        dialog.setOnCancelListener(onCancelListener);
        dialog.setButton(getString(R.string.change_settings), changeSettingsListener);
        dialog.setButton2(getString(R.string.cancel), changeSettingsListener);

        dialog.show();
	}


	@Override
    protected void onPause() {
        super.onPause();

        // stops the GPS. Note that this will turn off the GPS if the screen goes to sleep.
        mLocationManager.removeUpdates(this);

        // We're not using managed dialogs, so we have to dismiss the dialog to prevent it from
        // leaking memory.
        if (mLocationDialog != null && mLocationDialog.isShowing())
            mLocationDialog.dismiss();
    }


    @Override
    protected void onResume() {
        super.onResume();
        evalProviders();
        if (!mGPSOn && !mNetworkOn) {
            showNoGpsDialog();
        } else {
	        if (mGPSOn) {
	            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);            
	        }
	        if (mNetworkOn) {
	            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	        }
	        mLocationDialog.show();
        }
    }


    /**
     * Sets up the look and actions for the progress dialog while the GPS is searching.
     */
    private void setupLocationDialog() {
        // dialog displayed while fetching gps location
        
        OnClickListener cancelButtonListener =
            new OnClickListener() {
                @Override
    	   		public void onClick(View v){
                    mLocation = null;
                    finish();
    	   		}
        	};
        	
        OnClickListener okButtonListener =
    		new OnClickListener() {
    	   		public void onClick(View v){
                    returnLocation();
    	   		}
       	};
       	
       	mLocationDialog = new GeoProgressDialog(this, getString(R.string.finding_location), getString(R.string.found_location));
       	

        // back button doesn't cancel
        mLocationDialog.setCancelable(false);
        mLocationDialog.setImage(getResources().getDrawable(R.drawable.green_check_mark));
        mLocationDialog.setMessage(getString(R.string.please_wait_long));
        mLocationDialog.setOKButton(getString(R.string.accept_location),
            okButtonListener);
        mLocationDialog.setCancelButton(getString(R.string.cancel_location),
            cancelButtonListener);
    }


    private void returnLocation() {
        if (mLocation != null) {
            Intent i = new Intent();
            i.putExtra(
                FormEntryActivity.LOCATION_RESULT,
                mLocation.getLatitude() + " " + mLocation.getLongitude() + " "
                        + mLocation.getAltitude() + " " + mLocation.getAccuracy());
            setResult(RESULT_OK, i);
        }
        finish();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        if (mLocation != null) {
            mLocationDialog.setMessage(getString(R.string.location_provider_accuracy,
                mLocation.getProvider(), truncateDouble(mLocation.getAccuracy())));

            if (mLocation.getAccuracy() <= LOCATION_ACCURACY) {
                returnLocation();
            }
            
            if(mLocation.getAccuracy() < acceptableThreshold){
            	mLocationDialog.setLocationFound(true);
            } else{
            	mLocationDialog.setLocationFound(false);
            }
        }
    }


    private String truncateDouble(float number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }


    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                if (mLocation != null) {
                    mLocationDialog.setMessage(getString(R.string.location_accuracy,
                        mLocation.getAccuracy()));
                }
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
        }
    }

}
