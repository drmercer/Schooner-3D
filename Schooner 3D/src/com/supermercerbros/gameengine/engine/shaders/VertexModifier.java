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

package com.supermercerbros.gameengine.engine.shaders;

import com.supermercerbros.gameengine.objects.GameObject;

/**
 * Represents a vertex position modifier that is applied in the GPU
 */
public abstract class VertexModifier {
	public abstract void onLoadObject(Material mtl, GameObject object, float[] vbo);
	
	public abstract void onAttachAttribs(Material mtl, Program program);
	
	/**
	 * @param sb 
	 * @return Additional variables (uniforms and attributes) for the vertex
	 *         shader. These should all be set in
	 *         {@link #onAttachAttribs(Material)}.
	 */
	public abstract void getVars(StringBuilder sb);
	
	/**
	 * @return The vertex position modifier code to be inserted at the
	 *         beginning of the main method. Should write <code>vec3 pos</code>
	 *         and <code>vec3 normal</code> variables containing the modified
	 *         position and
	 *         normal respectively.
	 */
	public abstract void getCode(StringBuilder sb);
	
	/**
	 * @return Methods declared (and used) by the modifier. Inserted before the main
	 *         method declaration.
	 */
	public abstract void getMethods(StringBuilder sb);
	
	/**
	 * @return The number of additional floats per vertex.
	 */
	public abstract int getStride();
}
