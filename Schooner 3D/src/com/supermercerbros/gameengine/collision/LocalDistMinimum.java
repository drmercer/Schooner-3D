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
 * Thrown when a possible local distance minimum occurs. When the closest point
 * on a feature is behind a face but still inside it's constraint planes, it is
 * either on the other side of the face's polyhedron (a local distance minimum)
 * or inside the face's polyhedron.
 * 
 * This should only be thrown by {@link Face#test(Feature, Matrix)};
 */
public class LocalDistMinimum extends Throwable {
	private static final long serialVersionUID = -9180046964472056920L;
	/**
	 * The point that may be a local distance minimum (it's behind a face).
	 * Coordinates in the face's coordinate system.
	 */
	public final Point p;

	public LocalDistMinimum(final Point p) {
		this.p = p;
	}
}
