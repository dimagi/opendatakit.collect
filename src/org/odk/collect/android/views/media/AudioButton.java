/**
 * 
 */

package org.odk.collect.android.views.media;

import java.io.File;
import java.io.IOException;

import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.core.reference.ReferenceManager;
import org.odk.collect.android.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author ctsims
 * @author carlhartung
 * @author amstone326
 */
public class AudioButton extends ImageButton implements OnClickListener {
    private final static String t = "AudioButton";
    private String URI;
    private ButtonState currentState;
    private AudioController controller;
    private Object residingViewId;
    
    /*
     * Constructor for if not explicitly using an AudioController
     */
    public AudioButton(Context context, final String URI) {
    	super(context);
    	resetButton(URI);
    	//default implementation of controller if none is passed in
    	this.controller = new AudioController() {
    		private MediaPlayer mp;
    		
    		@Override
        	public MediaEntity getCurrMedia() {
        		return new MediaEntity(URI, this.mp, residingViewId, 
            			currentState);
        	}

        	@Override
        	public void setCurrent(MediaEntity newEntity) {
        		this.mp = newEntity.getPlayer();
        	}

        	@Override
        	public void setCurrent(MediaEntity newEntity, AudioButton newButton) {
        		setCurrent(newEntity);
        	}

        	@Override
        	public void releaseCurrentMediaEntity() {
        		if (mp != null) {
        			mp.reset();
        			mp.release();
        		}
        	}

        	@Override
        	public Object getMediaEntityId() {
        		return residingViewId;
        	}

        	@Override
        	public void refreshCurrentAudioButton(AudioButton clicked) {
        		return;
        	}


        	@Override
        	public void saveEntityStateAndClear() {
        		return;
        	}

        	@Override
        	public void setMediaEntityState(ButtonState state) {
        		return;
        	}

    		@Override
    		public void playCurrentMediaEntity() {
    			mp.start();
    		}

    		@Override
    		public void pauseCurrentMediaEntity() {
    			mp.pause();
    		}

			@Override
			public void setCurrentAudioButton(AudioButton b) {
				return;
			}

			@Override
			public void removeCurrentMediaEntity() {
				return;
			}
			
			@Override
			public void attemptSetStateToPauseForRenewal() {
				return;
			}

    	};
    }
    
    /*
     * Constructor for if an AudioController is being used
     */
    public AudioButton(Context context, String URI, Object id, AudioController controller) {
    	this(context, URI);
    	this.controller = controller;
    	this.residingViewId = id;
    	//System.out.println("Constructing AudioButton " + toString() + " at location " + locationToString());
    	/*
    	 * Check if the button in this view had media assigned to 
    	 * it in a previously-existing app (before rotation, etc.)
    	 */
    	MediaEntity currEntity = controller.getCurrMedia();
    	if (currEntity != null) {
    		Object oldId = currEntity.getId();
    		if (oldId.equals(id)) {
    			controller.setCurrentAudioButton(this);
    			restoreButtonFromEntity(currEntity);
    		}
    	}
    }
    
    public void resetButton(String URI) {
        this.URI = URI;
        this.currentState = ButtonState.Ready;
    	this.setImageResource(R.drawable.ic_media_btn_play);
        setFocusable(false);
        setFocusableInTouchMode(false);
        this.setOnClickListener(this);
    }
    
    public void resetButton(String URI, Object id) {
    	resetButton(URI);
    	this.residingViewId = id;
    }
    
    public void restoreButtonFromEntity(MediaEntity currentEntity) {
		this.URI = currentEntity.getSource();
		this.residingViewId = currentEntity.getId();
		this.currentState = currentEntity.getState();
		refreshAppearance();
    }
    
    public Object getViewId() {
    	return residingViewId;
    }
        
    public String getSource() {
    	return URI;
    }
    
    public String locationToString() {
    	if (residingViewId == null) {
    		return "";
    	}
    	ViewId viewid = (ViewId) residingViewId;
    	return viewid.toString();
    }
    
    public void modifyButtonForNewView(Object newViewId, String audioResource) {
		MediaEntity currentEntity = controller.getCurrMedia();
		if (currentEntity == null) {
			resetButton(audioResource, newViewId);
			return;
		}
    	Object activeId = currentEntity.getId();
    	if (activeId.equals(newViewId)) {
    		restoreButtonFromEntity(currentEntity);
    	}
    	else {
    		resetButton(audioResource, newViewId);
    	}
    }
    
    public void setStateToReady() {
		System.out.println("setStateToReady called on button " + this + " in view " + locationToString());
    	currentState = ButtonState.Ready;
    	refreshAppearance();
    }
    
    public void setStateToPlaying() {
		System.out.println("setStateToPlaying called on button " + this + " in view " + locationToString());
    	currentState = ButtonState.Playing;
    	refreshAppearance();
    }
    
    public void setStateToPaused() {
    	currentState = ButtonState.Paused;
    	refreshAppearance();
    }
    
    public void refreshAppearance() {
    	switch(currentState) {
    	case Ready:
            this.setImageResource(R.drawable.ic_media_btn_play);
            break;
    	case Playing:
        	this.setImageResource(R.drawable.ic_media_pause);
        	break;
    	case Paused:
    	case PausedForRenewal:
            this.setImageResource(R.drawable.ic_media_btn_continue);
    	}
    }

    @Override
    public void onClick(View v) {
    	System.out.println("AudioButton onClick called");
        if (URI == null) {
            // No audio file specified
            Log.e(t, "No audio file was specified");
            Toast.makeText(getContext(), getContext().getString(R.string.audio_file_error),
           			Toast.LENGTH_LONG).show();
            return;
        }

        String audioFilename = "";
        try {
            audioFilename = ReferenceManager._().DeriveReference(URI).getLocalURI();
        } catch (InvalidReferenceException e) {
            Log.e(t, "Invalid reference exception");
            e.printStackTrace();
        }

        File audioFile = new File(audioFilename);
        if (!audioFile.exists()) {
            // We should have an audio clip, but the file doesn't exist.
            String errorMsg = getContext().getString(R.string.file_missing, audioFile);
            Log.e(t, errorMsg);
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            return;
        }
        
        switch(currentState) {
        case Ready:
        	MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(audioFilename);
                player.prepare();
                player.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                    	endPlaying();
                    }

                });
                controller.setCurrent(new MediaEntity(URI, player, residingViewId, 
                			currentState), this);
                startPlaying();
            } catch (IOException e) {
                String errorMsg = getContext().getString(R.string.audio_file_invalid);
                Log.e(t, errorMsg);
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        	break;
        case PausedForRenewal:
        case Paused:
        	startPlaying();
        	break;
        case Playing:
        	pausePlaying();
        	break;
        }
    }
    
    public ButtonState getButtonState() {
    	return currentState;
    }

    public void startPlaying() {
    	controller.playCurrentMediaEntity();
    	setStateToPlaying();
    }

    public void endPlaying() {
    	controller.releaseCurrentMediaEntity();
    	setStateToReady();
    }

    public void pausePlaying() {
    	controller.pauseCurrentMediaEntity();
    	setStateToPaused();
    }

}
