
package org.odk.collect.android.views;

import java.io.File;

import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.core.reference.ReferenceManager;
import org.odk.collect.android.R;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.QRCodeEncoder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This layout is used anywhere we can have image/audio/video/text. TODO: It would probably be nice
 * to put this in a layout.xml file of some sort at some point.
 * 
 * @author carlhartung
 */
public class MediaLayout extends RelativeLayout {
    private static final String t = "AVTLayout";

    private TextView mView_Text;
    private AudioButton mAudioButton;
    private ImageButton mVideoButton;
    private ImageView mImageView;
    private TextView mMissingImage;


    public MediaLayout(Context c) {
        super(c);
        mView_Text = null;
        mAudioButton = null;
        mImageView = null;
        mMissingImage = null;
        mVideoButton = null;
    }

    
    public void setAVT(TextView text, String audioURI, String imageURI, final String videoURI, final String bigImageURI) {
    	setAVT(text, audioURI, imageURI, videoURI, bigImageURI, null);
    }

    public void setAVT(TextView text, String audioURI, String imageURI, final String videoURI, final String bigImageURI, final String qrCodeContent) {
    	
        mView_Text = text;
        
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.form_media_layout);
        addView(myLayout);
        
        mAudioButton = (AudioButton) findViewById(R.id.fe_audio_button);
        mImageView = (ImageView) findViewById(R.id.fe_image_view);
        mVideoButton = (ImageButton) findViewById(R.id.fe_image_button);
        mView_Text = (TextView) findViewById(R.id.fe_text_view);


        // First set up the audio button
        if (audioURI != null) {
            // An audio file is specified
        	
        	mAudioButton.setURI(audioURI);
            mAudioButton.setId(3245345); // random ID to be used by the relative layout.
        } else {
            // No audio file specified, so ignore.
        }

