package com.supermercerbros.gameengine.motion.curve;

import com.supermercerbros.gameengine.motion.Movement;
import com.supermercerbros.gameengine.motion.MovementData;
import com.supermercerbros.gameengine.objects.GameObject;

public class CurveMovement implements Movement {
	private final BezierInterpolator xPos, yPos, zPos, xRot, yRot, zRot, aRot,
			xScl, yScl, zScl;

	public CurveMovement(BezierInterpolator xPos, BezierInterpolator yPos,
			BezierInterpolator zPos, BezierInterpolator xRot,
			BezierInterpolator yRot, BezierInterpolator zRot,
			BezierInterpolator aRot, BezierInterpolator xScl,
			BezierInterpolator yScl, BezierInterpolator zScl) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
		this.aRot = aRot;
		
		this.xScl = xScl;
		this.yScl = yScl;
		this.zScl = zScl;
	}

	@Override
	public void getFrame(GameObject target, MovementData data, long time) {
		float framePoint = ((float) (time - data.startTime)) / (float) data.duration;
		
		final float posX = xPos.getInterpolation(framePoint);
		final float posY = yPos.getInterpolation(framePoint);
		final float posZ = zPos.getInterpolation(framePoint);
		//FIXME left off here
	}

}
