package com.supermercerbros.gameengine.objects;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.GLES2;
import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.engine.Program;
import com.supermercerbros.gameengine.engine.ShaderLib;

public abstract class Material {
	/**
	 * The name of the program that this Material uses.
	 */
	protected final String programName;
	protected final int stride;
	protected Program program;

	protected int a_pos;
	protected int a_normal;
	protected int a_mtl;

	private int a_model;
	private int vPos = 0;
	private int aPos = 0;

	protected Material(String programName, int stride) {
		this.programName = programName;
		this.stride = stride;
	}

	/**
	 * Called by the renderer to attach the vertex attributes.
	 * 
	 * @param primitive
	 *            The Metadata about the primitive to load
	 * @param vboOffset
	 *            The offset into the VBO where the vertex data is stored
	 * @param matrix
	 *            The model matrix of this primitive
	 * @param matrixOffset
	 *            The offset into <code>matrix</code> where the matrix is stored
	 * @return The byte size of the object's data in the VBO (
	 *         <code>primitive.count * stride * 4</code>)
	 */
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix,
			int matrixOffset) {
		if (program == null) {
			program = ShaderLib.getProgram(programName);
			a_pos = program.getAttribLocation(ShaderLib.A_POS);
			a_normal = program.getAttribLocation(ShaderLib.A_NORMAL);
			a_mtl = program.getAttribLocation(ShaderLib.A_MTL);
			a_model = program.getAttribLocation(ShaderLib.A_MODEL);
		}

		GLES20.glDisableVertexAttribArray(a_model);
		GLES20.glVertexAttrib4fv(a_model, matrix, matrixOffset * 16);
		GLES20.glVertexAttrib4fv(a_model, matrix, (matrixOffset * 16) + 4);
		GLES20.glVertexAttrib4fv(a_model, matrix, (matrixOffset * 16) + 8);
		GLES20.glVertexAttrib4fv(a_model, matrix, (matrixOffset * 16) + 12);
		return primitive.count * stride * 4;
	}

	public abstract int getGeometryType();

	public final String getProgramName() {
		return programName;
	}

	public final int getStride() {
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
	public abstract int loadObjectToVBO(GameObject obj, int[] vbo, int offset);

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
	protected final void loadArrayToVbo(float[] data, int[] vbo, int vboOffset,
			int size, int count) {
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < size; j++) {
				vbo[vboOffset + vPos + i * stride + j] = Float
						.floatToRawIntBits(data[i * size + j]);
			}
		}
		vPos += size;
	}

	/**
	 * Call from {@link #loadObjectToVBO(GameObject, int[], int)} before calling
	 * {@link #loadArrayToVbo(float[], int[], int, int, int)}
	 */
	protected final void clearLoadPosition() {
		vPos = 0;
	}

	/**
	 * Attaches the given attribute to the VBO.
	 * @param attrib The index of the attribute to attach.
	 * @param size The size of the attribute.
	 */
	protected final void attachAttrib(int attrib, int size) {
		GLES2.glGetError();
		final int byteStride = 4 * stride;
		GLES2.glEnableVertexAttribArray(attrib);
		GameRenderer.logError("EnableVertexAttribArray(" + attrib + ")");
		GLES2.glVertexAttribPointer(attrib, size, GLES20.GL_FLOAT, false,
				byteStride, aPos);
		GameRenderer.logError("VertexAttribPointer(" + attrib + ")");
		aPos += size * 4;
	}

	/**
	 * Attaches a generic (constant) vertex attribute.
	 * @param attrib The index of the attribute.
	 * @param value The value(s) of the attribute. Must be 1 to 4 values.
	 */
	protected final void attachAttrib(int attrib, float... value) {
		if (attrib < 0)
			return;
		if (value.length < 1)
			throw new IllegalArgumentException("No value supplied.");
		if (value.length > 4)
			throw new IllegalArgumentException(
					"Cannot attach more than 4 values.");

		GLES2.glDisableVertexAttribArray(attrib);
		GameRenderer.logError("DisableVertexAttribArray(" + attrib + ")");
		switch (value.length) {
		case 1:
			GLES2.glVertexAttrib1fv(attrib, value, 0);
			GameRenderer.logError("VertexAttrib1fv(" + attrib + ")");
			break;
		case 2:
			GLES2.glVertexAttrib2fv(attrib, value, 0);
			GameRenderer.logError("VertexAttrib2fv(" + attrib + ")");
			break;
		case 3:
			GLES2.glVertexAttrib3fv(attrib, value, 0);
			GameRenderer.logError("VertexAttrib3fv(" + attrib + ")");
			break;
		case 4:
			GLES2.glVertexAttrib4fv(attrib, value, 0);
			GameRenderer.logError("VertexAttrib4fv(" + attrib + ")");
			break;
		default:
		}
	}

	/**
	 * Call during {@link #attachAttribs(Metadata, int, float[], int)} before calling {@link #attachAttrib(int, int)}
	 * @param vboOffset
	 */
	protected void setVboOffset(int vboOffset) {
		aPos = vboOffset;
	}

}
