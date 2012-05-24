package com.supermercerbros.gameengine.collision;

import java.util.Collections;
import java.util.List;

public class Collision extends Throwable {
	private static final long serialVersionUID = -1094129965156016258L;
	
	/**
	 * An <b>unmodifiable</b> List of Points where intersection occurs.
	 */
	public final List<Point> points;
	/**
	 * The vector representing the normal of collision. Since only one collision
	 * can occur between two convex objects, there is only one vector while
	 * there may be many points of collision. This Vector is given in global
	 * coordinates, and is the average of {@link Collision#normalReference}'s
	 * normals at the points of collision.
	 */
	public final Vector vector;

	/**
	 * The Collider whose normals were used when calculating
	 * {@link #vector};
	 */
	public final Collider normalReference;

	public Collision(Vector normal, Collider normalReference, List<Point> points) {
		this.points = Collections.unmodifiableList(points);
		this.vector = normal;
		this.normalReference = normalReference;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Collision {");
		sb.append("\n\t" + vector);
		for (final Point p : points) {
			sb.append("\n\t" + p);
		}
		sb.append("}");
		return sb.toString();
	}

}
