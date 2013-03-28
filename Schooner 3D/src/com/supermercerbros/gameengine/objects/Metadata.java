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

package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.engine.shaders.Material;

public class Metadata {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject.Metadata";
	
	/**
	 * Contains the number of indices in the described GameObject. Used by the
	 * renderer.
	 */
	public int size;
	/**
	 * Contains the number of vertices in the described GameObject. Used by the
	 * renderer.
	 */
	public int count;
	/**
	 * True if the described GameObject is isVisible
	 */
	public boolean isVisible = true;
	/**
	 * The material to render this object with.
	 */
	public Material mtl;
	
	public final int[] bufferLocations;

	Metadata() {
		bufferLocations = new int[]{ -1, -1, -1, -1};
	}
	
	Metadata(int[] bufferOffsets) {
		this.bufferLocations = bufferOffsets;
	}
}
