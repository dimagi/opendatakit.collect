package org.odk.collect.android.views.media;

/**
 * Representation of the state of an AudioButton OR MediaEntity. For an
 * AudioButton, refers to the state of the media player that the button 
 * controls. For a MediaEntity, refers to the state of that entity's media 
 * player.
 * 
 * @author amstone326
 */

public enum ButtonState {
	
	/*
	 * Playing - the MediaPlayer is currently playing music
	 * Paused - setDataSource() and prepare() have been called for this
	 *   media player, but music is not currently playing
	 * Ready - the MediaPlayer has not had any data source initialized yet
	 * PausedForRenewal - Represents the same MediaPlayer state as paused, 
	 *   but used for activity life cycle purposes in handling rotation.
	 *   This state can be used to indicate to the onCreate method that 
	 *   music was playing in the previous version of the app, was paused 
	 *   onDestroy, and should be renewed to the Playing state upon resuming
	 */
	
    Playing, Ready, Paused, PausedForRenewal;

}
