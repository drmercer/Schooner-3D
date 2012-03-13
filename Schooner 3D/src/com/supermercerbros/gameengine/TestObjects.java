package com.supermercerbros.gameengine;

import java.util.ArrayList;

import com.supermercerbros.gameengine.animation.Keyframe;
import com.supermercerbros.gameengine.animation.MeshAnimation;
import com.supermercerbros.gameengine.objects.AnimatedMeshObject;
import com.supermercerbros.gameengine.objects.BasicMaterial;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.TexturedMaterial;

/**
 * Contains methods to create test objects.
 */
public class TestObjects {
	/**
	 * Creates an animated tetrahedron.
	 * 
	 * @return An animated tetrahedron.
	 */
	public static GameObject tetra() {
		ArrayList<Keyframe> keyframesA = new ArrayList<Keyframe>();
		short[] indices = { 0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3, };
		float[] colors = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				1.0f, 1.0f, 1.0f, 1.0f, };
		float[] normals = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f, };

		float[] frameA1 = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 0.0f, };
		keyframesA.add(new Keyframe(frameA1));
		float[] frameA2 = { 3.0f, 0.0f, 0.0f, 0.0f, 3.0f, 0.0f, 0.0f, 0.0f,
				3.0f, 0.0f, 0.0f, 0.0f, };
		keyframesA.add(new Keyframe(frameA2));
		float[] times = { 0, 3000 };
		MeshAnimation anim1 = new MeshAnimation(keyframesA, times, null);
		AnimatedMeshObject tetra = new AnimatedMeshObject(frameA1.clone(),
				indices, colors, normals, new BasicMaterial(), null);
		tetra.setAnimation(anim1, System.currentTimeMillis() + 1000, 3000, 1);
		return tetra;
	}

	/**
	 * Creates a textured cube.
	 * @param texName
	 *            The name of the texture to apply to the cube (as loaded into
	 *            {@link com.supermercerbros.gameengine.engine.TextureLib
	 *            TextureLib}).
	 * @return A textured cube.
	 */
	public static GameObject cube(String texName) {
		float[] verts = { 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f, };
		float[] uvs = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, };
		short[] indices = { 0, 1, 2, 1, 2, 3, 0, 1, 5, 0, 4, 5, 1, 5, 7, 1, 3,
				7, 0, 2, 4, 2, 4, 6, 3, 2, 6, 3, 6, 7, 4, 5, 7, 4, 6, 7, };
		float[] normals = { 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f, };

		GameObject cube = new GameObject(verts, indices, uvs, normals,
				new TexturedMaterial(texName), null);
		return cube;
	}

	/**
	 * Creates a textured quadrangle.
	 * @param texName
	 *            The name of the texture to apply to the quad (as loaded into
	 *            {@link com.supermercerbros.gameengine.engine.TextureLib
	 *            TextureLib}).
	 * @return A textured quad.
	 */
	public static GameObject quad(String texName) {
		float depth = -2.84f;
		float[] verts = { .5f, .5f, depth, .5f, -.5f, depth, -.5f, .5f, depth,
				-.5f, -.5f, depth, };
		float[] uvs = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };

		short[] indices = { 0, 1, 2, 1, 2, 3 };
		float[] normals = {};

		GameObject obj = new GameObject(verts, indices, uvs, normals,
				new TexturedMaterial(texName), null);
		return obj;
	}

	/**
	 * Creates a vertex-colored triangle.
	 * @return A colored triangle.
	 */
	public static GameObject tri() {
		float[] verts = { 0.0f, 0.0f, 0.0f, -0.6f, -0.6f, 0.0f, -0.6f, 0.6f,
				0.0f };
		float[] colors = { 1, 1, 0, 0, 1, 1, 1, 0, 1 };
		short[] indices = { 0, 1, 2 };
		float[] normals = {};

		GameObject tri = new GameObject(verts, indices, colors, normals,
				new BasicMaterial(), null);
		return tri;
	}

}
