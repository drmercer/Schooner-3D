package com.supermercerbros.gameengine.armature;

import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.motion.Movement;

public class Action {
	public static class BoneCurves {
		final Bezier rotW, rotX, rotY, rotZ;
		
		public BoneCurves(Bezier rotW, Bezier rotX, Bezier rotY, Bezier rotZ){
			this.rotW = rotW;
			this.rotX = rotX;
			this.rotY = rotY;
			this.rotZ = rotZ;
		}
	}
	protected final BoneCurves[] boneCurves;
	protected final Movement movement;
	
	public Action(Movement movement, BoneCurves... curves) {
		this.boneCurves = curves;
		this.movement = movement;
	}
}
