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

import java.util.Arrays;

import android.util.Log;

import com.supermercerbros.gameengine.math.Curve;
import com.supermercerbros.gameengine.math.MatrixUtils;
import com.supermercerbros.gameengine.objects.GameObject;

public class CurveMovement implements Movement {
	private static final String TAG = CurveMovement.class.getSimpleName();
	private final Curve xPos, yPos, zPos, wRot, xRot, yRot, zRot,
			xScl, yScl, zScl;

	/**
	 * @param flags
	 * @param curves
	 * 
	 * @throws ArrayIndexOutOfBoundsException if not enough Curves are supplied for the given flags
	 */
	public CurveMovement(int flags, Curve... curves) {
		short index = 0;
		
		if ((flags & POSITION) != 0 && curves[index] != null) {
			xPos = curves[index++];
			yPos = curves[index++];
			zPos = curves[index++];
		} else {
			xPos = null;
			yPos = null;
			zPos = null;
		}
		
		if ((flags & ROTATION) != 0 && curves[index] != null) {
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
		
		if ((flags & SCALE) != 0 && curves[index] != null) {
			xScl = curves[index++];
			yScl = null;
			zScl = null;
		} else if ((flags & SCALE_AXIS) != 0 && curves[index] != null) {
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
		if (framePoint >= 1) {
			target.endMovement();
		}
		
		// Translate
		if (xPos != null) {
			final float posX = xPos.getInterpolation(framePoint);
			final float posY = yPos.getInterpolation(framePoint);
			final float posZ = zPos.getInterpolation(framePoint);
			MatrixUtils.translateM(target.modelMatrix, 0, data.matrix, 0, posX, posY, posZ);
			Log.d(TAG, "Translate = " + posX + ", " + posY + ", " + posZ);
		}
		
		// Rotate
		if (wRot != null) {
			final float rotW = wRot.getInterpolation(framePoint);
			final float rotX = xRot.getInterpolation(framePoint);
			final float rotY = yRot.getInterpolation(framePoint);
			final float rotZ = zRot.getInterpolation(framePoint);
			MatrixUtils.rotateQuaternionM(target.modelMatrix, 0, rotW, rotX, rotY, rotZ);
			Log.d(TAG, "Rotate = " + rotW + ", " + rotX + ", " + rotY + ", " + rotZ);
		}
		
		// Scale
		if (xScl != null) {
			if (yScl != null) {
				// Nonuniform Scale
				final float sclX = xScl.getInterpolation(framePoint);
				final float sclY = yScl.getInterpolation(framePoint);
				final float sclZ = zScl.getInterpolation(framePoint);
				MatrixUtils.scaleM(target.modelMatrix, 0, sclX, sclY, sclZ);
				Log.d(TAG, "Scale = " + sclX + ", " + sclY + ", " + sclZ);
			} else {
				// Uniform Scale
				final float scl = xScl.getInterpolation(framePoint);
				MatrixUtils.scaleM(target.modelMatrix, 0, scl, scl, scl);
				Log.d(TAG, "Scale = " + scl);
			}
		}
		
		Log.d(TAG, "matrix = " + Arrays.toString(target.modelMatrix));
	}
}
