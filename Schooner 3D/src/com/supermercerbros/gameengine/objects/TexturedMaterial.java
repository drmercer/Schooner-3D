package com.supermercerbros.gameengine.objects;

import java.io.IOException;
import java.util.Arrays;

import android.opengl.GLES20;
import android.util.Log;

import com.supermercerbros.gameengine.engine.Texture;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;
import com.supermercerbros.gameengine.util.Utils;

/**
 * This is a basic textured material. Use it for smooth-shaded, uv-mapped triangles.
 */
public class TexturedMaterial extends Material {
	private static final String TAG = "TexturedMaterial";

	private final static int STRIDE = 8;
	
	private int debugCount = 0;
	
	private final static String VERTEX = 
			"precision mediump float;\n" + 
			"\n" + 
			"uniform mat4 u_viewProj;\n" + 
			"uniform vec3 u_lightVec;\n" + 
			"uniform vec3 u_lightColor;\n" + 
			"\n" + 
			"attribute mat4 a_model;\n" + 
			"attribute vec4 a_pos;\n" + 
			"attribute vec3 a_normal;\n" + 
			"attribute vec2 a_mtl; // Stores UV coords\n" + 
			"\n" + 
			"varying vec2 v_tc;\n" + 
			"varying vec3 v_lightColor;\n" + 
			"\n" + 
			"void main() {\n" + 
			"	gl_Position = (u_viewProj * a_model) * a_pos;\n" + 
			"	v_tc = a_mtl;\n" + 
			"	float brightness = max((dot(a_normal, u_lightVec) + 1.0) / 2.0, 0.0);\n" + 
			"	vec3 lighting = (u_lightColor * brightness + 0.2);\n" + 
			"	\n" + 
			"	v_lightColor.r = min(lighting.r, 1.0);\n" + 
			"	v_lightColor.g = min(lighting.g, 1.0);\n" + 
			"	v_lightColor.b = min(lighting.b, 1.0);\n" + 
			"}";
	
	private final static String FRAGMENT = 
			"precision highp float;\n" + 
			"\n" + 
			"varying vec2 v_tc;\n" + 
			"varying vec3 v_lightColor;\n" + 
			"\n" + 
			"uniform sampler2D s_baseMap;\n" + 
			"\n" + 
			"void main() {\n" + 
			"	gl_FragColor = texture2D(s_baseMap, v_tc) * vec4(v_lightColor, 1.0);\n" +
			"}";
	
	private Texture texture;
	
	public TexturedMaterial(String textureName){
		super(makeProgram(), STRIDE);
		try {
			texture = TextureLib.getTexture(textureName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Program makeProgram() {
		return ShaderLib.newProgram(VERTEX, FRAGMENT);
	}

	@Override
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
		int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
		
		if (debugCount < 10) {
			Log.d(TAG, "attachAttribs(primitve, " + vboOffset + ", matrix, " + matrixOffset + ")");
			Log.d(TAG, "matrix = " + Arrays.toString(matrix));
			Log.d(TAG, "a_pos = " + a_pos);
			Log.d(TAG, "a_normal = " + a_normal);
			Log.d(TAG, "a_mtl = " + a_mtl);
			Log.d(TAG, "a_model = " + a_model);
			debugCount++;
		}
		attachAttrib(a_pos, 3);
		attachAttrib(a_normal, 3);
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
	public int loadObjectToVBO(GameObject obj, int[] vbo, int offset) {
		final int numOfVerts = obj.info.count;

		setLoadOffset(offset);
		loadArrayToVbo(obj.verts, vbo, 3, numOfVerts);
		loadArrayToVbo(obj.normals, vbo, 3, numOfVerts);
		loadArrayToVbo(obj.mtl, vbo, 2, numOfVerts);
		
		if (debugCount < 10) {
			Log.d(TAG, "loadObjectToVBO(obj, vbo, " + offset + ")");
			Log.d(TAG, "vbo = " + Utils.vboToString(vbo, offset, obj.info.count * STRIDE));
			debugCount++;
		}
		
		return numOfVerts * STRIDE;

	}

}
