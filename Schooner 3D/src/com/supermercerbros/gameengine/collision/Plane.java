package com.supermercerbros.gameengine.collision;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a geometric plane. A Plane object is immutable.
 */
public class Plane {
	final Vector normal;
	final Point point;
	/**
	 * The tolerance of {@link #pointIsInFront(Point)} checks. If the cosine is
	 * greater than or equal to this, the test passes.
	 */
	public static final float TOLERANCE = -.000f;

	public Plane(final Point point, final Vector normal) {
		this.normal = normal.normalize();
		this.point = point;
	}

	public Plane transform(Matrix matrix) {
		return new Plane(point.transform(matrix), normal.transform(matrix));
	}

	/**
	 * Returns the distance from this <code>Plane</code> to the given
	 * <code>Point</code>. The distance is negative if the point is behind the
	 * plane.
	 * 
	 * @param p
	 *            The Point to find the distance to.
	 * @return The signed distance between this Plane and p.
	 */
	public double distanceTo(final Point p) {
		return (p.x - point.x) * normal.x + (p.y - point.y) * normal.y
				+ (p.z - point.z) * normal.z;
	}

	/**
	 * Returns the intersection of this Plane and the given Edge.
	 * 
	 * @param edge
	 *            The Edge to intersect with this Plane.
	 * @param matrix
	 * @return The Point of intersection, or null if the Edge does not intersect
	 *         this Plane.
	 */
	public Point intersect(final Line edge) {
		final double hDistance = -distanceTo(edge.head);
		final float alpha = (float) (hDistance / (hDistance + distanceTo(edge.tail)));
		
		if (alpha < 0.0 || alpha > 1.0) {
			return null;
		} else {
			return new Point(
					edge.head.x + alpha * (edge.tail.x - edge.head.x), 
					edge.head.y + alpha * (edge.tail.y - edge.head.y), 
					edge.head.z + alpha * (edge.tail.z - edge.head.z));
		}
	}

	/**
	 * Checks if the given point is in front of this Plane.
	 * 
	 * @param x
	 *            The x-coordinate of the Point to check.
	 * @param y
	 *            The y-coordinate of the Point to check.
	 * @param z
	 *            The z-coordinate of the Point to check.
	 * @return True if the point is in front of this Plane
	 */
	public boolean pointIsInFront(final float x, final float y, final float z) {
		return pointIsInFront(new Point(x, y, z));
	}

	/**
	 * Checks if the given point is in front of this Plane.
	 * 
	 * @param p
	 *            The Point to check.
	 * @return True if the point is in front of this Plane
	 */
	public boolean pointIsInFront(final Point p) {
		float x, y, z, length;
		x = p.x - point.x;
		y = p.y - point.y;
		z = p.z - point.z;
		length = Utils.pythagF(x, y, z);
		x /= length;
		y /= length;
		z /= length;
		float dot = x * normal.x + y * normal.y + z * normal.z;
		return dot >= TOLERANCE;
	}

	public Point projectPointOnto(final Point p) {
		final Vector v = new Vector(normal.x, normal.y, normal.z,
				(float) -distanceTo(p));
		return p.translate(v);
	}

	@Override
	public String toString(){
		return "Plane(" + ((Point) point).toString() + ", " + normal + ")";
	}
}
