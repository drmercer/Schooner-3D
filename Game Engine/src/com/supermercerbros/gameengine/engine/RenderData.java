package com.supermercerbros.gameengine.engine;

import android.util.Log;

import com.supermercerbros.gameengine.objects.Metadata;

public class RenderData {
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject.Metadata";
	public int[] vbo;
	public short[] ibo;
	int ibo_updatePos;

	public float[] modelMatrices;
	public float[] viewMatrix = new float[16];
	public float[] light;
	public float[] color;

	public Metadata[] primitives;

	public RenderData copy() {
		RenderData copy = new RenderData();
		copy.vbo = vbo;
		copy.ibo = ibo;
		copy.modelMatrices = modelMatrices;
		
		copy.light = light;
		copy.color = color;

		copy.viewMatrix = viewMatrix;
		copy.primitives = new Metadata[primitives.length];
		for (int i = 0; i < primitives.length; i++) {
			if (primitives[i] == null)
				Log.e(TAG, "null Metadata at " + i);
			copy.primitives[i] = primitives[i].clone();
		}

		return copy;
	}

}
