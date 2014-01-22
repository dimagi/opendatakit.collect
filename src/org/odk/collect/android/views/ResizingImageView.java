package org.odk.collect.android.views;

import org.odk.collect.android.preferences.PreferencesActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

public class ResizingImageView extends ImageView {

	private int mMaxWidth;
	private int mMaxHeight;

	public ResizingImageView(Context context) {
		super(context);
	}

	public ResizingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setMaxWidth(int maxWidth) {
		super.setMaxWidth(maxWidth);
		mMaxWidth = maxWidth;
	}

	@Override
	public void setMaxHeight(int maxHeight) {
		super.setMaxHeight(maxHeight);
		mMaxHeight = maxHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		SharedPreferences settings =
				PreferenceManager.getDefaultSharedPreferences(getContext());

		String resizeMethod = settings.getString(PreferencesActivity.KEY_RESIZE, "none");
		
		if(resizeMethod.equals("full")){

			Drawable drawable = getDrawable();
			if (drawable != null) {

				int wMode = MeasureSpec.getMode(widthMeasureSpec);
				int hMode = MeasureSpec.getMode(heightMeasureSpec);

				// Calculate the most appropriate size for the view. Take into
				// account minWidth, minHeight, maxWith, maxHeigh and allowed size
				// for the view.

				int maxWidth = wMode == MeasureSpec.AT_MOST
						? Math.min(MeasureSpec.getSize(widthMeasureSpec), mMaxWidth)
								: mMaxWidth;
						int maxHeight = hMode == MeasureSpec.AT_MOST
								? Math.min(MeasureSpec.getSize(heightMeasureSpec), mMaxHeight)
										: mMaxHeight;

								float dWidth = dipToPixels(getContext(), drawable.getIntrinsicWidth());
								float dHeight = dipToPixels(getContext(), drawable.getIntrinsicHeight());
								float ratio = (dWidth) / dHeight;

								int width = (int) Math.min(Math.max(dWidth, getSuggestedMinimumWidth()), maxWidth);
								int height = (int) (width / ratio);

								height = Math.min(Math.max(height, getSuggestedMinimumHeight()), maxHeight);
								width = (int) (height * ratio);

								if (width > maxWidth) {
									width = maxWidth;
									height = (int) (width / ratio);
								}

								setMeasuredDimension(width, height);
			}
		}else if(resizeMethod.equals("width")){
			Drawable d = getDrawable();

			if(d!=null){
				// ceil not round - avoid thin vertical gaps along the left/right edges
				int width = MeasureSpec.getSize(widthMeasureSpec);
				int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
				setMeasuredDimension(width, height);
			}
		}
	}

	public static float dipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
}