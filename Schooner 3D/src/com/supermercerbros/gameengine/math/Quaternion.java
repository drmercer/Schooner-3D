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

import com.supermercerbros.gameengine.collision.Point;

/**
 * Represents a rotation. (Although, technically speaking, a quaternion is a
 * four-component complex number with three imaginary parts.) Given an
 * axis-angle rotation, the corresponding quaternion is equal to
 * <code>{cos(theta/2), axis.x * sin(theta/2), axis.y * sin(theta/2), axis.z * sin(theta/2)}</code>
 * . Like {@link Point} objects, Quaternion objects are immutable.
 */
public class Quaternion {
	/**
	 * The W-component of the Quaternion.
	 */
	final float w;
	/**
	 * The X-component of the Quaternion.
	 */
	final float x;
	/**
	 * The Y-component of the Quaternion.
	 */
	final float y;
	/**
	 * The Z-component of the Quaternion.
	 */
	final float z;
	
	/**
	 * Creates a new Quaternion with the given components.
	 * 
	 * @param w
	 *            The W-component of the Quaternion.
	 * @param x
	 *            The X-component of the Quaternion.
	 * @param y
	 *            The Y-component of the Quaternion.
	 * @param z
	 *            The Z-component of the Quaternion.
	 */
	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Applies the rotation represented by this Quaternion to the given Point.
	 * 
	 * @param p
	 *            The Point to rotate.
	 * @return A new Point at the location of the rotated point.
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
	 *      #Using_quaternion_rotations>Quaternion Rotations (Wikipedia)</a>
	 */
	public Point rotate(Point p) {
		final float xx = x*x, yy = y*y, zz = z*z;
		final float xy = x*y, yz = y*z, xz = x*z;
		final float xw = x*w, yw = y*w, zw = z*w;
		
		final float rX = p.x * (1 - 2*yy - 2*zz) + p.y * (2*xy - 2*zw) + p.z * (2*xz + 2*yw);
		final float rY = p.x * (2*xy + 2*zw) + p.y * (1 - 2*xx - 2*zz) + p.z * (2*yz - 2*xw);
		final float rZ = p.x * (2*xz - 2*yw) + p.y * (2*yz + 2*xw) + p.z * (1 - 2*xx - 2*yy);
		
		return new Point(rX, rY, rZ);
	}
	
	/**
	 * Applies the rotation represented by this Quaternion to the given point.
	 * 
	 * @param pX The x-coordinate of the Point to rotate.
	 * @param pY The y-coordinate of the Point to rotate.
	 * @param pZ The z-coordinate of the Point to rotate.
	 * 
	 * @return A new Point at the location of the rotated point.
	 * 
	 * @see <a
	 *      href=http://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
	 *      #Using_quaternion_rotations>Quaternion Rotations (Wikipedia)</a>
	 */
	public Point rotate(float pX, float pY, float pZ) {
		final float xx = x*x, yy = y*y, zz = z*z;
		final float xy = x*y, yz = y*z, xz = x*z;
		final float xw = x*w, yw = y*w, zw = z*w;
		
		final float rX = pX * (1 - 2*yy - 2*zz) + pY * (2*xy - 2*zw) + pZ * (2*xz + 2*yw);
		final float rY = pX * (2*xy + 2*zw) + pY * (1 - 2*xx - 2*zz) + pZ * (2*yz - 2*xw);
		final float rZ = pX * (2*xz - 2*yw) + pY * (2*yz + 2*xw) + pZ * (1 - 2*xx - 2*yy);
		
		return new Point(rX, rY, rZ);
	}
	
	public static Point rotate(float qW, float qX, float qY, float qZ, float pX, float pY, float pZ) {
		final float xx = qX*qX, yy = qY*qY, zz = qZ*qZ;
		final float xy = qX*qY, yz = qY*qZ, xz = qX*qZ;
		final float xw = qX*qW, yw = qY*qW, zw = qZ*qW;
		
		final float rX = pX * (1 - 2*yy - 2*zz) + pY * (2*xy - 2*zw) + pZ * (2*xz + 2*yw);
		final float rY = pX * (2*xy + 2*zw) + pY * (1 - 2*xx - 2*zz) + pZ * (2*yz - 2*xw);
		final float rZ = pX * (2*xz - 2*yw) + pY * (2*yz + 2*xw) + pZ * (1 - 2*xx - 2*yy);
		
		return new Point(rX, rY, rZ);
	}
}
