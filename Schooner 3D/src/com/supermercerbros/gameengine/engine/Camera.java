package com.supermercerbros.gameengine.engine;

import com.supermercerbros.gameengine.util.IPO;

import android.opengl.Matrix;
import android.util.Log;

public class Camera {
	private static final String TAG = "com.supermercerbros.gameengine.engine.Camera";
	private float[] matrix = new float[16];
	private float[] begin = new float[16];
	private float[] end = new float[16];

	private long startTime;
	private long duration;

	private float upX;
	private float upY;
	private float upZ;
	private float centerX;
	private float centerY;
	private float centerZ;
	private boolean moving;

	/**
	 * @param eyeX The x-coord of the eye point
	 * @param eyeY The y-coord of the eye point
	 * @param eyeZ The z-coord of the eye point
	 * @param centerX The x-coord of the look-at point
	 * @param centerY The y-coord of the look-at point
	 * @param centerZ The z-coord of the look-at point
	 * @param upX The x-coord of the up vector
	 * @param upY The y-coord of the up vector
	 * @param upZ The z-coord of the up vector
	 */
	public Camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		Log.d(TAG, "Constructing Camera...");

		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;

		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;

		Matrix.setLookAtM(matrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
				centerZ, upX, upY, upZ);
	}

	public Camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ) {
		Log.d(TAG, "Constructing Camera...");
		
		this.upX = 0.0f;
		this.upY = 0.0f;
		this.upZ = 1.0f;

		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;

		Matrix.setLookAtM(matrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
				centerZ, upX, upY, upZ);
	}

	public Camera() {
		this.upX = 0.0f;
		this.upY = 1.0f;
		this.upZ = 0.0f;

		this.centerX = 0.0f;
		this.centerY = 0.0f;
		this.centerZ = -1.0f;
		
		Matrix.setIdentityM(matrix, 0);
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ, long duration) {

		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;

		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;

		Matrix.transposeM(begin, 0, matrix, 0);
		Matrix.setLookAtM(end, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ);
		Matrix.transposeM(end, 0, end, 0);
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, long duration) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;

		Matrix.transposeM(begin, 0, matrix, 0);
		Matrix.setLookAtM(end, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ);
		Matrix.transposeM(end, 0, end, 0);
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			long duration) {
		
		Matrix.transposeM(begin, 0, matrix, 0);
		Matrix.setLookAtM(end, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ);
		Matrix.transposeM(end, 0, end, 0);
		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}

	synchronized void update(long time) {
		if (!moving)
			return;
		float framePoint = (time - startTime) / duration;
		IPO.matrix(matrix, 0, begin, 0, end, 0, framePoint);
		if (matrix == end) {
			begin = null;
			end = null;
		}
		Matrix.transposeM(matrix, 0, matrix, 0);
		
	}
	
	void copyToArray(float[] a, int offset){
		if (a == null) {
			a = new float[16 + offset];
		}
		
		System.arraycopy(matrix, 0, a, offset, 16);
	}

}
