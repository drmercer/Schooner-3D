package com.supermercerbros.gameengine.collision;

public class Plane {
	Vector normal;
	Point point;
	public Plane(Point point, Vector normal){
		this.normal = normal;
		this.point = point;
	}
}
