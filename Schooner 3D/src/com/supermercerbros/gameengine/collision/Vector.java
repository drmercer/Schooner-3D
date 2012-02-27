package com.supermercerbros.gameengine.collision;

import com.supermercerbros.gameengine.util.Utils;

public class Vector {
	private float x, y, z;

	public Vector(Point tail, Point head, boolean normalized) {
		x = head.getX() - tail.getX();
		y = head.getY() - tail.getY();
		z = head.getZ() - tail.getZ();

		if (normalized) {
			float length = Utils.pythagF(x, y, z);
			x /= length;
			y /= length;
			z /= length;
		}
	}

	public Vector(float x, float y, float z, boolean normalized) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (normalized) {
			float length = Utils.pythagF(x, y, z);
			x /= length;
			y /= length;
			z /= length;
		}
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
