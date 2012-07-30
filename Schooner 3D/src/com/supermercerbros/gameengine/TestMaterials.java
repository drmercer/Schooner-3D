package com.supermercerbros.gameengine;

import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.shaders.ProgramSource;

public class TestMaterials {
	public static final int FRAMES_TO_LOG = 0;
	
	/**
	 * Flat white polys, no transformations
	 */
	public static class MinimalMaterial extends Material {
		private static final String VERT_VARS = 
				"attribute vec3 a_pos;\n";

		private static final String VERT_MAIN = 
				"gl_Position = vec4(a_pos, 1.0);\n";
		
		private static final String FRAG_MAIN =
				"gl_FragColor = vec4(1.0);\n";

		private static final int STRIDE = 3;

		@Override
		public void onLoadObject(GameObject obj, int[] vbo, int vertCount) {
			loadArrayToVbo(obj.verts, vbo, 3, vertCount);
		}
		
		@Override
		public void onAttachAttribs() {
			attachAttrib(a_pos, 3);
		}

		@Override
		public void makeProgram() {
			ProgramSource prog = new ProgramSource(null, null, VERT_VARS, VERT_MAIN, null, null, FRAG_MAIN);
			setProgram(prog, STRIDE);
		}
		
	}
	
	public static class ShadelessMaterial extends Material {
		private static final String VERT_VARS = 
				"uniform mat4 u_viewProj;\n" + 
				"uniform mat4 u_model;\n" + 
				
				"attribute vec3 a_pos;\n";

		private static final String VERT_MAIN = 
				"mat4 transform = u_viewProj * u_model;\n" +
				"gl_Position = transform * vec4(a_pos, 1.0);\n";
		
		private static final String FRAG_MAIN = 
				"gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n";
		
		private static final int STRIDE = 3;
		
		@Override
		protected void onLoadObject(GameObject obj, int[] vbo, int vertCount) {
			loadArrayToVbo(obj.verts, vbo, 3, vertCount);
		}

		@Override
		protected void onAttachAttribs() {
			attachAttrib(a_pos, 3);
		}

		@Override
		public void makeProgram() {
			ProgramSource prog = new ProgramSource(null, null, VERT_VARS, VERT_MAIN, null, null, FRAG_MAIN);
			setProgram(prog, STRIDE);
		}
		
	}
	
	/**
	 * Vertex-shaded polys
	 */
	public static class ShadedMaterial extends Material {
		private static final String VERT_VARS = 
				"uniform mat4 u_viewProj;\n" + 
				"uniform vec3 u_lightVec;\n" +
				"uniform mat4 u_model;\n" + 
				
				"attribute vec3 a_pos;\n" + 
				"attribute vec3 a_normal;\n";

		private static final String VERT_MAIN = 
				"mat4 transform = u_viewProj * u_model;\n" +
				"gl_Position = transform * vec4(a_pos, 1.0);\n" +
				"vec3 normal = (transform * vec4(a_normal, 0.0)).xyz;\n" +
				"v_brightness = (dot(normalize(normal), u_lightVec) + 1.0) / 2.0;\n";
		
		private static final String VARYINGS = 
				"varying float v_brightness;";
		
		private static final String FRAG_VARS =
				"uniform vec3 u_lightColor;\n";
		
		private static final String FRAG_MAIN = 
				"gl_FragColor = vec4(min(u_lightColor * v_brightness + 0.2, vec3(1.0)), 1.0);\n";
		
		private static final int STRIDE = 6;

		@Override
		public void onLoadObject(GameObject obj, int[] vbo, int vertCount) {
			loadArrayToVbo(obj.verts, vbo, 3, vertCount);
			loadArrayToVbo(obj.normals, vbo, 3, vertCount);
		}
		
		@Override
		public void onAttachAttribs() {
			attachAttrib(a_pos, 3);
			attachAttrib(a_normal, 3);
		}

		@Override
		public void makeProgram() {
			ProgramSource prog = new ProgramSource(VARYINGS, null, VERT_VARS, VERT_MAIN, null, FRAG_VARS, FRAG_MAIN);
			setProgram(prog, STRIDE);
		}
	}
}
