package com.supermercerbros.gameengine.collision;

public class Plane {
	Vector normal;
	Point point;

	public Plane(Point point, Vector normal) {
		this.normal = normal;
		this.point = point;
	}

	/**
	 * Checks if the given point is in front of this Plane.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param tolerance
	 *            The tolerance of the check.
	 * @return True if the point is in front of this Plane
	 */
	public boolean pointIsInFront(float x, float y, float z, float tolerance) {
		float dot = new Vector(point, new Vertex(x, y, z), true).dot(normal,
				true);
		return dot >= Math.abs(tolerance);
	}

	/**
	 * Returns the intersection of this Plane and the given Edge.
	 * 
	 * @param edge
	 *            The Edge to intersect with this Plane.
	 * @return The Point of intersection, or null if the Edge does not intersect
	 *         this Plane.
	 */
	public Point intersect(Edge edge) {
		// TODO Plane.intersect(Edge)
		return null;
	}

	/**
	 * Returns the distance from the Plane to the point. The distance is
	 * negative if the point is behind the plane.
	 * 
	 * @param p
	 *            The Point to find the distance to.
	 * @return The signed distance between this Plane and p.
	 */
	public double distanceTo(Point p) {
		// TODO Plane.distance(Point)
		return 0;
	}
}
