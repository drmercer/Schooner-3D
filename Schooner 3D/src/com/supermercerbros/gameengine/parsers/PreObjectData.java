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

package com.supermercerbros.gameengine.parsers;

import com.supermercerbros.gameengine.objects.GameObject;

/**
 * Contains GameObject data before it is made into a GameObject.
 */
public class PreObjectData {
	public final float[] verts;
	public final short[] indices;
	public final float[] uvs;
	public final short[][] doubles;
	
	public final byte[][] boneIndices;
	public final float[][] boneWeights;
	public GameObject parent;
	
	public float[] matrix;
	
	public PreObjectData(float[] verts, short[] indices, float[] uvs,
			short[][] doubles, byte[][] boneIndices, float[][] boneWeights) {
		this.verts = verts;
		this.doubles = doubles;
		this.indices = indices;
		this.uvs = uvs;
		this.boneIndices = boneIndices;
		this.boneWeights = boneWeights;
	}
}