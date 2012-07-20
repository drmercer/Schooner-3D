package com.supermercerbros.gameengine.armature;

import java.util.LinkedList;
import java.util.List;

import com.supermercerbros.gameengine.collision.Point;
import com.supermercerbros.gameengine.math.MatrixUtils;
import com.supermercerbros.gameengine.math.Quaternion;

public class Bone {
	private final byte index;
	private final LinkedList<Bone> children;
	private final float locX, locY, locZ;
	private float w = 1, x = 0, y = 0, z = 0;
	
	/**
	 * Creates a new Bone
	 * 
	 * @param index
	 *            The index of this Bone in its Skeleton's bone list.
	 * @param children
	 *            A List of this Bone's children.
	 * @param x
	 *            The x-coordinate of the Bone, in object-space coordinates
	 * @param y
	 *            The y-coordinate of the Bone, in object-space coordinates
	 * @param z
	 *            The z-coordinate of the Bone, in object-space coordinates
	 */
	public Bone(byte index, List<Bone> children, float x, float y, float z) {
		this.index = index;
		if (children != null) {
			this.children = new LinkedList<Bone>(children);
		} else {
			this.children = null;
		}
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
		array[index * 4    ] = w;
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
		final Point r = Quaternion.rotate(w, x, y, z, locX, locY, locZ);
		if (parentIndex != -1) {
			// Bone has a parent bone
			final int parentOffset = offset + parentIndex * 12;
			MatrixUtils.rotateQuaternionM(matrixArray, boneOffset, matrixArray, parentOffset, w, x, y, z);
			MatrixUtils.translateM(matrixArray, boneOffset, x - r.x, y - r.y, z - r.z);
		} else {
			// Bone is a root bone
			MatrixUtils.setRotateQuaternionM(matrixArray, boneOffset, w, x, y, z);
			MatrixUtils.translateM(matrixArray, boneOffset, x - r.x, y - r.y, z - r.z);
		}
	}
}
