package org.odk.collect.android.jr.extensions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.javarosa.core.model.Action;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.Recalculate;
import org.javarosa.core.model.data.AnswerDataFactory;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.AbstractTreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.PrototypeFactory;
import org.odk.collect.android.utilities.GeoUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class PollSensorAction extends Action implements LocationListener {
	private static String name = "pollsensor";
	private TreeReference target;
	private Context context;
	
	private static final String ACTION = "org.odk.collection.android.jr.extensions.PollSensorAction";
	
	private LocationManager mLocationManager;
	private FormDef mModel;
	private TreeReference mContextRef;
	
	private class LocationUpdateTimerTask extends TimerTask {
		@Override
		public void run() {
			mLocationManager.removeUpdates(PollSensorAction.this);
		}
	}

	public PollSensorAction() {
		super(name);
	}
	
	public PollSensorAction(TreeReference target, Context c) {
		super(name);
		this.target = target;
		this.context = c;
	}
	
	public void processAction(FormDef model, TreeReference contextRef) {
		mModel = model;
		mContextRef = contextRef;
		
		this.context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				// TODO: DRY up (with GeoPointActivity and GeoPointMapActivity)
				mLocationManager = (LocationManager) PollSensorAction.this.context.getSystemService(Context.LOCATION_SERVICE);
				boolean mGPSOn = false;
				boolean mNetworkOn = false;
				List<String> providers = mLocationManager.getProviders(true);
				for (String provider : providers) {
					if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
						mGPSOn = true;
					}
					if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
						mNetworkOn = true;
					}
				}
		        if (mGPSOn) {
		            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, PollSensorAction.this);            
		        }
		        if (mNetworkOn) {
		            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, PollSensorAction.this);
		        }
		        
		        Timer timeout = new Timer();
		        timeout.schedule(new LocationUpdateTimerTask(), GeoUtils.MAXIMUM_WAIT);
			}
		}, new IntentFilter(ACTION));
		
		Intent i = new Intent(ACTION);
		this.context.sendBroadcast(i);
	}
	
	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		target = (TreeReference)ExtUtil.read(in, TreeReference.class, pf);
	}

	public void writeExternal(DataOutputStream out) throws IOException {
		ExtUtil.write(out, target);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			if (this.target != null) {
				String result = GeoUtils.locationToString(location);
				TreeReference qualifiedReference = mContextRef == null ? target : target.contextualize(mContextRef);
				EvaluationContext context = new EvaluationContext(mModel.getEvaluationContext(), qualifiedReference);
	
				AbstractTreeElement node = context.resolveReference(qualifiedReference);
				if(node == null) { throw new NullPointerException("Target of TreeReference " + qualifiedReference.toString(true) +" could not be resolved!"); }
				int dataType = node.getDataType();
				IAnswerData val = Recalculate.wrapData(result, dataType);
	
				mModel.setValue(val == null ? null: AnswerDataFactory.templateByDataType(dataType).cast(val.uncast()), qualifiedReference);
			}
			
			if (location.getAccuracy() <= GeoUtils.ACCEPTABLE_ACCURACY) {
				mLocationManager.removeUpdates(this);
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
}
