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

package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	/**
	 * Represents the area of a buffer (VBO or IBO) that is dirty and needs to be reloaded to the GPU.
	 */
	public class Range {
		// Start is inclusive, end is exclusive
		int start = Integer.MAX_VALUE, end = 0;
		
		void include(int start, int end) {
			if (start < this.start) {
				this.start = start;
			}
			if (end > this.end) { 
				this.end = end;
			}
		}
		
		void reset() {
			start = Integer.MAX_VALUE;
			end = 0;
		}

		boolean needsToBeUpdated() {
			final boolean b = end - 1 > start;
			if (b) {
				Log.d("Range", "from " + start + " to " + end);
			}
			return b;
		}
	}


	public final int index;
	
	public final LinkedList<Metadata> primitives;
	public final float[] vbo;
	public final Range vboRange = new Range();
	public final short[] ibo;
	public final Range iboRange = new Range();

	public CopyOnWriteArrayList<float[]> modelMatrices;
	public float[] viewMatrix = new float[16];
	public Light light = new Light();

	
	public RenderData(int index, final int vboLength, final int iboLength) {
		this.index = index;
		
		vbo = new float[vboLength];
		ibo = new short[iboLength];
		primitives = new LinkedList<Metadata>();
		modelMatrices = new CopyOnWriteArrayList<float[]>();
	}
}
