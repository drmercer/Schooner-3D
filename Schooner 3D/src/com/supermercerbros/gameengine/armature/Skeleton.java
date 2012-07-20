package com.supermercerbros.gameengine.armature;

import java.util.LinkedList;
import java.util.List;

public class Skeleton {
	private final String id;
	private final LinkedList<Bone> rootParents;
	/**
	 * A LinkedList of the Bones in this Skeleton. It is recommended that you do not modify this list.
	 */
	protected final LinkedList<Bone> bones;
	
	public Skeleton(String id, List<Bone> roots, List<Bone> bones){
		this.id = id;
		this.rootParents = new LinkedList<Bone>(roots);
		this.bones = new LinkedList<Bone>(bones);
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
