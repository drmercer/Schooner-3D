package com.supermercerbros.gameengine.engine;

import android.opengl.ETC1Util.ETC1Texture;

public class ETC1CompressedTexture extends Texture {
	@SuppressWarnings("unused")
	private ETC1Texture tex;
	@SuppressWarnings("unused")
	private boolean mipmaps;

	/**
	 * Creates a Texture from the given ETC1Texture. Mipmaps are enabled by
	 * default.
	 * 
	 * @param tex
	 *            The bitmap to make into a texture.
	 */
	ETC1CompressedTexture(ETC1Texture tex) {
		this.tex = tex;
		mipmaps = true;
	}

	/**
	 * Creates a Texture from the given ETC1Texture.
	 * 
	 * @param tex
	 *            The bitmap to make into a texture.
	 * @param useMipmaps
	 *            true if mipmaps should be used, false if not.
	 */
	ETC1CompressedTexture(ETC1Texture tex, boolean useMipmaps) {
		this.tex = tex;
		mipmaps = useMipmaps;
	}

	@Override
	protected void load() {
		//TODO write ETC1 loading code and remove @SuppressWarnings
	}

}
