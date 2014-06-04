package org.odk.collect.android.views.media;


public interface AudioController {
	
	/*
	 * Returns the current MediaEntity, or null if none is set
	 */
	public MediaEntity getCurrMedia();
	
	/*
	 * Removes the current MediaEntity if there is one,
	 * and sets the current MediaEntity to newEntity
	 */
	public void setCurrent(MediaEntity newEntity);
	
	/*
	 * Replaces the current MediaEntity with newEntity
	 * and the current AudioButton with newButton
	 */
	public void setCurrent(MediaEntity newEntity, AudioButton newButton);
	
	/*
	 * Releases the current MediaEntity's associated MediaPlayer
	 */
	public void stopCurrent();
	
	/*
	 * Sets the current MediaEntity to null
	 */
	public void removeCurrent();
	
	/*
	 * Gets the associated viewId of the current MediaEntity
	 */
	public Object getCurrId();
	
	/*
	 * reset the state of the current MediaEntity
	 */
	public void setCurrState(ButtonState state);
	
	/*
	 * If the current button and the button passed in are
	 * not the same button, reset the current button to
	 * the ready state
	 */
	public void refreshCurrentButton(AudioButton clicked);
	
	/*
	 * Method to be called by the implementing class's
	 * onDestroy method
	 */
	public void onImplementerDestroy();
	
	/*
	 * Method to be called by the implementing class's
	 * onPause method
	 */
	public void onImplementerPause();
	
}
