package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.animation.Animation;

public interface AnimatedObject {

	/**
	 * Begins an {@link Animation}, interpolating from the current frame over the course of <code>delay</code>
	 * milliseconds.
	 * 
	 * @param index The index of the animation to begin.
	 * @param delay The time it should take to reach the first keyframe of the animation.
	 * @param speed The speed at which the animation should play. 1.0 is normal speed, 0.5 is half 
	 * speed, and so on.
	 * @param currentTime The current time, in milliseconds.
	 * 
	 * @return false if index is greater than or equal to the number of animations in the AnimatedObject's array of animations.
	 */
	public boolean start(int index, long delay, float speed, long currentTime);

	/**
	 * Sets the speed of the currently playing animation.
	 * @param speed The speed at which the animation should play. 1.0 is normal speed, 0.5 is half 
	 * speed, and so on.
	 */
	public void setSpeed(float speed);

	/**
	 * @return the speed of the current animation.
	 */
	public float getSpeed();

	public void stop();

	public void pause();

}