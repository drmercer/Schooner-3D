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

public class Intersection extends Throwable {
	private static final long serialVersionUID = 1L;

	/**
	 * The point of intersection, or null if the point is unknown. Given in the
	 * Face's coordinates.
	 */
	public final Point point;

	/**
	 * Creates an Intersection with a known point of intersection.
	 * 
	 * @param intersection
	 *            The point of intersection.
	 */
	public Intersection(Point intersection) {
		point = intersection;
	}

	/**
	 * Creates an Intersection with an unknown point of intersection.
	 */
	public Intersection() {
		point = null;
	}
}
