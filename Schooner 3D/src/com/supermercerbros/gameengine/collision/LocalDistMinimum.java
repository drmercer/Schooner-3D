package com.supermercerbros.gameengine.collision;

/**
 * Thrown when a possible local distance minimum occurs. When the closest point
 * on a feature is behind a face but still inside it's constraint planes, it is
 * either on the other side of the face's polyhedron (a local distance minimum)
 * or inside the face's polyhedron.
 * 
 * This should only be thrown by {@link Face#test(Feature, Matrix)};
 */
public class LocalDistMinimum extends Throwable {
	private static final long serialVersionUID = -9180046964472056920L;
	/**
	 * The point that may be a local distance minimum (it's behind a face).
	 * Coordinates in the face's coordinate system.
	 */
	public final Point p;

	public LocalDistMinimum(final Point p) {
		this.p = p;
	}
}