        // Then set up the video button
        if (videoURI != null) {
            // An audio file is specified
            //mVideoButton = new ImageButton(getContext());
            mVideoButton.setImageResource(android.R.drawable.ic_media_play);
            mVideoButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String videoFilename = "";
                    try {
                        videoFilename =
                            ReferenceManager._().DeriveReference(videoURI).getLocalURI();
                    } catch (InvalidReferenceException e) {
                        Log.e(t, "Invalid reference exception");
                        e.printStackTrace();
                    }

                    File videoFile = new File(videoFilename);
                    if (!videoFile.exists()) {
                        // We should have a video clip, but the file doesn't exist.
                        String errorMsg =
                            getContext().getString(R.string.file_missing, videoFilename);
                        Log.e(t, errorMsg);
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent i = new Intent("android.intent.action.VIEW");
                    i.setDataAndType(Uri.fromFile(videoFile), "video/*");
                    try {
                        ((Activity) getContext()).startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(),
                            getContext().getString(R.string.activity_not_found, "view video"),
                            Toast.LENGTH_SHORT);
                    }
                }

            });
            mVideoButton.setId(234982340);
        } else {
            // No video file specified, so ignore.
        }

        // Now set up the image view
        String errorMsg = null;
        
        //View imageView= null;
        if(qrCodeContent != null ) {
            Bitmap image;
            Display display =
                    ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay();

            
            //see if we're doing a new QR code display
            if(qrCodeContent != null) {
                int screenWidth = display.getWidth();
                int screenHeight = display.getHeight();
                
                int minimumDim = Math.min(screenWidth,  screenHeight);

            	try {
            		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCodeContent,minimumDim);
                
            		image = qrCodeEncoder.encodeAsBitmap();
            		
            		//mImageView = new ImageView(getContext());
            		mImageView.setPadding(10, 10, 10, 10);
            		mImageView.setAdjustViewBounds(true);
            		mImageView.setImageBitmap(image);
            		mImageView.setId(23423534);

            	} catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        	
    	} else if (imageURI != null) {
            try {

                
                //If we didn't get an image yet, try for a norm

                final String imageFilename = ReferenceManager._().DeriveReference(imageURI).getLocalURI();
                final File imageFile = new File(imageFilename);
                
                if (imageFile.exists()) {
                    Bitmap b = null;
                    try {
                        Display display =
                                ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                                        .getDefaultDisplay();


                        int screenWidth = display.getWidth();
                        int screenHeight = display.getHeight();
                        b =
                            FileUtils
                                    .getBitmapScaledToDisplay(imageFile, screenHeight, screenWidth);
                    } catch (OutOfMemoryError e) {
                        errorMsg = "ERROR: " + e.getMessage();
                    }

                    if (b != null) {
                       // mImageView = new ImageView(getContext());
                        mImageView.setPadding(10, 10, 10, 10);
                        mImageView.setAdjustViewBounds(true);
                        mImageView.setImageBitmap(b);
                        mImageView.setId(23423534);
                        if (bigImageURI != null) {
                            mImageView.setOnClickListener(new OnClickListener() {
                                String bigImageFilename = ReferenceManager._()
                                        .DeriveReference(bigImageURI).getLocalURI();
                                File bigImage = new File(bigImageFilename);


                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent("android.intent.action.VIEW");
                                    i.setDataAndType(Uri.fromFile(bigImage), "image/*");
                                    try {
                                        getContext().startActivity(i);
                                    } catch (ActivityNotFoundException e) {
                                        Toast.makeText(
                                            getContext(),
                                            getContext().getString(R.string.activity_not_found,
                                                "view image"), Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        }
                        else{
                        	/* don't override ODK default behavior, but in else case make image onClick 
                        	/ launch full screen mode.
                        	 * TODO: Decide if we should remove default behavior. 
                        	 */
                        	mImageView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {

                        	        Intent fullScreenIntent = new Intent(getContext(), FullScreenImage.class);
                        	        fullScreenIntent.putExtra("image-file-name",imageFilename);

                        	        getContext().startActivity(fullScreenIntent); 
								}
                        	});
                        }
//                        imageView = mImageView;
                    } else if (errorMsg == null) {
                        // An error hasn't been logged and loading the image failed, so it's likely
                        // a bad file.
                        errorMsg = getContext().getString(R.string.file_invalid, imageFile);

                    }
                } else if (errorMsg == null) {
                    // An error hasn't been logged. We should have an image, but the file doesn't
                    // exist.
                    errorMsg = getContext().getString(R.string.file_missing, imageFile);
                }

                if (errorMsg != null) {
                    // errorMsg is only set when an error has occured
                    Log.e(t, errorMsg);
                    mMissingImage = new TextView(getContext());
                    mMissingImage.setText(errorMsg);
                    mMissingImage.setPadding(10, 10, 10, 10);
                    mMissingImage.setId(234873453);
//                    imageView = mMissingImage;
                }
            } catch (InvalidReferenceException e) {
                Log.e(t, "image invalid reference exception");
                e.printStackTrace();
            }
        }
        
    }


    /**
     * This adds a divider at the bottom of this layout. Used to separate fields in lists.
     * 
     * @param v
     */
    public void addDivider(ImageView v) {
        RelativeLayout.LayoutParams dividerParams =
            new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        if (mImageView != null) {
            dividerParams.addRule(RelativeLayout.BELOW, mImageView.getId());
        } else if (mMissingImage != null) {
            dividerParams.addRule(RelativeLayout.BELOW, mMissingImage.getId());
        } else if (mVideoButton != null) {
            dividerParams.addRule(RelativeLayout.BELOW, mVideoButton.getId());
        } else if (mAudioButton != null) {
            dividerParams.addRule(RelativeLayout.BELOW, mAudioButton.getId());
        } else if (mView_Text != null) {
            // No picture
            dividerParams.addRule(RelativeLayout.BELOW, mView_Text.getId());
        } else {
            Log.e(t, "Tried to add divider to uninitialized ATVWidget");
            return;
        }
        addView(v, dividerParams);
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {
            if (mAudioButton != null) {
                mAudioButton.stopPlaying();
            }
        }
    }

}
