package com.supermercerbros.gameengine.collision;

import java.util.LinkedList;
import java.util.List;

public class SphereBounds extends MeshBounds {
	private List<Polyhedron> parts;

	public SphereBounds(float radius, float... center) {
		super(new LinkedList<Polyhedron>(), radius);
		if (center.length < 3) {
			throw new IllegalArgumentException(
					"Not enough coordinates provided for center.");
		}
		parts = getParts();

		LinkedList<Feature> features = new LinkedList<Feature>();
		Feature centerPoint = new Vertex(center[0], center[1], center[2]);
		features.add(centerPoint);
		parts.add(new Polyhedron(features));
	}

}
