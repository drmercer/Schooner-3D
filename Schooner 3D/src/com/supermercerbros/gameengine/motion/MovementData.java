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

package com.supermercerbros.gameengine.motion;

import com.supermercerbros.gameengine.engine.Time;
import com.supermercerbros.gameengine.engine.Time.Pausable;

public class MovementData implements Pausable {
	
	/**
	 * The duration of the movement, in milliseconds.
	 */
	public long duration;
	
	/**
	 * The initial state of the object
	 */
	public final float[] matrix;
	
	/**
	 * The time at which the movement starts, in milliseconds.
	 */
	public long startTime;
	
	/**
	 * Creates a new MovementData
	 */
	public MovementData() {
		matrix = new float[16];
		Time.getInstance().addPausable(this);
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

	@Override
	public void onResume(long millis) {
		this.startTime += millis;
	}
}
