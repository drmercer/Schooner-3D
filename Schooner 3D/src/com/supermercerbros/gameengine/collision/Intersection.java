package com.supermercerbros.gameengine.collision;

public class Intersection extends Throwable {
	private static final long serialVersionUID = 1L;

	/**
	 * The point of intersection, or null if the point is unknown. Given in the
	 * Face's coordinates.
	 */
	public final Point point;

	/**
	 * Creates an Intersection with a known point of intersection.
	 * 
	 * @param intersection
	 *            The point of intersection.
	 */
	public Intersection(Point intersection) {
		point = intersection;
	}

	/**
	 * Creates an Intersection with an unknown point of intersection.
	 */
	public Intersection() {
		point = null;
	}
}
