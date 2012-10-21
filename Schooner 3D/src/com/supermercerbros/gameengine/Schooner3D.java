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

package com.supermercerbros.gameengine;

public class Schooner3D {
	/**
	 * The default maximum number of objects.
	 */
	public static final int DEFAULT_MAX_OBJECTS = 100;
	/**
	 * The approximate number of vertices per object.
	 */
	private static final int vertsPerObject = 400;
	/**
	 * The approximate number of tris per object.
	 */
	private static final int trisPerObject = 800;
	
	/**
	 * The default VBO size, in bytes.
	 */
	public static final int DEFAULT_VBO_SIZE = 4 * (3 + 3 + 2) * DEFAULT_MAX_OBJECTS * vertsPerObject;
	/**
	 * The default IBO size, in bytes.
	 */
	public static final int DEFAULT_IBO_SIZE = 2 * 3 * DEFAULT_MAX_OBJECTS * trisPerObject;
	/**
	 * The default render backgroundColor color.
	 */
	public static final float[] DEFAULT_BACKGROUND_COLOR = 
		{ 0.0f, 0.0f, 0.0f, 1.0f };
	/**
	 * The default maximum number of light.
	 */
	public static final int DEFAULT_MAX_LIGHTS = 10;
	
	/**
	 * The render backgroundColor color.
	 */
	public static float[] backgroundColor = DEFAULT_BACKGROUND_COLOR;
	/**
	 * The size of the Index Buffer Object
	 */
	public static int iboSize = DEFAULT_IBO_SIZE;
	/**
	 * The size of the Vertex Buffer Object
	 */
	public static int vboSize = DEFAULT_VBO_SIZE;
	/**
	 * The maximum number of objects to allow.
	 */
	public static int maxObjects = DEFAULT_MAX_OBJECTS;
}
