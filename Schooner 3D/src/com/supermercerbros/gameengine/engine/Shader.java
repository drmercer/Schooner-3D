package com.supermercerbros.gameengine.engine;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.util.Log;

public class Shader {
	private static final String TAG = "com.supermercerbros.gameengine.engine.Shader";
	private String source;
	private int handle;
	private boolean loaded;
	
	
	Shader(String source){
		this.source = source;
	}
	
	int load(int type) throws GLException {
		if (loaded) {
			return handle;
		}
		handle = GLES20.glCreateShader(type);
		if (handle == 0) {
			int error = GameRenderer.logError("glCreateShader(" + type + ")");
			throw new GLException(error, "CreateShader failed");
		}
		GLES20.glShaderSource(handle, source);
		GameRenderer.logError("glShaderSource(handle, source)");
		GLES20.glCompileShader(handle);
		
		// Check compile status
		int[] compiled = { 0 };
		GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] != GLES20.GL_TRUE) {
			Log.e(TAG, "Error compiling shader. \n" + GLES20.glGetShaderInfoLog(handle));
			
			// Undo the shader
			GLES20.glDeleteShader(handle);
			return 0;
		}
		
		Log.d(TAG, "shader successfully compiled!");
		
		return handle;
	}
	
	void reset(){
		loaded = false;
		handle = -1;
	}

}
