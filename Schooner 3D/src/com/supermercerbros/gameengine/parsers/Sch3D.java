/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.supermercerbros.gameengine.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Log;
import android.util.SparseArray;

import com.supermercerbros.gameengine.armature.Action;
import com.supermercerbros.gameengine.armature.Bone;
import com.supermercerbros.gameengine.armature.Skeleton;
import com.supermercerbros.gameengine.math.BezierCurve;
import com.supermercerbros.gameengine.math.Curve;
import com.supermercerbros.gameengine.motion.CurveMovement;
import com.supermercerbros.gameengine.util.BetterDataInputStream;
import com.supermercerbros.gameengine.util.Utils;

/**
 * Contains parsers for the Schooner 3D file formats (sch3Dmesh,
 * sch3Dmovements, sch3Darmature).
 */
public class Sch3D {
	private static final String TAG = Sch3D.class.getSimpleName();
	
	public static PreObjectData parseMesh(InputStream is)
			throws IOException {
		BetterDataInputStream data = new BetterDataInputStream(is);
		
		final int version = data.readInt();
		if (version == 1) {
			final byte flags = data.readByte();
			final boolean tris = Utils.checkBit(flags, 0);
			final boolean textured = Utils.checkBit(flags, 1);
			final boolean armatureIndexed = Utils.checkBit(flags, 2);
			
			final int faceCount = data.readShort() & 0x0000FFFF;
			final short vertCount = data.readShort();
			
			final short[] indices;
			if (tris) {
				indices = new short[faceCount * 3];
				data.readShortArray(indices, 0, faceCount * 3);
			} else {
				indices = new short[faceCount * 6];
				final short[] temp = new short[faceCount * 4];
				data.readShortArray(temp, 0, faceCount * 4);
				
				// Triangulate quads (split each face into two tris)
				for (int i = 0; i < faceCount; i++) {
					// For each quad, get its four indices
					final short a = temp[i * 4];
					final short b = temp[i * 4 + 1];
					final short c = temp[i * 4 + 2];
					final short d = temp[i * 4 + 3];
					
					// First half of quad
					indices[i * 6] = a;
					indices[i * 6 + 1] = b;
					indices[i * 6 + 2] = c;
					
					// Second half of quad
					indices[i * 6 + 3] = a;
					indices[i * 6 + 4] = c;
					indices[i * 6 + 5] = d;
				}
			}
			
			final float[] verts = new float[vertCount * 3];
			data.readFloatArray(verts, 0, vertCount * 3);
			
			final short[][] doubles;
			final float[] uvs;
			if (textured) {
				final short sharpCount = data.readShort();
				if (sharpCount > 0) {
					final short[] sharpVerts = new short[sharpCount];
					data.readShortArray(sharpVerts, 0, sharpCount);
					
					final LinkedList<Short> doublesList = new LinkedList<Short>();
					
					int sharpArrayIndex1 = 0;
					for (short v1 = 0; v1 < vertCount - 1; v1++) {
						if (sharpArrayIndex1 < sharpCount && sharpVerts[sharpArrayIndex1] == v1) {
							sharpArrayIndex1++;
							continue;
						}
						
						int sharpArrayIndex2 = sharpArrayIndex1;
						for (short v2 = (short) (v1 + 1); v2 < vertCount; v2++) {
							if (sharpArrayIndex2 < sharpCount && sharpVerts[sharpArrayIndex2] == v2) {
								sharpArrayIndex2++;
								continue;
							}
							
							final boolean coincident = 
									verts[v1 * 3 + 0] == verts[v2 * 3 + 0] &&
									verts[v1 * 3 + 1] == verts[v2 * 3 + 1] &&
									verts[v1 * 3 + 2] == verts[v2 * 3 + 2];
							if (coincident) {
								doublesList.add(v1);
								doublesList.add(v2);
							}
						}
					}
					
					int pairCount = doublesList.size() / 2;
					doubles = new short[2][pairCount];
					for (int i = 0; i < pairCount; i++) {
						doubles[0][i] = doublesList.get(i * 2);
						doubles[1][i] = doublesList.get(i * 2 + 1);
					}
				} else {
					final LinkedList<Short> doublesList = new LinkedList<Short>();
					for (short v1 = 0; v1 < vertCount - 1; v1++) {
						for (short v2 = (short) (v1 + 1); v2 < vertCount; v2++) {
							final boolean coincident = 
									verts[v1 * 3 + 0] == verts[v2 * 3 + 0] &&
									verts[v1 * 3 + 1] == verts[v2 * 3 + 1] &&
									verts[v1 * 3 + 2] == verts[v2 * 3 + 2];
							if (coincident) {
								doublesList.add(v1);
								doublesList.add(v2);
							}
						}
					}
					
					int pairCount = doublesList.size() / 2;
					doubles = new short[2][pairCount];
					for (int i = 0; i < pairCount; i++) {
						doubles[0][i] = doublesList.get(i * 2);
						doubles[1][i] = doublesList.get(i * 2 + 1);
					}
				}
				uvs = new float[vertCount * 2];
				data.readFloatArray(uvs, 0, vertCount * 2);
			} else {
				doubles = null;
				uvs = null;
			}
			
			final byte[][] boneIndices;
			final float[][] boneWeights;
			if (armatureIndexed) {
				boneIndices = new byte[vertCount][];
				boneWeights = new float[vertCount][];
				for (int i = 0; i < vertCount; i++) {
					final byte boneCount = data.readByte();
					boneIndices[i] = new byte[boneCount];
					boneWeights[i] = new float[boneCount];
					data.readByteArray(boneIndices[i], 0, boneCount);
					data.readFloatArray(boneWeights[i], 0, boneCount);
				}
			} else {
				boneIndices = null;
				boneWeights = null;
			}
			
			data.close();
			return new PreObjectData(verts, indices, uvs, doubles,
					boneIndices, boneWeights);
		} else {
			data.close();
			throw new IOException("File version is incorrect.");
		}
	}
	
