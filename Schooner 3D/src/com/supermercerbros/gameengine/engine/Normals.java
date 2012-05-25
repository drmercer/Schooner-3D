package com.supermercerbros.gameengine.engine;

import java.util.Arrays;

import com.supermercerbros.gameengine.math.Vector;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.util.Utils;

public class Normals {

	public static void calculate(GameObject object) {
		if (object.normals == null || object.normals.length != object.verts.length) {
			object.normals = new float[object.verts.length];
		} else {
			Arrays.fill(object.normals, 0.0f);
		}
		float[] normals = object.normals, verts = object.verts;
		short[] indices = object.indices;
		short[][] doubles = object.doubles;

		float[] vecA = new float[3], vecB = new float[3], normal = new float[3];

		for (int faceIndex = 0; faceIndex < indices.length / 3; faceIndex++) {
			float cX = verts[indices[faceIndex * 3 + 1] * 3 + 0];
			float cY = verts[indices[faceIndex * 3 + 1] * 3 + 1];
			float cZ = verts[indices[faceIndex * 3 + 1] * 3 + 2];

			vecA[0] = verts[indices[faceIndex * 3 + 2] * 3 + 0] - cX;
			vecA[1] = verts[indices[faceIndex * 3 + 2] * 3 + 1] - cY;
			vecA[2] = verts[indices[faceIndex * 3 + 2] * 3 + 2] - cZ;

			vecB[0] = verts[indices[faceIndex * 3 + 0] * 3 + 0] - cX;
			vecB[1] = verts[indices[faceIndex * 3 + 0] * 3 + 1] - cY;
			vecB[2] = verts[indices[faceIndex * 3 + 0] * 3 + 2] - cZ;

			Vector.cross(normal, 0, vecA, 0, vecB, 0, true);
			

			for (int index = 0; index < 3; index++) {
				int offset = indices[faceIndex * 3 + index] * 3;
				normals[offset + 0] += normal[0];
				normals[offset + 1] += normal[1];
				normals[offset + 2] += normal[2];
			}
		}

		for (int i = 0; i < doubles[0].length; i++) {
			short indexA = doubles[0][i], indexB = doubles[1][i];
			float nX = normals[indexA * 3 + 0] + normals[indexB * 3 + 0];
			float nY = normals[indexA * 3 + 1] + normals[indexB * 3 + 1];
			float nZ = normals[indexA * 3 + 2] + normals[indexB * 3 + 2];

			normals[indexB * 3 + 0] = (normals[indexA * 3 + 0] = nX);
			normals[indexB * 3 + 1] = (normals[indexA * 3 + 1] = nY);
			normals[indexB * 3 + 2] = (normals[indexA * 3 + 2] = nZ);
		}
		
		for (int i = 0; i < normals.length / 3; i++) {
			float length = Utils.pythagF(normals[i * 3 + 0],
					normals[i * 3 + 1], normals[i * 3 + 2]);
//			length = Math.max(normals[i * 3 + 0], Math.max(normals[i * 3 + 1], normals[i * 3 + 2]));
			normals[i * 3 + 0] /= length; // Normalize our new vector
											// components.
			normals[i * 3 + 1] /= length;
			normals[i * 3 + 2] /= length;
		}
	}
}
