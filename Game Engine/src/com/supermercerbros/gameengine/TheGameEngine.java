package com.supermercerbros.gameengine;

public class TheGameEngine {
	/**
	 * The default VBO size, in bytes.
	 */
	public static final int DEFAULT_VBO_SIZE = 4 * (3 + 3 + 2) * 2000;
	/**
	 * The default IBO size, in bytes.
	 */
	public static final int DEFAULT_IBO_SIZE = 2 * 3 * 3000;
	/**
	 * The default maximum number of objects.
	 */
	public static final int DEFAULT_MAX_OBJECTS = 150;
	/**
	 * The default render backgroundColor color.
	 */
	public static final float[] DEFAULT_BACKGROUND_COLOR = { 0.5f, 0.5f, 0.5f,
			1.0f };
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
