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
import com.supermercerbros.gameengine.shaders.ProgramSource;

/**
 * Renders vertex-colored, shadeless triangles.
 */
public class BasicMaterial extends Material {
	private static final int STRIDE = 6;
	private static final String VARYINGS = 
			"varying vec3 v_color;\n";
	
	private static final String VERTEX_VARS =
			"uniform mat4 u_viewProj;\n" + 
	
			"uniform mat4 u_model;\n" + 
			"attribute vec3 a_pos;\n" + 
			"attribute vec3 a_mtl;\n";
	
	private static final String VERTEX_MAIN = 
			"gl_Position = (u_viewProj * u_model) * vec4(a_pos, 1.0);\n" + 
			"v_color = a_mtl;\n";
	
	private static final String FRAGMENT_MAIN = 
			"gl_FragColor = vec4(v_color.rgb, 1.0);\n";

	@Override
	public void onAttachAttribs() {
		attachAttrib(a_pos, 3); // Vertex position data
		attachAttrib(a_mtl, 3); // Vertex color data
	}

	@Override
	public void onLoadObject(GameObject obj, float[] vbo, int vertCount) {
		loadArrayToVbo(obj.verts, vbo, 3, vertCount); // Vertex position data
		loadArrayToVbo(obj.mtl, vbo, 3, vertCount);   // Vertex color data
	}
	
	@Override
	public void makeProgram() {
		super.setProgram(new ProgramSource(VARYINGS, null, VERTEX_VARS, VERTEX_MAIN, null, null, FRAGMENT_MAIN), STRIDE);
	}

}
