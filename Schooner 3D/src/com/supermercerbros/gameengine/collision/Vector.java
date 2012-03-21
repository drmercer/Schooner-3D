package com.supermercerbros.gameengine.collision;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a vector, which is a mathematical quantity with both direction and
 * magnitude (i.e. an arrow).
 * TODO Remove possibility of non-unit vector?
 * 
 * @see <a href=http://www.youtube.com/watch?v=KbrEBpCw3Ag&t=00m05s>"Vector"
 *      from Despicable Me</a> :P
 */
public class Vector {
	private float x, y, z;
	private boolean unit;

	public Vector(Point tail, Point head, boolean normalized) {
		float length;
		x = (head.getX() - tail.getX());
		y = (head.getY() - tail.getY());
		z = (head.getZ() - tail.getZ());

		unit = normalized;
		if (normalized) {
			length = Utils.pythagF(x, y, z);
			x /= length;
			y /= length;
			z /= length;
		}
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
	public synchronized Vector cross(Vector v) {
		Vector result = new Vector(0.0f, 0.0f, 0.0f, false);

		result.x = this.y * v.z - this.z * v.y;
		result.y = this.z * v.x - this.x * v.z;
		result.z = this.x * v.y - this.y * v.x;

		return result;
	}

	/**
	 * Computes the dot product of this Vector and another Vector. The dot
	 * product of two unit vectors is equal to the cosine of the angle between the two vectors.
	 * 
	 * @param v
	 *            The other vector.
	 * @param normalize True if the vectors should be normalized before computing the dot product.
	 * @return The dot product of the two vectors.
	 */
	public synchronized float dot(Vector v, boolean normalize) {
		Vector a, b;
		if (normalize) {
			a = this.toUnitVector();
			b = v.toUnitVector();
		} else {
			a = this;
			b = v;
		}
		
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	/**
	 * Normalizes this Vector.
	 * 
	 * @return this, for chaining.
	 */
	public synchronized Vector normalize() {
		unit = true;
		float length = Utils.pythagF(x, y, z);
		x /= length;
		y /= length;
		z /= length;
		return this;
	}

	/**
	 * Duplicates this vector and normalizes the duplicate. Identical result to
	 * <code>duplicate(1.0f)</code>.
	 * 
	 * @return The duplicate Vector.
	 */
	public synchronized Vector toUnitVector() {
		if (unit) {
			return this;
		} else {
			return new Vector(x, y, z, true);
		}
	}

	/**
	 * Checks whether this Vector is normalized.
	 * 
	 * @return True if this Vector is a unit vector; that is, it's length is
	 *         <code>1.0f</code>.
	 */
	public synchronized boolean isUnitVector() {
		return unit;
	}

	/**
	 * Duplicates this Vector and gives the duplicate the given length.
	 * 
	 * @param length
	 *            The length of the duplicate, or <code>0.0f</code> if it should
	 *            be the same length as this Vector.
	 * @return The duplicate Vector.
	 */
	public synchronized Vector duplicate(float length) {
		if (length == 1.0f) {
			return new Vector(x, y, z, true);
		} else if (length == 0.0f) {
			return new Vector(x, y, z, false);
		} else {
			Vector vec = new Vector(x, y, z, true);
			vec.scale(length);
			return vec;
		}
	}

	private void scale(float factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}

}
