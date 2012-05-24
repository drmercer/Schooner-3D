package com.supermercerbros.gameengine.objects;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.engine.Program;
import com.supermercerbros.gameengine.engine.ShaderLib;
import com.supermercerbros.gameengine.util.GLES2;

/**
 * Superclass for materials to be used when rendering 3D objects. Subclasses
 * should override this and call the super method like so:
 * 
 * <pre>
 * public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
 * 	int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
 * 	attachAttrib(a_pos, 3);
 * 	
 * 	... //
 * }
 * </pre>
 */
public abstract class Material {
	/**
	 * Contains the OpenGL shader program used by this Material. This field is
	 * initialized during {@link #attachAttribs(Metadata, int, float[], int)}.
	 */
	protected Program program;

	/**
	 * Contains the handle to the <code>a_pos</code> attribute in the shader,
	 * the attribute used to store vertex position. This handle is initialized
	 * during {@link #attachAttribs(Metadata, int, float[], int)}.
	 */
	protected int a_pos;
	/**
	 * Contains the handle to the <code>a_normal</code> attribute in the shader,
	 * the attribute used to store vertex normals. This handle is initialized
	 * during {@link #attachAttribs(Metadata, int, float[], int)}.
	 */
	protected int a_normal;
	/**
	 * Contains the handle to the <code>a_mtl</code> attribute in the shader,
	 * the attribute used to store vertex-specific material data, such as colors
	 * or uv-coordinates. This handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[], int)}.
	 */
	protected int a_mtl;

	/**
	 * Contains the handle to the <code>a_mtl</code> attribute in the shader,
	 * the attribute used to store the object-specific tranformation matrix.
	 * This handle is initialized during
	 * {@link #attachAttribs(Metadata, int, float[], int)}.
	 */
	private int a_model;

	/**
	 * The loading offset for the VBO. Starts at the offset provided by {@link #setLoadOffset(int)}
	 */
	private int inPos = 0;
	/**
	 * The unloading offset for the VBO. Starts at the offset where the data begins.
	 */
	private int outPos = 0;
	/**
	 * The name of the program that this Material uses.
	 */
	private final String programName;
	private final int stride;

	/**
	 * @param programName The name of the program in ShaderLib
	 * @param stride The number of floats per vert
	 */
	protected Material(String programName, int stride) {
		this.programName = programName;
		this.stride = stride;
	}

	/**
	 * Called by the renderer to attach the vertex attributes. Subclasses should
	 * override this and call the super method like so:
	 * <pre>
	 * public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix, int matrixOffset) {
	 * 	int response = super.attachAttribs(primitive, vboOffset, matrix, matrixOffset);
	 * 
	 * 	attachAttrib(a_pos, 3); // Attach attributes
	 * 	... 
	 *  
	 * 	return response;
	 * }
	 * </pre>
	 * 
	 * @param primitive
	 *            The Metadata about the primitive to load
	 * @param vboOffset
	 *            The offset into the VBO where the vertex data is stored
	 * @param matrix
	 *            The model matrix of this primitive
	 * @param matrixIndex
	 *            The offset into <code>matrix</code> where the matrix is stored
	 * @return The byte size of the object's data in the VBO (
	 *         <code>primitive.count * stride * 4</code>)
	 */
	public int attachAttribs(Metadata primitive, int vboOffset, float[] matrix,
			int matrixIndex) {
		if (program == null) {
			program = ShaderLib.getProgram(programName);
			a_pos = program.getAttribLocation(ShaderLib.A_POS);
			a_normal = program.getAttribLocation(ShaderLib.A_NORMAL);
			a_mtl = program.getAttribLocation(ShaderLib.A_MTL);
			a_model = program.getAttribLocation(ShaderLib.A_MODEL);
		}

		GLES20.glDisableVertexAttribArray(a_model);
		GLES20.glDisableVertexAttribArray(a_model + 1);
		GLES20.glDisableVertexAttribArray(a_model + 2);
		GLES20.glDisableVertexAttribArray(a_model + 3);
		
		GLES20.glVertexAttrib4fv(a_model + 0, matrix, matrixIndex * 16 + 0);
		GLES20.glVertexAttrib4fv(a_model + 1, matrix, matrixIndex * 16 + 4);
		GLES20.glVertexAttrib4fv(a_model + 2, matrix, matrixIndex * 16 + 8);
		GLES20.glVertexAttrib4fv(a_model + 3, matrix, matrixIndex * 16 + 12);

		outPos = vboOffset;

		return primitive.count * stride * 4;
	}

	/**
	 * @return The OpenGL geometry type. Usually {@link GLES20#GL_TRIANGLES}
	 */
	public abstract int getGeometryType();

	/**
	 * @return The name of the program that this Material uses
	 */
	public final String getProgramName() {
		return programName;
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
	protected final void loadArrayToVbo(float[] data, int[] vbo,
			int size, int count) {
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < size; j++) {
				vbo[inPos + i * stride + j] = Float
						.floatToRawIntBits(data[i * size + j]);
			}
		}
		inPos += size;
	}

	/**
	 * Call from {@link #loadObjectToVBO(GameObject, int[], int)} before calling
	 * {@link #loadArrayToVbo(float[], int[], int, int, int)}
	 */
	protected final void setLoadOffset(int vboOffset) {
		inPos = vboOffset;
	}

	/**
	 * Attaches the given attribute to the VBO.
	 * 
	 * @param attrib
	 *            The index of the attribute to attach.
	 * @param size
	 *            The size of the attribute.
	 */
	protected final void attachAttrib(int attrib, int size) {
		GLES2.glGetError();
		final int byteStride = 4 * stride;
		GLES2.glEnableVertexAttribArray(attrib);
		GameRenderer.logError("EnableVertexAttribArray(" + attrib + ")");
		GLES2.glVertexAttribPointer(attrib, size, GLES20.GL_FLOAT, false,
				byteStride, outPos);
		GameRenderer.logError("VertexAttribPointer(" + attrib + ")");
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
	
	protected void setUniform(int location, float... args){
		if (args.length == 1) {
			GLES2.glUniform1fv(location, 1, args, 0);
		} else if (args.length == 2) {
			GLES2.glUniform2fv(location, 1, args, 0);
		} else if (args.length == 3) {
			GLES2.glUniform3fv(location, 1, args, 0);
		} else if (args.length == 4) {
			GLES2.glUniform4fv(location, 1, args, 0);
		} else {
			throw new UnsupportedOperationException("args must have 1 to 4 elements.");
		}
	}
	
	protected void setUniform(String name, float... args) {
		setUniform(program.getUniformLocation(name), args);
	}
	
	public int getStride(){
		return stride;
	}
}
