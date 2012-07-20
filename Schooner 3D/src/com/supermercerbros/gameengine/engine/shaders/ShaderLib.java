package com.supermercerbros.gameengine.engine.shaders;

import java.util.LinkedList;

import android.util.Log;

public class ShaderLib {
	private static final String TAG = ShaderLib.class.getSimpleName();
	
	public static final String A_POS = "a_pos";
	public static final String A_NORMAL = "a_normal";
	public static final String A_MTL = "a_mtl";
	
	public static final String U_MODEL = "u_model";
	public static final String U_MATRICES = "u_matrices";
	
	public static final String U_LIGHTVEC = "u_lightVec";
	public static final String U_LIGHTCOLOR = "u_lightColor";
	public static final String U_VIEWPROJ = "u_viewProj";
	
	public static final String S_BASEMAP = "s_baseMap";
	
	private static LinkedList<Program> programs;
	private static LinkedList<Shader> shaders;
	private static boolean initialized = false;
	
	/**
	 * Initializes the ShaderLib.
	 */
	public static void init() {
		if (initialized) {
			Log.e(TAG, "ShaderLib.init() was called when already initialized.");
			return;
		}
		initialized = true;
		programs = new LinkedList<Program>();
		shaders = new LinkedList<Shader>();
	}
	
	public static synchronized void close() {
		if (initialized) {
			initialized = false;
			programs.clear();
			programs = null;
			shaders.clear();
			shaders = null;
			Log.d(TAG, "ShaderLib is now closed.");
		} else {
			Log.e(TAG, "ShaderLib.close() was called before init()");
		}
	}
	
	/**
	 * Returns an OpenGL program with the given shaders. If a matching program
	 * already exists, returns that one instead.
	 * 
	 * @param vertShader
	 * @param fragShader
	 * @return
	 */
	public static synchronized Program newProgram(String vertShader,
			String fragShader) {
		Shader vert = null, frag = null;
		for (Shader s : shaders) {
			if (s.source.equals(vertShader)) {
				vert = s;
			} else if (s.source.equals(fragShader)) {
				frag = s;
			}
			
			if (vert != null && frag != null) {
				break;
			}
		}
		
		if (vert != null && frag != null) {
			for (Program p : programs) {
				if (p.fragment == frag && p.vertex == vert) {
					return p;
				}
			}
		}
		if (vert == null) {
			vert = new Shader(vertShader);
			shaders.add(vert);
		}
		if (frag == null) {
			frag = new Shader(fragShader);
			shaders.add(frag);
		}
		
		final Program program = new Program(vert, frag);
		programs.add(program);
		return program;
	}
	
}
