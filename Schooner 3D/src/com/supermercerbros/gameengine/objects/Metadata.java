package com.supermercerbros.gameengine.objects;

public class Metadata {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject.Metadata";

	public static final int SHADELESS = 0;
	public static final int SMOOTH_SHADING = 1;
	public static final int NORMAL_MAP = 2;

	public static final int SINGLE_COLOR = 3;
	public static final int MULTICOLOR = 4;
	public static final int ETC1 = 5;
	public static final int BITMAP = 6;

	/**
	 * Contains the number of indices in the described GameObject. Used by the
	 * renderer.
	 */
	public int size;
	/**
	 * Contains the number of vertices in the described GameObject. Used by the
	 * renderer.
	 */
	public int count;
	/**
	 * True if the described GameObject is marked for deletion.
	 */
	public boolean delete = false;
	/**
	 * The material to render this object with.
	 */
	public Material mtl;

	Metadata() {
	}

	public void prep() {
		// When I put a dynamic variable in this class, I will need to make a
		// duplicate variable for the renderer to use. I'll update that
		// duplicate here.
	}

}