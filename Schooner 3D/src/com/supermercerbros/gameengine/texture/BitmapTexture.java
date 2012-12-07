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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.supermercerbros.gameengine.engine.GameRenderer;

public class BitmapTexture extends Texture {
	private static final String TAG = "BitmapTexture";

	private final Resources res;
	private final int resID;
	private final boolean useMipmaps;
	
	/**
	 * Creates a Texture based on the given Bitmap. Mipmaps are enabled by
	 * default.
	 * 
	 * @param res The Resources to load the Bitmap from.
	 * 
	 * @param bmp
	 *            The bitmap to make into a texture.
	 */
	public BitmapTexture(Resources res, int id) {
		this(res, id, true);
	}

	/**
	 * Creates a Texture based on the given Bitmap. Mipmaps are enabled by
	 * default.
	 * 
	 * @param res
	 *            The Resources to load the Bitmap from.
	 * @param id
	 *            The resource id of the image to use as a texture.
	 * @param useMipmaps
	 *            true if useMipmaps should be used.
	 */
	public BitmapTexture(Resources res, int id, boolean useMipmaps) {
		this.res = res;
		this.resID = id;
		this.useMipmaps = useMipmaps;
	}

	@Override
	protected void load() {
		final BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		final Bitmap bmp = BitmapFactory.decodeResource(res, resID, opts);
		handle = genTextureHandle();
		Log.d(TAG, "glGenTextures generated handle: " + handle);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

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
		if (useMipmaps) {
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		}
		GameRenderer.logError("BitmapTexture.load()");
	}

}
