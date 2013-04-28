/*
 * Copyright 2013 Dan Mercer
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

import java.util.HashMap;
import java.util.Map;

import com.supermercerbros.gameengine.armature.Action;
import com.supermercerbros.gameengine.motion.Movement;
import com.supermercerbros.gameengine.objects.GameObject;

/**
 * A container for GameObjects, Movements, Actions, etc. to be passed between
 * loading and gameplay activities.
 */
public class GameData {
	private HashMap<String, GameObject> objects = new HashMap<String, GameObject>();
	private HashMap<String, Action> actions = new HashMap<String, Action>();
	private HashMap<String, Movement> movements = new HashMap<String, Movement>();
	
	// ======= GameObject methods =============================================
	
	public synchronized void putGameObject(String key, GameObject object) {
		this.objects.put(key, object);
	}
	
	public synchronized void putGameObjects(
			Map<String, ? extends GameObject> objects) {
		this.objects.putAll(objects);
	}
	
	public synchronized GameObject getGameObject(String key) {
		return objects.get(key);
	}
	
	// ======= Action methods =================================================
	
	public synchronized void putAction(String key, Action action) {
		this.actions.put(key, action);
	}
	
	public synchronized void putActions(Map<String, ? extends Action> actions) {
		this.actions.putAll(actions);
	}
	
	public synchronized Action getAction(String key) {
		return actions.get(key);
	}
	
	// ======= Movement methods ===============================================
	
	public synchronized void putMovement(String key, Movement movement) {
		this.movements.put(key, movement);
	}
	
	public synchronized void putMovements(
			Map<String, ? extends Movement> movements) {
		this.movements.putAll(movements);
	}
	
	public synchronized Movement getMovement(String key) {
		return movements.get(key);
	}
}
