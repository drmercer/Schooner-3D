package com.supermercerbros.gameengine.parsers;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.motion.CurveMovement;
import com.supermercerbros.gameengine.objects.BonedObject;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Material;
import com.supermercerbros.gameengine.util.BetterDataInputStream;
import com.supermercerbros.gameengine.util.Utils;

/**
 * Creates GameObjects, Movements, Armatures, etc.
 */
public class GameFactory {
	/**
	 * Contains GameObject data before it is made into a GameObject.
	 */
	public static class PreObjectData {
		public final float[] verts;
		public final short[] indices;
		public final float[] uvs;
		public final short[][] doubles;
		public final byte[][] boneIndices;
		public final float[][] boneWeights;
		
		PreObjectData(float[] verts, short[] indices, float[] uvs,
				short[][] doubles, byte[][] boneIndices, float[][] boneWeights) {
			this.verts = verts;
			this.doubles = doubles;
			this.indices = indices;
			this.uvs = uvs;
			this.boneIndices = boneIndices;
			this.boneWeights = boneWeights;
		}
		
		public boolean isArmatureIndexed() {
			return boneIndices != null;
		}
		
		public boolean isTextured() {
			return doubles != null;
		}
		
	}
	
	/**
	 * Contains parsers for the Schooner 3D file formats (sch3Dmesh,
	 * sch3Dmovements, sch3Darmature).
	 */
	public static class Sch3D {
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
					readMovement(data, map);
				}
				return map;
			} else {
				throw new IOException("File version is incorrect.");
			}
		}
		
		/**
		 * @param data
		 * @param map
		 * @throws IOException
		 */
		private static void readMovement(final BetterDataInputStream data,
				final Map<String, CurveMovement> map) throws IOException {
			try {
				String name = data.readString();
				Log.d(TAG, "NAME: " + name);
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
				CurveMovement m = new CurveMovement(flagsByte, curves);
				map.put(name, m);
			} catch (EOFException e) {
				Log.e(TAG, "EOF reached while trying to read movement.", e);
			}
		}
	}
	
	private final Resources res;
	private final AssetManager am;
	
	/**
	 * Creates a new GameFactory for the given Context.
	 * 
	 * @param context
	 */
	public GameFactory(Context context) {
		this.res = context.getResources();
		this.am = context.getAssets();
	}
	
	/**
	 * Reads GameObject data from the given file.
	 * This is for advanced purposes, where one needs to modify the data before
	 * creating a GameObject from it.
	 * 
	 * @param fileName
	 *            The asset path of the file to read.
	 * @return A PreObjectData object containing the data obtained from the
	 *         file.
	 * @throws IOException
	 *             If an error occurs opening or reading the file, i.e. if it
	 *             does not exist or is corrupt.
	 */
	public PreObjectData getObjectData(String fileName) throws IOException {
		return Sch3D.parseMesh(am.open(fileName));
	}
	
	/**
	 * Reads GameObject data from the given file.
	 * This is for advanced purposes, where one needs to modify the data before
	 * creating a GameObject from it.
	 * 
	 * @param resId
	 *            The ID of the resource to parse.
	 * @return A PreObjectData object containing the data obtained from the
	 *         resource.
	 * @throws IOException
	 *             If an error occurs opening or reading the file, i.e. if it
	 *             does not exist or is corrupt.
	 */
	public PreObjectData getObjectData(int resId) throws IOException {
		return Sch3D.parseMesh(res.openRawResource(resId));
	}
	
	public GameObject getGameObject(String fileName, Material mtl)
			throws IOException {
		PreObjectData data = Sch3D.parseMesh(am.open(fileName));
		return new GameObject(data.verts, data.indices, null, data.uvs,
				data.doubles, mtl);
	}
	
	public GameObject getGameObject(int resId, Material mtl) throws IOException {
		PreObjectData data = Sch3D.parseMesh(res.openRawResource(resId));
		return new GameObject(data.verts, data.indices, null, data.uvs,
				data.doubles, mtl);
	}
	
	public BonedObject getBonedObject(String objectFile, String armatureFile,
			Material mtl) throws IOException {
		PreObjectData data = Sch3D.parseMesh(am.open(objectFile));
		if (!data.isArmatureIndexed()) {
			throw new UnsupportedOperationException("\"" + objectFile
					+ "\" does not contain armature indices.");
		}
		// TODO: return BonedObject
		return null;
	}
	
	/**
	 * @param fileName
	 *            The asset path of the file to read.
	 * @return A HashMap of movements and their names
	 * @throws IOException
	 *             If an error occurs opening or reading the file, i.e. if it
	 *             does not exist or is corrupt.
	 */
	public HashMap<String, CurveMovement> getMovements(String fileName)
			throws IOException {
		return Sch3D.parseMovements(am.open(fileName));
	}
	
	/**
	 * @param resId
	 *            The ID of the resource to parse.
	 * @return A HashMap of movements and their names
	 * @throws IOException
	 *             If an error occurs opening or reading the file, i.e. if it
	 *             does not exist or is corrupt.
	 */
	public HashMap<String, CurveMovement> getMovements(int resId)
			throws IOException {
		return Sch3D.parseMovements(res.openRawResource(resId));
	}
	
	/**
	 * Closes the GameFactory
	 */
	public void close() {
		am.close();
	}
}
