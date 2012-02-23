package com.supermercerbros.gameengine.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.supermercerbros.gameengine.Schooner3D;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.util.Utils;

public class GameRenderer implements Renderer {
	private static final String TAG = GameRenderer.class.getName();
	private static final boolean alwaysDebug = true; 
	private static final int framesToDebug = 2;

	/**
	 * @param location
	 *            A string that names the just-called method.
	 * @return The GL_ code of the error.
	 */
	public static int logError(String location) {
		final String TAG = "OpenGL";
		int error = GLES20.glGetError();
		switch (error) {
		default:
		case GLES20.GL_NO_ERROR:
			break;
		case GLES20.GL_INVALID_ENUM:
			Log.e(TAG, location + ": GL_INVALID_ENUM");
			break;
		case GLES20.GL_INVALID_VALUE:
			Log.e(TAG, location + ": GL_INVALID_VALUE");
			break;
		case GLES20.GL_INVALID_OPERATION:
			Log.e(TAG, location + ": GL_INVALID_OPERATION");
			break;
		case GLES20.GL_OUT_OF_MEMORY:
			Log.e(TAG, location + ": GL_OUT_OF_MEMORY");
			break;
		case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
			Log.e(TAG, location + ": GL_INVALID_FRAMEBUFFER_OPERATION");
		}
		return error;
	}

	private DataPipe pipe;
	private RenderData in;
	private IntBuffer vbo; // Vertex Buffer Object to hold dynamic data
	private ShortBuffer ibo; // Index Buffer Object
	/**
	 * [0] = vbo handle, [1] = ibo handle
	 */
	private int[] buffers = new int[2];

	private Program activeProgram;
	private float[] wvpMatrix = new float[16];
	private float[] projMatrix = new float[16];

	// Shader variable handles
	private int u_viewProj = -1;
	private int u_lightVec = -1;
	private int u_lightColor = -1;

	private int drawFrameCount = 0;

	public GameRenderer(DataPipe pipe) {
		Log.d(TAG, "Constructing GameRenderer...");
		this.pipe = pipe;

		Matrix.setIdentityM(projMatrix, 0);
		Matrix.setIdentityM(wvpMatrix, 0);
		
		if (vbo == null) {
			vbo = ByteBuffer.allocateDirect(pipe.VBO_capacity).order(
					ByteOrder.nativeOrder()).asIntBuffer();
		}
		if (ibo == null) {
			ibo = ByteBuffer.allocateDirect(pipe.IBO_capacity).order(
					ByteOrder.nativeOrder()).asShortBuffer();
		}
		Log.d(TAG, "GameRenderer constructed!");
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		drawFrameCount++;
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		in = pipe.retrieveData();
		if (in == null) {
			Log.w(TAG, "in == null");
			drawFrameCount--;
			return;
		}

		// long startFrame = System.nanoTime();

		// Load VBO data
		vbo.clear();
		vbo.put(in.vbo);
		vbo.position(0);

		// Load index data to IBO
		ibo.clear();
		ibo.put(in.ibo);
		ibo.position(0);

		GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0,
				in.ibo.length * 2, ibo);
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, in.vbo.length * 4,
				vbo);

		// Render each primitive
		int matrixNumber = 0, iboOffset = 0, vboOffset = 0;
		for (int i = 0; i < in.primitives.length; i++) {
			Metadata primitive = in.primitives[i];

			useProgram(primitive.mtl.getProgramName());
			loadUniforms(in.viewMatrix, in.light, in.color);

			vboOffset += primitive.mtl.attachAttribs(primitive, vboOffset,
					in.modelMatrices, matrixNumber);
			logError("attachAttribs");

			// Render primitive!
			GLES20.glDrawElements(primitive.mtl.getGeometryType(),
					primitive.size, GLES20.GL_UNSIGNED_SHORT, iboOffset);
			logError("DrawElements");

			iboOffset += primitive.size * 2;
			matrixNumber++;
		}

	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float aspect = width / (float) height;
		Utils.perspectiveM(projMatrix, 0, 45, aspect, 0.5f, 5.f);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		EGLContextLostHandler.contextLost();
		GLES20.glGenBuffers(2, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);

		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, pipe.VBO_capacity, vbo,
				GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, pipe.IBO_capacity,
				ibo, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glClearColor(Schooner3D.backgroundColor[0],
				Schooner3D.backgroundColor[1], Schooner3D.backgroundColor[2],
				Schooner3D.backgroundColor[3]);
		// GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

	}

	/**
	 * @param name
	 * @return True if a new program has been loaded
	 */
	private boolean useProgram(String name) {
		if (drawFrameCount <= framesToDebug || alwaysDebug) {
			Log.d(TAG, "useProgram(" + name + ")");
		}
		Program program = ShaderLib.getProgram(name);

		boolean success = true;
		if (!(program.equals(activeProgram) && program.getHandle() > 0)) {
			try {
				program.load();
			} catch (GLException e) {
				Log.e(TAG, "Program Could not be loaded.", e);
				if (activeProgram != null)
					activeProgram.load();
				success = false;
			}
			if (success) {
				GLES20.glUseProgram(program.getHandle());

				u_viewProj = program.getUniformLocation(ShaderLib.U_VIEWPROJ);
				u_lightVec = program.getUniformLocation(ShaderLib.U_LIGHTVEC);
				u_lightColor = program
						.getUniformLocation(ShaderLib.U_LIGHTCOLOR);

				activeProgram = program;
				return true;
			}
		}
		return false;
	}

	private void loadUniforms(float[] viewMatrix, float[] light, float[] color) {
		// Load World View-Projection matrix
		Matrix.multiplyMM(wvpMatrix, 0, projMatrix, 0, viewMatrix, 0);

		GLES20.glUniformMatrix4fv(u_viewProj, 1, false, wvpMatrix, 0);
		logError("glUniformMatrix4fv (wvpMatrix)");

		// Load directional light
		if (u_lightVec != -1) {
			GLES20.glUniform3fv(u_lightVec, 1, light, 0);
			logError("glUniform3fv (light vector)");
			if (drawFrameCount <= framesToDebug) {
				Log.d(TAG, "Uniform3fv(u_lightVec, 1, "
						+ Arrays.toString(light) + ", 0)");
			}
		}
		if (u_lightColor != -1) {
			GLES20.glUniform3fv(u_lightColor, 1, color, 0);
			logError("glUniform3fv (light color)");
			if (drawFrameCount <= framesToDebug) {
				Log.d(TAG, "Uniform3fv(" + u_lightColor
						+ " (u_lightColor), 1, " + Arrays.toString(color)
						+ ", 0)");
			}
		}
	}
}
