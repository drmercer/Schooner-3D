package com.supermercerbros.gameengine.objects;

import java.io.IOException;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.ShaderLib;
import com.supermercerbros.gameengine.engine.Texture;
import com.supermercerbros.gameengine.engine.TextureLib;

/**
 * This is a basic textured material. Use it for smooth-shaded, uv-mapped triangles.
 * 
 * This material requires a shader with the following attributes:
 * 
 * <pre>
 * attribute vec3 a_normal;
 * attribute vec4 a_pos;
 * attribute vec2 a_mtl; // Stores UV coords
 * </pre>
 */
public class TexturedMaterial extends Material {
	private final static int STRIDE = 5;
	private Texture texture;
	
	public TexturedMaterial(String textureName){
		super("texture", STRIDE);
		try {
			texture = TextureLib.getTexture(textureName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
		int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);

		attachAttrib(a_pos, 3);
		attachAttrib(a_mtl, 2);
				
		texture.use(0, ShaderLib.S_BASEMAP, 
					this.program.getHandle());
		
		return response;
	}

	@Override
	public int getGeometryType() {
		return GLES20.GL_TRIANGLES;
	}

	@Override
	public int loadObjectToVBO(GameObject obj, int[] vbo, int vboOffset) {
		int numOfVerts = obj.verts.length / 3;

		clearLoadPosition();
		loadArrayToVbo(obj.verts, vbo, vboOffset, 3, numOfVerts);
		loadArrayToVbo(obj.mtl, vbo, vboOffset, 2, numOfVerts);
		
		return obj.info.count * STRIDE;

	}

}
