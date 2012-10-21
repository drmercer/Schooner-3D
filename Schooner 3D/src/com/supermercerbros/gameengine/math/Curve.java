package com.supermercerbros.gameengine.math;

public interface Curve {

	/**
	 * Maps a value representing the elapsed fraction of an animation to the
	 * value corresponding to that fraction.
	 * 
	 * @param x
	 *            The elapsed fraction.
	 */
	public abstract float getInterpolation(float x);

	/**
	 * @return The value of the first keyframe of this Curve
	 */
	public abstract float getStartValue();

}