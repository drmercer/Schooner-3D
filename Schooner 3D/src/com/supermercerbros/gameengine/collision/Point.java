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

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a point in 3D space. Points are immutable (not entirely true for subclasses).
 */
public class Point {

	public final float x;
	public final float y;
	public final float z;

	/**
	 * Constructs a Point at the given location.
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Computes the distance from this Point to another Point.
	 * 
	 * @param other
	 *            The other Point to measure to.
	 * @return The distance between the two points.
	 */
	public float distanceTo(final Point other) {
		return Utils.pythagF(x - other.x, y - other.y, z - other.z);
	}

	/**
	 * Transforms this Point by the given transformation matrix.
	 * @param matrix
	 * @return
	 */
	public Point transform(final Matrix matrix) {
		return matrix.transform(x, y, z);
	}
	
	/**
	 * Averages two Points.
	 * @param a
	 * @param b
	 * @return
	 */
	public static Point average(final Point a, final Point b) {
		final float x = (a.x + b.x) / 2;
		final float y = (a.y + b.y) / 2;
		final float z = (a.z + b.z) / 2;
		return new Point(x, y, z);
	}
	
	/**
	 * Translates this Point along the given Vector.
	 * @param v The vector to translate along.
	 * @return The translated Point
	 */
	public Point translate(final Vector v) {
		return new Point(x + v.x, y + v.y, z + v.z);
	}
	
	@Override
	public String toString() {
		return "Point[" + x + ", " + y + ", " + z + "]";
	}
}
