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
package com.supermercerbros.gameengine.engine;

/**
 * Represents the set of objects in the game at a certain time (i.e. the level,
 * world, etc.).
 */
public interface Scene {
	public void loadObjects(Engine engine);
	
	public void onBegin();
	
	public void onBeginFrame(long time);
	
	public void onCollisionDetectorFinished();
	
	/**
	 * A Scene implementation that does absolutely nothing. This class is for
	 * debugging or testing purposes - if you're using it in an actual game
	 * you're doing something wrong.
	 */
	public static class NullScene implements Scene {
		
		@Override
		public void loadObjects(Engine engine) {
		}
		
		@Override
		public void onBegin() {
		}
		
		@Override
		public void onBeginFrame(long time) {
		}
		
		@Override
		public void onCollisionDetectorFinished() {
		}
		
	}
}
