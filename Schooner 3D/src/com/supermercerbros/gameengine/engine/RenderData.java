package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	public final int index;
	
	public final LinkedList<Metadata> primitives;
	public final int[] vbo;
	public final short[] ibo;

	public CopyOnWriteArrayList<float[]> modelMatrices;
	public float[] viewMatrix = new float[16];
	public Light light = new Light();

	
	public RenderData(int index, final int vboLength, final int iboLength) {
		this.index = index;
		
		vbo = new int[vboLength];
		ibo = new short[iboLength];
		primitives = new LinkedList<Metadata>();
		modelMatrices = new CopyOnWriteArrayList<float[]>();
	}
}
