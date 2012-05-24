package com.supermercerbros.gameengine.motion.curve;

import android.view.animation.Interpolator;

public class BezierInterpolator implements Interpolator {
	final float[] values;
	final float[] times;
	final int numOfSplines;

	public BezierInterpolator(final float[] x, final float[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException(
					"x and y arrays must be of equal length.");
		} else if ((x.length - 4) % 3 != 0) {
			throw new IllegalArgumentException(
					"(x.length - 4) % 3 must equal zero.");
		}

		if (x[x.length - 1] != 1.0f) {
			final float end = x[x.length - 1];
			for (int i = 0; i < x.length; i++) {
				x[i] /= end;
			}
		}
		
		values = y;
		numOfSplines = (x.length - 4) / 3 + 1;
		times = new float[numOfSplines * 10 + 1];

		for (int i = 0; i < numOfSplines; i++) {
			final int index = i * 10;

			final float p0 = x[i * 3];
			final float p1 = x[i * 3 + 1];
			final float p2 = x[i * 3 + 2];
			final float p3 = x[i * 3 + 3];
			
			times[index] = p0;
			for (int j = 1; j < 10; j++) {
				times[index + j] = solve(p0, p1, p2, p3, j / 10);
			}
		}
		
		times[numOfSplines * 10] = 1.0f;

	}

	@Override
	public float getInterpolation(float x) {
		x %= 1.0f;
		int subframe = 0;
		while (x > times[subframe + 1])
			subframe++;
		
		final int frame = subframe / 10;
		subframe %= 10;
		
		final float t = ((float) subframe) / 10;
		return solve(values[frame*3], values[frame*3 + 1], values[frame*3 + 2], values[frame*3 + 3], t);
	}

	private float solve(final float p0, final float p1, final float p2,
			final float p3, final float t) {
		if (t == 0.0) {
			return p0;
		} else if (t == 1.0) {
			return p3;
		}
		
		final float d = 1 - t;
		return (d * d * d * p0) + (3 * d * d * t * p1) + (3 * d * t * t * p2)
				+ (t * t * t * p3);
	}
}
