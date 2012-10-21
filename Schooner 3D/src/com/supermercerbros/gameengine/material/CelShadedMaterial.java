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

import java.io.IOException;

import com.supermercerbros.gameengine.engine.Texture;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.shaders.ProgramSource;

/**
 * This is a basic textured material. Use it for smooth-shaded, uv-mapped
 * triangles.
 */
public class CelShadedMaterial extends Material {
	private final static int STRIDE = 8;
	
	private static final String VERT_VARS =
			"attribute vec3 a_pos;" +
			"attribute vec3 a_normal;" +
			"attribute vec2 a_mtl;" + // Stores UV coords
					
			"uniform mat4 u_viewProj;" +
			"uniform mat4 u_model;" + 
			"uniform vec3 u_lightVec;";
	
	private static final String VERT_MAIN =
			"gl_Position = (u_viewProj * u_model) * vec4(a_pos, 1.0);" +
			
			"v_tc = vec2(a_mtl.x, 1.0 - a_mtl.y);" +
			
			"vec3 normal = mat3(u_model) * a_normal;" +
			"v_brightness = (dot(normal, u_lightVec) + 1.0) / 2.0;";
	
	private static final String VARYINGS =
			"varying vec2 v_tc;" +
			"varying float v_brightness;";
	
	private static final String FRAG_VARS =
			"uniform sampler2D s_baseMap;" +
			"uniform vec3 u_lightColor;";
	
	private static final String FRAG_MAIN =
			"vec3 texColor = texture2D(s_baseMap, v_tc).rgb;" +
			"float brightness = v_brightness;" + 
			"if (brightness > .6) {" +
			"  brightness = 1.0;" +
			"} else if (brightness > .4) {" +
			"  brightness = .6;" +
			"} else {" +
			"  brightness = .4;" +
			"}" +
			"gl_FragColor = vec4(texColor * brightness, 1.0);";
	
	private Texture texture;
	
	public CelShadedMaterial(String textureName) {
		try {
			texture = TextureLib.getTexture(textureName);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
