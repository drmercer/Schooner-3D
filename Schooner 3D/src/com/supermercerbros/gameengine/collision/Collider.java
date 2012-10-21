/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
