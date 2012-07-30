package com.supermercerbros.gameengine.armature;

import java.util.LinkedList;

public class Skeleton {
	private final String id;
	private final LinkedList<Bone> rootParents;
	/**
	 * A LinkedList of the Bones in this Skeleton. It is recommended that you do not modify this list.
	 */
	protected final LinkedList<Bone> bones;
	
	public Skeleton(String id, LinkedList<Bone> roots){
		this.id = id;
		this.rootParents = roots;
		this.bones = new LinkedList<Bone>(roots);
		for (Bone root : roots) {
			root.getChildren(bones);
		}
	}
	
	public int boneCount() {
		return bones.size();
	}

	public void writeMatrices(float[] matrixArray, int offset) {
		for (Bone root : rootParents) {
			root.writeMatrix(matrixArray, offset, -1);
		}
	}
	
}
