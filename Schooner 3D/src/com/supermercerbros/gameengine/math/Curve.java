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

public interface Curve {

	/**
	 * Maps a value representing the elapsed fraction of an animation to the
	 * value corresponding to that fraction.
	 * 
	 * @param x
	 *            The elapsed fraction.
	 */
	public abstract float getInterpolation(float x);

	/**
	 * @return The value of the first keyframe of this Curve
	 */
	public abstract float getStartValue();

}