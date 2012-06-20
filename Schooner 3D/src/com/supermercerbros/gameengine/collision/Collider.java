package com.supermercerbros.gameengine.collision;


/**
 * Represents a virtual 3D object that can physically collide with other objects
 *
 */
public interface Collider {
	/**
	 * @return The Bounds of this Collider
	 */
	Bounds getBounds();
	
	/**
	 * @return The current transformation matrix of this Collider
	 */
	float[] getMatrix();
	
	/**
	 * Clears the list of Collisions stored in this Collider.
	 * 
	 * @see #addCollision(Collider, Collision)
	 */
	void clearCollisions();
	
	/**
	 * @param other The other Collider with which this one collided
	 * @param collision The Collision object describing the collision
	 */
	void addCollision(Collider other, Collision collision);

}
