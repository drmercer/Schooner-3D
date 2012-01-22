package com.supermercerbros.gameengine.animation;

public class Keyframe {
	float[] verts;
	private final int size;
	
	public Keyframe(float[] verts){
		this.verts = verts;
		size = verts.length;
	}

	public int count() {
		return size / 3;
	}
	
	void loadTo(float[] array) {
		if (array.length != size){
			throw new IllegalArgumentException("Cannot copy Keyframe to a vert array of a different size.");
		}
		System.arraycopy(this.verts, 0, array, 0, size);
		
	}

}
