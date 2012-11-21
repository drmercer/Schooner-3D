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

/**
 * 
 */
public class CoordsConverter {
	private final int width;
	private final int halfWidth;
	private final int height;
	private final int halfHeight;

	/**
	 * Constructs a new CoordsConverter
	 */
	CoordsConverter(int width, int height) {
		this.width = width;
		this.halfWidth = width / 2;
		this.height = height;
		this.halfHeight = height / 2;
	}
	
	public float toOpenGLWidth(float pixelX) {
		return (pixelX - halfWidth) / width;
	}
	
	public float toOpenGLHeight(float pixelY) {
		// Y is made negative because the Android UI's origin is in the top
		// left corner.
		return -(pixelY - halfHeight) / height;
	}
}
