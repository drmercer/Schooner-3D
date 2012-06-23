package com.supermercerbros.gameengine.motion;

import com.supermercerbros.gameengine.objects.GameObject;


/**
 * Similar to the Animation interface, implementations of Movement can be used 
 * to animate the location of a GameObject (instead of the geometry, like an Animation would do).
 *
 * @see GameObject#startMotion(Movement, float)
 * @see GameObject#getMotion()
 */
public interface Movement {
	
	public static final int POSITION = 1;
	public static final int ROTATION = 2;
	public static final int SCALE = 4;
	public static final int SCALE_AXIS = 8;

	/** Computes a transformation frame and applies it to the given Transformable
	 * @param target The Transformable to transform.
	 * @param data The MovementData describing the target's motion
	 * @param time The time of the frame being drawn
	 */
	public void getFrame(GameObject target, MovementData data, long time);
}
