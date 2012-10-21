/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.supermercerbros.gameengine.engine;

import android.opengl.Matrix;

/**
 * Represents the "eye" in a 3D scene, through which the user views the scene.
 * By default, the camera is at the origin looking in the direction of the
 * negative z-axis, with the y-axis as the up vector.
 */
public class Camera {
	@SuppressWarnings("unused")
	private static final String TAG = "Camera";
	
	// Begin state
	private float beginEyeX;
	private float beginEyeY;
	private float beginEyeZ;
	private float beginCenterX;
	private float beginCenterY;
	private float beginCenterZ;
	private float beginUpX;
	private float beginUpY;
	private float beginUpZ;
	
	// End state
	private float endEyeX;
	private float endEyeY;
	private float endEyeZ;
	private float endCenterX;
	private float endCenterY;
	private float endCenterZ;
	private float endUpX;
	private float endUpY;
	private float endUpZ;
	
	// Animation data
	private long startTime;
	private long duration;
	private boolean moving;
	
	// Current state
	private float eyeX;
	private float eyeY;
	private float eyeZ;
	private float centerX;
	private float centerY;
	private float centerZ;
	private float upX;
	private float upY;
	private float upZ;
	
	/**
	 * Creates a default Camera (Eye at the origin, looking along the negative
	 * Z-axis, with the Y-axis pointing up). Follow with
	 * {@link #set(float, float, float, float, float, float, float, float, float)}
	 * ;
	 */
	public Camera() {
		centerZ = -1.0f;
		upY = 1.0f;
	}
	
	/**
	 * Moves the Camera to the given orientation and position over the given
	 * amount of time.
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 * @param duration
	 *            The duration of the transition, in milliseconds.
	 */
	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ, long duration) {
		
		beginEyeX = this.eyeX;
		beginEyeY = this.eyeY;
		beginEyeZ = this.eyeZ;
		beginCenterX = this.centerX;
		beginCenterY = this.centerY;
		beginCenterZ = this.centerZ;
		beginUpX = this.upX;
		beginUpY = this.upY;
		beginUpZ = this.upZ;
		
		endEyeX = eyeX;
		endEyeY = eyeY;
		endEyeZ = eyeZ;
		endCenterX = centerX;
		endCenterY = centerY;
		endCenterZ = centerZ;
		endUpX = upX;
		endUpY = upY;
		endUpZ = upZ;
		
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}
	
	/**
	 * Moves the Camera to the given orientation and position over the given
	 * amount of time.
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param duration
	 *            The duration of the transition, in milliseconds.
	 */
	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, long duration) {
		
		beginEyeX = this.eyeX;
		beginEyeY = this.eyeY;
		beginEyeZ = this.eyeZ;
		beginCenterX = this.centerX;
		beginCenterY = this.centerY;
		beginCenterZ = this.centerZ;
		beginUpX = this.upX;
		beginUpY = this.upY;
		beginUpZ = this.upZ;
		
		endEyeX = eyeX;
		endEyeY = eyeY;
		endEyeZ = eyeZ;
		endCenterX = centerX;
		endCenterY = centerY;
		endCenterZ = centerZ;
		endUpX = this.upX;
		endUpY = this.upY;
		endUpZ = this.upZ;
		
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}
	
	/**
	 * Moves the Camera to the given position over the given
	 * amount of time.
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param duration
	 *            The duration of the transition, in milliseconds.
	 */
	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			long duration) {
		
		beginEyeX = this.eyeX;
		beginEyeY = this.eyeY;
		beginEyeZ = this.eyeZ;
		beginCenterX = this.centerX;
		beginCenterY = this.centerY;
		beginCenterZ = this.centerZ;
		beginUpX = this.upX;
		beginUpY = this.upY;
		beginUpZ = this.upZ;
		
		endEyeX = eyeX;
		endEyeY = eyeY;
		endEyeZ = eyeZ;
		endCenterX = this.centerX;
		endCenterY = this.centerY;
		endCenterZ = this.centerZ;
		endUpX = this.upX;
		endUpY = this.upY;
		endUpZ = this.upZ;
		
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}
	
	/**
	 * Updates the Camera for the given point in time.
	 * 
	 * @param time
	 *            The time of the current frame, in milliseconds.
	 */
	synchronized void update(long time) {
		if (!moving) {
			return;
		}
		float framePoint = ((float) (time - startTime)) / (float) duration;
		if (framePoint > 1.0f) {
			this.eyeX = endEyeX;
			this.eyeY = endEyeY;
			this.eyeZ = endEyeZ;
			this.centerX = endCenterX;
			this.centerY = endCenterY;
			this.centerZ = endCenterZ;
			this.upX = endUpX;
			this.upY = endUpY;
			this.upZ = endUpZ;
			moving = false;
			return;
		} else if (framePoint < 0.0f) {
			return;
		}
		
		this.eyeX = (float) (beginEyeX + ((endEyeX - beginEyeX) * (double) framePoint));
		this.eyeY = (float) (beginEyeY + ((endEyeY - beginEyeY) * (double) framePoint));
		this.eyeZ = (float) (beginEyeZ + ((endEyeZ - beginEyeZ) * (double) framePoint));
		this.centerX = (float) (beginCenterX + ((endCenterX - beginCenterX) * (double) framePoint));
		this.centerY = (float) (beginCenterY + ((endCenterY - beginCenterY) * (double) framePoint));
		this.centerZ = (float) (beginCenterZ + ((endCenterZ - beginCenterZ) * (double) framePoint));
		this.upX = (float) (beginUpX + ((endUpX - beginUpX) * (double) framePoint));
		this.upY = (float) (beginUpY + ((endUpY - beginUpY) * (double) framePoint));
		this.upZ = (float) (beginUpZ + ((endUpZ - beginUpZ) * (double) framePoint));
	}
	
	/**
	 * Writes this Camera as a view matrix to the given array.
	 * 
	 * @param a
	 *            The array to write to.
	 * @param offset
	 *            The offset into <code>a</code> where the matrix will start.
	 */
	synchronized void writeToArray(float[] a, int offset) {
		if (a == null) {
			throw new IllegalArgumentException(
					"Cannot write Camera to null array.");
		} else if (a.length < offset + 16) {
			throw new IllegalArgumentException(
					"Cannot write Camera. Array is too small.");
		}
		
		Matrix.setLookAtM(a, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ);
	}
	
	/**
	 * Sets the Camera to the given position and orientation.
	 * 
	 * @param eyeX
	 *            The x-coord of the eye point
	 * @param eyeY
	 *            The y-coord of the eye point
	 * @param eyeZ
	 *            The z-coord of the eye point
	 * @param centerX
	 *            The x-coord of the look-at point
	 * @param centerY
	 *            The y-coord of the look-at point
	 * @param centerZ
	 *            The z-coord of the look-at point
	 * @param upX
	 *            The x-coord of the up vector
	 * @param upY
	 *            The y-coord of the up vector
	 * @param upZ
	 *            The z-coord of the up vector
	 */
	public synchronized void set(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ) {
		this.eyeX = eyeX;
		this.eyeY = eyeY;
		this.eyeZ = eyeZ;
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;
	}
	
	/**
	 * Sets the Camera to the given position.
	 * 
	 * @param eyeX
	 *            The x-coord of the eye point
	 * @param eyeY
	 *            The y-coord of the eye point
	 * @param eyeZ
	 *            The z-coord of the eye point
	 */
	public synchronized void set(float eyeX, float eyeY, float eyeZ) {
		this.eyeX = eyeX;
		this.eyeY = eyeY;
		this.eyeZ = eyeZ;
	}
	
}
