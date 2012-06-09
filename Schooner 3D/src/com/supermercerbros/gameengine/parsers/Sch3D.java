package com.supermercerbros.gameengine.parsers;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Material;
import com.supermercerbros.gameengine.util.BetterDataInputStream;
import com.supermercerbros.gameengine.util.Utils;

public class Sch3D {
	private static class V1 {
		final static int TEXTURED = 0;
		final static int ARMATURE_INDEXED = 1;
	}
	
	private static Resources res;
	private static AssetManager am;

	/**
	 * Sets the default Context to use when loading resources and assets.
	 * 
	 * @param context
	 *            The default Context to use.
	 */
	public static void setContext(Context context) {
		res = context.getResources();
		am = context.getAssets();
	}

	/**
	 * Parse a GameObject from the given Sch3D file.
	 * 
	 * @param id
	 *            The resource ID of the file to load from.
	 * @param res
	 *            The {@link Resources} to use, or null if the default (set by
	 *            {@link #setContext(Context)} should be used.
	 * @param mtl
	 *            The Material to give to this GameObject.
	 * @return A shiny new GameObject!
	 * @throws IOException
	 *             If the file is corrupt.
	 * @throws IllegalStateException
	 *             If a Context has not been set and no Resources is supplied.
	 */
	public static GameObject parse(int id, Resources res, Material mtl)
			throws IOException {
		Resources r = (res != null) ? res : Sch3D.res;
		if (r == null) {
			throw new IllegalStateException("Resources have not been set.");
		}
		InputStream is = r.openRawResource(id);
		return parseInputStream(is, mtl, "r@" + res.getResourceName(id));
	}

	/**
	 * Parse a GameObject from the given Sch3D file.
	 * 
	 * @param fileName
	 *            The name of the asset to open. This name can be hierarchical.
	 * @param am
	 *            The AssetManager to use, or null if the default (set by
	 *            {@link #setContext(Context)} should be used.
	 * @param mtl
	 *            The Material to give to this GameObject.
	 * @return A shiny new GameObject!
	 * @throws IOException
	 *             If an error occurs when opening the asset (see
	 *             {@link AssetManager#open(String)}) or the file is corrupt.
	 * @throws IllegalStateException
	 *             If a Context has not been set and no AssetManager is
	 *             supplied.
	 */
	public static GameObject parse(String fileName, AssetManager am,
			Material mtl) throws IOException {
		AssetManager a = (am != null) ? am : Sch3D.am;
		if (a == null) {
			throw new IllegalStateException("AssetManager has not been set.");
		}
		InputStream is = a.open(fileName);
		return parseInputStream(is, mtl, "a@" + fileName);
	}

	private static GameObject parseInputStream(InputStream is, Material mtl, String idStem)
			throws IOException {
		BetterDataInputStream data = new BetterDataInputStream(is);
		
		final byte flags = data.readByte();
		final boolean tris = Utils.checkByte(flags, 0);
		final boolean textured = Utils.checkByte(flags, 1);
		final boolean armatureIndexed = Utils.checkByte(flags, 2);
		
		final short faceCount = data.readShort();
		final short vertCount = data.readShort();

		final int indexCount = tris ? faceCount * 3 : faceCount * 4;
		final short[] indices = new short[indexCount];
		data.readShortArray(indices, 0, indexCount);

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

		if (armatureIndexed) {
			for(int i = 0; i < vertCount; i++){
				final byte boneCount = data.readByte();
				final byte[] boneIndices = new byte[boneCount];
				final float[] boneWeights = new float[boneCount];
				data.readByteArray(boneIndices, 0, boneCount);
				data.readFloatArray(boneWeights, 0, boneCount);
			}
			// TODO: do something with these.
		}

		GameObject object = new GameObject(verts, indices, uvs,
				new float[vertCount * 3], mtl, doubles);
		data.close();
		return object;
	}

	/**
	 * Closes the AssetManager if one has been set by
	 * {@link #setContext(Context)}.
	 */
	public static void close() {
		if (am != null) {
			am.close();
		}
	}
}
