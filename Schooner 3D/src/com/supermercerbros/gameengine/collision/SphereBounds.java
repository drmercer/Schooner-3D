package com.supermercerbros.gameengine.collision;

import java.util.LinkedList;

public class SphereBounds extends Bounds {
	public SphereBounds(float radius, float x, float y, float z) {
		super(new LinkedList<Polyhedron>(), radius);
		final LinkedList<Feature> features = new LinkedList<Feature>();
		final Feature centerPoint = new Vertex(x, y, z);
		features.add(centerPoint);
		parts.add(new Polyhedron(features));
	}

}
