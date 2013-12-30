package org.odk.collect.android.views;

import java.io.File;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * @author William Pride
 * activity for full screening images in FormEntryActivities
 */

public class FullScreenImage extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.full_image);
		Intent intent = getIntent();
		String imageId = intent.getStringExtra("image-file-name");
		ImageView imageView = (ImageView)findViewById(R.id.full_image);

		final File imageFile = new File(imageId);
		Bitmap b = null;

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		b = FileUtils.getBitmapScaledToDisplay(imageFile, height, width);

		imageView.setPadding(10, 10, 10, 10);
		imageView.setAdjustViewBounds(true);
		imageView.setImageBitmap(b);
		imageView.setId(23423534);       

		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	}
}
