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
