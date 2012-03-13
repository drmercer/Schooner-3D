package com.supermercerbros.gameengine.engine;

import android.opengl.Matrix;

import com.supermercerbros.gameengine.util.IPO;

public class Camera {
	private static final String TAG = "Camera";
	private float[] begin = new float[9];
	private float[] end = new float[9];
	private volatile boolean initialized = false;

	private long startTime;
	private long duration;

	/**
	 * {eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ}
	 */
	private float[] current;
	private boolean moving;

	/**
	 * Creates an undefined Camera. Follow with
	 * {@link #set(float, float, float, float, float, float, float, float, float)}
	 * ;
	 */
	public Camera() {
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ, long duration) {
		if (!initialized) {
			throw new IllegalStateException(
					"Camera has not been initialized! Use set()");
		}

		System.arraycopy(current, 0, begin, 0, 9);

		end[0] = eyeX;
		end[1] = eyeY;
		end[2] = eyeZ;
		end[3] = centerX;
		end[4] = centerY;
		end[5] = centerZ;
		end[6] = upX;
		end[7] = upY;
		end[8] = upZ;

		startTime = System.currentTimeMillis();
		this.duration = duration;
		moving = true;
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, long duration) {
		if (!initialized) {
			throw new IllegalStateException(
					"Camera has not been initialized! Use set()");
		}

		moveTo(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, current[6],
				current[7], current[8], duration);
		// TODO inline this once sure it works
	}

	public synchronized void moveTo(float eyeX, float eyeY, float eyeZ,
			long duration) {
		if (!initialized) {
			throw new IllegalStateException(
					"Camera has not been initialized! Use set()");
		}

		moveTo(eyeX, eyeY, eyeZ, current[3], current[4], current[5],
				current[6], current[7], current[8], duration);
		// TODO inline this once sure it works
	}

	synchronized void update(long time) {
		if (!moving)
			return;
		if (!initialized) {
			throw new IllegalStateException(
					"Camera has not been initialized! Use set()");
		}
		float framePoint = ((float) (time - startTime)) / (float) duration;
		if (framePoint > 1.0f){
			System.arraycopy(end, 0, current, 0, 9);
			moving = false;
			return;
		} else if (framePoint < 0.0f){
			return;
		}
		IPO.mesh(current, begin, end, framePoint);
	}

	void writeToArray(float[] a, int offset) {
		if (a == null) {
			throw new IllegalStateException("Cannot copy Camera to null array.");
		}
		if (!initialized) {
			throw new IllegalStateException(
					"Camera has not been initialized! Use set()");
		}

		Matrix.setLookAtM(a, 0, current[0], current[1], current[2], current[3],
				current[4], current[5], current[6], current[7], current[8]);
	}

	/**
	 * Sets the Camera to the given position and orientation.
	 * 
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
	public void set(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {

		current = new float[] { eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
				upX, upY, upZ };
		initialized = true;
	}

}
