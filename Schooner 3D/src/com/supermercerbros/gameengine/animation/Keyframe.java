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

package com.supermercerbros.gameengine.animation;

public class Keyframe {
	float[] verts;
	private final int size;
	
	public Keyframe(float[] verts){
		this.verts = verts;
		size = verts.length;
	}

	public int count() {
		return size / 3;
	}
	
	void loadTo(float[] array) {
		if (array.length != size){
			throw new IllegalArgumentException("Cannot copy Keyframe to a vert array of a different size.");
		}
		System.arraycopy(this.verts, 0, array, 0, size);
		
	}

}
