package com.supermercerbros.gameengine.engine;

public class Light {
	public float x;
	public float y;
	public float z;
	public float r;
	public float g;
	public float b;

	public void copyTo(Light light) {
		if (light == this) {
			return;
		}
		if (light.x != x) {
			light.x = x;
		}
		if (light.y != y) {
			light.y = y;
		}
		if (light.z != z) {
			light.z = z;
		}
		if (light.r != r) {
			light.r = r;
		}
		if (light.g != g) {
			light.g = g;
		}
		if (light.b != b) {
			light.b = b;
		}
	}
}
