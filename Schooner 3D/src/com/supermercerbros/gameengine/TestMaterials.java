package com.supermercerbros.gameengine;

import java.util.Arrays;

import android.opengl.GLES20;
import android.util.Log;

import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Material;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.util.Utils;

public class TestMaterials {
	public static final int FRAMES_TO_LOG = 0;
	
	/**
	 * Flat white polys, no transformations
	 */
	public static class MinimalMaterial extends Material {
		private static final String TAG = MinimalMaterial.class.getSimpleName();
		
		private static final String vertex = 
				"attribute vec3 a_pos;\n" +
				"void main() {\n" + 
				"	gl_Position = vec4(a_pos, 1.0);\n" + 
				"}";
		
		private static final String fragment = 
				"void main() {\n" + 
				"	gl_FragColor = vec4(1.0);\n" +
				"}";
		
		private static final int STRIDE = 3;
		
		private int logCount = 0;
		private static Program makeProgram() {
			return ShaderLib.newProgram(vertex, fragment);
		}

		public MinimalMaterial() {
			super(makeProgram(), STRIDE);
		}


		@Override
		public int getGeometryType() {
			return GLES20.GL_TRIANGLES;
		}

		@Override
		public int loadObjectToVBO(GameObject obj, int[] vbo, int offset) {
			final int numOfVerts = obj.info.count;

			setLoadOffset(offset);
			loadArrayToVbo(obj.verts, vbo, 3, numOfVerts);
			
			if (logCount < FRAMES_TO_LOG) {
				Log.d(TAG, "loadObjectToVBO(obj, vbo, " + offset + ")");
				Log.d(TAG, "vbo = " + Utils.vboToString(vbo, offset, numOfVerts * STRIDE));
			}
			
			return numOfVerts * STRIDE;
		}
		
		@Override
		public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
			int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
			
			if (logCount < FRAMES_TO_LOG) {
				Log.d(TAG, "attachAttribs(primitve, " + vboOffset + ", matrix, " + matrixOffset + ")");
				Log.d(TAG, "a_pos = " + a_pos);
				Log.d(TAG, "a_normal = " + a_normal);
				Log.d(TAG, "a_mtl = " + a_mtl);
				Log.d(TAG, "a_model = " + a_model);
				logCount++;
			}
			attachAttrib(a_pos, 3);
			return response;
		}
		
	}
	
	/**
	 * Vertex-shaded polys
	 */
	public static class ShadedMaterial extends Material {
		private static final String uniforms = 
				"uniform mat4 u_viewProj;\n" + 
				"uniform vec3 u_lightVec;\n" + 
				"uniform vec3 u_lightColor;\n";

		private static final String TAG = ShadedMaterial.class.getSimpleName();
		
		private static final String varyings = 
				"varying vec3 v_normal;\n";
		
		private static final String vertex = 
				uniforms + 
				"attribute mat4 a_model;\n" + 
				"attribute vec3 a_pos;\n" + 
				"attribute vec3 a_normal;\n" + 
				varyings + 
				"void main() {\n" + 
				"	gl_Position = (u_viewProj * a_model) * vec4(a_pos, 1.0);\n" + 
				"   v_normal = a_normal;\n" + 
				"}";
		
		private static final String fragment = 
				"precision mediump float;\n" +
				uniforms +
				varyings + 
				"void main() {\n" + 
				"   float brightness = (dot(normalize(v_normal), u_lightVec) + 1.0) / 2.0;\n" + 
				"	gl_FragColor = vec4(min(u_lightColor * brightness + 0.2, vec3(1.0)), 1.0);\n" +
				"}";
		
		private static final int STRIDE = 6;
		
		private int logCount = 0;
		private static Program makeProgram() {
			return ShaderLib.newProgram(vertex, fragment);
		}

		public ShadedMaterial() {
			super(makeProgram(), STRIDE);
		}


		@Override
		public int getGeometryType() {
			return GLES20.GL_TRIANGLES;
		}

		@Override
		public int loadObjectToVBO(GameObject obj, int[] vbo, int offset) {
			final int numOfVerts = obj.info.count;

			setLoadOffset(offset);
			loadArrayToVbo(obj.verts, vbo, 3, numOfVerts);
			loadArrayToVbo(obj.normals, vbo, 3, numOfVerts);
			
			if (logCount < FRAMES_TO_LOG) {
				Log.d(TAG, "loadObjectToVBO(obj, vbo, " + offset + ")");
				Log.d(TAG, "vbo = " + Utils.vboToString(vbo, offset, numOfVerts * STRIDE));
			}
			
			return numOfVerts * STRIDE;
		}
		
		@Override
		public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
			int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
			
			if (logCount < FRAMES_TO_LOG) {
				Log.d(TAG, "attachAttribs(primitve, " + vboOffset + ", matrix, " + matrixOffset + ")");
				Log.d(TAG, "matrix = " + Arrays.toString(matrix));
				Log.d(TAG, "a_pos = " + a_pos);
				Log.d(TAG, "a_normal = " + a_normal);
				Log.d(TAG, "a_mtl = " + a_mtl);
				Log.d(TAG, "a_model = " + a_model);
				logCount++;
			}
			attachAttrib(a_pos, 3);
			attachAttrib(a_normal, 3);
			return response;
		}
		
	}
}
