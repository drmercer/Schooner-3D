package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	public final LinkedList<Metadata> primitives;
	public final int[] vbo;
	public final short[] ibo;
	int ibo_updatePos;

	public CopyOnWriteArrayList<float[]> modelMatrices;
	public float[] viewMatrix = new float[16];
	public Light light = new Light();

	
	public RenderData(final int vboLength, final int iboLength) {
		vbo = new int[vboLength];
		ibo = new short[iboLength];
		primitives = new LinkedList<Metadata>();
	}
	
	protected RenderData prep(){
		for (Metadata primitive : primitives){
			primitive.prep();
		}
		return this;
	}

}
