package com.supermercerbros.gameengine.collision;

import java.util.List;

public class MeshBounds {
	private List<Polyhedron> parts;
	private float buffer;
	
	public MeshBounds(List<Polyhedron> subparts, float buffer){
		this.parts = subparts;
		this.buffer = buffer;
	}
	
	protected List<Polyhedron> getParts(){
		return parts;
	}
}
