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
