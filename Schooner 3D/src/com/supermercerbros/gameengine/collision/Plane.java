package com.supermercerbros.gameengine.collision;

public class Plane {
	Vector normal;
	Point point;
	public Plane(Point point, Vector normal){
		this.normal = normal;
		this.point = point;
	}
	public boolean pointIsInFront(float x, float y, float z, float tolerance) {
		float dot = new Vector(point, new Vertex(x,y,z), true).dot(normal);
		return dot >= tolerance || dot > 0.0f;
	}
}
