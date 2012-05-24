package com.supermercerbros.gameengine.collision;

import java.util.LinkedList;
import java.util.List;

public class Bounds {
	final LinkedList<Polyhedron> parts;
	final double buffer;
	
	public Bounds(List<Polyhedron> subparts, double d){
		if (subparts instanceof LinkedList) {
			this.parts = (LinkedList<Polyhedron>) subparts;	
		} else {
			this.parts = new LinkedList<Polyhedron>(subparts);
		}
		this.buffer = d;
	}
}
