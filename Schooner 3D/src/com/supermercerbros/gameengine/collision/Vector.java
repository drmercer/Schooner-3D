package com.supermercerbros.gameengine.collision;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a vector, which is a mathematical quantity with both direction and magnitude (i.e. an arrow).
 * 
 * @see <a href=http://www.youtube.com/watch?v=KbrEBpCw3Ag&t=00m05s>"Vector" from Despicable Me</a> :P
 */
public class Vector {
	private float x, y, z;

	public Vector(Point tail, Point head, boolean normalized) {
		float length;
		if (normalized) {
			length = Utils.pythagF(x, y, z);
		} else {
			length = 1.0f;
		}
		x = (head.getX() - tail.getX()) / length;
		y = (head.getY() - tail.getY()) / length;
		z = (head.getZ() - tail.getZ()) / length;
	}

	public Vector(float x, float y, float z, boolean normalized) {
		float length;
		if (normalized) {
			length = Utils.pythagF(x, y, z);
		} else {
			length = 1.0f;
		}
		this.x = x / length;
		this.y = y / length;
		this.z = z / length;
	}

	/**
	 * Computes the cross product of this Vector and another Vector.
	 * 
	 * @param v
	 *            The other vector.
	 * @return The cross product of the two vectors.
	 */
	public Vector cross(Vector v) {
		// TODO write cross product code.
		return null;
	}

	/**
	 * Computes the dot product of this Vector and another Vector. The dot
	 * product is equal to the cosine of the angle between the two vectors.
	 * 
	 * @param v
	 *            The other vector.
	 * @return The dot product of the two vectors.
	 */
	public float dot(Vector v) {
		// TODO write dot product code.
		return 0;
	}

	/**
	 * Normalizes this Vector.
	 * @return this, for chaining.
	 */
	public Vector normalize() {
		float length = Utils.pythagF(x, y, z);
		x /= length;
		y /= length;
		z /= length;
		return this;
	}

}