	public static HashMap<String, CurveMovement> parseMovements(
			InputStream is) throws IOException {
		final BetterDataInputStream data = new BetterDataInputStream(is);
		final HashMap<String, CurveMovement> map = new HashMap<String, CurveMovement>();
		
		final int version = data.readInt();
		if (version == 1) {
			while (data.hasNext()) {
				String name = data.readString();
				CurveMovement movement = readMovement(data);
				map.put(name, movement);
			}
			data.close();
			return map;
		} else {
			data.close();
			throw new IOException("File version is incorrect.");
		}
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private static CurveMovement readMovementOld(final BetterDataInputStream data) throws IOException {
		// TODO: Remove this method?
		CurveMovement movement;
		byte flagsByte = data.readByte();
		boolean[] flags = Utils.checkBits(flagsByte, 4);
		Log.d(TAG, "FLAGS: " + Arrays.toString(flags));
		int curveCount = 0;
		if (flags[0]) {
			curveCount += 3;
		}
		if (flags[1]) {
			curveCount += 4;
		}
		if (flags[2]) {
			curveCount += 1;
		} else if (flags[3]) {
			curveCount += 3;
		}
		Log.d(TAG, "CURVE COUNT: " + curveCount);

		Curve[] curves = new Curve[curveCount];
		for (int curveIndex = 0; curveIndex < curveCount; curveIndex++) {
			final int pointCount = ((data.readByte() & 0x00FF) - 1) * 3 + 1;
			Log.d(TAG, "Curve " + curveIndex + " has "+ pointCount + " points.");
			if (pointCount == 1) {
				data.readFloatDebug(); // Frame is not necessary
				curves[curveIndex] = new ConstantCurve(data.readFloatDebug());
			}

			float[] frames = new float[pointCount];
			float[] values = new float[pointCount];

			for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
				frames[pointIndex] = data.readFloatDebug();
				values[pointIndex] = data.readFloatDebug();
				Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
			}

			curves[curveIndex] = new BezierCurve(frames, values);
		}
		movement = new CurveMovement(flagsByte, curves);
		return movement;
	}

