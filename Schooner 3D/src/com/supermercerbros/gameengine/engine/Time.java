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
package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;

/**
 * This singleton class handles pausing in the game engine.
 */
public enum Time {
	INSTANCE;
	
	/**
	 * Represents an object that should be notified when the game engine resumes
	 * after being paused.
	 */
	public interface Pausable {
		/**
		 * Called when the game engine resumes after being paused. 
		 * @param millis The length of the pause duration, in millis.
		 */
		void onResume(long millis);
	}
	
	// Fields
	private final LinkedList<Pausable> pausables;
	private boolean paused = false;
	private long pauseTime = 0L;

	private Time() {
		this.pausables = new LinkedList<Time.Pausable>();
	}
	
	/**
	 * Adds the given Pausable to the Time object's list.
	 * @param p The Pausable to add.
	 */
	public synchronized void addPausable(Pausable p) {
		pausables.add(p);
	}
	
	/**
	 * Removes the given Pausable from the Time object's list.
	 * @param p The Pausable to remove.
	 */
	public synchronized void removePausable(Pausable p) {
		pausables.remove(p);
	}
	
	/**
	 * Called when the Engine pauses
	 */
	synchronized void pause() {
		paused = true;
		pauseTime = System.currentTimeMillis();
	}
	
	/**
	 * Called when the Engine resumes
	 */
	synchronized void resume() {
		if (paused) {
			final long timeDelta = System.currentTimeMillis() - pauseTime;
			for (Pausable p : pausables) {
				p.onResume(timeDelta);
			}
			paused = false;
		}
	}

}
