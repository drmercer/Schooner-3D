package com.supermercerbros.gameengine.collision;

import com.supermercerbros.gameengine.util.Utils;

/**
 * A mathematical quantity with both magnitude and direction.
 */
public class Vector {
	public final float x;
	public final float y;
	public final float z;
	public final float length;

	/**
	 * Creates a new Vector in the given direction and with the given length. If
	 * <code>length = 0.0</code>, the components are unchanged.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param length
	 */
	Vector(final float x, final float y, final float z, final float length) {
		if (length == 0.0f) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.length = Utils.pythagF(x, y, z);
		} else {
			final float mag = Utils.pythagF(x, y, z);
			this.x = (x / mag) * length;
			this.y = (y / mag) * length;
			this.z = (z / mag) * length;
			this.length = length;
		}

	}

	/**
	 * Creates a new Vector between the given Points and with the given length.
	 * If <code>length = 0.0f</code>, the components are unchanged.
	 * 
	 * @param t
	 *            The tail Point
	 * @param h
	 *            the head Point
	 * @param length
	 *            The length of the Vector, or 0.0f if the length should be left
	 *            as is.
	 */
	public Vector(Point t, Point h, final float length) {
		final float x = h.x - t.x;
		final float y = h.y - t.y;
		final float z = h.z - t.z;

		if (length == 0.0f) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.length = Utils.pythagF(x, y, z);
		} else {
			final float mag = Utils.pythagF(x, y, z);
			this.x = (x / mag) * length;
			this.y = (y / mag) * length;
			this.z = (z / mag) * length;
			this.length = length;
		}
	}

	/**
	 * Computes the normalized cross product of this Vector and another
	 * Vector. (The vectors are normalized before the operation.)
	 * 
	 * @param v
	 *            The other vector.
	 * @return The cross product of the two vectors.
	 */
	public Vector cross(Vector v) {
		final float vx = v.x / v.length, vy = v.y / v.length, vz = v.z
				/ v.length;

		final float rx = this.y * vz - this.z * vy;
		final float ry = this.z * vx - this.x * vz;
		final float rz = this.x * vy - this.y * vx;

		return new Vector(rx, ry, rz, 0.0f);
	}

	/**
	 * Computes the cosine of the angle between this Vector and another Vector.
	 * The cosine is equal to the dot product ({@link #dot(Vector)}) divided by
	 * the product of the vectors' magnitudes.
	 * 
	 * @param other
	 *            The other vector.
	 * @return The cosine of the angle between the two vectors.
	 */
	public float cos(Vector other) {
		if (equals(other)) {
			return 1.0f;
		} else {
			return (this.x * other.x + this.y * other.y + this.z * other.z)
					/ (this.length * other.length);
		}
	}

	/**
	 * Computes the dot product of this Vector and another vector. The
	 * computation is this:
	 * 
	 * <pre>
	 * this.x * other.x + this.y * other.y + this.z * other.z
	 * </pre>
	 * 
	 * @param other
	 *            The other Vector.
	 * @return The dot product of the two vectors.
	 * @see <a href="http://en.wikipedia.org/wiki/Dot_product">Dot Product
	 *      (Wikipedia)</a>
	 */
	public float dot(Vector other) {
		if (equals(other)) {
			return length * length;
		} else {
			return this.x * other.x + this.y * other.y + this.z * other.z;
		}
	}

	/**
	 * Normalizes this vector.
	 * 
	 * @return A unit vector (length = 1) whose direction is equal to that of
	 *         this Vector.
	 */
	public Vector normalize() {
		return new Vector(x, y, z, 1.0f);
	}

	/**
	 * Transforms this Vector by the given transformation matrix.
	 * 
	 * @param matrix
	 * @return The transformed Vector
	 */
	public Vector transform(final Matrix matrix) {
		// TODO: Support non-uniform axis scale
		final float tx = matrix.m0 * x + matrix.m4 * y + matrix.m8 * z;
		final float ty = matrix.m1 * x + matrix.m5 * y + matrix.m9 * z;
		final float tz = matrix.m2 * x + matrix.m6 * y + matrix.m10 * z;
		return new Vector(tx, ty, tz, 0.0f);
	}

	/**
	 * Flips this Vector.
	 * 
	 * @return
	 */
	public Vector flip() {
		return new Vector(-x, -y, -z, 0.0f);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Vector)){
			return false;
		}
		
		final Vector v = (Vector) o;
		
		return x == v.x && y == v.y && z == v.z;
	}
	
	@Override
	public String toString() {
		return "Vector[" + x + ", " + y + ", " + z + "]";
	}
}
