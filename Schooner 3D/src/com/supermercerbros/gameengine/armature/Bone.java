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

package com.supermercerbros.gameengine.armature;

import java.util.LinkedList;
import java.util.List;

import com.supermercerbros.gameengine.math.MatrixUtils;

public class Bone {
	public final byte index;
	private final LinkedList<Bone> children;
	private final float locX, locY, locZ;
	private float w = 1, x = 0, y = 0, z = 0;
	
	/**
	 * Creates a new Bone
	 * 
	 * @param index
	 *            The index of this Bone in its Skeleton's bone list.
	 * @param children
	 *            A LinkedList of this Bone's children.
	 * @param x
	 *            The x-coordinate of the Bone, in object-space coordinates
	 * @param y
	 *            The y-coordinate of the Bone, in object-space coordinates
	 * @param z
	 *            The z-coordinate of the Bone, in object-space coordinates
	 */
	public Bone(byte index, LinkedList<Bone> children, float x, float y, float z) {
		this.index = index;
		this.children = children;
		this.locX = x;
		this.locY = y;
		this.locZ = z;
	}

	/**
	 * Writes this Bone's current rotation (in quaternion form) to the given
	 * array.
	 * 
	 * @param array
	 */
	void getRotation(float[] array) {
		array[index * 4] = w;
		array[index * 4 + 1] = x;
		array[index * 4 + 2] = y;
		array[index * 4 + 3] = z;
	}

	/**
	 * Sets this Bone's current rotation (in quaternion form).
	 * 
	 * @param w
	 *            The w-component of the quaternion.
	 * @param x
	 *            The y-component of the quaternion.
	 * @param y
	 *            The x-component of the quaternion.
	 * @param z
	 *            The z-component of the quaternion.
	 */
	void setRotation(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void writeMatrix(float[] matrixArray, int offset, int parentIndex) {
		final int boneOffset = offset + index * 16;

		if (parentIndex != -1) {
			// Bone has a parent bone
			final int parentOffset = offset + parentIndex * 16;
			MatrixUtils.translateM(matrixArray, boneOffset, matrixArray,
					parentOffset, locX, locY, locZ);
		} else {
			// Bone is a root bone
			MatrixUtils.setTranslateM(matrixArray, boneOffset, locX, locY, locZ);
		}
		// This "incorrect" order of coordinates seems to make skeletal
		// animation work correctly. I think Blender uses a different axis
		// system for rotating bones with quaternions. 
		// TODO: Move this hack to export.py
		MatrixUtils.rotateQuaternionM(matrixArray, boneOffset, w, -x, z, y);
		MatrixUtils.translateM(matrixArray, boneOffset, -locX, -locY, -locZ);

		if (children != null) {
			for (Bone child : children) {
				child.writeMatrix(matrixArray, offset, index);
			}
		}
	}

	public void getChildren(List<Bone> list) {
		if (children != null) {
			list.addAll(children);
			for (Bone child : children) {
				child.getChildren(list);
			}
		}
	}
}
