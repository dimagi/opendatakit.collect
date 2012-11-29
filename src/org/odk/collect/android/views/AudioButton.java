/**
 * 
 */

package org.odk.collect.android.views;

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

    enum ButtonState {
    	Playing,
    	Ready,
    	Paused;
    }
    
    ButtonState currentState;

    public AudioButton(Context context, String URI) {
        super(context);
        this.setOnClickListener(this);
        this.URI = URI;
        this.setImageResource(R.drawable.ic_media_btn_play);
        player = null;
        currentState = ButtonState.Ready;
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
                player.start();
                player.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                    	stopPlaying();
                    }

                });
                currentState = ButtonState.Playing;
                this.setImageResource(R.drawable.ic_media_pause);
            } catch (IOException e) {
                String errorMsg = getContext().getString(R.string.audio_file_invalid);
                Log.e(t, errorMsg);
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        	break;
        case Paused:
        	player.start();
        	currentState = ButtonState.Playing;
        	this.setImageResource(R.drawable.ic_media_pause);
        	break;
        case Playing:
        	player.pause();
        	currentState = ButtonState.Paused;
        	this.setImageResource(R.drawable.ic_media_btn_continue);
        	break;
        }
    }


    public void stopPlaying() {
        if (player != null) {
            player.release();
            currentState = ButtonState.Ready;
            this.setImageResource(R.drawable.ic_media_btn_play);
        }
    }
}
