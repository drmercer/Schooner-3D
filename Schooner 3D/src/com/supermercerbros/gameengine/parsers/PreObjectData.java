package com.supermercerbros.gameengine.parsers;

/**
 * Contains GameObject data before it is made into a GameObject.
 */
public class PreObjectData {
	public final float[] verts;
	public final short[] indices;
	public final float[] uvs;
	public final short[][] doubles;
	
	public final byte[][] boneIndices;
	public final float[][] boneWeights;
	
	public float[] matrix;
	
	PreObjectData(float[] verts, short[] indices, float[] uvs,
			short[][] doubles, byte[][] boneIndices, float[][] boneWeights) {
		this.verts = verts;
		this.doubles = doubles;
		this.indices = indices;
		this.uvs = uvs;
		this.boneIndices = boneIndices;
		this.boneWeights = boneWeights;
	}
	
	public boolean isArmatureIndexed() {
		return boneIndices != null;
	}
	
	public boolean isTextured() {
		return doubles != null;
	}
	
}