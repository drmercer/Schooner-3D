package com.supermercerbros.gameengine.collision;

public class Intersection extends Exception {
	private static final long serialVersionUID = 1L;

	private Point p;

	public Intersection(Point intersection) {
		p = intersection;
	}
	
	public Point getPoint() {
		return p;
	}

}
