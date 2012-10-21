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
