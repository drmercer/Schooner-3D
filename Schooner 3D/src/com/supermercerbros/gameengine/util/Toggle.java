package com.supermercerbros.gameengine.util;

/**
 * Like a {@link Boolean} but better. Can be used as a monitor (with
 * {@link #notify()} and {@link #wait()}) while still being toggle-able. This is
 * not true (at least in my experience) of a <code>Boolean</code>.
 * 
 */
public class Toggle {
	private boolean state;

	public Toggle(boolean initialState) {
		state = initialState;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}
}
