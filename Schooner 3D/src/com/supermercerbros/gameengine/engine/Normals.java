package com.supermercerbros.gameengine.engine;

import java.util.Arrays;

import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.util.Utils;

public class Normals {

	public static void calculate(GameObject object) {
		if (object.normals == null || object.normals.length != object.verts.length) {
			throw new IllegalStateException("object.normals is not equal in length to object.verts");
		} else {
			Arrays.fill(object.normals, 0.0f);
		}
		final float[] normals = object.normals, verts = object.verts;
		final short[] indices = object.indices;
		final short[][] doubles = object.doubles;

		final float[] vectors = new float[9];

		for (int faceIndex = 0; faceIndex < indices.length; faceIndex += 3) {
			// For each face...
			
			final int index1 = indices[faceIndex + 1] * 3;
			final float cX = verts[index1    ];
			final float cY = verts[index1 + 1];
			final float cZ = verts[index1 + 2];

			final int index2 = indices[faceIndex + 2] * 3;
			vectors[0] = verts[index2    ] - cX;
			vectors[1] = verts[index2 + 1] - cY;
			vectors[2] = verts[index2 + 2] - cZ;

			final int index0 = indices[faceIndex    ] * 3;
			vectors[3] = verts[index0    ] - cX;
			vectors[4] = verts[index0 + 1] - cY;
			vectors[5] = verts[index0 + 2] - cZ;
			
			// Compute cross product
			vectors[6] = vectors[1] * vectors[5]
					- vectors[2] * vectors[4];
			vectors[7] = vectors[2] * vectors[3]
					- vectors[0] * vectors[5];
			vectors[8] = vectors[0] * vectors[4]
					- vectors[1] * vectors[3];
			
			// Normalize result
			float length = Utils.pythagF(vectors[6],
					vectors[7], vectors[8]);
			vectors[6] /= length;
			vectors[7] /= length;
			vectors[8] /= length;
			
			// Add result to normals of vertices of face			
			normals[index0    ] += vectors[6];
			normals[index0 + 1] += vectors[7];
			normals[index0 + 2] += vectors[8];
			
			normals[index1    ] += vectors[6];
			normals[index1 + 1] += vectors[7];
			normals[index1 + 2] += vectors[8];
			
			normals[index2    ] += vectors[6];
			normals[index2 + 1] += vectors[7];
			normals[index2 + 2] += vectors[8];
		}
		
		if (doubles != null) {
			for (int i = 0; i < doubles[0].length; i++) {
				int indexA = doubles[0][i] * 3, indexB = doubles[1][i] * 3;
				normals[indexB    ] = (normals[indexA    ] = normals[indexA    ] + normals[indexB    ]);
				normals[indexB + 1] = (normals[indexA + 1] = normals[indexA + 1] + normals[indexB + 1]);
				normals[indexB + 2] = (normals[indexA + 2] = normals[indexA + 2] + normals[indexB + 2]);
			}
		}
		
		for (int i = 0; i < normals.length / 3; i++) {
			float length = Utils.pythagF(normals[i * 3    ],
					normals[i * 3 + 1], normals[i * 3 + 2]);
			normals[i * 3    ] /= length; // Normalize our new vector components.
			normals[i * 3 + 1] /= length;
			normals[i * 3 + 2] /= length;
		}
	}
}
