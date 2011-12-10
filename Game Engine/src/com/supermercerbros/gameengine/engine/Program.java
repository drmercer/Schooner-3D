package com.supermercerbros.gameengine.engine;

import java.util.HashMap;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.util.Log;

/**
 * Represents a GLSL program, consisting of a vertex shader and a fragment
 * shader. Obtained from {@link ShaderLib}.
 */
public class Program {
	private static final String TAG = "com.supermercerbros.gameengine.engine.Program";
	private Shader vertex;
	private Shader fragment;

	private int handle;

	private int a_pos;
	private int a_normal;
	private int a_mtl;
	private int a_model;

	private boolean loaded;
	private HashMap<String, Integer> attribs;

	Program() {

	}

	void setVertexShader(Shader shader) {
		vertex = shader;
	}

	void setFragmentShader(Shader shader) {
		fragment = shader;
	}

	public int load() throws GLException {
		if (loaded)
			return handle;

		vertex.load(GLES20.GL_VERTEX_SHADER);
		fragment.load(GLES20.GL_FRAGMENT_SHADER);

		handle = GLES20.glCreateProgram();
		if (handle == 0) {
			int error = GameRenderer.logError("glCreateProgram");
			throw new GLException(error, "Error Creating program");
		}

		// Attach shaders
		GLES20.glAttachShader(handle, vertex.handle);
		GameRenderer.logError("glAttachShader(handle, vertex.handle)");
		GLES20.glAttachShader(handle, fragment.handle);
		GameRenderer.logError("glAttachShader(handle, fragment.handle)");

		// Link program
		GLES20.glLinkProgram(handle);

		// Check link status
		int[] status = { 0 };
		GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, status, 0);
		if (status[0] != GLES20.GL_TRUE) { // If there is an error...
			String infoLog = GLES20.glGetProgramInfoLog(handle);
			GLES20.glDeleteProgram(handle);
			handle = 0;
			throw new GLException(0, "Error linking program. \n" + infoLog);
		}

		Log.d(TAG, "Program successfully created and linked!");
		loaded = true;

		a_pos = GLES20.glGetAttribLocation(handle, ShaderLib.A_POS);
		a_normal = GLES20.glGetAttribLocation(handle, ShaderLib.A_NORMAL);
		a_mtl = GLES20.glGetAttribLocation(handle, ShaderLib.A_MTL);
		a_model = GLES20.glGetAttribLocation(handle, ShaderLib.A_MODEL);

		return handle;
	}

	public int getAttribLocation(String name) {
		if (!loaded)
			throw new IllegalStateException("Program is not loaded");

		int location;
		if (name.equals(ShaderLib.A_POS)) {
			location = a_pos;

		} else if (name.equals(ShaderLib.A_NORMAL)) {
			location = a_normal;

		} else if (name.equals(ShaderLib.A_MTL)) {
			location = a_mtl;

		} else if (name.equals(ShaderLib.A_MODEL)) {
			location = a_model;

		} else if (attribs.containsKey(name)) {
			location = attribs.get(name);

		} else {
			location = GLES20.glGetAttribLocation(handle, name);
			attribs.put(name, location);
		}

		return location;
	}

	public int getUniformLocation(String name) {
		if (!loaded)
			throw new IllegalStateException("Program is not loaded");
		return GLES20.glGetUniformLocation(handle, name);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public int getHandle() {
		return handle;
	}
}
