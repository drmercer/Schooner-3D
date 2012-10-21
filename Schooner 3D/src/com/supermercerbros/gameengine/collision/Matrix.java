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

package com.supermercerbros.gameengine.collision;

public class Matrix {
	/**
	 * The column-major matrix stored in a float array.
	 */
	final float m0, m1, m2, m4, m5, m6, m8, m9, m10, m12, m13, m14;

	/**
	 * Constructs a new Matrix that can give the position of a point (given in
	 * b's coordinate system) in a's coordinate system.
	 * 
	 * @param a
	 * @param b
	 */
	public Matrix(float[] a, float[] b) {
		final float[] m = new float[16];
		final float[] aInverse = new float[16];
		
		android.opengl.Matrix.invertM(aInverse, 0, a, 0);
		android.opengl.Matrix.multiplyMM(m, 0, aInverse, 0, b, 0);

		m0 = m[0];
		m1 = m[1];
		m2 = m[2];
		
		m4 = m[4];
		m5 = m[5];
		m6 = m[6];
		
		m8 = m[8];
		m9 = m[9];
		m10 = m[10];
		
		m12 = m[12];
		m13 = m[13];
		m14 = m[14];
	}
	
	public Matrix(float[] m){
		m0 = m[0];
		m1 = m[1];
		m2 = m[2];
		
		m4 = m[4];
		m5 = m[5];
		m6 = m[6];
		
		m8 = m[8];
		m9 = m[9];
		m10 = m[10];
		
		m12 = m[12];
		m13 = m[13];
		m14 = m[14];
	}

	/**
	 * Constructs an identity matrix.
	 */
	public Matrix() {
		m1 = m2 = m4 = m6 = m8 = m9 = m12 = m13 = m14 = 0.0f;
		m0 = m5 = m10 = 1.0f;
	}

	public Point transform(float x, float y, float z) {
		final float rx = m0 * x + m4 * y + m8 * z + m12;
		final float ry = m1 * x + m5 * y + m9 * z + m13;
		final float rz = m2 * x + m6 * y + m10 * z + m14;
		return new Point(rx, ry, rz);
	}

}
