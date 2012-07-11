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
	/*
	 * The technique of using a temp float array was obtained from Matrix.java of the AOSP
	 */
	private static final float[] temp = new float[32];

	/**
	 * Calculates the cross product of two vectors, a and b, and stores it
	 * in result.
	 * 
	 * @param result
	 *            The float array where the cross product will be stored.
	 * @param resultOffset
	 *            The offset into the result array where the result will be
	 *            stored.
	 * @param a
	 *            The float array where the first vector is stored.
	 * @param aOffset
	 *            The offset into the a array where the first vector is
	 *            stored.
	 * @param b
	 *            The float array where the second vector is stored.
	 * @param bOffset
	 *            The offset into the b array where the second vector is
	 *            stored.
	 * @param normalize
	 *            If <code>true</code>, the result is normalized.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if resultOffset + 3 > result.length, aOffset + 3 >
	 *             a.length, or bOffset + 3 > b.length, or if
	 *             resultOffset, aOffset, or bOffset is negative.
	 * @throws NullPointerException
	 *             if result, a, or b is null.
	 */
	public static void cross(float[] result, int resultOffset, float[] a,
			int aOffset, float[] b, int bOffset, boolean normalize) {
		if (resultOffset + 3 > result.length || aOffset + 3 > a.length 
				|| bOffset + 3 > b.length) {
			throw new IndexOutOfBoundsException();
		}
		if (result == null || a == null || b == null) {
			throw new NullPointerException();
		}
		
		result[resultOffset    ] = a[aOffset + 1] * b[bOffset + 2]
				- a[aOffset + 2] * b[bOffset + 1];
		result[resultOffset + 1] = a[aOffset + 2] * b[bOffset    ]
				- a[aOffset    ] * b[bOffset + 2];
		result[resultOffset + 2] = a[aOffset    ] * b[bOffset + 1]
				- a[aOffset + 1] * b[bOffset    ];
		
		if (normalize) {
			float length = Utils.pythagF(result[resultOffset    ],
					result[resultOffset + 1], result[resultOffset + 2]);
			result[resultOffset    ] /= length;
			result[resultOffset + 1] /= length;
			result[resultOffset + 2] /= length;
		}
	}
	
	/**
	 * Rotates the given matrix in place by the given quaternion rotation
	 * 
	 * @param m The float array that holds matrix to rotate
	 * @param mOffset The offset into <code>m</code> where the matrix starts
	 * @param w The w-component of the quaternion
	 * @param x The x-component of the quaternion
	 * @param y The y-component of the quaternion
	 * @param z The z-component of the quaternion
	 */
	public static void rotateQuaternionM(float[] m, int mOffset, float w,
			float x, float y, float z) {
		synchronized (temp) {
			setRotateQuaternionM(temp, 0, w, x, y, z);
			multiplyMM(temp, 16, m, mOffset, temp, 0);
			System.arraycopy(temp, 16, m, mOffset, 16);
		}
	}
	
	/**
	 * Rotates the given matrix by the given quaternion rotation, putting the
	 * result in rm
	 * 
	 * @param rm The array to store the result
	 * @param rmOffset The offset into rm where the matrix should start.
	 * @param m The float array that holds matrix to rotate
	 * @param mOffset The offset into <code>m</code> where the matrix starts
	 * @param w The w-component of the quaternion
	 * @param x The x-component of the quaternion
	 * @param y The y-component of the quaternion
	 * @param z The z-component of the quaternion
	 */
	public static void rotateQuaternionM(float[] rm, int rmOffset, float[] m,
			int mOffset, float w, float x, float y, float z) {
		synchronized (temp) {
			setRotateQuaternionM(temp, 0, w, x, y, z);
			multiplyMM(rm, rmOffset, m, mOffset, temp, 0);
		}
	}
	
	/**
	 * Converts a quaternion (w, x, y, z) to a rotation matrix.
	 * 
	 * @param m The float array that holds matrix to rotate
	 * @param mOffset The offset into <code>m</code> where the matrix starts
	 * @param w The w-component of the quaternion
	 * @param x The x-component of the quaternion
	 * @param y The y-component of the quaternion
	 * @param z The z-component of the quaternion
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
