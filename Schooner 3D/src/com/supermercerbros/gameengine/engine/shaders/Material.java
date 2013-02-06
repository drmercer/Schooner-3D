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

package com.supermercerbros.gameengine.engine.shaders;

import java.nio.ByteOrder;

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
	private static final boolean NATIVE_ORDER_IS_BIG_ENDIAN = ByteOrder
			.nativeOrder() == ByteOrder.BIG_ENDIAN;
	
	public static final String VAR_A_POS = "attribute vec3 a_pos; \n";
	public static final String VAR_A_NORMAL = "attribute vec3 a_normal; \n";
	public static final String VAR_U_VIEWPROJ = "uniform mat4 u_viewProj;\n";
	public static final String VAR_U_MODEL = "uniform mat4 u_model;\n";
	
	public static final String VARS_U_LIGHT = "uniform vec3 u_lightVec;\n"
			+ "uniform vec3 u_lightColor;\n";
	
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
	 * the uniform used to store the object-specific tranformation matrix. This
	 * handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[])}.
	 */
	private int u_model;
	/**
	 * Contains the handle to the <code>u_matrices</code> uniform in the shader,
	 * which is the uniform used to store extra matrices for the object. This
	 * handle is initialized during
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
	private int byteStride;
	
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
			StringBuilder vertSB = new StringBuilder(source.vertPrecision);
			
			// variables
			vertSB.append(source.vertVars);
			modifier.getVars(vertSB);
			vertSB.append(source.varyings);
			
			// methods
			vertSB.append(source.vertMethods);
			modifier.getMethods(vertSB);
			
			vertSB.append(ProgramSource.MAIN_HEADER);
			modifier.getCode(vertSB);
			vertSB.append(source.vertMain.replaceAll("\\ba_pos\\b", "pos")
					.replaceAll("\\ba_normal\\b", "normal"));
			vertSB.append(ProgramSource.MAIN_FOOTER);
			vertex = vertSB.toString();
			
			//@formatter:off
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
		
		this.byteStride = stride * 4;
		
//		Log.d("Vertex Shader", vertex);
//		Log.d("Fragment Shader", fragment);
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
	 */
	public void attachAttribs(Metadata primitive, int vboOffset,
			float[] matrices) {
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
	}
	
	/**
	 * @return The OpenGL geometry type. Usually {@link GLES20#GL_TRIANGLES}.
	 *         Override this to use a different geometry type.
	 */
	public int getGeometryType() {
		return GLES20.GL_TRIANGLES;
	}
	
	/**
	 * @return The number of floats per vertex
	 */
	public int getStride() {
		return stride;
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
	public int loadObjectToVBO(GameObject obj, float[] vbo, int offset) {
		inPos = offset;
		final int vertCount = obj.info.count;
		onLoadObject(obj, vbo, vertCount);
		if (modifier != null) {
			modifier.onLoadObject(this, obj, vbo);
		}
		return vertCount * stride;
	}
	
	protected abstract void onLoadObject(GameObject obj, float[] vbo,
			int vertCount);
	
	/**
	 * Called by {@link #attachAttribs(Metadata, int, float[])} to set
	 * material-specific attributes. The <code>u_model</code> and
	 * <code>a_matrices</code> attributes have already been set.
	 */
	protected abstract void onAttachAttribs();
	
	/**
	 * Called after a VertexModifier has been set if necessary. Subclasses
	 * should call {@link #setProgram(ProgramSource, int)} in their
	 * implementation of this method.
	 */
	public abstract void makeProgram();
	
	/**
	 * @param data
	 *            The array of vertex data to load (such as obj.verts)
	 * @param vbo
	 *            The vertex buffer array to load to
	 * @param size
	 *            The number of values for each vertex
	 * @param count
	 *            The number of vertices represented
	 */
	public void loadArrayToVbo(float[] data, float[] vbo, int size, int count) {
		for (int i = 0; i < count; i++) {
			System.arraycopy(data, i * size, vbo, inPos + i * stride, size);
		}
		inPos += size;
	}
	
	/**
	 * Loads byte vertex attribute data to the VBO array.
	 * 
	 * @param data
	 *            The data to load
	 * @param vbo
	 *            The VBO array to load to
	 * @param size
	 *            The number of bytes per vertex
	 * @param count
	 *            The number of vertices represented
	 */
	public void loadArrayToVbo(byte[] data, float[] vbo, int size, int count) {
		
		final int intSize = (size + 3) / 4;
		final int byteSize = intSize * 4;
		for (int i = 0; i < count; i++) {
			// Convert bytes to ints
			for (int intCount = 0; intCount < intSize; intCount++) {
				final int intIndex = inPos + i * stride + intCount;
				int byteIndex = i * byteSize + (intCount * 4);
				
				// Convert 4 bytes to int
				int value = 0;
				if (NATIVE_ORDER_IS_BIG_ENDIAN) {
					for (int counter = size - 1; counter >= 0; counter--) {
						value |= data[byteIndex++] << (8 * counter);
						// First byte is most significant
					}
				} else {
					for (int counter = 0; counter < size; counter++) {
						value |= data[byteIndex++] << (8 * counter);
						// First byte is least significant
					}
				}
				vbo[intIndex] = Float.intBitsToFloat(value);
			}
		}
		inPos += intSize;
	}
	
	/**
	 * Attaches the given float attribute to the GPU.
	 * 
	 * @param attrib
	 *            The index of the attribute to attach.
	 * @param size
	 *            The size of the attribute.
	 */
	public void attachAttrib(int attrib, int size) {
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
	 *            The number of elements in the attribute.
	 * @param glType
	 *            the GL_ enum describing the data type
	 */
	public void attachAttrib(int attrib, int size, int glType) {
		GLES2.glEnableVertexAttribArray(attrib);
		GLES2.glVertexAttribPointer(attrib, size, glType, false, byteStride,
				outPos);
		if (glType == GLES20.GL_BYTE || glType == GLES20.GL_UNSIGNED_BYTE) {
			size = (size + 3) / 4;
		} else if (glType == GLES20.GL_SHORT
				|| glType == GLES20.GL_UNSIGNED_SHORT) {
			size = (size + 1) / 2;
		}
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
