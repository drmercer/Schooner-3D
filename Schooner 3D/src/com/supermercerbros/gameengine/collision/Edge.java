package com.supermercerbros.gameengine.collision;

public class Edge extends Feature {
	/**
	 * An endpoint of the Edge (in the Edge's boundary)
	 */
	private final Vertex head, tail;
	/**
	 * A Face neighboring the Edge (in the Edge's coboundary)
	 */
	private Face right, left;
	private final Vector vector;
	private boolean locked;

	/**
	 * Creates a new Edge between the given vertices.
	 * 
	 * @param head
	 *            The head endpoint of the Edge.
	 * @param tail
	 *            The tail endpoint of the Edge.
	 */
	public Edge(Vertex head, Vertex tail) {
		this.head = head;
		head.addEdge(this);
		this.tail = tail;
		tail.addEdge(this);
		
		vector = new Vector(tail, head, true);

	}

	/**
	 * Adds a Face to this Edge's coboundary
	 * @param face
	 *            The Face to add
	 * @param point
	 *            A point on the face
	 */
	protected void addFace(Face face, Point point) {
		if(locked){
			throw new IllegalStateException("Edge is locked. Cannot add face.");
		}
		
		Vector v = new Vector(tail, point, true);
		float dot = vector.cross(v).dot(face.getNormal());
		if (dot > 0) {
			left = face;
		} else {
			right = face;
		}
	}
	
	@Override
	protected void lock() {
		// TODO Lock Edge
		locked = true;
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
	
	/**
	 * Returns the head of this Edge.
	 * @return the head endpoint of this Edge.
	 */
	public Vertex getHead(){
		return head;
	}
	
	/**
	 * Returns the tail of this Edge.
	 * @return the tail endpoint of this Edge.
	 */
	public Vertex getTail(){
		return tail;
	}

	/**
	 * Determines if the Edge is open (has only one neighboring face).
	 * @return True if this Edge is open.
	 */
	public boolean isOpen() {
		return right == null || left == null;
	}

	/**
	 * Gets the right wing of this Edge.
	 * @return The face on this Edge's right.
	 */
	public Face getRight(){
		return right;
	}
	
	/**
	 * @return The normalized vector of this Edge's direction
	 */
	public Vector asVector(){
		return vector;
	}

}
