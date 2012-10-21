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

package com.supermercerbros.gameengine.util;


/**
 * Contains interpolation utilities. (IPO is shorthand for interpolation.)
 * 
 * @version 1.0
 */
public class IPO {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.util.IPO";

	/**
	 * Interpolates to the given point between two mesh keyframes.
	 * 
	 * @param frame
	 *            The float array that holds the interpolated frame.
	 * @param startKeyframe
	 *            The float array that holds the first keyframe.
	 * @param endKeyframe
	 *            The float array that holds the second keyframe.
	 * @param framePoint
	 *            The point to be interpolated to. For example, 0.5f would
	 *            result in the halfway-point between the two keyframes.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>frame</code>, <code>startKeyframe</code>, and
	 *             <code>endKeyframe</code> are not equal lengths.
	 * @throws NullPointerException
	 *             if <code>frame</code>, <code>startKeyframe</code>, or
	 *             <code>endKeyframe</code> is null.
	 */
	public static void mesh(float[] frame, float[] startKeyframe,
			float[] endKeyframe, double framePoint) {
		
		int size = frame.length;
		
		if (frame == null || startKeyframe == null || endKeyframe == null)
			throw new NullPointerException();
		if (size != startKeyframe.length && size != endKeyframe.length)
			throw new IllegalArgumentException();

		for (int i = 0; i < size; i++) {
			frame[i] = (float) (startKeyframe[i]
					+ ((endKeyframe[i] - startKeyframe[i]) * framePoint));
		}
	}

}
