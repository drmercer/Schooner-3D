/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.supermercerbros.gameengine.math;


/**
 * Represents a piecewise Bezier curve, used for interpolation
 */
public class BezierCurve implements Curve {
	private final float[] values;
	private final float[] times;
	private final int lastIndex;
	
	public BezierCurve(float[] x, float[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException(
					"x and y arrays must be of equal length.");
		} else if ((x.length - 1) % 3 != 0) {
			throw new IllegalArgumentException(
					"(x.length - 1) % 3 must equal zero.");
		}
		
		times = x;
		values = y;
		lastIndex = times.length - 1;
	}
	
	/* (non-Javadoc)
	 * @see com.supermercerbros.gameengine.math.Curve#getInterpolation(float)
	 */
	@Override
	public float getInterpolation(float x) {
		final float frame = x * times[lastIndex];
		if (x >= 1) {
			return values[lastIndex];
		} else if (x <= 0) {
			return values[0];
		}
		
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
	
	private static float solve(final float p0, final float p1, final float p2,
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
	
	/* (non-Javadoc)
	 * @see com.supermercerbros.gameengine.math.Curve#getStartValue()
	 */
	@Override
	public float getStartValue() {
		return values[0];
	}
}
