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

package com.supermercerbros.gameengine.animation;

import com.supermercerbros.gameengine.engine.Time.Pausable;

public class AnimationData implements Pausable {

	/**
	 * The duration of one loop of the Animation, in milliseconds.
	 */
	long duration;

	/**
	 * The time at which the Animation starts or, in other words, the first
	 * Keyframe is reached.
	 */
	long startTime;

	/**
	 * The time at which the object started preparing for the animation (moving
	 * towards the first frame).
	 */
	long callTime;

	/**
	 * The number of times the animation should play. 0 is infinite.
	 */
	int loop;

	/**
	 * Contains the initial frame data of the animated object.
	 */
	float[] initialState;

	/**
	 * @param callTime
	 *            The time at which the animated object begins to move towards
	 *            the first keyframe.
	 */
	public synchronized void setCallTime(long callTime) {
		this.callTime = callTime;
	}

	/**
	 * @param duration
	 *            The duration of the Animation, in milliseconds.
	 */
	public synchronized void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * @param initialState
	 *            The data of the animated object's initial frame.
	 */
	public synchronized void setInitialState(float[] initialState) {
		this.initialState = initialState.clone();
	}

	/**
	 * @param loop
	 *            The number of times the animation should loop.
	 */
	public synchronized void setLoop(int loop) {
		this.loop = loop;
	}

	/**
	 * @param startTime
	 *            The time at which the animation should start.
	 */
	public synchronized void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public synchronized void onResume(long millis) {
		this.startTime += millis;
	}
}
