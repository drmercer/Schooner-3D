package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class Face extends Plane implements Feature {

	/**
	 * A List of the Edges in that border this face
	 */
	private final ArrayList<Edge> edges;
	/**
	 * A List of the constraint planes that make up this Face's voronoi region.
	 * 
	 * Constraint planes <b>always</b> face away from edges, toward faces and
	 * vertices.
	 */
	private final ArrayList<Plane> voronoi;
	/**
	 * The number of edges in this face. Equal to edges.size()
	 */
	private final int edgeCount;
	
	// Fields for debugging:
	private final String debugID;
	private static int nextID = 0;

	@Override
	public String toString() {
		return debugID;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public Face(Vector normal, Edge... edges) {
		super(getAveragePoint(edges), normal);
		if (edges.length < 3) {
			throw new IllegalArgumentException(
					"A Face cannot be constructed with less than 3 edges.");
		}
		this.edges = new ArrayList<Edge>(Arrays.asList(edges));
		this.voronoi = new ArrayList<Plane>();

		for (final Edge edge : edges) {
			final Plane constraint = edge.addFace(this, point);
			voronoi.add(constraint);
		}

		edgeCount = this.edges.size();

		this.debugID = "Face " + nextID++;
	}

	private static Point getAveragePoint(Edge[] edges) {
		float avgX = 0, avgY = 0, avgZ = 0;
		for (final Edge edge : edges) {
			avgX += edge.head.x;
			avgY += edge.head.y;
			avgZ += edge.head.z;
		}
		avgX /= edges.length;
		avgY /= edges.length;
		avgZ /= edges.length;

		return new Point(avgX, avgY, avgZ);
	}

	public static void resetIdCounter() {
		nextID = 0;
	}

	@Override
	public Feature test(Feature other, Matrix matrix) throws Intersection,
			LocalDistMinimum {
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

	private Feature testF(Face face, Matrix matrix) throws LocalDistMinimum {
		final Plane tFace = face.transform(matrix);

		final boolean parallel = Math.abs(tFace.normal.dot(this.normal)) == 1.f;
		if (parallel) { // Faces are parallel; check if they overlap when
						// projected together.
			Point potentialClosest = tFace.projectPointOnto(Point.average(
					tFace.point, this.point));
			double distance = CollisionDetector.CONSTRAINT_CHECK_TOLERANCE;
			Edge infringed = null;

			for (ListIterator<Plane> vIter = voronoi.listIterator(); vIter
					.hasNext();) {
				final int i = vIter.nextIndex();
				final Plane constraint = vIter.next();

				final double d = constraint.distanceTo(potentialClosest);
				if (d < distance) {
					distance = d;
					infringed = edges.get(i);
				}
			}

			if (infringed == null
					&& this.distanceTo(potentialClosest) < CollisionDetector.CONSTRAINT_CHECK_TOLERANCE) {
				throw new LocalDistMinimum(potentialClosest);
			}
			if (infringed != null) {
				for (final Edge edge : face.edges) {
					final Point p = edge.head.transform(matrix);
					if (pointPasses(p)) {
						CollisionDetector
								.setDistance(distanceTo(potentialClosest));
						return null;
					}
				}
				return infringed;
			} else {
				CollisionDetector.setClosestPoint(potentialClosest);
				return null;
			}
		} else { // Not parallel, not closest.
			Vertex v1 = edges.get(0).head;
			double distance = Math.abs(tFace.distanceTo(v1));
			Vertex closer = v1;

			for (int i = 1; i < edgeCount; i++) {
				final Vertex v = edges.get(i).head;
				final double d = Math.abs(tFace.distanceTo(v));
				if (d < distance) {
					distance = d;
					closer = v;
				}
			}

			return closer;
		}
	}

	/**
	 * Checks the Point p (given in this Face's coordinate system) against this
	 * Face's voronoi region.
	 * 
	 * @param p
	 *            The point to check.
	 * @return
	 */
	public boolean pointPasses(Point p) {
		for (final Plane constraint : voronoi) {
			final double d = constraint.distanceTo(p);
			if (d < CollisionDetector.CONSTRAINT_CHECK_TOLERANCE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the Point p (given in global coordinates) against this Face's
	 * voronoi region.
	 * 
	 * @param p
	 *            The point to check.
	 * @param faceM
	 *            The matrix defining this Face's transformation.
	 * @return
	 */
	public boolean pointPasses(Point p, Matrix faceM) {
		for (final Plane constraint : voronoi) {
			final double d = constraint.transform(faceM).distanceTo(p);
			if (d < CollisionDetector.CONSTRAINT_CHECK_TOLERANCE) {
				return false;
			}
		}
		return true;
	}

	private Edge testE(Edge edge, Matrix matrix) throws Intersection,
			LocalDistMinimum {
		final Line tEdge = edge.transform(matrix);
		final Vector tVector = tEdge.vector;
		final Point intersection = intersect(tEdge);
		final Point head = tEdge.head, tail = tEdge.tail;

		if (intersection != null) {
			// If edge intersects plane

			int index = -1;
			double distance = 0.0;
			for (int i = 0; i < edgeCount; i++) {
				Plane constraint = voronoi.get(i);

				double d = constraint.distanceTo(intersection);

				if (d < distance) {
					distance = d;
					index = i;
				}

			}
			if (index > -1) {
				return edges.get(index);
			} else {
				throw new Intersection(intersection);
			}

		} else if (tVector.cos(normal) == 0.0) {
			// If edge is parallel to Face

			Point headward = null, tailward = null;
			float hDistance = tEdge.length;
			float tDistance = hDistance;
			float twh = 0.0f;
			int index = -1;

			for (int i = 0; i < edgeCount; i++) {
				final Plane constraint = voronoi.get(i);

				// For each constrant plane, intersect it with edge.
				final Point p = constraint.intersect(tEdge);

				if (p == null) {
					// If the edge does not intersect the constraint

					if (constraint.pointIsInFront(head)) {
						// If the edge is in front of the constraint
						continue;
					} else {
						// If the edge is behind the constraint, we know
						// it is not the closest feature
						if (index == -1) {
							hDistance = 0.0f;
						}
						index = i;
						double d = constraint.distanceTo(head);
						if (d < hDistance) {
							hDistance = (float) d;
						}
					}

				} else {
					// If the edge does intersect the constraint and we haven't
					// ruled it out

					boolean facingHead = tVector.cos(constraint.normal) > 0.0;

					if (facingHead) {
						// If the constraint is facing the head

						float ph = p.distanceTo(head);
						if (ph < hDistance) {
							headward = p;
							hDistance = ph;
						}

					} else {
						// If the constraint is facing the tail

						float pt = p.distanceTo(tail);
						if (pt < tDistance) {
							tailward = p;
							tDistance = pt;
							twh = p.distanceTo(head);
						}
					}

					if (hDistance < twh) {
						index = i;
						hDistance = (float) constraint
								.distanceTo(facingHead ? tail : head);
					}
				}
			}

			if (index == -1) {
				final Point p;
				
				if (headward != null && tailward != null) {
					p = Point.average(headward, tailward);
				} else if (headward != null) {
					p = headward;
				} else if (tailward != null) {
					p = tailward;
				} else {
					p = Point.average(tail, head);
				}

				if (pointIsInFront(p)) {
					CollisionDetector.setClosestPoint(p);
					return null;
				} else {
					throw new LocalDistMinimum(p);
				}
			} else {
				return edges.get(index);
			}

		} else {
			// If edge is not parallel and doesn't intersect

			double distance = 0.0;
			int index = -1;

			// Check head and tail against all planes
			for (int i = 0; i < edgeCount; i++) {
				final Plane constraint = voronoi.get(i);

				final double h = constraint.distanceTo(head);
				final double t = constraint.distanceTo(tail);

				if (h < distance || t < distance) {
					distance = h < t ? h : t;
					index = i;
				}

			}
			if (index > -1) {
				// If an endpoint failed, return the edge whose constraint was
				// most infringed
				return edges.get(index);
			} else {
				// No endpoint failed
				final double distToTail = distanceTo(tail), distToHead = distanceTo(head);
				if (distToTail > distToHead) {
					CollisionDetector.setClosestPoint(head);
				} else {
					CollisionDetector.setClosestPoint(tail);
				}
				return null;
			}
		}
	}

	private Edge testV(Vertex vert, Matrix matrix) throws LocalDistMinimum {
		final Point tVert = vert.transform(matrix);

		double distance = 0.0;
		int index = -1;

		for (int i = 0; i < edgeCount; i++) {
			Plane constraint = voronoi.get(i);

			double d = constraint.distanceTo(tVert);

			if (d < distance) {
				distance = d;
				index = i;
			}
		}

		if (index > -1) {
			return edges.get(index);
		} else {
			if (!pointIsInFront(tVert)) {
				throw new LocalDistMinimum(tVert);
			} else {
				CollisionDetector.setClosestPoint(tVert);
				return null;
			}
		}
	}

}