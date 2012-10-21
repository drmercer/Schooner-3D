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
			return map;
		} else {
			throw new IOException("File version is incorrect.");
		}
	}
	
	/**
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private static CurveMovement readMovement(final BetterDataInputStream data) throws IOException {
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
				data.readFloat(); // Frame is not necessary
				curves[curveIndex] = new ConstantCurve(data.readFloat());
			}

			float[] frames = new float[pointCount];
			float[] values = new float[pointCount];

			for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
				frames[pointIndex] = data.readFloat();
				values[pointIndex] = data.readFloat();
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
			final short boneCount = (short) (data.readByte() + 1);
			ArrayList<PreBoneData> preBones = new ArrayList<PreBoneData>();
			for (byte i = 0; i < boneCount; i++) {
				preBones.add(new PreBoneData(i, 
						data.readFloat(), 
						data.readFloat(), 
						data.readFloat(), 
						(byte) (data.readByte() - 1)));
			}
			
			final LinkedList<Bone> roots = new LinkedList<Bone>();
			for (PreBoneData preBone : preBones) {
				if (preBone.isRoot()) {
					roots.add(preBone.toBone());
				}
			}
			final Skeleton skeleton = new Skeleton(id, roots);
			
			// Parse Actions
			final HashMap<String, Action> actions = new HashMap<String, Action>();
			
			while (data.hasNext()) {
				final String name = data.readString();
				
				CurveMovement movement;
				final byte flagsByte = data.readByte();
				
				// Parse Movement
				if (flagsByte != 0) { // If Movement exists for this Action...
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
						float[] frames = new float[pointCount];
						float[] values = new float[pointCount];
						
						for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
							frames[pointIndex] = data.readFloat();
							values[pointIndex] = data.readFloat();
							Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
						}
						
						curves[curveIndex] = new BezierCurve(frames, values);
					}
					movement = new CurveMovement(flagsByte, curves);
				} else {
					movement = null;
				}
				
				// Parse Action
				SparseArray<BezierCurve> curves = new SparseArray<BezierCurve>();
				for (byte i = 0; i < boneCount; i++) {
					// For each bone
					final int pointCount = ((data.readByte() & 0x00FF) - 1) * 3 + 1;
					Log.d(TAG, "Bone " + i + " has " + pointCount + " points.");
					if (pointCount < 1) {
						continue; // Bone has no points
					} else {
						final int offset = i * 4;
						final float[] wFrames = new float[pointCount], wValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							wFrames[index] = data.readFloat();
							wValues[index] = data.readFloat();
						}
						curves.append(offset, new BezierCurve(wFrames, wValues));
						
						final float[] xFrames = new float[pointCount], xValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							xFrames[index] = data.readFloat();
							xValues[index] = data.readFloat();
						}
						curves.append(offset, new BezierCurve(xFrames, xValues));
						
						final float[] yFrames = new float[pointCount], yValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							yFrames[index] = data.readFloat();
							yValues[index] = data.readFloat();
						}
						curves.append(offset, new BezierCurve(yFrames, yValues));
						
						final float[] zFrames = new float[pointCount], zValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							zFrames[index] = data.readFloat();
							zValues[index] = data.readFloat();
						}
						curves.append(offset, new BezierCurve(zFrames, zValues));
					}
				}
				actions.put(name, new Action(movement, curves));
			}
			gf.setActions(actions);
			return skeleton;
		} else {
			throw new IOException("File version is incorrect.");
		}
	}
}