package com.supermercerbros.gameengine.objects;

import android.opengl.GLES20;

/**
 * Renders vertex-colored, shadeless triangles.
 */
public class BasicMaterial extends Material {
	private static final int STRIDE = 6;

	public BasicMaterial(){
		super("vertexColor", STRIDE);
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
		
		clearLoadPosition();
		loadArrayToVbo(obj.verts, vbo, offset, 3, numOfVerts); // Vertex position data
		loadArrayToVbo(obj.mtl, vbo, offset, 3, numOfVerts);   // Vertex color data
		
		return obj.info.count * STRIDE;
	}

}
