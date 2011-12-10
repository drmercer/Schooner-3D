package com.supermercerbros.gameengine.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TextureLib {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.engine.TextureLib";

	private static boolean initialized = false;

	private static Resources res;
	private static AssetManager am;
	private static BitmapFactory.Options opts;

	private static HashMap<String, Texture> textures;

	static Texture[] boundTextures;

	/**
	 * Initializes the Texture Library
	 * 
	 * @param context
	 *            The Activity context.
	 */
	static void init(Context context) {
		initialized = true;
		res = context.getResources();
		am = context.getAssets();
		textures = new HashMap<String, Texture>();
		opts = new BitmapFactory.Options();
		opts.inScaled = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
	}

	/**
	 * @param id
	 *            The Resource id of the texture to load.
	 * @throws IOException
	 *             If the texture could not be loaded.
	 */
	public static synchronized void loadTexture(int id) throws IOException {
		if (!initialized)
			throw new IllegalStateException(
					"TextureLib has not been initialized");

		Bitmap bmp = BitmapFactory.decodeResource(res, id, opts);
		if (bmp == null) {
			throw new IOException("Could not load resource");
		}

		String name = res.getResourceEntryName(id);
//		textures.put(name, new Texture(bmp)); TODO subclass Texture
	}

	/**
	 * Stores the given texture with the file's name. For example, if
	 * <code>fileName</code> is <code>"textures/image.png"</code>, then it will
	 * be stored under the name <code>"image.png"</code>.
	 * 
	 * @param fileName
	 *            The asset to load
	 * @return The name with which one can reference the Texture.
	 * @throws IOException
	 */
	public static synchronized String loadTexture(String fileName)
			throws IOException {
		if (!initialized)
			throw new IllegalStateException(
					"TextureLib has not been initialized");

		InputStream is = am.open(fileName);
		Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);

		String[] parts = fileName.split("/");
		String name = parts[parts.length - 1];
//		textures.put(name, new Texture(bmp)); TODO subclass Texture

		return name;
	}

	public static synchronized Texture getTexture(String name) {
		return textures.get(name);
	}

}