	public static Skeleton parseSkeleton(GameFactory gf, InputStream is, String id) throws IOException {
		final BetterDataInputStream data = new BetterDataInputStream(is);
		final int version = data.readInt();
		if (version == 1) {
			
			// Parse Skeleton
			final short boneCount = (short) (((short) data.readByteDebug()) + 1);
			ArrayList<PreBoneData> preBones = new ArrayList<PreBoneData>();
			for (byte i = 0; i < boneCount; i++) {
				final float x = data.readFloatDebug(),
						y = data.readFloatDebug(),
						z = data.readFloatDebug();
				preBones.add(new PreBoneData(i, x, y, z, 
						(byte) (data.readByteDebug() - 1)));
				Log.d(TAG, "Bone " + i + " coords: " + x + ", " + y + ", " + z);
			}
			
			// connect parents to children
			for (PreBoneData preBone : preBones) {
				final byte parentIndex = preBone.parentIndex;
				if (parentIndex != -1) {
					preBones.get(parentIndex).addChild(preBone);
				}
			}
			
			// convert PreBoneDatas to Bones
			final LinkedList<Bone> roots = new LinkedList<Bone>();
			for (PreBoneData preBone : preBones) {
				if (preBone.isRoot()) {
					roots.add(preBone.toBone());
				}
			}
			// Construct skeleton
			final Skeleton skeleton = new Skeleton(id, roots);
			
			// Parse Actions
			final HashMap<String, Action> actions = new HashMap<String, Action>();
			while (data.hasNext()) {
				final String name = data.readString();
				Log.d(TAG, "NAME: " + name);
				
				CurveMovement movement = readMovement(data);
				
				// Parse Action
				SparseArray<Curve> curves = new SparseArray<Curve>();
				for (byte i = 0; i < boneCount; i++) {
					// For each bone
					final int offset = i * 4;
					
					for(int j = 0; j < 4; j++) {
						// For each curve
						final int pointCount = ((data.readByteDebug() & 0x00FF) - 1) * 3 + 1;
						
						if (pointCount > 0) { // If the curve has keyframes
//							Log.d(TAG, name + "[" + i + "]." + j + " has " + pointCount + " points");
							final float[] frames = new float[pointCount], values = new float[pointCount];
							for (int index = 0; index < pointCount; index++) {
								frames[index] = data.readFloatDebug();
								values[index] = data.readFloatDebug();
							}
							curves.append(offset + j, new BezierCurve(frames, values));
						} else {
//							Log.d(TAG, name + "[" + i + "]." + j + " has no points");
						}
					}
				}
				actions.put(name, new Action(movement, curves));
			}
			data.close();
			gf.setActions(actions);
			return skeleton;
		} else {
			data.close();
			throw new IOException("File version is incorrect.");
		}
	}

