package com.supermercerbros.gameengine.math;

public class Bezier {
	private final float[] values;
	private final float[] times;
	
	public Bezier(float[] x, float[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException(
					"x and y arrays must be of equal length.");
		} else if ((x.length - 1) % 3 != 0) {
			throw new IllegalArgumentException(
					"(x.length - 1) % 3 must equal zero.");
		}
		
		times = x;
		values = y;
	}
	
	/**
	 * Maps a value representing the elapsed fraction of an animation to the
	 * value corresponding to that fraction.
	 * 
	 * @param x
	 *            The elapsed fraction.
	 */
	public float getInterpolation(float x) {
		final float frame = x * times[times.length - 1];
		
		// Get index of lower keyframe
		int keyframe = 0;
		while (frame > times[(keyframe + 1) * 3]) {
			keyframe++;
		}
		
		// Point frame coordinates
		final float fp0 = times[(keyframe * 3) + 0];
		final float fp1 = times[(keyframe * 3) + 1];
		final float fp2 = times[(keyframe * 3) + 2];
		final float fp3 = times[(keyframe * 3) + 3];
		
		// Point value coordinates
		final float vp0 = values[(keyframe * 3) + 0];
		final float vp1 = values[(keyframe * 3) + 1];
		final float vp2 = values[(keyframe * 3) + 2];
		final float vp3 = values[(keyframe * 3) + 3];
		
		// Estimate T given X
		float lowerT = 0;
		float upperT = 1;
		float tGuess = (fp0 - frame) / (fp0 - fp3);
		
		for (int i = 1; i <= 5; i++) {
			final float frameGuess = solve(fp0, fp1, fp2, fp3, tGuess);
			if (frameGuess < frame) {
				lowerT = tGuess;
			} else if (frameGuess > frame){
				upperT = tGuess;
			} else {
				return solve(vp0, vp1, vp2, vp3, tGuess);
			}
			
			tGuess = (lowerT + upperT) / 2;
		}
		
		final float lowerFrame = solve(fp0, fp1, fp2, fp3, lowerT);
		final float upperFrame = solve(fp0, fp1, fp2, fp3, upperT);
		final float alpha = (lowerFrame - frame) / (lowerFrame - upperFrame);
		final float t = lowerT + (upperT - lowerT) * alpha;
		
		// Solve for Y now that we have an estimated T
		return solve(vp0, vp1, vp2, vp3, t);
		
	}
	
	private float solve(final float p0, final float p1, final float p2,
			final float p3, final float t) {
		if (t == 0.0) {
			return p0;
		} else if (t == 1.0) {
			return p3;
		}
		
		final float d = 1 - t;
		return (d * d * d * p0) +
				(3 * d * d * t * p1) +
				(3 * d * t * t * p2) +
				(t * t * t * p3);
	}
	
	public float getFirstValue(){
		return values[0];
	}
}
