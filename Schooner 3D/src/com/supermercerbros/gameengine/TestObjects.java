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

import java.util.ArrayList;
import java.util.LinkedList;

import com.supermercerbros.gameengine.animation.Keyframe;
import com.supermercerbros.gameengine.animation.MeshAnimation;
import com.supermercerbros.gameengine.collision.Bounds;
import com.supermercerbros.gameengine.collision.Polyhedron;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.material.TexturedMaterial;
import com.supermercerbros.gameengine.objects.AnimatedMeshObject;
import com.supermercerbros.gameengine.objects.BasicMaterial;
import com.supermercerbros.gameengine.objects.GameObject;

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
		// @formatter:off
		short[] indices = { 
				0, 1, 2, 
				0, 1, 3, 
				0, 2, 3, 
				1, 2, 3, };
		float[] colors = { 
				1.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, };
		float[] normals = new float[12];

		float[] frameA1 = { 
				1.0f, 0.0f, 0.0f, 
				0.0f, 1.0f, 0.0f, 
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 0.0f, };
		float[] frameA2 = { 
				3.0f, 0.0f, 0.0f, 
				0.0f, 3.0f, 0.0f, 
				0.0f, 0.0f, 3.0f, 
				0.0f, 0.0f, 0.0f, };
		//@formatter:on
		
		keyframesA.add(new Keyframe(frameA1));
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
	public static GameObject cube(Material mtl) {
		// @formatter:off
		final float[] verts = { 
				1.0f, 1.0f, 1.0f, 
				1.0f, 0.0f, 1.0f, 
				0.0f, 1.0f, 1.0f, 
				0.0f, 0.0f, 1.0f, 
				1.0f, 1.0f, 0.0f, 
				1.0f, 0.0f, 0.0f, 
				0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, };
		final float[] uvs;
		if (mtl instanceof TexturedMaterial) {
			uvs = new float[] { 
					0.0f, 0.0f, 
					0.0f, 1.0f, 
					1.0f, 0.0f, 
					1.0f, 1.0f,
					1.0f, 0.0f, 
					1.0f, 1.0f, 
					1.0f, 1.0f, 
					0.0f, 0.0f, };
		} else {
			uvs = new float[] { 
					0.0f, 0.0f, 0.0f, 
					1.0f, 0.0f, 0.0f, 
					0.0f, 1.0f, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 0.0f, 
					1.0f, 0.0f, 1.0f, 
					0.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f
			};
		}
		final short[] indices = { 
				3, 1, 0, 
				2, 3, 0, 
				0, 1, 5,
				0, 5, 4, 
				2, 0, 6, 
				6, 0, 4, 
				1, 3, 5, 
				3, 7, 5, 
				3, 2, 6, 
				3, 6, 7, 
				4, 5, 6, 
				5, 7, 6, };
		float[] normals = null;
		// formatter:on

		GameObject cube = new GameObject(verts, indices, normals, uvs,
				null, mtl);
		mtl.makeProgram();
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
		//@formatter:off
		float[] verts = { 
				.5f, .5f, depth, 
				.5f, -.5f, depth, 
				-.5f, .5f, depth,
				-.5f, -.5f, depth, };
		float[] uvs = { 
				0.0f, 0.0f, 
				0.0f, 1.0f, 
				1.0f, 0.0f, 
				1.0f, 1.0f };

		short[] indices = { 
				0, 1, 2, 
				1, 2, 3 };
		float[] normals = null;
		//@formatter:on

		GameObject obj = new GameObject(verts, indices, normals, uvs,
				null, new TexturedMaterial(texName));
		return obj;
	}

	/**
	 * Creates a vertex-colored triangle.
	 * @param mtl The Material to use
	 * 
	 * @return A colored triangle.
	 */
	public static GameObject tri(Material mtl) {
		//@formatter:off
		
		float[] verts = { 
				0.0f, 0.0f, -1.0f, 
				-0.6f, -0.6f, -1.0f, 
				-0.6f, 0.6f, -1.0f };
		float[] colors = { 
				1, 1, 0, 
				0, 1, 1, 
				1, 0, 1 };
		short[] indices = { 
				0, 2, 1 
				};
		float[] normals = null;
		
		//@formatter:on
		
		final Material material;
		if (mtl == null) {
			material = new BasicMaterial();
		} else {
			material = mtl;
		}
		GameObject tri = new GameObject(verts, indices, normals, colors,
				null, material);
		return tri;
	}
	
	public static Bounds cubeBounds() {
		//@formatter:off
		final float[] cubeV = { 
			0.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 
			1.0f, 1.0f, 0.0f, 
			0.0f, 0.0f, 1.0f, 
			1.0f, 0.0f, 1.0f, 
			0.0f, 1.0f, 1.0f, 
			1.0f, 1.0f, 1.0f, };
		final short[] cubeI = { 
			0, 2, 3, 1, // Z
			4, 5, 7, 6, // Z one

			0, 1, 5, 4, // Y
			2, 6, 7, 3, // Y one

			1, 3, 7, 5, // X
			2, 0, 4, 6, // X one
		};
		//@formatter:on
		
		final LinkedList<Polyhedron> parts = new LinkedList<Polyhedron>();
		final Polyhedron poly = new Polyhedron(Polyhedron.featureMesh(cubeV, cubeI));
		parts.add(poly);
		return new Bounds(parts, 0.0);
	}

}
