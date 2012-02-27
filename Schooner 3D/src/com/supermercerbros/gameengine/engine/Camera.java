package com.supermercerbros.gameengine.engine;

import com.supermercerbros.gameengine.util.IPO;

import android.opengl.Matrix;
import android.util.Log;

public class Camera {
	private static final String TAG = "com.supermercerbros.gameengine.engine.Camera";
	private float[] begin = new float[9];
	private float[] end = new float[9];

	private long startTime;
	private long duration;

	/**
	 * {eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ}
	 */
	private float[] current;
	private boolean moving;

	/**
	 * @param eyeX
	 *            The x-coord of the eye point
	 * @param eyeY
	 *            The y-coord of the eye point
	 * @param eyeZ
	 *            The z-coord of the eye point
	 * @param centerX
	 *            The x-coord of the look-at point
	 * @param centerY
	 *            The y-coord of the look-at point
	 * @param centerZ
	 *            The z-coord of the look-at point
	 * @param upX
	 *            The x-coord of the up vector
	 * @param upY
	 *            The y-coord of the up vector
	 * @param upZ
	 *            The z-coord of the up vector
	 */
	public Camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		Log.d(TAG, "Constructing Camera...");

		current = new float[] { eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ };
	}

	public Camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ) {
		Log.d(TAG, "Constructing Camera...");

		current = new float[] { eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				0.0f, 0.0f, 1.0f };
	}

	public Camera() {
		current = new float[9];
		current[7] = 1.0f;
		current[5] = -1.0f;
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ, long duration) {

		System.arraycopy(current, 0, begin, 0, 9);

		end[0] = upX;
		end[1] = upY;
		end[2] = upZ;
		end[3] = centerX;
		end[4] = centerY;
		end[5] = centerZ;
		end[6] = eyeX;
		end[7] = eyeY;
		end[8] = eyeZ;

		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, long duration) {
		
		moveTo(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, current[6],
				current[7], current[8], duration); 
		// TODO inline this once sure it works
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			long duration) {

		moveTo(eyeX, eyeY, eyeZ, current[3], current[4], current[5], current[6],
				current[7], current[8], duration);
		// TODO inline this once sure it works
	}

	synchronized void update(long time) {
		if (!moving)
			return;
		float framePoint = (time - startTime) / duration;
		IPO.mesh(current, begin, end, framePoint);
	}

	void copyToArray(float[] a, int offset) {
		assert a != null : "Cannot copy Camera to null array.";

		Matrix.setLookAtM(a, 0, current[0], current[1], current[2],
				current[3], current[4], current[5], current[6], current[7],
				current[8]);
	}

}