	/**
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private static CurveMovement readMovement(final BetterDataInputStream data)
			throws IOException {
		CurveMovement movement;
		final byte flagsByte = data.readByteDebug();
		
		// Parse Movement
		if (flagsByte != 0) { // If Movement exists for this Action...
			boolean[] flags = Utils.checkBits(flagsByte, 4);
			Log.d(TAG, "FLAGS: " + Arrays.toString(flags));
			
			int curveCount = 0;
			final boolean moveLoc = flags[0];
			final boolean moveRot = flags[1];
			final boolean moveScale = flags[2];
			final boolean moveScaleAxis = flags[3];
			if (moveLoc) {
				curveCount += 3;
			}
			if (moveRot) {
				curveCount += 4;
			}
			if (moveScale) {
				curveCount += 1;
			} else if (moveScaleAxis) {
				curveCount += 3;
			}
			Log.d(TAG, "CURVE COUNT: " + curveCount);
			
			Curve[] curves = new Curve[curveCount];
			int curveIndex = 0;
			// Parse location curves
			if (moveLoc) {
				final int pointCount = ((data.readByteDebug() & 0xFFFF) - 1) * 3 + 1;
				if (pointCount > 0) {
					Log.d(TAG, "Location curves have "+ pointCount + " points.");
					for (int i = 0; i < 3; i++) { 
						float[] frames = new float[pointCount];
						float[] values = new float[pointCount];
						
						for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
							frames[pointIndex] = data.readFloatDebug();
							values[pointIndex] = data.readFloatDebug();
							Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
						}
						curves[curveIndex + i] = new BezierCurve(frames, values);
					}
				} else {
					curves[curveIndex] = null;
					curves[curveIndex + 1] = null;
					curves[curveIndex + 2] = null;
					Log.d(TAG, "Location curves have 0 points.");
				}
				curveIndex += 3;
			}
			// Parse rotation curves
			if (moveRot) {
				final int pointCount = ((data.readByteDebug() & 0xFFFF) - 1) * 3 + 1;
				if (pointCount > 0) {
					Log.d(TAG, "Rotation curves have "+ pointCount + " points.");
					for (int i = 0; i < 4; i++) { 
						float[] frames = new float[pointCount];
						float[] values = new float[pointCount];
						
						for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
							frames[pointIndex] = data.readFloatDebug();
							values[pointIndex] = data.readFloatDebug();
							Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
						}
						
						curves[curveIndex + i] = new BezierCurve(frames, values);
					}
				} else {
					curves[curveIndex] = null;
					curves[curveIndex + 1] = null;
					curves[curveIndex + 2] = null;
					curves[curveIndex + 3] = null;
					Log.d(TAG, "Rotation curves have 0 points.");
				}
				curveIndex += 4;
			}
			// Parse scale curves
			if (moveScale) {
				// Uniform scale (1 curve)
				final int pointCount = ((data.readByteDebug() & 0xFFFF) - 1) * 3 + 1;
				if (pointCount > 0) {
					Log.d(TAG, "Scale curve (uniform) has "+ pointCount + " points.");
					float[] frames = new float[pointCount];
					float[] values = new float[pointCount];

					for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
						frames[pointIndex] = data.readFloatDebug();
						values[pointIndex] = data.readFloatDebug();
						Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
					}

					curves[curveIndex] = new BezierCurve(frames, values);
				} else {
					curves[curveIndex] = null;
					Log.d(TAG, "Scale curve (uniform) has 0 points.");
				}
				//curveIndex += 1; Don't need to increment after last curve
			} else if (moveScaleAxis) {
				// Per-axis scale (3 curves)
				final int pointCount = ((data.readByteDebug() & 0xFFFF) - 1) * 3 + 1;
				if (pointCount > 0) {
					Log.d(TAG, "Scale curves (per-axis) have "+ pointCount + " points.");
					for (int i = 0; i < 3; i++) {
						float[] frames = new float[pointCount];
						float[] values = new float[pointCount];
						
						for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
							frames[pointIndex] = data.readFloatDebug();
							values[pointIndex] = data.readFloatDebug();
							Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
						}
						
						curves[curveIndex + i] = new BezierCurve(frames, values);
					}
				} else {
					curves[curveIndex] = null;
					curves[curveIndex + 1] = null;
					curves[curveIndex + 2] = null;
					Log.d(TAG, "Scale curves (per-axis) have 0 points.");
				}
				//curveIndex += 3; Don't need to increment after last curves
			}
			
			movement = new CurveMovement(flagsByte, curves);
		} else {
			Log.d(TAG, "No movement curves.");
			movement = null;
		}
		return movement;
	}
}