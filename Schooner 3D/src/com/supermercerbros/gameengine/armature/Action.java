package com.supermercerbros.gameengine.armature;

import java.util.Iterator;

import android.util.SparseArray;

import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.motion.Movement;

public class Action {
	protected final SparseArray<Bezier[]> boneCurves;
	public final Movement movement;
	
	public Action(Movement movement, SparseArray<Bezier[]> curves) {
		this.boneCurves = curves;
		this.movement = movement;
	}

	public void getFrame(ActionData data, Skeleton armature, long time) {		
		final float framePoint;
		//TODO: check if action has ended
		if (time < data.startTime) {
			// Interpolating to the action
			framePoint = (time - data.callTime) / (data.startTime - data.callTime);
			Iterator<Bone> iter = armature.bones.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				Bone bone = iter.next();
				Bezier[] curves = boneCurves.get(i);
				
				float sw = data.callState.boneStates[i*4    ];
				float sx = data.callState.boneStates[i*4 + 1];
				float sy = data.callState.boneStates[i*4 + 2];
				float sz = data.callState.boneStates[i*4 + 3];
				
				float fw, fx, fy, fz;
				if (curves != null) {
					fw = curves[0].getStartValue();
					fx = curves[1].getStartValue();
					fy = curves[2].getStartValue();
					fz = curves[3].getStartValue();
				} else {
					fw = 1.0f;
					fx = 0.0f;
					fy = 0.0f;
					fz = 0.0f;
				}
				float w = sw + (fw - sw) * framePoint;
				float x = sx + (fx - sx) * framePoint;
				float y = sy + (fy - sy) * framePoint;
				float z = sz + (fz - sz) * framePoint;
				bone.setRotation(w, x, y, z);
			}
		} else {
			// Interpolating in the action
			framePoint = (time - data.startTime) / data.duration;
			Iterator<Bone> iter = armature.bones.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				Bone bone = iter.next();
				Bezier[] curves = boneCurves.get(i);
				if (curves != null) {
					float w = curves[0].getInterpolation(framePoint);
					float x = curves[1].getInterpolation(framePoint);
					float y = curves[2].getInterpolation(framePoint);
					float z = curves[3].getInterpolation(framePoint);
					bone.setRotation(w, x, y, z);
				} 
			}
		}
		
	}
}
