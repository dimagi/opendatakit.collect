package org.odk.collect.android.views.media;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.views.media.ViewId;

public class MediaEntity {
	
	private String source;
	private Object idOfOriginView;
	private MediaPlayer player;
	private ButtonState buttonState;
	
	public MediaEntity(String source, MediaPlayer player, Object id, ButtonState state) {
		this.player = player;
		this.source = source;
		this.idOfOriginView = id;
		this.buttonState = state;
	}
	
	public MediaEntity() {
		
	}
	
	public Object getId() {
		return idOfOriginView;
	}
	
	public ButtonState getState() {
		return buttonState;
	}
	
	public void setPlayer(MediaPlayer mp) {
		this.player = mp;
	}
	
	public void setState(ButtonState state) {
		this.buttonState = state;
	}
	
	public MediaPlayer getPlayer() {
		return player;
	}
	
	public String getSource() {
		return source;
	}
	
	/*public MediaEntity createDuplicate() {
		String newSource = this.source;
		Object newId = this.idOfOriginView;
		MediaPlayer newPlayer = duplicatePlayer();
		ButtonState newState = duplicateState(); 
		return new MediaEntity(newSource, newPlayer, newId, newState);
	}
	
	private MediaPlayer duplicatePlayer() {
		MediaPlayer newPlayer = new MediaPlayer();
        try {
        	newPlayer.setDataSource(this.source);
        	newPlayer.prepare();
        	newPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                	player.reset();
                	player.release();
                }

            });
        } catch (IOException e) {
            String errorMsg = getContext().getString(R.string.audio_file_invalid);
            Log.e(t, errorMsg);
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return newPlayer;
	}
	
	private ButtonState duplicateState() {
		
	}*/

}
