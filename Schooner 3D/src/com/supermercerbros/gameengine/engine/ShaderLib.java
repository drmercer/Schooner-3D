package com.supermercerbros.gameengine.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.supermercerbros.gameengine.util.Utils;

public class ShaderLib {
	private static final String TAG = "com.supermercerbros.gameengine.engine.ShaderLib";

	public static final String A_POS = "a_pos";
	public static final String A_NORMAL = "a_normal";
	public static final String A_MTL = "a_mtl";
	public static final String A_MODEL = "a_model";

	public static final String U_LIGHTVEC = "u_lightVec";
	public static final String U_LIGHTCOLOR = "u_lightColor";
	public static final String U_VIEWPROJ = "u_viewProj";

	public static final String S_BASEMAP = "s_baseMap";

	private static HashMap<String, Program> programs;
	private static HashMap<String, Shader> shaders;
	private static AssetManager am;
	private static boolean initialized = false;

	/**
	 * Returns the program of the given name.
	 * 
	 * @param name
	 *            The name of the program to retrieve.
	 * @return The program with the given name.
	 */
	public static synchronized Program getProgram(String name) {
		if (!initialized) {
			throw new IllegalStateException("ShaderLib has not been initialized.");
		}
		if (programs == null) {
			Log.e("ShaderLib", "ShaderLib is initialized, but ShaderLib.programs == null.");
		}
		return programs.get(name);
	}

	/**
	 * Loads programs from an XML file in the app's assets.
	 * 
	 * @param filepath
	 *            The path to the xml file.
	 */
	public static void loadPrograms(String filepath) {
		if (!initialized) {
			throw new IllegalStateException("ShaderLib has not been initialized.");
		}
		XmlResourceParser xrp;
		try {
			xrp = am.openXmlResourceParser(filepath);
		} catch (IOException e) {
			Log.e(TAG, "Could not open assets/" + filepath);
			e.printStackTrace();
			return;
		}

		parseXml(xrp, true);
	}

	/**
	 * Initializes the ShaderLib.
	 * @param context
	 */
	static void init(Context context) {
		if (initialized){
			return;
		}
		initialized = true;
		am = context.getAssets();
		programs = new HashMap<String, Program>();
		shaders = new HashMap<String, Shader>();
		String[] files;

		try {
			files = am.list("shaders/");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (String filename : files) {
			if (filename.matches(".*\\.xml")) {
				loadPrograms("shaders/" + filename);
			}
		}

		try {
			XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = xppf.newPullParser();
			InputStream s = ShaderLib.class
					.getResourceAsStream("shaders/programs.xml");
			xpp.setInput(s, null);

			parseXml(xpp, false);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void addProgram(String name, Program program) {
		programs.put(name, program);
		Log.d(TAG, "Added program \"" + name + "\"");
	}

	private static Shader createShader(String name, String source) {
		Shader shader = new Shader(source);
		shaders.put(name, shader);
		return shader;
	}

	/**
	 * @param xrp
	 */
	private static void parseXml(XmlPullParser xrp, boolean fromAsset) {
		try {
			int eventType = xrp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG
						&& xrp.getName().equals("Program")) {

					String name = xrp.getAttributeValue("",
							"schooner:program-name");
					String vsFile = xrp.getAttributeValue("",
							"schooner:vertex-shader");
					String fsFile = xrp.getAttributeValue("",
							"schooner:fragment-shader");

					if (name == null || vsFile == null || fsFile == null)
						continue;
					if (programs.containsKey(name))
						continue;

					String vName = (fromAsset ? "c-" : "")
							+ vsFile.split("\\..+")[0];
					String fName = (fromAsset ? "c-" : "")
							+ fsFile.split("\\..+")[0];

					Shader v, f;
					if (!shaders.containsKey(vName)) {
						String vs;
						if (fromAsset) {
							vs = Utils.readAssetAsString(am, "shaders/"
									+ vsFile);
						} else {
							vs = Utils.readInputStreamAsString(ShaderLib.class
									.getResourceAsStream("shaders/" + vsFile));
						}

						v = createShader(vName, vs);
					} else {
						v = shaders.get(vName);
					}

					if (!shaders.containsKey(fName)) {
						String fs;
						if (fromAsset) {
							fs = Utils.readAssetAsString(am, "shaders/"
									+ fsFile);
						} else {
							fs = Utils.readInputStreamAsString(ShaderLib.class
									.getResourceAsStream("shaders/" + fsFile));
						}
						f = createShader(fName, fs);
					} else {
						f = shaders.get(fName);
					}

					addProgram(name, new Program(v, f));
				}
				eventType = xrp.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "Error parsing XML");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Log.e(TAG, "Error parsing XML");
			e.printStackTrace();
			return;
		}
	}

	static synchronized void close() {
		initialized = false;
		am = null;
		programs.clear();
		programs = null;
		shaders.clear();
		shaders = null;
		Log.d("ShaderLib", "ShaderLib is now closed.");
	}

}
