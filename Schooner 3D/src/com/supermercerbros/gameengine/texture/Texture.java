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

package com.supermercerbros.gameengine.texture;

import com.supermercerbros.gameengine.engine.EGLContextLostHandler;
import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.engine.EGLContextLostHandler.EGLContextLostListener;

import android.opengl.GLES20;

/**
 * A texture that can be applied to a primitive during rendering. This could be
 * an actual texture or some kind of map, such as a normal map, specularity map,
 * or environment map. (Note that these maps would require specialized
 * materials, which Schooner does not include at the current time.)
 * 
 */
public abstract class Texture implements EGLContextLostListener {
	protected static int genTextureHandle() {
		int[] handle = { 0 };
		GLES20.glGenTextures(1, handle, 0);
		return handle[0];
	}

	/**
	 * Contains the integer handle to this texture used by the OpenGL context.
	 */
	protected int handle = -1;
	protected boolean loaded = false;

	private int wrapU = GLES20.GL_CLAMP_TO_EDGE;
	private int wrapV = GLES20.GL_CLAMP_TO_EDGE;

	/**
	 * This is called to load the Texture into the OpenGL context.
	 */
	protected abstract void load();

	/**
	 * This is called during rendering.
	 * 
	 * @param glTexture
	 *            The index of the GL texture to bind to.
	 * @param samplerName
	 * @param programHandle
	 */
	public void use(int glTexture, String samplerName, int programHandle) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + glTexture);
		GameRenderer.logError("ActiveTexture(GL_TEXTURE" + glTexture + ")");
		if (!loaded) {
			load();
			loaded = true;
			EGLContextLostHandler.addListener(this);
			// If problems arise, move this back to BitmapTexture.load
			// (right after texImage2D)
			synchronized (this) {
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_S, wrapU);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_T, wrapV);
			}
		}

		int samplerLoc = GLES20
				.glGetUniformLocation(programHandle, samplerName);
		GameRenderer.logError("Texture.java: GetUniformLocation");
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GameRenderer.logError("BindTexture");

		GLES20.glUniform1i(samplerLoc, glTexture);
		GameRenderer.logError("Uniform1i");
	}

	/**
	 * Sets the wrapping modes for the Texture. At least one parameter must be
	 * one of {@link GLES20#GL_CLAMP_TO_EDGE}, {@link GLES20#GL_REPEAT}, or
	 * {@link GLES20#GL_MIRRORED_REPEAT}. If one given parameter is not a valid
	 * wrap mode, the mode for that direction is not changed.
	 * 
	 * @param wrapU
	 *            The wrapping mode for the U (a.k.a S or X) direction.
	 * @param wrapV
	 *            The wrapping mode for the V (a.k.a T or Y) direction.
	 * 
	 * @throws IllegalArgumentException
	 *             if neither wrapU nor wrapV is a valid wrap mode.
	 */
	public void setWrap(int wrapU, int wrapV) {
		final boolean wrapUIsValid = wrapU == GLES20.GL_CLAMP_TO_EDGE
				|| wrapU == GLES20.GL_MIRRORED_REPEAT
				|| wrapU == GLES20.GL_REPEAT;
		final boolean wrapVIsValid = wrapV == GLES20.GL_CLAMP_TO_EDGE
				|| wrapV == GLES20.GL_MIRRORED_REPEAT
				|| wrapV == GLES20.GL_REPEAT;
		if (!wrapUIsValid && !wrapVIsValid) {
			throw new IllegalArgumentException(
					"Neither wrapU nor wrapV is a valid wrap mode.");
		}
		synchronized (this) {
			if (wrapUIsValid) {
				this.wrapU = wrapU;
			}
			if (wrapVIsValid) {
				this.wrapV = wrapV;
			}
		}
	}

	/**
	 * Unloads this texture from the GPU
	 */
	public void unload() {
		int[] tex = { handle };
		GLES20.glDeleteTextures(1, tex, 0);
		loaded = false;
		handle = -1;
	}

	@Override
	public final void onContextLost() {
		if (loaded && !GLES20.glIsTexture(handle)) {
			loaded = false;
			handle = -1;
		}
	}
}
