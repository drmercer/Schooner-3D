package com.supermercerbros.gameengine.engine.shaders;

import com.supermercerbros.gameengine.engine.EGLContextLostHandler;
import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.engine.EGLContextLostHandler.EGLContextLostListener;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.util.Log;

/**
 * Represents a GLSL program, consisting of a vertex shader and a fragment
 * shader. Obtained from {@link ShaderLib}.
 */
public class Program implements EGLContextLostListener {
	private static final String TAG = "com.supermercerbros.gameengine.engine.Program";
	public final Shader vertex;
	public final Shader fragment;

	private int handle;

	private boolean loaded;

	Program(Shader vertexShader, Shader fragmentShader) {
		vertex = vertexShader;
		fragment = fragmentShader;
		EGLContextLostHandler.addListener(this);
	}

	public int load() throws GLException {
		if (loaded) {
			return handle;
		}

		int vHandle = vertex.load(GLES20.GL_VERTEX_SHADER);
		int fHandle = fragment.load(GLES20.GL_FRAGMENT_SHADER);

		handle = GLES20.glCreateProgram();
		if (handle == 0) {
			int error = GameRenderer.logError("glCreateProgram");
			throw new GLException(error, "Error Creating program");
		}

		// Attach shaders
		GLES20.glAttachShader(handle, vHandle);
		GameRenderer.logError("glAttachShader(handle, vertex.handle)");
		GLES20.glAttachShader(handle, fHandle);
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
		return handle;
	}

	public int getAttribLocation(String name) {
		if (!loaded) {
			throw new IllegalStateException("Program is not loaded");
		}
		return GLES20.glGetAttribLocation(handle, name);
	}

	public int getUniformLocation(String name) {
		if (!loaded) {
			throw new IllegalStateException("Program is not loaded");
		}
		return GLES20.glGetUniformLocation(handle, name);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public int getHandle() {
		return handle;
	}

	@Override
	public void onContextLost() {
		Log.d("Program", "received context lost notification");
		if (!GLES20.glIsProgram(handle)){
			Log.d("Program", "handle is not a program.");
			loaded = false;
			handle = -1;
		}
	}
}
