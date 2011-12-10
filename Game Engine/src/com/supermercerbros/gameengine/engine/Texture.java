package com.supermercerbros.gameengine.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.ETC1Util.ETC1Texture;

public abstract class Texture {
	/**
	 * An instance of Bitmap or ETC1Texture that contains the actual texture
	 * data.
	 */
	private Object texture;
	private boolean compressed;
	private boolean useMipmaps = false;
	/**
	 * Contains the integer handle to this texture used by the OpenGL context.
	 */
	private int handle = -1;
	@SuppressWarnings("unused")
	private int binding = -1;
	private boolean loaded = false;
	private boolean unloadAtEndOfFrame = false;

	/**
	 * Creates a new texture with the given {@link Bitmap}.
	 * 
	 * @param texture
	 *            The pixel data to use in this texture.
	 */
	Texture(Bitmap texture) {
		this.texture = texture;
		compressed = false;
	}

	/**
	 * Creates a new texture with the given {@link ETC1Texture}.
	 * 
	 * @param texture
	 *            The pixel data to use in this texture.
	 */
	Texture(ETC1Texture texture) {
		this.texture = texture;
		compressed = true;
	}

	/**
	 * @param unloadOnUnbind
	 *            True if this Texture should unload from GL when the frame has
	 *            been rendered.
	 */
	public synchronized void unloadAtEndOfFrame() {
		unloadAtEndOfFrame = true;
	}

	/**
	 * Disables mipmapping for this Texture
	 */
	public void setMipmap(boolean useMipmaps) {
		this.useMipmaps = useMipmaps;
	}

	/**
	 * This is called to load the Texture to OpenGL.
	 * 
	 * @param target {@link GLES20#GL_TEXTURE_2D}
	 */
	protected abstract void load(int target);

	/**
	 * This is called during rendering.
	 * 
	 * @param glTexture
	 * @param samplerName
	 * @param programHandle
	 */
	public void use(int glTexture, String samplerName, int programHandle) {
		load(GLES20.GL_TEXTURE_2D);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + glTexture);
		GameRenderer.logError("glActiveTexture(GL_TEXTURE" + glTexture + ")");
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GameRenderer.logError("glBindTexture");
		GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle,
				samplerName), glTexture);
		GameRenderer.logError("glUniform1i");

		binding = glTexture;

		if (TextureLib.boundTextures[glTexture] != null)
			TextureLib.boundTextures[glTexture].binding = -1;
	}

	void onEndFrame() {
		if (unloadAtEndOfFrame) {
			int[] handle = { this.handle };
			GLES20.glDeleteTextures(1, handle, 0);
			this.handle = -1;
			loaded = false;
		}
	}

	public int getHandle() {
		return handle;
	}
}
