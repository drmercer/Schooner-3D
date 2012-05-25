package com.supermercerbros.gameengine.math;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Vector and matrix utilities not found in the android.opengl.Matrix class.
 * 
 * @see <a
 *      href="http://www.sjbaker.org/steve/omniv/matrices_can_be_your_friends.html">"Matrices can be your Friends"
 *      by Steve Baker</a>
 * 
 */
public class Vector {

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
			float length = Utils.pythagF(result[resultOffset + 0], result[resultOffset + 1], result[resultOffset + 2]);
			result[resultOffset + 0] /= length;
			result[resultOffset + 1] /= length;
			result[resultOffset + 2] /= length;
		}
	}

	/**
	 * Translates matrix m in place. This is different from
	 * {@link android.opengl.Matrix#translateM(float[], int, float, float, float)}
	 * in that it translates the matrix in global coordinates rather than local
	 * coordinates. The code for this method is very simple:
	 * 
	 * <pre>
	 * m[mOffset + 12] += x;
	 * m[mOffset + 13] += y;
	 * m[mOffset + 14] += z;
	 * </pre>
	 * 
	 * @param m
	 *            matrix
	 * @param mOffset
	 *            index into m where the matrix starts
	 * @param x
	 *            translation factor x
	 * @param y
	 *            translation factor y
	 * @param z
	 *            translation factor z
	 */
	public static void globalTranslateM(float[] m, int mOffset, float x,
			float y, float z) {
		m[mOffset + 12] += x;
		m[mOffset + 13] += y;
		m[mOffset + 14] += z;
	}

	/*
	 * This code is from
	 * http://libgdx.googlecode.com/svn/trunk/gdx/src/com/badlogic
	 * /gdx/math/Quaternion.java (Copyright 2011 Mario Zechner, Nathan Sweet.
	 * See NOTICE file.), which is licensed under the Apache License, Version
	 * 2.0, available here: http://www.apache.org/licenses/LICENSE-2.0
	 */
	/**
	 * Converts the rotation portion of matrix m into a quaternion.
	 * 
	 * @param m
	 *            The matrix to convert
	 * @param mOffset
	 *            The offset into m where the matrix is located.
	 */
	public static float[] matrixToQuaternion(float[] m, int mOffset) {
		final float m00 = m[mOffset + 0], m01 = m[mOffset + 1], m02 = m[mOffset + 2];
		final float m10 = m[mOffset + 4], m11 = m[mOffset + 5], m12 = m[mOffset + 6];
		final float m20 = m[mOffset + 8], m21 = m[mOffset + 9], m22 = m[mOffset + 10];

		// the trace is the sum of the diagonal elements; see
		// http://mathworld.wolfram.com/MatrixTrace.html
		final float t = m00 + m11 + m22;

		// we protect the division by s by ensuring that s>=1
		double x, y, z, w;
		if (t >= 0) { // |w| >= .5
			double s = Math.sqrt(t + 1); // |s|>=1 ...
			w = 0.5 * s;
			s = 0.5 / s; // so this division isn't bad
			x = (m21 - m12) * s;
			y = (m02 - m20) * s;
			z = (m10 - m01) * s;
		} else if ((m00 > m11) && (m00 > m22)) {
			double s = Math.sqrt(1.0 + m00 - m11 - m22); // |s|>=1
			x = s * 0.5; // |x| >= .5
			s = 0.5 / s;
			y = (m10 + m01) * s;
			z = (m02 + m20) * s;
			w = (m21 - m12) * s;
		} else if (m11 > m22) {
			double s = Math.sqrt(1.0 + m11 - m00 - m22); // |s|>=1
			y = s * 0.5; // |y| >= .5
			s = 0.5 / s;
			x = (m10 + m01) * s;
			z = (m21 + m12) * s;
			w = (m02 - m20) * s;
		} else {
			double s = Math.sqrt(1.0 + m22 - m00 - m11); // |s|>=1
			z = s * 0.5; // |z| >= .5
			s = 0.5 / s;
			x = (m02 + m20) * s;
			y = (m21 + m12) * s;
			w = (m10 - m01) * s;
		}
		float[] quat = { (float) x, (float) y, (float) z, (float) w };
		return quat;
	}

	/**
	 * Normalizes a vector in place.
	 * 
	 * @param vec
	 *            The array that holds the vector to be normalized.
	 * @param vecOffset
	 *            The offset into vec where the vector is stored.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if vecOffset + 3 > vec.length, or if vecOffset is negative.
	 * @throws NullPointerException
	 *             if vec is null.
	 */
	public static void normalize(float[] vec, int vecOffset) {
		if (vec == null)
			throw new NullPointerException();
		if (vecOffset + 3 > vec.length || vecOffset < 0)
			throw new IndexOutOfBoundsException();

		float length = Utils.pythagF(vec[vecOffset + 0], vec[vecOffset + 1],
				vec[vecOffset + 1]);
		vec[vecOffset + 0] /= length;
		vec[vecOffset + 1] /= length;
		vec[vecOffset + 2] /= length;
	}

	/**
	 * Normalizes a vector, putting the normalized vector in result.
	 * 
	 * @param vec
	 *            The array that holds the vector to be normalized.
	 * @param vecOffset
	 *            The offset into vec where the vector is stored.
	 * @param result
	 *            The array that holds the normalized vector.
	 * @param resultOffset
	 *            The offset into result where the normalized vector will be
	 *            stored.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if resultOffset + 3 > result.length or vecOffset + 3 >
	 *             vec.length, or if resultOffset or vecOffset is negative.
	 * @throws NullPointerException
	 *             if result or vec is null.
	 */
	public static void normalize(float[] vec, int vecOffset, float[] result,
			int resultOffset) {
		if (result == null || vec == null)
			throw new NullPointerException();
		if (resultOffset + 3 > result.length || vecOffset + 3 > vec.length
				|| resultOffset < 0 || vecOffset < 0)
			throw new IndexOutOfBoundsException();

		float length = Utils.pythagF(vec[vecOffset + 0], vec[vecOffset + 1],
				vec[vecOffset + 1]);
		result[resultOffset + 0] = vec[vecOffset + 0] / length;
		result[resultOffset + 1] = vec[vecOffset + 1] / length;
		result[resultOffset + 2] = vec[vecOffset + 2] / length;
	}

	/*
	 * This code is from
	 * http://libgdx.googlecode.com/svn/trunk/gdx/src/com/badlogic
	 * /gdx/math/Quaternion.java (Copyright 2011 Mario Zechner, Nathan Sweet.
	 * See NOTICE file.), which is licensed under the Apache License, Version
	 * 2.0, available here: http://www.apache.org/licenses/LICENSE-2.0
	 */
	/**
	 * Converts a quaternion to a rotation matrix and stores that in the given
	 * transformation matrix.
	 * 
	 * @param quaternion
	 *            The quaternion to convert, given as a float array
	 * @param matrix
	 *            The transformation matrix in which to store the converted
	 *            rotation.
	 * @param mOffset
	 *            The offset into matrix where the transformation matrix is
	 *            stored.
	 * @see <a href=http://en.wikipedia.org/wiki/Quaternion>Quaternion
	 *      (Wikipedia)</a>
	 */
	public static void quaternionToMatrix(float[] quaternion, float[] matrix,
			int mOffset) {
		float xx = quaternion[0] * quaternion[0];
		float xy = quaternion[0] * quaternion[1];
		float xz = quaternion[0] * quaternion[2];
		float xw = quaternion[0] * quaternion[3];
		float yy = quaternion[1] * quaternion[1];
		float yz = quaternion[1] * quaternion[2];
		float yw = quaternion[1] * quaternion[3];
		float zz = quaternion[2] * quaternion[2];
		float zw = quaternion[2] * quaternion[3];
		// Set matrix from quaternion
		matrix[mOffset + 0] = 1 - 2 * (yy + zz);
		matrix[mOffset + 4] = 2 * (xy - zw);
		matrix[mOffset + 8] = 2 * (xz + yw);
		matrix[mOffset + 1] = 2 * (xy + zw);
		matrix[mOffset + 5] = 1 - 2 * (xx + zz);
		matrix[mOffset + 9] = 2 * (yz - xw);
		matrix[mOffset + 2] = 2 * (xz - yw);
		matrix[mOffset + 6] = 2 * (yz + xw);
		matrix[mOffset + 10] = 1 - 2 * (xx + yy);
	}
}
