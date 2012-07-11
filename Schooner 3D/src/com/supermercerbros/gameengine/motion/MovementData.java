package com.supermercerbros.gameengine.motion;

public class MovementData {
	
	/**
	 * The duration of the movement, in milliseconds.
	 */
	protected long duration;
	
	/**
	 * The initial state of the object
	 */
	protected final float[] matrix;
	
	/**
	 * The time at which the movement starts, in milliseconds.
	 */
	protected long startTime;
	
	/**
	 * Creates a new MovementData
	 */
	public MovementData() {
		matrix = new float[16];
	}
	
	/**
	 * @param startTime
	 *            The time at which the movement starts, in milliseconds.
	 * @param duration
	 *            The duration of the movement, in milliseconds.
	 * @param modelMatrix
	 *            The object's current transformation matrix.
	 */
	public void set(long startTime, long duration, float[] modelMatrix) {
		this.startTime = startTime;
		this.duration = duration;
		System.arraycopy(modelMatrix, 0, matrix, 0, 16);
	}
}
