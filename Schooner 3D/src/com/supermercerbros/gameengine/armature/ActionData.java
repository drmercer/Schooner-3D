package com.supermercerbros.gameengine.armature;

public class ActionData {
	static class ArmatureState {
		final float[] boneStates;
		ArmatureState(byte size) {
			this.boneStates = new float[((int) size) * 4];
		}
		
		void setState(Armature current) {
			for (Bone bone : current.bones) {
				bone.getRotation(boneStates);
			}
		}
	}
	long startTime;
	long callTime;
	long duration;
	final ArmatureState callState;
	
	public ActionData(byte boneCount) {
		this.callState = new ArmatureState(boneCount);
	}
	
	public void writeState(long time, long start, long duration, Armature current) {
		this.callTime = time;
		this.startTime = start;
		this.duration = duration;
		if (start > time) {
			callState.setState(current);
		}
	}
}
