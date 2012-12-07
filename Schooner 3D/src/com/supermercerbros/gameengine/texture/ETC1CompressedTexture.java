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

public class ETC1CompressedTexture extends Texture {
	@SuppressWarnings("unused")
	private final Resources res;
	@SuppressWarnings("unused")
	private final int resID;
	@SuppressWarnings("unused")
	private final boolean mipmaps;

	/**
	 * Creates an Texture from the given ETC1-compressed texture file. Mipmaps
	 * are enabled by default.
	 * 
	 * @param res
	 *            The Resources to load the file from.
	 * @param id
	 *            The resource identifier of the file.
	 */
	public ETC1CompressedTexture(Resources res, int id) {
		this(res, id, true);
	}

	/**
	 * Creates a Texture from the given ETC1Texture.
	 * 
	 * @param res
	 *            The Resources to load the file from.
	 * @param id
	 *            The resource identifier of the file.
	 * @param useMipmaps
	 *            true if mipmaps should be used, false if not.
	 */
	public ETC1CompressedTexture(Resources res, int id, boolean useMipmaps) {
		this.res = res;
		this.resID = id;
		mipmaps = useMipmaps;
	}

	@Override
	protected void load() {
		// TODO write ETC1 loading code and remove @SuppressWarnings
	}

}
