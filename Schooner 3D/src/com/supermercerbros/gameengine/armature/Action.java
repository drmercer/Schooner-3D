package com.supermercerbros.gameengine.armature;

import java.util.Iterator;

import android.util.SparseArray;

import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.motion.Movement;

public class Action {
	protected final SparseArray<Bezier> boneCurves;
	public final Movement movement;
	
	public Action(Movement movement, SparseArray<Bezier> curves) {
		this.boneCurves = curves;
		this.movement = movement;
	}

	public void getFrame(ActionData data, Skeleton skeleton, long time) {		
		final float framePoint;
		//TODO: check if action has ended
		if (time < data.startTime) {
			// Interpolating to the action
			framePoint = (time - data.callTime) / (data.startTime - data.callTime);
			Iterator<Bone> iter = skeleton.bones.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				final int offset = i*4;
				final Bone bone = iter.next();
				
				final float sw = data.callState.boneStates[offset    ];
				final float sx = data.callState.boneStates[offset + 1];
				final float sy = data.callState.boneStates[offset + 2];
				final float sz = data.callState.boneStates[offset + 3];
				
				final float fw, fx, fy, fz;
				Bezier wCurve = boneCurves.get(offset    );
				if (wCurve != null) {
					Bezier xCurve = boneCurves.get(offset + 1);
					Bezier yCurve = boneCurves.get(offset + 2);
					Bezier zCurve = boneCurves.get(offset + 3);
					fw = wCurve.getStartValue();
					fx = xCurve.getStartValue();
					fy = yCurve.getStartValue();
					fz = zCurve.getStartValue();
				} else {
					fw = 1.0f;
					fx = 0.0f;
					fy = 0.0f;
					fz = 0.0f;
				}
				final float w = sw + (fw - sw) * framePoint;
				final float x = sx + (fx - sx) * framePoint;
				final float y = sy + (fy - sy) * framePoint;
				final float z = sz + (fz - sz) * framePoint;
				bone.setRotation(w, x, y, z);
			}
		} else {
			// Interpolating in the action
			framePoint = (time - data.startTime) / data.duration;
			Iterator<Bone> iter = skeleton.bones.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				final int offset = i*4;
				final Bone bone = iter.next();
				
				Bezier wCurve = boneCurves.get(offset    );
				if (wCurve != null) {
					Bezier xCurve = boneCurves.get(offset + 1);
					Bezier yCurve = boneCurves.get(offset + 2);
					Bezier zCurve = boneCurves.get(offset + 3);
					
					final float w = wCurve.getInterpolation(framePoint);
					final float x = xCurve.getInterpolation(framePoint);
					final float y = yCurve.getInterpolation(framePoint);
					final float z = zCurve.getInterpolation(framePoint);
					bone.setRotation(w, x, y, z);
				} 
			}
		}
		
	}
}
