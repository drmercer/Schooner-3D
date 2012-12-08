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

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	public final int index;
	
	public final LinkedList<Metadata> primitives;
	public final float[] vbo;
	public final short[] ibo;

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
