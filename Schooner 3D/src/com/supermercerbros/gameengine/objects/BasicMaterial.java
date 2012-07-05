package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;

import android.opengl.GLES20;

/**
 * Renders vertex-colored, shadeless triangles.
 */
public class BasicMaterial extends Material {
	private static final int STRIDE = 6;
	
	private static final String VERTEX = 
			"precision mediump float;\n" + 
			"\n" + 
			"uniform mat4 u_viewProj;\n" + 
			"\n" + 
			"attribute mat4 a_model;\n" + 
			"attribute vec4 a_pos;\n" + 
			"attribute vec3 a_mtl;\n" + 
			"\n" + 
			"varying vec3 v_color;\n" + 
			"\n" + 
			"void main() {\n" + 
			"	gl_Position = (u_viewProj * a_model) * a_pos;\n" + 
			"	v_color = a_mtl;\n" + 
			"}";
	
	private static final String FRAGMENT = 
			"precision mediump float;\n" + 
			"\n" + 
			"varying vec3 v_color;\n" + 
			"\n" + 
			"uniform sampler2D s_baseMap;\n" + 
			"\n" + 
			"void main() {\n" + 
			"	gl_FragColor = vec4(v_color.rgb, 1.0);\n" + 
			"}";

	public BasicMaterial(){
		super(program(), STRIDE);
	}
	
	private static Program program() {
		return ShaderLib.newProgram(VERTEX, FRAGMENT);
	}

	@Override
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
		int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
		
		attachAttrib(a_pos, 3); // Vertex position data
		attachAttrib(a_mtl, 3); // Vertex color data

		return response;
	}

	@Override
	public int getGeometryType() {
		return GLES20.GL_TRIANGLES;
	}

	@Override
	public int loadObjectToVBO(GameObject obj, int[] vbo, int offset) {
		int numOfVerts = obj.verts.length / 3;
		
		setLoadOffset(offset);
		loadArrayToVbo(obj.verts, vbo, 3, numOfVerts); // Vertex position data
		loadArrayToVbo(obj.mtl, vbo, 3, numOfVerts);   // Vertex color data
		
		return obj.info.count * STRIDE;
	}

}
