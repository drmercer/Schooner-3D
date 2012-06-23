package com.supermercerbros.gameengine.motion;

import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.math.MatrixUtils;
import com.supermercerbros.gameengine.objects.GameObject;

public class CurveMovement implements Movement {
	private final Bezier xPos, yPos, zPos, wRot, xRot, yRot, zRot,
			xScl, yScl, zScl;

	/**
	 * @param flags
	 * @param curves
	 * 
	 * @throws ArrayIndexOutOfBoundsException if not enough Beziers are supplied for the given flags
	 */
	public CurveMovement(int flags, Bezier... curves) {
		short index = 0;
		
		if ((flags & POSITION) != 0) {
			xPos = curves[index++];
			yPos = curves[index++];
			zPos = curves[index++];
		} else {
			xPos = null;
			yPos = null;
			zPos = null;
		}
		
		if ((flags & ROTATION) != 0) {
			wRot = curves[index++];
			xRot = curves[index++];
			yRot = curves[index++];
			zRot = curves[index++];
		} else {
			wRot = null;
			xRot = null;
			yRot = null;
			zRot = null;
		}
		
		if ((flags & SCALE) != 0) {
			xScl = curves[index++];
			yScl = null;
			zScl = null;
		} else if ((flags & SCALE_AXIS) != 0) {
			xScl = curves[index++];
			yScl = curves[index++];
			zScl = curves[index++];
		} else {
			xScl = null;
			yScl = null;
			zScl = null;
		}
	}

	@Override
	public void getFrame(GameObject target, MovementData data, long time) {
		final float framePoint = ((float) (time - data.startTime))
				/ (float) data.duration;
		
		// Translate
		if (xPos != null) {
			final float posX = xPos.getInterpolation(framePoint);
			final float posY = yPos.getInterpolation(framePoint);
			final float posZ = zPos.getInterpolation(framePoint);
			MatrixUtils.translateM(target.modelMatrix, 0, data.matrix, 0, posX, posY, posZ);
		}
		
		// Rotate
		if (wRot != null) {
			final float rotW = wRot.getInterpolation(framePoint);
			final float rotX = xRot.getInterpolation(framePoint);
			final float rotY = yRot.getInterpolation(framePoint);
			final float rotZ = zRot.getInterpolation(framePoint);
			MatrixUtils.rotateQuaternionM(target.modelMatrix, 0, data.matrix, 0, rotW, rotX, rotY, rotZ);
		}
		
		// Scale
		if (xScl != null) {
			if (yScl != null) {
				// Nonuniform Scale
				final float sclX = xScl.getInterpolation(framePoint);
				final float sclY = yScl.getInterpolation(framePoint);
				final float sclZ = zScl.getInterpolation(framePoint);
				MatrixUtils.scaleM(target.modelMatrix, 0, data.matrix, 0, sclX, sclY, sclZ);
			} else {
				// Uniform Scale
				final float scl = xScl.getInterpolation(framePoint);
				MatrixUtils.scaleM(target.modelMatrix, 0, data.matrix, 0, scl, scl, scl);
			}
		}
		
	}

}
