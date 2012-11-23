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

package com.supermercerbros.gameengine.armature;

import com.supermercerbros.gameengine.engine.Time;
import com.supermercerbros.gameengine.engine.Time.Pausable;
import com.supermercerbros.gameengine.objects.BonedObject;

public class ActionData implements Pausable {
	static class ArmatureState {
		final float[] boneStates;
		
		ArmatureState(int boneCount) {
			this.boneStates = new float[boneCount * 4];
		}
		
		void setState(Skeleton current) {
			for (Bone bone : current.bones) {
				bone.getRotation(boneStates);
			}
		}
	}
	
	/**
	 * The time at which the action starts, in milliseconds.
	 */
	long startTime;
	
	/**
	 * The time at which {@link BonedObject#setAction(Action, long, long)} was
	 * called, in milliseconds.
	 */
	long callTime;
	
	/**
	 * The duration of the action, in milliseconds.
	 */
	long duration;
	
	/**
	 * The initial state of the armature
	 */
	final ArmatureState callState;
	
	public ActionData(int boneCount) {
		this.callState = new ArmatureState(boneCount);
		Time.getInstance().addPausable(this);
	}
	
	public void writeState(long time, long start, long duration,
			Skeleton current) {
		this.callTime = time;
		this.startTime = start;
		this.duration = duration;
		if (start > time) {
			callState.setState(current);
		}
	}

	@Override
	public void onResume(long millis) {
		this.startTime += millis;
		this.callTime += millis;
	}
}
