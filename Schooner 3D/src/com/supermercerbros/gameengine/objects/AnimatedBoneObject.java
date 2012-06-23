package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.armature.Armature;

public class AnimatedBoneObject extends GameObject {
	
	public final float[] weights;
	private final Armature armature;

	public AnimatedBoneObject(float[] verts, short[] indices, float[] uvs, short[][] doubles, Material mtl, byte[][] boneIndices,
			float[][] boneWeights, Armature armature) {
		super(verts, indices, null, uvs, doubles, mtl);
		this.armature = armature;
		
		int vertCount = verts.length / 3;
		int boneCount = armature.boneCount();
		this.weights = new float[boneCount * vertCount];
		for(int i = 0; i < vertCount; i++) {
			byte[] vertBones = boneIndices[i];
			float[] vertWeights = boneWeights[i];
			for (int j = 0; j < vertBones.length; j++) {
				weights[i * boneCount + vertBones[j]] = vertWeights[j];
			}
		}
	}
}
