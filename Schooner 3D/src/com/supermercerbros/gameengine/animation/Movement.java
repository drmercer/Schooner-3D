package com.supermercerbros.gameengine.animation;

/**
 * Similar to the Animation interface, implementations of Movement can be used 
 * to animate the location of a GameObject (instead of the geometry, like an Animation would do).
 *
 * @see GameObject#startMotion(Movement, float)
 * @see GameObject#getMotion()
 */
public interface Movement{
	/** Draws a frame to the given matrix.
	 * @param matrix The array in which to store the resulting transformation matrix
	 * @param time The time of the frame being drawn
	 */
	public void getFrame(float[] matrix, int mOffset, long time);
	/** Starts the Movement.
	 * @param time the start time
	 * @param relativeMatrix the matrix from which to animate
	 * @param speed an alpha value representing the speed of the movement
	 */
	public void start(long time, float[] relativeMatrix, float speed);
	public void lag(long offset);
	public float getSpeed();
	public void setSpeed(float speed);
}
