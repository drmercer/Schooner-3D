/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a Vertex on a mesh.
 */
public class Vertex extends Point implements Feature {
	/**
	 * The edges that end or begin at this Vertex.
	 */
	private final ArrayList<Edge> coboundary;

	/**
	 * The voronoi region of this Vertex
	 */
	private final LinkedList<Plane> voronoi;
	
	private final String debugID;
	private static int nextID = 0;
	
	@Override
	public String toString() {
		return debugID;
	}
	public static void resetIdCounter() {
		nextID = 0;
	}

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
	public Vertex(final float x, final float y, final float z) {
		super(x, y, z);
		coboundary = new ArrayList<Edge>();
		voronoi = new LinkedList<Plane>();
		
		this.debugID = "Vert " + nextID++;
	}

	/**
	 * Adds an Edge to this vertex's coboundary. Fails with an
	 * <code>IllegalStateException</code> if this vertex is locked.
	 * 
	 * @param edge
	 *            The Edge to add to this vertex's coboundary
	 * @param constraint
	 *            The constraint plane between <code>edge</code> and this Vertex
	 */
	protected void addEdge(Edge edge, Plane constraint) {
		coboundary.add(edge);
		voronoi.add(constraint);
	}

	/**
	 * @return An <b>unmodifiable</b> List of the Vertex's edges.
	 */
	public List<Edge> getEdges() {
		return Collections.unmodifiableList(coboundary);
	}

	@Override
	public Feature test(Feature other, Matrix matrix) throws Intersection,
			LocalDistMinimum {
		if (coboundary.isEmpty()){
			return null; //Nothing to check (such as in a SphereBounds)
		}
		
		if (other instanceof Face) {
			return testF((Face) other, matrix);
		} else if (other instanceof Edge) {
			return testE((Edge) other, matrix);
		} else if (other instanceof Vertex) {
			return testV((Vertex) other, matrix);
		} else {
			throw new IllegalArgumentException(
					"other is not a Face, Edge, or Vertex");
		}
	}

	private Feature testV(Vertex other, Matrix matrix) {
		final Point p = other.transform(matrix);
		return checkPoint(p);
	}

	private Edge testE(Edge e, Matrix matrix) {
		final Line tEdge = e.transform(matrix);
		final Vector vector = tEdge.vector, vectorInverse = vector.flip();
		final Vector fromHead = new Vector(tEdge.head, this, 1.0f);
		final Vector fromTail = new Vector(tEdge.tail, this, 1.0f);

		final Point closestPoint;
		if (vector.cos(fromTail) < Plane.TOLERANCE) {
			closestPoint = tEdge.tail;
		} else if (vectorInverse.cos(fromHead) < Plane.TOLERANCE) {
			closestPoint = tEdge.head;
		} else {
			closestPoint = tEdge.projectPoint(this);
		}
		return checkPoint(closestPoint);
	}

	private Edge testF(Face f, Matrix matrix) {
		final Point closestPoint = f.transform(matrix).projectPointOnto(
				this);
		return checkPoint(closestPoint);
	}

	private Edge checkPoint(final Point p) {
		double distance = CollisionDetector.CONSTRAINT_CHECK_TOLERANCE;
		Edge infringed = null;

		for (ListIterator<Plane> vIter = voronoi.listIterator(); vIter
				.hasNext();) {
			final int i = vIter.nextIndex();
			final Plane constraint = vIter.next();

			final double d = constraint.distanceTo(p);
			if (d < distance) {
				distance = d;
				infringed = coboundary.get(i);
			}
		}
		
		if (infringed == null) {
			CollisionDetector.setClosestPoint(p);
			return null;
		} else {
			return infringed;
		}
	}

}
