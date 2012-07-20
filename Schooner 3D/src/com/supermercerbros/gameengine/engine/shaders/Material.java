package com.supermercerbros.gameengine.engine.shaders;

import android.opengl.GLES20;
import android.util.Log;

import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.shaders.ProgramSource;
import com.supermercerbros.gameengine.util.GLES2;

/**
 * Superclass for materials to be used when rendering 3D objects.
 */
public abstract class Material {
	public static final String VAR_A_POS = 
			"attribute vec4 a_pos; \n";
	public static final String VAR_A_NORMAL =
			"attribute vec3 a_normal; \n";
	public static final String VAR_U_VIEWPROJ = 
			"uniform mat4 u_viewProj;\n";
	public static final String VAR_U_MODEL = 
			"uniform mat4 u_model;\n";
	
	public static final String VARS_U_LIGHT = 
			"uniform vec3 u_lightVec;\n" +
			"uniform vec3 u_lightColor;\n";
	
	/**
	 * Contains the OpenGL shader program used by this Material.
	 */
	protected Program program;
	
	/**
	 * Contains the handle to the <code>a_pos</code> attribute in the shader,
	 * the attribute used to store vertex position. This handle is initialized
	 * during {@link #attachAttribs(Metadata, int, float[])}.
	 */
	protected int a_pos = -2;
	/**
	 * Contains the handle to the <code>a_normal</code> attribute in the shader,
	 * the attribute used to store vertex normals. This handle is initialized
	 * during {@link #attachAttribs(Metadata, int, float[])}.
	 */
	protected int a_normal;
	/**
	 * Contains the handle to the <code>a_mtl</code> attribute in the shader,
	 * the attribute used to store vertex-specific material data, such as colors
	 * or uv-coordinates. This handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[])}.
	 */
	protected int a_mtl;
	
	/**
	 * Contains the handle to the <code>u_model</code> uniform in the shader,
	 * the uniform used to store the object-specific tranformation matrix.
	 * This handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[])}.
	 */
	private int u_model;
	/**
	 * Contains the handle to the <code>u_matrices</code> uniform in the
	 * shader, which is the uniform used to store extra matrices for the
	 * object. This handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[])}.
	 */
	private int u_matrices;
	
	/**
	 * The loading offset for the VBO.
	 */
	private int inPos = 0;
	/**
	 * The unloading offset for the VBO.
	 */
	private int outPos = 0;
	
	/**
	 * The stride of this Material
	 */
	private int stride;
	
	private VertexModifier modifier;
	
	public void setVertexModifier(VertexModifier mod) {
		if (program != null) {
			throw new IllegalStateException("Program has already been set.");
		}
		modifier = mod;
	}
	
	protected void setProgram(ProgramSource source, int stride) {
		if (program != null) {
			throw new IllegalStateException("Program has already been set.");
		}
		
		final String vertex;
		final String fragment;
		if (modifier != null) {
			//@formatter:off
			vertex = 
					source.vertPrecision +
					source.vertVars +
					modifier.getVars() +
					source.varyings +
					source.vertMethods +
					modifier.getMethods() +
					ProgramSource.MAIN_HEADER +
					modifier.getCode() +
					source.vertMain
						.replaceAll("\\ba_pos\\b", "pos")
						.replaceAll("\\ba_normal\\b", "normal") + 
					ProgramSource.MAIN_FOOTER;
			
			fragment =
					source.fragPrecision +
					source.fragVars +
					source.varyings +
					source.fragMethods +
					ProgramSource.MAIN_HEADER +
					source.fragMain +
					ProgramSource.MAIN_FOOTER;
			//@formatter:on
			this.stride = stride + modifier.getStride();
		} else {
			//@formatter:off
			vertex =
					source.vertPrecision +
					source.vertVars +
					source.varyings +
					ProgramSource.MAIN_HEADER +
					source.vertMain +
					ProgramSource.MAIN_FOOTER +
					source.vertMethods;
			
			fragment = 
					source.fragPrecision +
					source.fragVars +
					source.varyings +
					ProgramSource.MAIN_HEADER +
					source.fragMain +
					ProgramSource.MAIN_FOOTER +
					source.fragMethods;
			//@formatter:on
			this.stride = stride;
		}
		
		Log.d("Vertex Shader", vertex);
		Log.d("Fragment Shader", fragment);
		this.program = ShaderLib.newProgram(vertex, fragment);
	}
	
