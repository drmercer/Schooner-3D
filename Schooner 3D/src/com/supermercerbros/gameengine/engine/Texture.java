package com.supermercerbros.gameengine.engine;

import android.opengl.GLES20;

/**
 * A texture that can be applied to a primitive during rendering. This could be
 * an actual texture or some kind of map, such as a normal map, specularity map,
 * or environment map. (Note that these maps would require specialized
 * materials, which Schooner does not include at the current time.)
 * 
 * @author Daniel
 * 
 */
public abstract class Texture {
	/**
	 * Contains the integer handle to this texture used by the OpenGL context.
	 */
	protected int handle = -1;
	private int boundTo;

	/**
	 * This is called to load the Texture to OpenGL.
	 * 
	 * @param target
	 *            {@link GLES20#GL_TEXTURE_2D}
	 */
	protected abstract void load(int target);

	/**
	 * This is called during rendering.
	 * 
	 * @param glTexture
	 *            The index of the GL texture to bind to.
	 * @param samplerName
	 * @param programHandle
	 */
	public void use(int glTexture, String samplerName, int programHandle) {
		if (boundTo != glTexture) {
			load(GLES20.GL_TEXTURE_2D);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + glTexture);
			GameRenderer.logError("glActiveTexture(GL_TEXTURE" + glTexture);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
			GameRenderer.logError("glBindTexture");

			boundTo = glTexture;
			if (TextureLib.boundTextures[glTexture] != null)
				TextureLib.boundTextures[glTexture].boundTo = -1;
			TextureLib.boundTextures[glTexture] = this;
		}
		GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle,
				samplerName), glTexture);
		GameRenderer.logError("glUniform1i");

	}
}
