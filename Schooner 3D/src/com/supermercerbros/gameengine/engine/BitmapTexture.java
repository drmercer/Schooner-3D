package com.supermercerbros.gameengine.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class BitmapTexture extends Texture {
	private static final String TAG = "BitmapTexture";
	private Bitmap bmp;
	private boolean useMipmaps;

	/**
	 * Creates a Texture based on the given Bitmap. Mipmaps are enabled by
	 * default.
	 * 
	 * @param bmp
	 *            The bitmap to make into a texture.
	 */
	BitmapTexture(Bitmap bmp) {
		this.bmp = bmp;
		useMipmaps = true;
	}

	/**
	 * Creates a Texture based on the given Bitmap. Mipmaps are enabled by
	 * default.
	 * 
	 * @param bmp
	 *            The bitmap to make into a texture.
	 * @param useMipmaps
	 *            true if useMipmaps should be used.
	 */
	BitmapTexture(Bitmap bmp, boolean useMipmaps) {
		this.bmp = bmp;
		this.useMipmaps = useMipmaps;
	}

	@Override
	protected void load() {
		handle = genTextureHandle();
		Log.d(TAG, "glGenTextures generated handle: " + handle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		if (useMipmaps) {
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_LINEAR_MIPMAP_NEAREST);
		} else {
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		}
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		if (useMipmaps)
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GameRenderer.logError("BitmapTexture.load()");
	}

}
