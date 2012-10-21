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

import java.util.LinkedList;

public class SphereBounds extends Bounds {
	public SphereBounds(float radius, float x, float y, float z) {
		super(new LinkedList<Polyhedron>(), radius);
		final LinkedList<Feature> features = new LinkedList<Feature>();
		final Feature centerPoint = new Vertex(x, y, z);
		features.add(centerPoint);
		parts.add(new Polyhedron(features));
	}

}
