package com.supermercerbros.gameengine.collision;

public class Edge extends Line implements Feature {
	/**
	 * An endpoint of the Edge (in the Edge's boundary)
	 */
	public final Vertex head, tail;
	/**
	 * A Face neighboring the Edge (in the Edge's coboundary)
	 */
	private Face right, left;

	/**
	 * Constraint planes <b>always</b> face away from edges, toward faces and
	 * vertices.
	 */
	private Plane rConstraint, lConstraint;
	/**
	 * Constraint planes <b>always</b> face away from edges, toward faces and
	 * vertices.
	 */
	private final Plane hConstraint, tConstraint;

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
	 * Creates a new Edge between the given vertices.
	 * 
	 * @param head
	 *            The head endpoint of the Edge.
	 * @param tail
	 *            The tail endpoint of the Edge.
	 */
	public Edge(Vertex head, Vertex tail) {
		super(head, tail);
		this.head = head;
		this.tail = tail;

		hConstraint = new Plane(head, vector);
		head.addEdge(this, hConstraint);

		tConstraint = new Plane(tail, new Vector(head, tail, 1.0f));
		tail.addEdge(this, tConstraint);
		
		this.debugID = "Edge " + nextID++;
	}

	/**
	 * Adds a Face to this Edge's coboundary
	 * 
	 * @param face
	 *            The Face to add
	 * @param point
	 *            A point on the face
	 * @return
	 */
	protected Plane addFace(Face face, Point point) {
		Vector v = new Vector(tail, point, 1.0f);
		Vector cDirection = vector.cross(v);
		Vector normal = cDirection.cross(vector);

		float dot = cDirection.cos(face.normal);
		if (dot > 0) {
			left = face;
			lConstraint = new Plane(head, normal);
			return lConstraint;
		} else {
			right = face;
			rConstraint = new Plane(head, normal);
			return rConstraint;
		}
	}

	/**
	 * Returns true if this Edge's endpoints are the same as those of
	 * <code>o</code>.
	 * 
	 * @param o
	 *            The Edge to compare with this one.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Edge) {
			Edge e = (Edge) o;
			return (e.head == this.head && e.tail == this.tail)
					|| (e.head == this.tail && e.tail == this.head);
		} else {
			return false;
		}
	}
	
	public boolean matches(Vertex a, Vertex b) {
		final boolean matches = a == head && b == tail;
		final boolean opposite = b == head && a == tail;
		final boolean result = matches || opposite;
		return result;
	}

	/**
	 * Determines if the Edge is open (has only one neighboring face).
	 * 
	 * @return True if this Edge is open.
	 */
	public boolean isOpen() {
		return right == null || left == null;
	}

	/**
	 * Gets the right wing of this Edge.
	 * 
	 * @return The face on this Edge's right.
	 */
	public Face getRight() {
		return right;
	}
	
	/**
	 * Gets the left wing of this Edge.
	 * 
	 * @return The face on this Edge's left.
	 */
	public Face getLeft() {
		return left;
	}

	/**
	 * Returns the opposite endpoint of the edge given one endpoint.
	 * 
	 * @param endpoint
	 *            The endpoint to retrieve the opposite endpoint of.
	 * @return The opposite endpoint.
	 */
	public Vertex getOpposite(Vertex endpoint) {
		if (endpoint == head) {
			return tail;
		} else if (endpoint == tail) {
			return head;
		} else {
			throw new IllegalArgumentException(
					"The given Vertex is not an endpoint of this Edge.");
		}
	}

	public Line transform(Matrix matrix) {
		return new Line(head.transform(matrix), tail.transform(matrix));
	}

	@Override
	public Feature test(final Feature other, final Matrix matrix)
			throws Intersection {
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

	private Feature testV(final Vertex vertex, final Matrix matrix) {
		final Point tVertex = vertex.transform(matrix);
		return checkPoint(tVertex);
	}

	private Feature testE(final Edge edge, final Matrix matrix) {
		final Line tEdge = edge.transform(matrix);
		final Vector a = this.asVector(), b = tEdge.asVector(), h = new Vector(
				tEdge.head, head, 0.0f);
		final float dotAA = a.dot(a);
		final float dotAB = a.dot(b);
		final float dotBB = b.dot(b);
		final float dotHA = h.dot(a);
		final float dotHB = h.dot(b);
		
		final float det = dotAB * dotAB - dotAA * dotBB;
		
		final float u;
		if (det == 0.0f) {
			u = .5f; // May be a problem
		} else {
			u = 1 - (dotAA * dotHB - dotAB * dotHA) / det;
		}

		final Point closestPoint;
		if (u > 1.0f) {
			closestPoint = tEdge.head;
		} else if (u < 0.0f) {
			closestPoint = tEdge.tail;
		} else {
			closestPoint = new Point(tEdge.head.x - u * b.x, tEdge.head.y - u
					* b.y, tEdge.head.z - u * b.z);
		}
		
		return checkPoint(closestPoint);
	}

	private Feature testF(final Face face, final Matrix matrix) {
		final Plane tFace = face.transform(matrix);

		final double headDistance = tFace.distanceTo(head);
		final double tailDistance = tFace.distanceTo(tail);

		if (headDistance < tailDistance) {
			return head;
		} else if (tailDistance < headDistance) {
			return tail;
		} else if (left.normal.cross(vector).dot(tFace.normal) < CollisionDetector.CONSTRAINT_CHECK_TOLERANCE) {
			return left;
		} else if (vector.cross(right.normal).dot(tFace.normal) < CollisionDetector.CONSTRAINT_CHECK_TOLERANCE) {
			return right;
		} else {
			CollisionDetector.setClosestPoint(tFace.projectPointOnto(Point.average(tail, head)));
			return null;
		}

	}

	private Feature checkPoint(Point p) {
		double distance = CollisionDetector.CONSTRAINT_CHECK_TOLERANCE;
		Feature infringed = null;

		// Check head and tail constraints
		double d;
		if ((d = -hConstraint.distanceTo(p)) < distance) {
			distance = d;
			infringed = head;
		} else if ((d = -tConstraint.distanceTo(p)) < distance) {
			// Use else here because the head and tail planes are parallel, so
			// if the head constraint fails, the tail will always pass.
			distance = d;
			infringed = tail;
		}
		
		// Check left and right wing constraints
		
		if (lConstraint != null) {
			final double lDistance = -lConstraint.distanceTo(p);
			if (lDistance < distance) {
				infringed = left;
			}			
		}

		if (rConstraint != null) {
			final double rDistance = -rConstraint.distanceTo(p);
			if (rDistance < distance) {
				infringed = right;
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
