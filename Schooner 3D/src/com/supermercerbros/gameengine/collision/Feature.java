package com.supermercerbros.gameengine.collision;

/**
 * Represents a Face, Edge, or Vertex.
 */
public interface Feature {
	/**
	 * Checks the given Feature against this Feature's constraint planes.
	 * 
	 * @param other
	 *            The Feature to test.
	 * @param matrix
	 *            The transformation matrix defining <code>other</code>'s
	 *            coordinate system relative to this Feature's coordinate
	 *            system.
	 * @return The next closest Feature, or <code>null</code> if there is none.
	 * 
	 * @throws Intersection
	 *             If an Edge is found to be intersecting a Face.
	 * @throws LocalDistMinimum
	 *             If a Feature is behind a Face and inside its constraint
	 *             planes.
	 */
	public Feature test(Feature other, Matrix matrix) throws Intersection,
			LocalDistMinimum;
}
