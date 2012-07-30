package com.supermercerbros.gameengine.parsers;

import java.io.EOFException;
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
import com.supermercerbros.gameengine.math.Bezier;
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
				Log.d(TAG, "Object is textured");
				final short pairCount = data.readShort();
				if (pairCount > 0) {
					doubles = new short[2][pairCount];
					for (int i = 0; i < pairCount; i++) {
						doubles[0][i] = data.readShort();
						doubles[1][i] = data.readShort();
					}
				} else {
					doubles = null;
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
			Log.d(TAG, "object has " + (verts.length / 3) + " verts and " + (indices.length / 3) + " faces");
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
		try {
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
			final int pointCount = (data.readByte() & 0x00FF) * 3 + 1;
			Log.d(TAG, "POINT COUNT: " + pointCount);
			
			Bezier[] curves = new Bezier[curveCount];
			for (int curveIndex = 0; curveIndex < curveCount; curveIndex++) {
				Log.d(TAG, "Curve " + curveIndex);
				float[] frames = new float[pointCount];
				float[] values = new float[pointCount];
				
				for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
					frames[pointIndex] = data.readFloat();
					values[pointIndex] = data.readFloat();
					Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
				}
				
				curves[curveIndex] = new Bezier(frames, values);
			}
			movement = new CurveMovement(flagsByte, curves);
		} catch (EOFException e) {
			throw new IOException("EOF reached while reading Movement", e);
		}
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
				String name = data.readString();
				
				CurveMovement movement;
				byte flagsByte = data.readByte();
				final int pointCount = (data.readByte() & 0x00FF) * 3 + 1;
				Log.d(TAG, "POINT COUNT: " + pointCount);
				
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
					Bezier[] curves = new Bezier[curveCount];
					for (int curveIndex = 0; curveIndex < curveCount; curveIndex++) {
						Log.d(TAG, "Curve " + curveIndex);
						float[] frames = new float[pointCount];
						float[] values = new float[pointCount];
						
						for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
							frames[pointIndex] = data.readFloat();
							values[pointIndex] = data.readFloat();
							Log.d(TAG, "(" + frames[pointIndex] + ", " + values[pointIndex] + ")");
						}
						
						curves[curveIndex] = new Bezier(frames, values);
					}
					movement = new CurveMovement(flagsByte, curves);
				} else {
					movement = null;
				}
				
				SparseArray<Bezier> curves = new SparseArray<Bezier>();
				for (byte i = 0; i < boneCount; i++) {
					float firstFrame = data.readFloat();
					if (firstFrame == 0.0f) {
						continue;
					} else {
						final int offset = i * 4;
						final float[] wFrames = new float[pointCount], wValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							wFrames[index] = (index == 0) ? firstFrame : data.readFloat();
							wValues[index] = data.readFloat();
						}
						curves.append(offset, new Bezier(wFrames, wValues));
						
						final float[] xFrames = new float[pointCount], xValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							xFrames[index] = data.readFloat();
							xValues[index] = data.readFloat();
						}
						curves.append(offset, new Bezier(xFrames, xValues));
						
						final float[] yFrames = new float[pointCount], yValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							yFrames[index] = data.readFloat();
							yValues[index] = data.readFloat();
						}
						curves.append(offset, new Bezier(yFrames, yValues));
						
						final float[] zFrames = new float[pointCount], zValues = new float[pointCount];
						for (int index = 0; index < pointCount; index++) {
							zFrames[index] = data.readFloat();
							zValues[index] = data.readFloat();
						}
						curves.append(offset, new Bezier(zFrames, zValues));
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