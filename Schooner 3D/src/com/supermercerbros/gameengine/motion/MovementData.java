package com.supermercerbros.gameengine.motion;

public class MovementData {
	
	public MovementData(){
		matrix = new float[16];
	}
	/**
	 * The time at which the movement starts
	 */
	public long startTime;
	
	/**
	 * The duration of the movement, in milliseconds.
	 */
	public long duration;
	
	/**
	 * The initial state of the described object
	 */
	public float[] matrix;
}
