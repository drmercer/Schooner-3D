/**
 * 
 */
package com.supermercerbros.gameengine.collision;

/**
 * For debugging. TODO remove debug
 */
public interface DebugListener {
	public void closestPoints(Point a, Point b);
	public void onFrameComplete(final long frameLength);
}
