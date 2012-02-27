package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Face extends Feature {
	
	private List<Edge> edgeList;
	private Map<Plane, Boolean> voronoi;
	private boolean locked;
	private Vector normal;
	private Point point;

	public Face(Vector normal, Edge... edges) {
		this.normal = normal;
		this.edgeList = Arrays.asList(edges);
		
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
	}

	@Override
	protected void lock() {
		locked = true;
		// TODO Lock Face

	}
	
	public Vector getNormal(){
		return normal;
	}

}
