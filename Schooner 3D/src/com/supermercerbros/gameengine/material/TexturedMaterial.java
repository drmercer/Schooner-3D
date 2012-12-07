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

package com.supermercerbros.gameengine.material;

import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.shaders.ProgramSource;
import com.supermercerbros.gameengine.texture.Texture;

/**
 * This is a basic textured material. Use it for smooth-shaded, uv-mapped
 * triangles.
 */
public class TexturedMaterial extends Material {
	private final static int STRIDE = 8;
	
	private static final String VERT_VARS =
			"attribute vec3 a_pos;\n" +
			"attribute vec3 a_normal;\n" +
			"attribute vec2 a_mtl;\n" + // Stores UV coords
					
			"uniform mat4 u_viewProj;\n" +
			"uniform vec3 u_lightVec;\n" +
			"uniform vec3 u_lightColor;\n" + 
			"uniform mat4 u_model;\n";
	
	private static final String VERT_MAIN =
			"mat4 transform = (u_viewProj * u_model);" +
			"gl_Position = transform * vec4(a_pos, 1.0);\n" +
			"v_tc = vec2(a_mtl.x, 1.0 - a_mtl.y);\n" +
			"vec3 normal = (transform * vec4(a_normal, 0.0)).xyz;\n" +
			"float brightness = max((dot(normal, u_lightVec) + 1.0) / 2.0, 0.0);\n" +
			"vec3 color = (u_lightColor * brightness);\n" +
			
			"v_lightColor = min(color, vec3(1.0));\n";
	
	private static final String VARYINGS =
			"varying vec2 v_tc;\n" +
			"varying vec3 v_lightColor;\n";
	
	private static final String FRAG_VARS =
			"uniform sampler2D s_baseMap;\n";
	
	private static final String FRAG_MAIN =
			"gl_FragColor = vec4(texture2D(s_baseMap, v_tc).rgb, 1.0) * vec4(v_lightColor, 1.0);\n";
	
	private Texture texture;
	
	public TexturedMaterial(Texture texture) {
		this.texture = texture;
	}
	
	@Override
	public void onAttachAttribs() {
		attachAttrib(a_pos, 3);
		attachAttrib(a_normal, 3);
		attachAttrib(a_mtl, 2);
		
		texture.use(0, ShaderLib.S_BASEMAP,
				this.program.getHandle());
	}
	
	@Override
	public void onLoadObject(GameObject obj, int[] vbo, int vertCount) {
		loadArrayToVbo(obj.verts, vbo, 3, vertCount);
		loadArrayToVbo(obj.normals, vbo, 3, vertCount);
		loadArrayToVbo(obj.mtl, vbo, 2, vertCount);
	}
	
	@Override
	public void makeProgram() {
		final ProgramSource prog = new ProgramSource(VARYINGS, null, VERT_VARS,
				VERT_MAIN, null, FRAG_VARS, FRAG_MAIN);
		setProgram(prog, STRIDE);
	}
}
