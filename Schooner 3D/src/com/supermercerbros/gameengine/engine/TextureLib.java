package com.supermercerbros.gameengine.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.ETC1Util.ETC1Texture;

public class TextureLib {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.engine.TextureLib";

	private static boolean initialized = false;

	private static Resources res;
	private static AssetManager am;
	private static BitmapFactory.Options opts;

	private static HashMap<String, Texture> textures;
	
	private TextureLib(){} //This class should never be instantiated

	/**
	 * Initializes the Texture Library
	 * 
	 * @param context
	 *            The Activity context.
	 */
	static synchronized void init(Context context) {
		if (initialized){
			return;
		}
		initialized = true;
		res = context.getResources();
		am = context.getAssets();
		textures = new HashMap<String, Texture>();
		opts = new BitmapFactory.Options();
		opts.inScaled = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
	}
	
	static synchronized void close(){
		if (!initialized){
			return;
		}
		initialized = false;
		res = null;
		am = null;
		textures.clear();
		textures = null;
		opts = null;
	}

	/**
	 * @param id
	 *            The Resource id of the texture to load.
	 * @return The String name of the texture that was loaded.
	 * @throws IOException
	 *             If the texture could not be loaded.
	 */
	public static synchronized String loadTexture(int id) throws IOException {
		if (!initialized)
			throw new IllegalStateException(
					"TextureLib has not been initialized");

		Bitmap bmp = BitmapFactory.decodeResource(res, id, opts);
		if (bmp == null) {
			throw new IOException("Could not load resource");
		}

		String name = res.getResourceEntryName(id);
		textures.put(name, new BitmapTexture(bmp, false));
		return name;
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

		String[] pathParts = fileName.split("/");
		String name = pathParts[pathParts.length - 1];
		String[] nameParts = name.split("\\.");
		String ext = nameParts[nameParts.length - 1];

		InputStream is = am.open(fileName);
		if (ext.equals("pkm")) {
			ETC1Texture tex = ETC1Util.createTexture(is);
			textures.put(name, new ETC1CompressedTexture(tex));
		} else {
			Bitmap tex = BitmapFactory.decodeStream(is, null, opts);
			textures.put(name, new BitmapTexture(tex));
		}
		return name;
	}

	public static synchronized Texture getTexture(String name)
			throws IOException {
		Texture tex = textures.get(name);
		if (tex == null) {
			throw new IOException("Texture \"" + name + "\" could not be found");
		}
		return tex;
	}

}
