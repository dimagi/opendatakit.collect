package org.odk.collect.android.application;
import org.odk.collect.android.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GeoProgressDialog extends Dialog {
	
	TextView mText;
	ImageView mImage;
	Button mAccept;
	Button mCancel;

    public GeoProgressDialog(Context context) {
        super(context);
        setContentView(R.layout.geo_progress);
        this.mImage = (ImageView)findViewById(R.id.geoImage);
        this.mText = (TextView)findViewById(R.id.geoText);
        this.mAccept=(Button)findViewById(R.id.geoOK);
        this.mCancel=(Button)findViewById(R.id.geoCancel);
    }
    
    public void setMessage(String txt){
    	mText.setText(txt);
    }
    public void setImage(Drawable img){
    	mImage.setImageDrawable(img);
    }
    
    public void setOKButton(String title, View.OnClickListener ocl){
    	mAccept.setText(title);
    	mAccept.setOnClickListener(ocl);
    }
    public void setCancelButton(String title, View.OnClickListener ocl){
    	mCancel.setText(title);
    	mCancel.setOnClickListener(ocl);
    }
    

}