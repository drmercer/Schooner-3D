package com.supermercerbros.gameengine.math;

import com.supermercerbros.gameengine.collision.Point;

/**
 * Represents a rotation. (Although, technically speaking, a quaternion is a
 * four-component complex number with three imaginary parts.) Given an
 * axis-angle rotation, the corresponding quaternion is equal to
 * <code>{cos(theta/2), axis.x * sin(theta/2), axis.y * sin(theta/2), axis.z * sin(theta/2)}</code>
 * . Like {@link Point} objects, Quaternion objects are immutable.
 */
public class Quaternion {
	/**
	 * The W-component of the Quaternion.
	 */
	final float w;
	/**
	 * The X-component of the Quaternion.
	 */
	final float x;
	/**
	 * The Y-component of the Quaternion.
	 */
	final float y;
	/**
	 * The Z-component of the Quaternion.
	 */
	final float z;
	
	/**
	 * Creates a new Quaternion with the given components.
	 * 
	 * @param w
	 *            The W-component of the Quaternion.
	 * @param x
	 *            The X-component of the Quaternion.
	 * @param y
	 *            The Y-component of the Quaternion.
	 * @param z
	 *            The Z-component of the Quaternion.
	 */
	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a Quaternion form of the given Point.
	 * <code>{0, p.x, p.y, p.z}</code>
	 * 
	 * @param p
	 *            The point to convert to Quaternion form.
	 */
	private Quaternion(Point p) {
		this(0, p.x, p.y, p.z);
	}
	
	/**
	 * Converts this Quaternion to a Point. <code>(x, y, z)</code>
	 * 
	 * @return The point form of this Quaternion.
	 */
	private Point toPoint() {
		return new Point(x, y, z);
	}
	
	/**
	 * Returns the conjugate of this Quaternion.
	 * 
	 * @return Returns the conjugate of this Quaternion, which is simply
	 *         <code>{w, -x, -y, -z}</code>
	 */
	private Quaternion conjugate() {
		return new Quaternion(w, -x, -y, -z);
	}
	
	/**
	 * Applies the rotation represented by this Quaternion to the given Point.
	 * 
	 * @param p
	 *            The Point to rotate.
	 * @return A new Point at the location of the rotated point.
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
	 *      #Using_quaternion_rotations>Quaternion Rotations (Wikipedia)</a>
	 */
	public Point rotate(Point p) {
		return this.multiply(new Quaternion(p)).multiply(this.conjugate())
				.toPoint();
	}
	
	/**
	 * Multiplies this Quaternion (left-hand side) with another Quaternion
	 * (left-hand side).
	 * 
	 * @param q
	 *            The right-hand Quaternion
	 * 
	 * @return The Hamilton product of the two quaternions
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Quaternion#Hamilton_product>Hamilton
	 *      Product (Wikipedia)</a>
	 */
	public Quaternion multiply(Quaternion q) {
		//@formatter:off
		return new Quaternion(
				(w * q.w - x * q.x - y * q.y - z * q.z), 
				(w * q.x + x * q.w + y * q.z - z * q.y),
				(w * q.y - x * q.z + y * q.w + z * q.x), 
				(w * q.z + x * q.y - y * q.x + z * q.w));
		//@formatter:on
	}

	/**
	 * Applies the rotation represented by this Quaternion to the given point.
	 * 
	 * @param x The x-coordinate of the Point to rotate.
	 * @param y The y-coordinate of the Point to rotate.
	 * @param z The z-coordinate of the Point to rotate.
	 * 
	 * @return A new Point at the location of the rotated point.
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
	 *      #Using_quaternion_rotations>Quaternion Rotations (Wikipedia)</a>
	 */
	public Point rotate(float x, float y, float z) {
		return this.multiply(new Quaternion(0, x, y, z)).multiply(this.conjugate())
				.toPoint();
	}
}
