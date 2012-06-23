package com.supermercerbros.gameengine.math;

import android.opengl.Matrix;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Vector and matrix utilities not found in the android.opengl.Matrix class.
 * 
 * @see <a
 *      href="http://www.sjbaker.org/steve/omniv/matrices_can_be_your_friends.html">"Matrices can be your Friends"
 *      by Steve Baker</a>
 * 
 */
public class MatrixUtils extends Matrix {
	
	/**
	 * Calculates the cross product of two vectors, vecA and vecB, and stores it
	 * in result.
	 * 
	 * @param result
	 *            The float array where the cross product will be stored.
	 * @param resultOffset
	 *            The offset into the result array where the result will be
	 *            stored.
	 * @param vecA
	 *            The float array where the first vector is stored.
	 * @param vecAOffset
	 *            The offset into the vecA array where the first vector is
	 *            stored.
	 * @param vecB
	 *            The float array where the second vector is stored.
	 * @param vecBOffset
	 *            The offset into the vecB array where the second vector is
	 *            stored.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if resultOffset + 3 > result.length, vecAOffset + 3 >
	 *             vecA.length, or vecBOffset + 3 > vecB.length, or if
	 *             resultOffset, vecAOffset, or vecBOffset is negative.
	 * @throws NullPointerException
	 *             if result, vecA, or vecB is null.
	 */
	public static void cross(float[] result, int resultOffset, float[] vecA,
			int vecAOffset, float[] vecB, int vecBOffset, boolean normalize) {
		if (resultOffset + 3 > result.length || vecAOffset + 3 > vecA.length
				|| vecBOffset + 3 > vecB.length)
			throw new IndexOutOfBoundsException();
		if (result == null || vecA == null || vecB == null)
			throw new NullPointerException();
		
		if (normalize) {
			float length = Utils.pythagF(vecA[vecAOffset + 0],
					vecA[vecAOffset + 1], vecA[vecAOffset + 2]);
			vecA[vecAOffset + 0] /= length;
			vecA[vecAOffset + 1] /= length;
			vecA[vecAOffset + 2] /= length;
			
			length = Utils.pythagF(vecB[vecBOffset + 0], vecB[vecBOffset + 1],
					vecB[vecBOffset + 2]);
			vecB[vecBOffset + 0] /= length;
			vecB[vecBOffset + 1] /= length;
			vecB[vecBOffset + 2] /= length;
		}
		
		result[resultOffset + 0] = vecA[vecAOffset + 1] * vecB[vecBOffset + 2]
				- vecA[vecAOffset + 2] * vecB[vecBOffset + 1];
		result[resultOffset + 1] = vecA[vecAOffset + 2] * vecB[vecBOffset + 0]
				- vecA[vecAOffset + 0] * vecB[vecBOffset + 2];
		result[resultOffset + 2] = vecA[vecAOffset + 0] * vecB[vecBOffset + 1]
				- vecA[vecAOffset + 1] * vecB[vecBOffset + 0];
		
		if (normalize) {
			float length = Utils.pythagF(result[resultOffset + 0],
					result[resultOffset + 1], result[resultOffset + 2]);
			result[resultOffset + 0] /= length;
			result[resultOffset + 1] /= length;
			result[resultOffset + 2] /= length;
		}
	}
	
	/**
	 * Rotates the given matrix in place by the given quaternion rotation
	 * 
	 * @param m
	 *            The matrix to rotate
	 * @param mOffset
	 *            The offset into <code>m</code> where the matrix starts
	 * @param w
	 *            The W-component of the quaternion rotation
	 * @param x
	 *            The X-component of the quaternion rotation
	 * @param y
	 *            The Y-component of the quaternion rotation
	 * @param z
	 *            The Z-component of the quaternion rotation
	 */
	public static void rotateQuaternionM(float[] m, int mOffset, float w,
			float x, float y, float z) {
		final double theta = 2 * Math.acos(w);
		final double sin = Math.sin(theta / 2);
		rotateM(m, mOffset, (float) Math.toDegrees(theta), (float) (x / sin),
				(float) (y / sin), (float) (z / sin));
	}
	
	/**
	 * Rotates the given matrix by the given quaternion rotation, putting the
	 * result in rm
	 * 
	 * @param rm
	 *            The array to store the result
	 * @param rmOffset
	 *            The offset into rm where the matrix should start.
	 * @param m
	 *            The matrix to rotate
	 * @param mOffset
	 *            The offset into <code>m</code> where the matrix starts
	 * @param w
	 *            The W-component of the quaternion rotation
	 * @param x
	 *            The X-component of the quaternion rotation
	 * @param y
	 *            The Y-component of the quaternion rotation
	 * @param z
	 *            The Z-component of the quaternion rotation
	 */
	public static void rotateQuaternionM(float[] rm, int rmOffset, float[] m,
			int mOffset, float w, float x, float y, float z) {
		final double theta = 2 * Math.acos(w);
		final double sin = Math.sin(theta / 2);
		rotateM(rm, rmOffset, m, mOffset, (float) Math.toDegrees(theta),
				(float) (x / sin), (float) (y / sin), (float) (z / sin));
	}
	
	/**
	 * Converts a quaternion (w, x, y, z) to a rotation matrix.
	 * 
	 * @param m
	 *            The array to store the result
	 * @param mOffset
	 *            The offset into rm where the matrix should start.
	 * @param w
	 *            The W-component of the quaternion rotation
	 * @param x
	 *            The X-component of the quaternion rotation
	 * @param y
	 *            The Y-component of the quaternion rotation
	 * @param z
	 *            The Z-component of the quaternion rotation
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion>Quaternion
	 *      -Derived Rotation Matrix (Wikipedia)</a>
	 */
	public static void setRotateQuaternionM(float[] m, int mOffset, float w,
			float x, float y, float z) {
		
		if (m.length < mOffset + 16) {
			throw new IllegalArgumentException("m.length < mOffset + 16");
		}
		
		final float xx = x*x, yy = y*y, zz = z*z;
		final float xy = x*y, yz = y*z, xz = x*z;
		final float xw = x*w, yw = y*w, zw = z*w;
		
		m[mOffset +  0] = 1 - 2*yy - 2*zz;
		m[mOffset +  1] = 2*xy + 2*zw;
		m[mOffset +  2] = 2*xz - 2*yw;
		m[mOffset +  3] = 0;
		
		m[mOffset +  4] = 2*xy - 2*zw;
		m[mOffset +  5] = 1 - 2*xx - 2*zz;
		m[mOffset +  6] = 2*yz + 2*xw;
		m[mOffset +  7] = 0;
		
		m[mOffset +  8] = 2*xz + 2*yw;
		m[mOffset +  9] = 2*yz - 2*xw;
		m[mOffset + 10] = 1 - 2*xx - 2*yy;
		m[mOffset + 11] = 0;
		
		m[mOffset + 12] = 0;
		m[mOffset + 13] = 0;
		m[mOffset + 14] = 0;
		m[mOffset + 15] = 1;
	}
}
