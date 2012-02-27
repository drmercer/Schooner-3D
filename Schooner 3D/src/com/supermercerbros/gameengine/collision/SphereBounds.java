package com.supermercerbros.gameengine.collision;

import java.util.LinkedList;
import java.util.List;

public class SphereBounds extends MeshBounds {
	private List<Polyhedron> parts;

	public SphereBounds(float radius, float x, float y, float z) {
		super(new LinkedList<Polyhedron>(), radius);
		parts = getParts();
		
		LinkedList<Feature> features = new LinkedList<Feature>();
		Feature center = new Vertex(x, y, z);
		features.add(center);
		parts.add(new Polyhedron(features));
	}

}
