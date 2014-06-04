package org.odk.collect.android.views.media;

import android.media.MediaPlayer;
import org.odk.collect.android.views.media.ViewId;

public class MediaEntity {
	
	private String source;
	private ViewId idOfOriginView;
	private MediaPlayer player;
	private ButtonState buttonState;
	
	public MediaEntity(String source, MediaPlayer player, ViewId id, ButtonState state) {
		this.player = player;
		this.source = source;
		this.idOfOriginView = id;
		this.buttonState = state;
	}
	
	public ViewId getId() {
		return idOfOriginView;
	}
	
	public ButtonState getState() {
		return buttonState;
	}
	
	public void setPlayer(MediaPlayer mp) {
		this.player = mp;
	}
	
	public MediaPlayer getPlayer() {
		return player;
	}
	
	public String getSource() {
		return source;
	}

}
