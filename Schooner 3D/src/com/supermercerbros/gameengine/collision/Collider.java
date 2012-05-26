package com.supermercerbros.gameengine.collision;


public interface Collider {
	Bounds getBounds();
	float[] getMatrix();
	void clearCollisions();
	void addCollision(Collider other, Collision collision);

}
