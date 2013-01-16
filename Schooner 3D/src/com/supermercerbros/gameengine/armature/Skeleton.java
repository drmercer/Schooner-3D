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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import android.util.Log;

public class Skeleton {
	private static final String TAG = "Skeleton";
	private final String id;
	private final LinkedList<Bone> rootParents;
	/**
	 * A LinkedList of the Bones in this Skeleton. It is recommended that you do not modify this list.
	 */
	public final LinkedList<Bone> bones;
	
	public Skeleton(String id, LinkedList<Bone> roots){
		this.id = id;
		this.rootParents = roots;
		this.bones = new LinkedList<Bone>(roots);
		for (Bone root : roots) {
			root.getChildren(bones);
		}

		// sort Bones by index
		final Comparator<Bone> c = new Comparator<Bone>() {
			@Override
			public int compare(Bone lhs, Bone rhs) {
				return lhs.index - rhs.index;
			}
		};
		Collections.sort(roots, c);
		Collections.sort(bones, c);
		
		Log.d(TAG, "roots.size() = " + roots.size());
		Log.d(TAG, "bones.size() = " + bones.size());
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
