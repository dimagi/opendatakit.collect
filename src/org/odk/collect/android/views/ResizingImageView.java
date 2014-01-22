package org.odk.collect.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;


public class ResizingImageView extends ImageView {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
   	
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height2 = MeasureSpec.getSize(heightMeasureSpec);
        
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        
        System.out.println("1515  wms: "  + MeasureSpec.toString(widthMeasureSpec));
        System.out.println("1515  hms: " + MeasureSpec.toString(heightMeasureSpec));
        
        boolean resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
        boolean resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
        
        System.out.println("1515 resizing width : " + resizeWidth);
        System.out.println("1515 resizing height : " + resizeHeight);
    	
        System.out.println("1515 resizing width : " + width + " , height: " + height2);
        
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
    public void resizeMaxMin(int min, int max){
    	
    	System.out.println("1515 resizing min : " + min + " , max: " + max);
    	
    	if(min < 0 || max < 0) return;
    	
    	int currentHeight = this.getMeasuredHeight();
    	if(currentHeight < min){
    		int multiplier = min/currentHeight;
    		setMeasuredDimension(currentHeight * multiplier, this.getMeasuredWidth()*multiplier);
    	}
    	if(currentHeight > max){
    		int multiplier = max/currentHeight;
    		setMeasuredDimension(currentHeight * multiplier, this.getMeasuredWidth()*multiplier);
    	}
    }
    
/*    
    public void onMeasure2(int widthMeasureSpec, int heightMeasureSpec) {
    	
        float screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        
        float screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        
        float imageWidth = getDrawable().getIntrinsicWidth();
        		
        float imageHeight = getDrawable().getIntrinsicHeight();
        
        System.out.println("1514 SCREEN height: " + screenHeight + ", width: " + screenWidth);
        System.out.println("1514 IMAGE height: " + imageHeight + ", width: " + imageWidth);
        
        if(screenHeight == 0 || screenWidth == 0){
        	setMeasuredDimension((int)imageWidth, (int)imageHeight);
        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        	return;
        }
        
        float widthMultiplier = imageWidth/screenWidth;
        
        float heightMultiplier = imageHeight/screenHeight;

        
        System.out.println("1514 multiplier height: " + heightMultiplier + ", width: " + widthMultiplier);
        
        //image larger than screen
        if(widthMultiplier > 1 && heightMultiplier > 1){
        	if(widthMultiplier > heightMultiplier){
        		float width = imageWidth/widthMultiplier;
        		float height = imageHeight/widthMultiplier;
        		System.out.println("1515 multiplier height: " + height + ", width: " + width);
        		setMeasuredDimension((int)width, (int)height);
        	}else{
        		float width = imageWidth/heightMultiplier;
        		float height = imageHeight/heightMultiplier;
        		System.out.println("1515 multiplier height: " + height + ", width: " + width);
        		setMeasuredDimension((int)width, (int)height);
        	}
        	return;
        }
        
        // screen larger than image
        if(widthMultiplier < 1 && heightMultiplier < 1){
        	if(widthMultiplier > heightMultiplier){
        		float width = imageWidth/widthMultiplier;
        		float height = imageHeight/widthMultiplier;
        		System.out.println("1515 multiplier height: " + height + ", width: " + width);
        		setMeasuredDimension((int)width, (int)height);
        	}else{
        		float width = imageWidth/heightMultiplier;
        		float height = imageHeight/heightMultiplier;
        		System.out.println("1515 multiplier height: " + height + ", width: " + width);
        		setMeasuredDimension((int)width, (int)height);
        	}
        	return;
        }
        
        if(widthMultiplier > 1){
    		float width = imageWidth/widthMultiplier;
    		float height = imageHeight/widthMultiplier;
    		System.out.println("1515 multiplier height: " + height + ", width: " + width);
    		setMeasuredDimension((int)width, (int)height);
        }
        else{
    		float width = imageWidth/heightMultiplier;
    		float height = imageHeight/heightMultiplier;
    		System.out.println("1515 multiplier height: " + height + ", width: " + width);
    		setMeasuredDimension((int)width, (int)height);
    	}
    }
*/
}
