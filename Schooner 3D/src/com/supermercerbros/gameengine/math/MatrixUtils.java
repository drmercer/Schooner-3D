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

package com.supermercerbros.gameengine.math;

import android.opengl.Matrix;

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

	/**
	 * Makes a String representation of a matrix.
	 * @param mat The <code>float</code> array containing the matrix.
	 * @param matOffset The offset into <code>mat</code> where the matrix starts.
	 * @return The String representation of the matrix, with all numbers printed to 4 digits of precision.
	 */
	public static String matrixToString(float[] mat, int matOffset) {
		final String rowFormat = "% .8f, % .8f, % .8f, % .8f\n";
		final StringBuilder sb = new StringBuilder();
		
		for (int row = 0; row < 4; row++) { // For each row
			sb.append(String.format(rowFormat, 
					mat[matOffset + row     ], 
					mat[matOffset + row + 4 ], 
					mat[matOffset + row + 8 ], 
					mat[matOffset + row + 12]));
		}
		return sb.toString();
	}

	/**
	 * @param mat
	 * @param matOffset
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void setTranslateM(float[] mat, int matOffset,
			float x, float y, float z) {
		setIdentityM(mat, matOffset);
		mat[matOffset + 12] = x;
		mat[matOffset + 13] = y;
		mat[matOffset + 14] = z;
	}
}
