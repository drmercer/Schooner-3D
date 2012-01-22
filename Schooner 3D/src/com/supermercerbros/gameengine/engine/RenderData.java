package com.supermercerbros.gameengine.engine;

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	public int[] vbo;
	public short[] ibo;
	int ibo_updatePos;

	public float[] modelMatrices;
	public float[] viewMatrix = new float[16];
	public float[] light;
	public float[] color;

	public Metadata[] primitives;
	
	protected RenderData prep(){
		for (Metadata primitive : primitives){
			primitive.prep();
		}
		return this;
	}

}
