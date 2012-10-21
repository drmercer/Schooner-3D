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
