package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.armature.Action;
import com.supermercerbros.gameengine.armature.ActionData;
import com.supermercerbros.gameengine.armature.Skeleton;
import com.supermercerbros.gameengine.armature.SkeletalVertexModifier;
import com.supermercerbros.gameengine.engine.shaders.Material;

public class BonedObject extends GameObject {
	public static final int BONES_PER_VERTEX = 4;
	
	public final float[] boneWeights;
	public final byte[] boneIndices;
	private final Skeleton skeleton;
	
	private Action currentAction;
	private final ActionData actionData;
	private final int boneCount;
	
	public BonedObject(float[] verts, short[] indices, float[] uvs,
			short[][] doubles, Material mtl, byte[][] boneIndices,
			float[][] boneWeights, Skeleton skeleton) {
		super(verts, indices, null, uvs, doubles, mtl);
		
		this.skeleton = skeleton;
		
		int vertCount = verts.length / 3;
		boneCount = skeleton.boneCount();
		
		this.boneIndices = new byte[BONES_PER_VERTEX * vertCount];
		this.boneWeights = new float[BONES_PER_VERTEX * vertCount];
		
		mtl.setVertexModifier(new SkeletalVertexModifier(boneCount));
		actionData = new ActionData(boneCount);
	}
	
	/**
	 * Sets the Action of this BonedObject to the given Action
	 * 
	 * @param name
	 *            The name of the Action to begin.
	 */
	public void setAction(Action action, long time, long duration) {
		if (action != null) {
			currentAction = action;
			super.startMovement(action.movement, time, duration);
		} else {
			throw new IllegalArgumentException("action == null");
		}
	}
	
	@Override
	public void drawVerts(long time) {
		if (currentAction != null) {
			currentAction.getFrame(actionData, skeleton, time);
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
