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
package com.supermercerbros.gameengine.hud;

import java.nio.ByteBuffer;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.util.GLES2;

/**
 * Represents an element of a {@link GameHud}. Subclasses can override
 * {@link #getIndices()}, {@link #getUVs()}, and {@link #getVerts()} to change
 * the default setup.
 */
public abstract class HudElement {
	private final Program program;
	final float bottom;
	final float right;
	final float top;
	final float left;

	// Handles
	private int programHandle;
	private int a_uv;
	private int a_pos;
	private boolean hasUVs;

	private int vboOffset;
	private int iboOffset;
	private int indexCount;

	// Protected fields
	/**
	 * This HudElement's OpenGL primitive type
	 */
	protected int primitiveType = GLES20.GL_TRIANGLE_STRIP;

	/**
	 * Constructs a new HudElement. Coordinates are given in the range [-1, 1].
	 * 
	 * @param program
	 *            The Program used to render this HudElement.
	 * @param left
	 *            The x-coordinate of the left edge of the element.
	 * @param right
	 *            The x-coordinate of the right edge of the element.
	 * @param top
	 *            The y-coordinate of the top edge of the element.
	 * @param bottom
	 *            The y-coordinate of the bottom edge of the element.
	 */
	protected HudElement(Program program, float left, float right, float top,
			float bottom) {
		if (program == null) {
			throw new NullPointerException("program == null");
		}
		if (right <= left) {
			throw new IllegalArgumentException(
					"right must be greater than left");
		}
		if (top <= bottom) {
			throw new IllegalArgumentException(
					"top must be greater than bottom");
		}
		this.program = program;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	// ============================
	// OVERRIDEABLE METHODS
	// ============================

	/**
	 * Returns the indices of the element.
	 * 
	 * By default, the returned array is this:
	 * 
	 * <pre>
	 * { 0, 1, 2, 3 }
	 * </pre>
	 * 
	 * Subclasses can override this to use custom indices. Make sure that they
	 * are compatible with the primitive type returned by
	 * {@link #getPrimitiveType()};
	 * 
	 * @return The indices of the element.
	 */
	protected byte[] getIndices() {
		return new byte[] { 0, 1, 2, 3 };
	}

	/**
	 * Returns the vertices of the element. By default, the returned array is
	 * this:
	 * 
	 * <pre>
	 * { left, top, left, bottom, right, top, right, bottom }
	 * </pre>
	 * 
	 * Subclasses can override this to use custom vertex coordinates.
	 * 
	 * @return The verts of the element.
	 */
	protected float[] getVerts() {
		return new float[] { left, top, left, bottom, right, top, right, bottom };
	}

	/**
	 * Returns the texture coordinates of the element. By default, the returned
	 * array is this:
	 * 
	 * <pre>
	 * { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f }
	 * </pre>
	 * 
	 * Subclasses can override this to use custom vertex coordinates.
	 * 
	 * @return The UV coordinates of the element.
	 */
	protected float[] getUVs() {
		return new float[] { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
	}

	/**
	 * Called immediately before the element is drawn. Any custom OpenGL stuff
	 * should be done here. This method is called while synchronized on this
	 * object.
	 */
	protected abstract void onRender();

	// ============================
	// OTHER METHODS
	// ============================

	/** Called by the {@link GameHud}. */
	synchronized void render() {
		GLES20.glUseProgram(programHandle);
		GLES20.glEnableVertexAttribArray(a_pos);
		if (hasUVs) {
			GLES2.glVertexAttribPointer(a_pos, 2, GLES20.GL_FLOAT, false, 4,
					vboOffset);
			GLES20.glEnableVertexAttribArray(a_uv);
			GLES2.glVertexAttribPointer(a_uv, 2, GLES20.GL_FLOAT, false, 4,
					vboOffset + 2);
		} else {
			GLES2.glVertexAttribPointer(a_pos, 2, GLES20.GL_FLOAT, false, 0,
					vboOffset);
		}
		onRender();
		GLES2.glDrawElements(primitiveType, indexCount,
				GLES20.GL_UNSIGNED_BYTE, iboOffset);
	}

	/** Called by the {@link GameHud}. */
	synchronized void writeIndicesToBuffer(ByteBuffer ibo) {
		iboOffset = ibo.position();
		final byte[] indices = getIndices();
		ibo.put(indices);
		indexCount = indices.length;
	}

	/** Called by the {@link GameHud}. */
	synchronized void writeVertsToBuffer(ByteBuffer vbo) {
		// Get vboOffset
		this.vboOffset = vbo.position();

		// Get verts and UVs
		final float[] verts = getVerts();
		final int count = verts.length;
		if (hasUVs) {
			final float[] uvs = getUVs();
			for (int i = 0; i < count; i += 2) {
				// Write verts and UVs to buffer
				vbo.putFloat(verts[i]);
				vbo.putFloat(verts[i + 1]);
				vbo.putFloat(uvs[i]);
				vbo.putFloat(uvs[i + 1]);
			}
		} else {
			for (int i = 0; i < count; i += 2) {
				// Write verts to buffer
				vbo.putFloat(verts[i]);
				vbo.putFloat(verts[i + 1]);
			}
		}
	}

	/** Called by the {@link GameHud}. */
	synchronized void loadProgram() {
		// Load program
		this.programHandle = program.load();

		// get attribute locations
		this.a_pos = program.getAttribLocation("a_pos");
		this.a_uv = program.getAttribLocation("a_uv");
		this.hasUVs = (a_uv != -1);
		onLoadProgram();
	}

	/**
	 * Called (while synchronized) after the program has been loaded.
	 */
	protected abstract void onLoadProgram();

	/**
	 * Called when a touch event occurs in this HudElement.
	 * 
	 * @param event
	 *            The MotionEvent.
	 * @param converter
	 *            Use this to convert the event coordinates to normalized device
	 *            coordinates.
	 * @return <code>true</code> if the event was handled, <code>false</code> if
	 *         otherwise.
	 */
	protected abstract boolean onTouchEvent(MotionEvent event,
			CoordsConverter converter);
	
	/**
	 * Tests whether the given coordinates are within this HudElement.
	 * @param x
	 * @param y
	 * @return
	 */
	protected boolean testCoordinates(float x, float y) {
		return x > this.left && x < this.right &&
				y > this.bottom && y < this.top;
	}

}
