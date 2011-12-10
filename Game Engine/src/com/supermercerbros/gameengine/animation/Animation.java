package com.supermercerbros.gameengine.animation;

public interface Animation {
	public static final int STOPPED = 0;
	public static final int PAUSED = 1;
	public static final int RUNNING = 2;
	
	public float[] getFrame(long time);
	public void start(long time, long delay, float[] currentFrame, float speed);
	public void lag(long offset);
	/**
	 * Sets the Animation's speed.
	 * @param speed the multiplier to use as the Animation's speed. 1.0 is normal speed, 0.5 is half speed, etc.
	 */
	public void setSpeed(float speed);

	/**
	 * Returns the Animation's speed.
	 * @return the current speed of the Animation.
	 * @see #setSpeed(float)
	 */
	public float getSpeed();

}
