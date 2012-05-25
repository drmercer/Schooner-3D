package com.supermercerbros.gameengine.engine;

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
		int[] handle = {0};
		GLES20.glGenTextures(1, handle, 0);
		return handle[0];
	}

	/**
	 * Contains the integer handle to this texture used by the OpenGL context.
	 */
	protected int handle = -1;
	private boolean loaded = false;

	/**
	 * This is called to load the Texture into the OpenGL context.
	 * 
	 * @param target
	 *            {@link GLES20#GL_TEXTURE_2D}
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
		if (!loaded) {
			load();
			loaded = true;
			EGLContextLostHandler.addListener(this);
		}
		
		int samplerLoc = GLES20.glGetUniformLocation(programHandle,
				samplerName);
		GameRenderer.logError("Texture.java: GetUniformLocation");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + glTexture);
		GameRenderer.logError("ActiveTexture(GL_TEXTURE" + glTexture + ")");
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GameRenderer.logError("BindTexture");

		GLES20.glUniform1i(samplerLoc, glTexture);
		GameRenderer.logError("Uniform1i");
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
	public final void onContextLost(){
		if (!GLES20.glIsTexture(handle)){ 
			loaded = false;
			handle = -1;
		}
	}
}
