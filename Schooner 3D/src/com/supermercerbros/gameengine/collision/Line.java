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

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a Line defined by two points, known as the "head" and "tail". 
 */
public class Line {
	final Point head, tail;
	/**
	 * The <b>unit</b> vector of this Line's direction
	 */
	final Vector vector;
	final float length;

	public Line(Point head, Point tail) {
		this.head = head;
		this.tail = tail;
		this.length = Utils.pythagF(head.x - tail.x, head.y - tail.y, head.z
				- tail.z);
		this.vector = new Vector(tail, head, 1.0f);
	}

	/**
	 * @return The <b>non-unit</b> vector of this Line's direction.
	 */
	public Vector asVector() {
		return new Vector(tail, head, 0.0f);
	}

	/**
	 * Projects point <code>p</code> onto this Line.
	 * @param p
	 * @return The point on this Line closest to <code>p</code>.
	 */
	public Point projectPoint(Point p) {
		float scl = vector.cos(new Vector(tail, p, 1.0f));
		return new Point(tail.x + (head.x - tail.x) * scl, tail.y + (head.y - tail.y) * scl, tail.z + (head.z - tail.z) * scl);
	}
	
	@Override
	public String toString() {
		return "Line{" + tail + ", " + head + "}";
	}
}
