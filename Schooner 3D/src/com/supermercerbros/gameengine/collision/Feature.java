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
 * Represents a Face, Edge, or Vertex.
 */
public interface Feature {
	/**
	 * Checks the given Feature against this Feature's constraint planes.
	 * 
	 * @param other
	 *            The Feature to test.
	 * @param matrix
	 *            The transformation matrix defining <code>other</code>'s
	 *            coordinate system relative to this Feature's coordinate
	 *            system.
	 * @return The next closest Feature, or <code>null</code> if there is none.
	 * 
	 * @throws Intersection
	 *             If an Edge is found to be intersecting a Face.
	 * @throws LocalDistMinimum
	 *             If a Feature is behind a Face and inside its constraint
	 *             planes.
	 */
	public Feature test(Feature other, Matrix matrix) throws Intersection,
			LocalDistMinimum;
}
