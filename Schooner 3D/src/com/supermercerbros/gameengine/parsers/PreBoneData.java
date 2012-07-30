package com.supermercerbros.gameengine.parsers;

import java.util.LinkedList;

import com.supermercerbros.gameengine.armature.Bone;

class PreBoneData {
	final byte index, parentIndex;
	final float x, y, z;
	private final LinkedList<PreBoneData> children;
	
	PreBoneData(byte index, float x, float y, float z, byte parent) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.z = z;
		this.parentIndex = parent;
		
		this.children = new LinkedList<PreBoneData>();
	}
	
	void addChild(PreBoneData child) {
		children.add(child);
	}
	
	boolean isRoot() {
		return parentIndex == -1;
	}
	
	Bone toBone() {
		if (!children.isEmpty()) {
			LinkedList<Bone> boneChildren = new LinkedList<Bone>();
			for (PreBoneData child : children) {
				boneChildren.add(child.toBone());
			}
			return new Bone(index, boneChildren, x, y, z);
		} else {
			return new Bone(index, null, x, y, z);
		}
	}
}
