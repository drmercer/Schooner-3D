package com.supermercerbros.gameengine.util;

import com.supermercerbros.gameengine.math.Vector;
import android.util.Log;

/**
 * Contains interpolation utilities. (IPO is shorthand for interpolation.)
 * 
 * @version 1.0
 */
public class IPO {
	private static final String TAG = "com.supermercerbros.gameengine.util.IPO";

	/**
	 * Interpolates between the given start and end transformation matrices,
	 * based on framePoint, a value between 0 and 1. Stores the result in matrix
	 * m.
	 * 
	 * @param m
	 *            The matrix in which to store the result
	 * @param mOffset
	 *            The offset into m where the matrix is to be stored
	 * @param sm
	 *            The start matrix
	 * @param smOffset
	 *            The offset into sm where the matrix is stored
	 * @param em
	 *            The end matrix
	 * @param emOffset
	 *            The offset into em where the matrix is stored
	 * @param framePoint
	 *            The point to be interpolated to. For example, 0.5f would
	 *            result in the halfway-point between the start and end
	 *            matrices.
	 * 
	 * @throws IllegalArgumentException
	 *             if m.length < mOffset + 16 or startKeyframe.length < skOffset
	 *             + 16 or endKeyframe.length < ekOffset + 16
	 */
	public static void matrix(float[] m, int mOffset, float[] sm, int smOffset,
			float[] em, int emOffset, float framePoint) {

		if (m.length < mOffset + 16)
			throw new IllegalArgumentException("matrix.length < mOffset + 16");
		if (sm.length < smOffset + 16)
			throw new IllegalArgumentException(
					"startKeyframe.length < skOffset + 16");
		if (em.length < emOffset + 16)
			throw new IllegalArgumentException(
					"endKeyframe.length < ekOffset + 16");
		if (m == null)
			throw new IllegalArgumentException("matrix == null");
		if (sm == null)
			throw new IllegalArgumentException("startKeyframe == null");
		if (em == null)
			throw new IllegalArgumentException("endKeyframe == null");

		// If framePoint is less than 0 or greater than 1, use the start or
		// end matrices (respectively) as the final matrix
		if (framePoint <= 0) {
			Log.d(TAG, "framePoint <= 0");
			System.arraycopy(sm, smOffset, m, mOffset, 16);
			return;
		} else if (framePoint >= 1) {
			Log.d(TAG, "framePoint >= 1");
			System.arraycopy(em, emOffset, m, mOffset, 16);
			return;
		}

		// Translation interpolation
		for (int i = 0; i < 3; i++) {
			int mi = mOffset + i;
			int si = smOffset + i;
			int ei = emOffset + i;
			m[12 + mi] = sm[12 + si] + (em[12 + ei] - sm[12 + si]) * framePoint;
		}

		// Linearly interpolate the bottom row
		for (int i = 3; i < 16; i += 4) {
			int mi = mOffset + i;
			int si = smOffset + i;
			int ei = emOffset + i;
			m[mi] = sm[si] + (em[ei] - sm[si]) * framePoint;
		}

		// Convert rotation matrices into quaternions
		float[] sq = Vector.matrixToQuaternion(sm, smOffset);
		Log.d(TAG, "sq = {" + sq[0] + ", " + sq[1] + ", " + sq[2] + ", "
				+ sq[3] + "}");
		float[] eq = Vector.matrixToQuaternion(em, emOffset);
		Log.d(TAG, "eq = {" + eq[0] + ", " + eq[1] + ", " + eq[2] + ", "
				+ eq[3] + "}");

		/*
		 * This code was adapted from
		 * http://libgdx.googlecode.com/svn/trunk/gdx/
		 * src/com/badlogic/gdx/math/Quaternion.java (Copyright 2011 Mario
		 * Zechner, Nathan Sweet. See NOTICE file.), which is licensed under the
		 * Apache License, Version 2.0, available here:
		 * http://www.apache.org/licenses/LICENSE-2.0
		 */

		// Perform SLERP (Spherical Linear intERPolation) on the quaternions
		if (sq.equals(eq)) {
			Log.d(TAG, "sq.equals(eq)");
			Vector.quaternionToMatrix(sq, m, mOffset);
			return;
		}

		float result = sq[0] * eq[0] + sq[1] * eq[1] + sq[2] * eq[2] + sq[3]
				* eq[3];

		if (result < 0.0) {
			// Negate the second quaternion and the result of the dot product
			eq[0] *= -1;
			eq[1] *= -1;
			eq[2] *= -1;
			eq[3] *= -1;
			result = -result;
		}

		// Set the first and second scale for the interpolation
		float scale0 = 1 - framePoint;
		float scale1 = framePoint;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - result) > 0.1) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final double theta = Math.acos(result);
			final double invSinTheta = 1f / Math.sin(theta);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = (float) (Math.sin((1 - framePoint) * theta) * invSinTheta);
			scale1 = (float) (Math.sin((framePoint * theta)) * invSinTheta);
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special form of linear interpolation for quaternions.
		final float x = (scale0 * sq[0]) + (scale1 * eq[0]);
		final float y = (scale0 * sq[1]) + (scale1 * eq[1]);
		final float z = (scale0 * sq[2]) + (scale1 * eq[2]);
		final float w = (scale0 * sq[3]) + (scale1 * eq[3]);
		float[] mq = { x, y, z, w };
		Vector.quaternionToMatrix(mq, m, mOffset);

	}

	/**
	 * Interpolates to the given point between two mesh keyframes.
	 * 
	 * @param frame
	 *            The float array that holds the interpolated frame.
	 * @param frameOffset
	 *            The offset into the <code>frame</code> array where the frame
	 *            is to be stored.
	 * @param size
	 *            The number of values per keyframe. (Number of verts in object
	 *            * 3)
	 * @param startKeyframe
	 *            The float array that holds the first keyframe.
	 * @param startKeyframeOffset
	 *            The offset into the <code>startKeyframe</code> array where the
	 *            keyframe is stored.
	 * @param endKeyframe
	 *            The float array that holds the second keyframe.
	 * @param endKeyframeOffset
	 *            The offset into the <code>endKeyframe</code> array where the
	 *            keyframe is stored.
	 * @param framePoint
	 *            The point to be interpolated to. For example, 0.5f would
	 *            result in the halfway-point between the two keyframes.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>frameSize</code> is negative.
	 * @throws IndexOutOfBoundsException
	 *             if <code>frameOffset + frameSize > frame.length</code>,
	 *             <code>startKeyframeOffset + frameSize > startKeyframe.length</code>
	 *             ,
	 *             <code>endKeyframeOffset + frameSize > endKeyframe.length</code>
	 *             , or if <code>frameOffset</code>,
	 *             <code>startKeyframeOffset</code>, or
	 *             <code>endKeyframeOffset</code> is negative.
	 * @throws NullPointerException
	 *             if <code>frame</code>, <code>startKeyframe</code>, or
	 *             <code>endKeyframe</code> is null.
	 */
	public static void mesh(float[] frame, float[] startKeyframe,
			float[] endKeyframe, float framePoint) {
		
		int size = frame.length;
		
		if (frame == null || startKeyframe == null || endKeyframe == null)
			throw new NullPointerException();
		if (size != startKeyframe.length && size != endKeyframe.length)
			throw new IllegalArgumentException();

		for (int i = 0; i < size; i++) {
			frame[i] = startKeyframe[i]
					+ ((endKeyframe[i] - startKeyframe[i]) * framePoint);
		}
	}

}
