package com.supermercerbros.gameengine.objects;

import java.io.IOException;

import com.supermercerbros.gameengine.engine.Texture;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;
import com.supermercerbros.gameengine.shaders.ProgramSource;

/**
 * This is a basic textured material. Use it for smooth-shaded, uv-mapped
 * triangles.
 */
public class TexturedMaterial extends Material {
	private final static int STRIDE = 8;
	
	private static final String VERT_VARS =
			"attribute mat4 a_model;\n" +
			"attribute vec4 a_pos;\n" +
			"attribute vec3 a_normal;\n" +
			"attribute vec2 a_mtl;\n" + // Stores UV coords
					
			"uniform mat4 u_viewProj;\n" +
			"uniform vec3 u_lightVec;\n" +
			"uniform vec3 u_lightColor;\n";
	
	private static final String VERT_MAIN =
			"gl_Position = (u_viewProj * a_model) * a_pos;\n" +
			"v_tc = a_mtl;\n" +
			"float brightness = max((dot(a_normal, u_lightVec) + 1.0) / 2.0, 0.0);\n" +
			"vec3 lighting = (u_lightColor * brightness + 0.2);\n" +
			
			"v_lightColor = min(lighting, vec3(1.0f));\n";
	
	private static final String VARYINGS =
			"varying vec2 v_tc;\n" +
			"varying vec3 v_lightColor;\n";
	
	private static final String FRAG_VARS =
			"uniform sampler2D s_baseMap;\n";
	
	private static final String FRAG_MAIN =
			"gl_FragColor = texture2D(s_baseMap, v_tc) * vec4(v_lightColor, 1.0);\n";
	
	private Texture texture;
	
	public TexturedMaterial(String textureName) {
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
