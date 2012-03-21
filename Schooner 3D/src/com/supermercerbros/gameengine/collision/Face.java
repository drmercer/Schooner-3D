package com.supermercerbros.gameengine.collision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Face extends Feature {

	private List<Edge> edges;
	private List<Plane> voronoi;
	private List<Boolean> frontFacing;
	private boolean locked;
	private Vector normal;
	private Point point;
	private Plane plane;

	public Face(Vector normal, Edge... edges) {
		if (edges.length < 3) {
			throw new IllegalArgumentException(
					"Face cannot be constructed with less than 3 edges.");
		}
		this.normal = normal;
		this.edges = Arrays.asList(edges);

		float avgX = 0, avgY = 0, avgZ = 0;
		for (Edge edge : edges) {
			Vertex vert = edge.getHead();
			avgX += vert.getX();
			avgY += vert.getY();
			avgZ += vert.getZ();
		}
		avgX /= edges.length;
		avgY /= edges.length;
		avgZ /= edges.length;

		point = new Vertex(avgX, avgY, avgZ);

		for (Edge edge : edges) {
			edge.addFace(this, point);
		}
		
		this.plane = new Plane(point, normal);
	}

	/* *
	 * Locks this Face to prevent further modifications.
	 */
	@Override
	protected void lock() {
		locked = true;
		// TODO Lock Face: create voronoi and frontFacing
	}

	/**
	 * @return The normal vector of this face.
	 */
	public Vector getNormal() {
		return normal;
	}
	
	public Edge test(Edge edge) throws Intersection {
		Point intersection = this.plane.intersect(edge);
		
		if (intersection != null) {
			double distance = 0.0;
			/** Index of the constraint plane that intersection is farthest outside */
			int index = -1; 
			for (int i = 0; i < this.voronoi.size(); i++) {
				double planeDistance = voronoi.get(i).distanceTo(intersection);
				
				if (!frontFacing.get(i)){
					planeDistance = -planeDistance;
				}
				if (planeDistance < distance) {
					distance = planeDistance;
					index = i;
				}
				
			}
			if (distance < 0) {
				return this.edges.get(index);
			} else {
				throw new Intersection(intersection);
			}
				
		} else if (edge.asVector().dot(this.normal, true) == 0.0) {
			// If edge is parallel to Face
			Vertex head = edge.getHead(), tail = edge.getTail();
			Point headward = head, tailward = tail;
			for (Plane constraint : this.voronoi) {
				Point p = constraint.intersect(edge);
				
				if (!edge.contains(p)) {
					continue;
				}
				
				boolean facingHead = constraint.normal.dot(edge.asVector(), true) > 0.0;
				
				if (facingHead && p.distanceTo(head) > headward.distanceTo(head)){
					headward = p;
				} else if (!facingHead && p.distanceTo(tail) > tailward.distanceTo(tail)){
					tailward = p;
				}
			}
			
			for (Plane constraint : this.voronoi) {
				//TODO check headward and tailward against all constraint planes
			}
		}
		//TODO edge is not parallel and doesn't intersect plane
		return null;
	}
	
	private boolean check(Point p) {
		for (Plane constraint : this.voronoi) {
			if (!constraint.pointIsInFront(p.getX(), p.getY(), p.getZ(), Polyhedron.PLANE_CHECK_TOLERANCE)){
				return false;
			}
		}
		return true;
	}

}
