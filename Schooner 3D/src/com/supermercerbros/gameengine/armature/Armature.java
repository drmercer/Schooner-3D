package com.supermercerbros.gameengine.armature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.supermercerbros.gameengine.motion.Movement;
import com.supermercerbros.gameengine.objects.AnimatedBoneObject;

public class Armature {
	private final String id;
	private final LinkedList<Bone> rootParents;
	private final HashMap<String, Action> actions;
	/**
	 * An ArrayList of the Bones in this Armature. It is recommended that you do not modify this list.
	 */
	protected final ArrayList<Bone> bones;
	
	public Armature(String id, List<Bone> roots, List<Bone> bones, HashMap<String, Action> actions){
		this.id = id;
		this.rootParents = new LinkedList<Bone>(roots);
		this.bones = new ArrayList<Bone>(bones);
		this.actions = actions;
	}
	
	public int boneCount() {
		return bones.size();
	}
	
	/**
	 * Sets the Action of this Armature to the given Action
	 * @param name The name of the Action to begin.
	 * 
	 * @return The Movement associated with the Action
	 */
	public Movement setAction(AnimatedBoneObject target, String name) {
		Action newAction = actions.get(name);
		if (newAction != null) {
			for (Bone root : rootParents) {
				root.setAction(newAction);
			}
			return newAction.movement;
		} else {
			throw new IllegalArgumentException(name + " is not an Action of Armature " + id);
		}
	}
	
	public void getFrame(AnimatedBoneObject target, ActionData data, long time){
		final float framePoint;
		if (time < data.startTime) { 
			//interpolating to the Action
			framePoint = (time - data.startTime) / (data.startTime - data.callTime);
		} else {
			framePoint = (time - data.startTime) / data.duration;
		}
		for (Bone root : rootParents) {
			root.getFrame(target, framePoint, data.callState);
		}
	}
}
