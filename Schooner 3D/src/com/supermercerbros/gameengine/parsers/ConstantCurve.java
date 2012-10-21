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

package com.supermercerbros.gameengine.parsers;

import com.supermercerbros.gameengine.math.Curve;

public class ConstantCurve implements Curve {
	private final float value;

	public ConstantCurve(float value) {
		this.value = value;
	}

	/**
	 * Returns the constant value of this ConstantCurve.
	 * 
	 * @see com.supermercerbros.gameengine.math.Curve#getInterpolation(float)
	 */
	@Override
	public float getInterpolation(float x) {
		return value;
	}

	/**
	 * Returns the constant value of this ConstantCurve.
	 * 
	 * @see com.supermercerbros.gameengine.math.Curve#getStartValue()
	 */
	@Override
	public float getStartValue() {
		return value;
	}

}
