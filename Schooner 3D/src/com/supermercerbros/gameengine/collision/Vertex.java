package com.supermercerbros.gameengine.collision;

import java.util.LinkedList;
import java.util.List;

public class Vertex extends Feature implements Point {
	/**
	 * A coordinate of the Vertex
	 */
	private final float x, y, z;
	/**
	 * The edges that end or begin at this Vertex.
	 */
	private List<Edge> coboundary;
	/**
	 * True if this Vertex is locked.
	 */
	private boolean locked;

	/**
	 * Constructs a Vertex at the given point.
	 * 
	 * @param x
	 *            The X-coordinate of the Vertex's location.
	 * @param y
	 *            The Y-coordinate of the Vertex's location.
	 * @param z
	 *            The Z-coordinate of the Vertex's location.
	 */
	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		locked = false;
		coboundary = new LinkedList<Edge>();
	}

	/**
	 * Locks this Vertex to prevent further changes.
	 */
	@Override
	protected void lock() {
		// TODO Do locking stuff for coboundary of Vertex
		locked = true;
	}

	/**
	 * Adds an Edge to this vertex's coboundary. Fails with an
	 * <code>AssertionError</code> if this vertex is locked.
	 * 
	 * @param edge
	 *            The Edge to add to this vertex's coboundary
	 */
	protected void addEdge(Edge edge) {
		if(locked){
			throw new IllegalStateException("Vertex is locked. Cannot add edge.");
		}
		coboundary.add(edge);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}
}
