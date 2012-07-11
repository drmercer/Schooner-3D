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
