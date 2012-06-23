package com.supermercerbros.gameengine.armature;

import java.util.LinkedList;
import java.util.List;

import com.supermercerbros.gameengine.armature.ActionData.ArmatureState;
import com.supermercerbros.gameengine.math.Bezier;
import com.supermercerbros.gameengine.objects.AnimatedBoneObject;

public class Bone {
	private final byte index;
	private final LinkedList<Bone> children;
	private final float locX, locY, locZ;
	private Bezier rotW, rotX, rotY, rotZ;
	private float w, x, y, z;
	
	/**
	 * Creates a new Bone
	 * @param index The index of this Bone in its Armature's bone list.
	 * @param children A List of this Bone's children.
	 * @param x The x-coordinate of the Bone, relative to its parent
	 * @param y The y-coordinate of the Bone, relative to its parent
	 * @param z The z-coordinate of the Bone, relative to its parent
	 */
	public Bone(byte index, List<Bone> children, float x, float y, float z) {
		this.index = index;
		if (children != null) {
			this.children = new LinkedList<Bone>(children);
		} else {
			this.children = null;
		}
		this.locX = x;
		this.locY = y;
		this.locZ = z;
	}
	
	/**
	 * Sets the active Action of this Bone to the given Action. This Bone's
	 * Bezier curves are obtained from the given Action.
	 * 
	 * @param action
	 *            The Action to use.
	 */
	void setAction(Action action) {
		rotW = action.boneCurves[index].rotW;
		rotX = action.boneCurves[index].rotX;
		rotY = action.boneCurves[index].rotY;
		rotZ = action.boneCurves[index].rotZ;
		for (Bone child : children) {
			child.setAction(action);
		}
	}
	
	/**
	 * Writes this Bone's current rotation (in quaternion form) to the given array.
	 * @param array
	 */
	void getRotation(float[] array) {
		array[index * 4    ] = w;
		array[index * 4 + 1] = x;
		array[index * 4 + 2] = y;
		array[index * 4 + 3] = z;
	}

	/**
	 * Gets the Bone's rotation for the current frame.
	 * @param target The animated object
	 * @param framePoint The [-1.0f, 0.0f) value describing the frame's proximity to the start of the animation
	 * @param callState The state of the Armature when the Action was set
	 */
	public void getFrame(AnimatedBoneObject target, float framePoint,
			ArmatureState callState) {
		// TODO Bone.getFrame(callState)
	}
}
