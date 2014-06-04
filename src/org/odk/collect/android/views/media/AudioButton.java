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
 */
public class AudioButton extends ImageButton implements OnClickListener {
    private final static String t = "AudioButton";
    private String URI;
    private MediaPlayer player;
    private ButtonState currentState;
    private AudioController controller;
    private Object residingViewId;
    
    /*
     * Constructor for if not using an AudioController
     */
    public AudioButton(Context context, String URI) {
    	super(context);
    	resetButton(URI);
    	//default implementation of controller if none is passed in
    	this.controller = new DefaultController();
    }
    
    /*
     * Constructor for if an AudioController is being used
     */
    public AudioButton(Context context, String URI, Object id, AudioController controller) {
    	this(context, URI);
    	this.controller = controller;
    	this.residingViewId = id;
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
    
    public Object getViewId() {
    	return residingViewId;
    }
        
    public String getSource() {
    	return URI;
    }
    
    public void modifyButtonForNewView(Object newViewId, String audioResource) {
		MediaEntity currentEntity = controller.getCurrMedia();
		if (currentEntity == null) {
			resetButton(audioResource, newViewId);
			return;
		}
    	Object activeId = currentEntity.getId();
    	if (activeId.equals(newViewId)) {
    		//restore old button
    		this.URI = currentEntity.getSource();
    		this.player = currentEntity.getPlayer();
    		this.residingViewId = newViewId;
    		this.currentState = currentEntity.getState();
    		System.out.println("state was reset to " + currentState);
    		refreshAppearance();
    	}
    	else {
    		resetButton(audioResource, newViewId);
    	}
    }
    
    public void setStateToReady() {
    	currentState = ButtonState.Ready;
    	refreshAppearance();
    }
    
    public void setStateToPlaying() {
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
            this.setImageResource(R.drawable.ic_media_btn_continue);
    	}
    }

    @Override
    public void onClick(View v) {

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
        	player = new MediaPlayer();
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
    	controller.playCurrent();
    	setStateToPlaying();
    	/*if (!currentState.equals(ButtonState.Playing)) {
    		player.start();
    		setStateToPlaying();
    	}*/

    }

    public void endPlaying() {
    	controller.removeCurrent();
    	setStateToReady();
    	/*if (currentState.equals(ButtonState.Playing)) {
    		player.reset();
    		player.release();
    		setStateToReady();
    	}*/
    }

    public void pausePlaying() {
    	controller.pauseCurrent();
    	setStateToPaused();
    	/*if (currentState.equals(ButtonState.Playing)) {
    		player.pause();
    		setStateToPaused();
    	}*/
    }

    private class DefaultController implements AudioController {
    	private AudioButton currentButton;
    	private MediaEntity currentEntity;

    	@Override
    	public MediaEntity getCurrMedia() {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public void setCurrent(MediaEntity newEntity) {
    		// TODO Auto-generated method stub

    	}

    	@Override
    	public void setCurrent(MediaEntity newEntity, AudioButton newButton) {
    		// TODO Auto-generated method stub

    	}

    	@Override
    	public void removeCurrent() {
    		// TODO Auto-generated method stub

    	}

    	@Override
    	public Object getCurrId() {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public void refreshCurrentButton(AudioButton clicked) {
    		// TODO Auto-generated method stub

    	}

    	@Override
    	public void onImplementerDestroy() {
    		//leave empty

    	}

    	@Override
    	public void onImplementerPause() {
    		//leave empty
    	}

    	@Override
    	public void setCurrState(ButtonState state) {

    	}

		@Override
		public void playCurrent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pauseCurrent() {
			// TODO Auto-generated method stub
			
		}

    }
}
