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

package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.armature.Action;
import com.supermercerbros.gameengine.armature.ActionData;
import com.supermercerbros.gameengine.armature.SkeletalVertexModifier;
import com.supermercerbros.gameengine.armature.Skeleton;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.parsers.PreObjectData;

public class BonedObject extends GameObject {
	public static final int BONES_PER_VERTEX = 4;
	
	public final float[] boneWeights;
	public final byte[] boneIndices;
	private final Skeleton skeleton;
	
	private Action currentAction;
	private final ActionData actionData;
	private final int boneCount;
	
	public BonedObject(PreObjectData data, Material material, Skeleton skeleton) {
		super(data, material);
		this.skeleton = skeleton;
		
		int vertCount = info.count;
		boneCount = skeleton.boneCount();
		
		// Init boneIndices and boneWeights
		this.boneIndices = new byte[BONES_PER_VERTEX * vertCount];
		this.boneWeights = new float[BONES_PER_VERTEX * vertCount];
		
		// Fill boneIndices and boneWeights
		final byte[][] localIndices = data.boneIndices;
		final float[][] localWeights = data.boneWeights;
		final int vertexWeightCount = Math.min(BONES_PER_VERTEX,
				localIndices[0].length);
		
		for (int vert = 0; vert < vertCount; vert++) {
			final int vertOffset = vert * 4;
			for (int j = 0; j < vertexWeightCount; j++) {
				int offset = vertOffset + j;
				boneIndices[offset] = localIndices[vert][j];
				boneWeights[offset] = localWeights[vert][j];
			}
		}
		
		// Init VertexModifier and ActionData
		material.setVertexModifier(new SkeletalVertexModifier(BONES_PER_VERTEX,
				boneCount));
		actionData = new ActionData(boneCount);
	}
	
	/**
	 * Sets the Action of this BonedObject to the given Action
	 * 
	 * @param action
	 *            The Action to start.
	 * @param duration
	 *            The duration of the Action, in milliseconds.
	 */
	public void setAction(Action action, long duration) {
		if (action != null) {
			final long time = System.currentTimeMillis();
			currentAction = action;
			actionData.writeState(time, time, duration, skeleton);
			// TODO: time, time? (add delay support to this method and to movements)
			super.startMovement(action.movement, time, duration);
		} else {
			throw new IllegalArgumentException("action == null");
		}
	}
	
	@Override
	public void drawVerts(long time) {
		if (currentAction != null) {
			currentAction.update(actionData, skeleton, time);
		}
	}
	
	@Override
	public int getExtraMatrixCount() {
		return boneCount;
	}
	
	@Override
	public void writeMatrices(float[] matrixArray) {
		super.writeMatrices(matrixArray);
		skeleton.writeMatrices(matrixArray, 16);
	}
}