	/**
	 * Called by the renderer to attach the vertex attributes.
	 * 
	 * @param primitive
	 *            The Metadata about the primitive to load
	 * @param vboOffset
	 *            The offset into the VBO where the vertex data is stored
	 * @param matrices
	 *            A float array containing the matrices for this primitve,
	 *            starting with the model matrix.
	 * @return The byte size of the object's data in the VBO (
	 *         <code>primitive.count * stride * 4</code>)
	 */
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrices) {
		if (a_pos == -2) {
			a_pos = program.getAttribLocation(ShaderLib.A_POS);
			a_normal = program.getAttribLocation(ShaderLib.A_NORMAL);
			a_mtl = program.getAttribLocation(ShaderLib.A_MTL);
			u_model = program.getUniformLocation(ShaderLib.U_MODEL);
			u_matrices = program.getUniformLocation(ShaderLib.U_MATRICES);
		}
		
		if (u_model != -1) {
			GLES20.glUniformMatrix4fv(u_model, 1, false, matrices, 0);
		}
		
		if (u_matrices != -1 && matrices.length != 16) {
			GLES20.glUniformMatrix4fv(u_matrices, (matrices.length - 16) / 16,
					false, matrices, 16);
		}
		
		outPos = vboOffset;
		
		onAttachAttribs();
		if (modifier != null) {
			modifier.onAttachAttribs(this, program);
		}
		return primitive.count * stride * 4;
	}
	
	/**
	 * @return The OpenGL geometry type. Usually {@link GLES20#GL_TRIANGLES}.
	 *         Override this to use a different geometry type.
	 */
	public int getGeometryType() {
		return GLES20.GL_TRIANGLES;
	}
	
	/**
	 * Called by the Engine thread to load a GameObject's data to the VBO array.
	 * 
	 * @param obj
	 *            The GameObject to load
	 * @param vbo
	 *            The vertex buffer array
	 * @param offset
	 *            The offset into vbo to load the data at
	 * @return The size of the object's data in the vbo (
	 *         <code>obj.info.count * stride</code>)
	 */
	public int loadObjectToVBO(GameObject obj, int[] vbo, int offset) {
		inPos = offset;
		onLoadObject(obj, vbo, obj.verts.length / 3);
		if (modifier != null) {
			modifier.onLoadObject(this, obj, vbo);
		}
		return obj.info.count * stride;
	}
	
	protected abstract void onLoadObject(GameObject obj, int[] vbo,
			int numOfVerts);
	
	/**
	 * Called by {@link #attachAttribs(Metadata, int, float[])} to set
	 * material-specific attributes. The <code>u_model</code> and
	 * <code>a_matrices</code> attributes have already been set.
	 */
	protected abstract void onAttachAttribs();
	
	/**
	 * Called after a VertexModifier has been set if necessary. Subclasses
	 * should call {@link #setProgram(ProgramSource, int)} in their implementation
	 * of this method.
	 */
	public abstract void makeProgram();
	
	/**
	 * @param data
	 *            The array of vertex data to load (such as obj.verts)
	 * @param vbo
	 *            The vertex buffer array to load to
	 * @param vboOffset
	 *            The offset into vbo to load the data at
	 * @param size
	 *            The number of values for each vertex
	 * @param count
	 *            The number of vertices represented
	 */
	public void loadArrayToVbo(float[] data, int[] vbo, int size,
			int count) {
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < size; j++) {
				vbo[inPos + i * stride + j] = Float.floatToRawIntBits(data[i
						* size + j]);
			}
		}
		inPos += size;
	}
	
	public void loadArrayToVbo(byte[] data, int[] vbo, int intSize,
			int count) {
		final int byteSize = intSize * 4;
		for (int i = 0; i < count; i++) {
			// Convert bytes to ints
			for (int intCount = 0; intCount < intSize; intCount++) {
				final int intIndex = inPos + i * stride + intCount;
				final int byteIndex = i * byteSize + (intCount * 4);
				
				// Convert 4 bytes to int
				int value = 0;
				for (int index = byteIndex, counter = 3; counter >= 0; index++, counter--) {
					value |= data[index] << (8 * counter);
				}
				vbo[intIndex] = value;
			}
		}
		inPos += intSize;
	}
	
	/**
	 * Attaches the given attribute to the GPU.
	 * 
	 * @param attrib
	 *            The index of the attribute to attach.
	 * @param size
	 *            The size of the attribute.
	 */
	public void attachAttrib(int attrib, int size) {
		final int byteStride = 4 * stride;
		GLES2.glEnableVertexAttribArray(attrib);
		GLES2.glVertexAttribPointer(attrib, size, GLES20.GL_FLOAT, false,
				byteStride, outPos);
		outPos += size * 4;
	}
	
	/**
	 * Attaches the given attribute to the GPU.
	 * 
	 * @param attrib
	 *            The index of the attribute to attach.
	 * @param size
	 *            The size of the attribute.
	 * @param glType
	 *            the GL_ enum describing the data type
	 */
	public void attachAttrib(int attrib, int size, int glType) {
		final int byteStride = 4 * stride;
		GLES2.glEnableVertexAttribArray(attrib);
		GLES2.glVertexAttribPointer(attrib, size, glType, false,
				byteStride, outPos);
		outPos += size * 4;
	}
	
	/**
	 * Attaches a generic (constant) vertex attribute.
	 * 
	 * @param attrib
	 *            The index of the attribute.
	 * @param value
	 *            The value(s) of the attribute. Must be 1 to 4 values.
	 */
	public void attachAttrib(int attrib, float... value) {
		if (attrib < 0) {
			return;
		}
		if (value.length == 0) {
			throw new IllegalArgumentException("No value supplied.");
		}
		if (value.length > 4) {
			throw new IllegalArgumentException(
					"Cannot attach more than 4 values.");
		}
		
		GLES2.glDisableVertexAttribArray(attrib);
		switch (value.length) {
		case 1:
			GLES2.glVertexAttrib1fv(attrib, value, 0);
			break;
		case 2:
			GLES2.glVertexAttrib2fv(attrib, value, 0);
			break;
		case 3:
			GLES2.glVertexAttrib3fv(attrib, value, 0);
			break;
		case 4:
			GLES2.glVertexAttrib4fv(attrib, value, 0);
			break;
		default:
		}
	}
	
	protected void setUniform(int location, float... args) {
		if (args.length == 1) {
			GLES2.glUniform1fv(location, 1, args, 0);
		} else if (args.length == 2) {
			GLES2.glUniform2fv(location, 1, args, 0);
		} else if (args.length == 3) {
			GLES2.glUniform3fv(location, 1, args, 0);
		} else if (args.length == 4) {
			GLES2.glUniform4fv(location, 1, args, 0);
		} else {
			throw new UnsupportedOperationException(
					"args must have 1 to 4 elements.");
		}
	}
	
	protected void setUniform(String name, float... args) {
		setUniform(program.getUniformLocation(name), args);
	}
	
	public Program getProgram() {
		return program;
	}
}
