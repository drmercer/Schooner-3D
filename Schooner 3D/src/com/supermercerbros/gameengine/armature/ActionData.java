package com.supermercerbros.gameengine.armature;

import com.supermercerbros.gameengine.objects.BonedObject;

public class ActionData {
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
}
