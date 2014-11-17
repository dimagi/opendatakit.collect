package org.odk.collect.android.activities

import android.location.LocationListener
import android.os.Bundle
import android.app.Activity
import org.odk.collect.android.listeners.TimerListener
import org.odk.collect.android.application.GeoProgressDialog
import android.location.Location
import android.location.LocationManager
import org.odk.collect.android.utilities.ODKTimer
import org.odk.collect.android.R
import org.odk.collect.android.utilities.GeoUtils
import android.content.Context
import java.util.Set
import android.content.DialogInterface
import scala.collection.JavaConversions._
import android.content.Intent
import android.view.View.OnClickListener
import android.view.View
import java.text.DecimalFormat
import android.location.LocationProvider

class GeoPointActivity extends Activity with LocationListener with TimerListener {
  var mLocationDialog: GeoProgressDialog = null
  var mLocationManager: LocationManager = null
  var mLocation: Location = null
  var mProviders: java.util.Set[String] = null
  var mTimer: ODKTimer = null
  
  val millisToWait: Int = 60000 // allow to accept location after 60 seconds
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setTitle(getString(R.string.app_name) + " > " + getString(R.string.get_location))
    mLocationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
    mProviders = GeoUtils.evaluateProviders(mLocationManager)
    
    setupLocationDialog()

    var mLong: Long = -1
    if (savedInstanceState != null) {
        mLong = savedInstanceState.getLong("millisRemaining",-1)
    }
    if (mLong > 0) {
        mTimer = new ODKTimer(mLong, this)
    }
    else {
        mTimer = new ODKTimer(millisToWait, this)
    }
        
    mTimer.start()
  }
  
  override def onPause() {
    super.onPause()
    
    // Stop the GPS. Note that this will turn off the GPS if the screen goes to sleep.
    mLocationManager.removeUpdates(this)

    // We're not using managed dialogs, so we have to dismiss the dialog to prevent it from leaking memory.
    if (mLocationDialog != null && mLocationDialog.isShowing()) mLocationDialog.dismiss()
  }
  
  override def onResume() {
    super.onResume()
    
    mProviders = GeoUtils.evaluateProviders(mLocationManager)
    
    if (mProviders.isEmpty()) {
      var onCancelListener: DialogInterface.OnCancelListener = new DialogInterface.OnCancelListener() {
        override def onCancel(dialog: DialogInterface) {
            mLocation = null
            GeoPointActivity.this.finish()
        }
      }
      
      var onChangeListener: DialogInterface.OnClickListener = new DialogInterface.OnClickListener() {
        override def onClick(dialog: DialogInterface, i: Int) {
          i match {
            case DialogInterface.BUTTON_POSITIVE => {
              var intent: Intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
              startActivity(intent)
            }
            case DialogInterface.BUTTON_NEGATIVE => {
              mLocation = null
              GeoPointActivity.this.finish()
            }
          }
        }
      }
      
      GeoUtils.showNoGpsDialog(this, onChangeListener, onCancelListener)
    }
    else {
      for (provider <- mProviders) {
        mLocationManager.requestLocationUpdates(provider, 0, 0, this)
      }
      mLocationDialog.show()
    }
  }
  
  def setupLocationDialog() {
    var cancelButtonListener: OnClickListener = new OnClickListener() {
      override def onClick(v: View) {
        mLocation = null
        finish()
      }
    }
    
    var okButtonListener: OnClickListener = new OnClickListener() {
      override def onClick(v: View) {
        returnLocation()
      }
    }
    
    mLocationDialog = new GeoProgressDialog(this, getString(R.string.found_location), getString(R.string.finding_location))

    mLocationDialog.setCancelable(false)
    mLocationDialog.setImage(getResources().getDrawable(R.drawable.green_check_mark))
    mLocationDialog.setMessage(getString(R.string.please_wait_long))
    mLocationDialog.setOKButton(getString(R.string.accept_location), okButtonListener)
    mLocationDialog.setCancelButton(getString(R.string.cancel_location), cancelButtonListener)
  }
  
  def returnLocation() {
    if (mLocation != null) {
      var i: Intent = new Intent()
      i.putExtra(FormEntryActivity.LOCATION_RESULT, GeoUtils.locationToString(mLocation))
      setResult(Activity.RESULT_OK, i)
    }
    finish()
  }
  
  override def onLocationChanged(location: Location) {
    mLocation = location
    if (mLocation != null) {
      mLocationDialog.setMessage(getString(R.string.location_provider_accuracy, mLocation.getProvider(), truncateDouble(mLocation.getAccuracy())))

      // If location is accurate, we're done
      if (mLocation.getAccuracy() <= GeoUtils.GOOD_ACCURACY) {
        returnLocation()
      }

      // If location isn't great but might be acceptable, notify
      // the user and let them decide whether or not to record it
      mLocationDialog.setLocationFound(mLocation.getAccuracy() < GeoUtils.ACCEPTABLE_ACCURACY || mTimer.getMillisUntilFinished() == 0)
    }
  }
  
  def truncateDouble(number: Float): String = {
    var df: DecimalFormat = new DecimalFormat("#.##")
    df.format(number)
  }
  
  override def onProviderDisabled(provider: String) {}

  override def onProviderEnabled(provider: String) {}
            
  override def onStatusChanged(provider: String, status: Int, extras: Bundle) {
    if (status == LocationProvider.AVAILABLE) {
      if (mLocation != null) {
        mLocationDialog.setMessage(getString(R.string.location_accuracy, new Integer(mLocation.getAccuracy().toInt)))
      }
    }
  }
  
  override def notifyTimerFinished() {
    onLocationChanged(mLocation)
  }
  
  override def onSaveInstanceState(savedInstanceState: Bundle) {
    savedInstanceState.putLong("millisRemaining", mTimer.getMillisUntilFinished())
    super.onSaveInstanceState(savedInstanceState)
  }

}