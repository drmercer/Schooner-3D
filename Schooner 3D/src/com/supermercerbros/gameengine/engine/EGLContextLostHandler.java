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

public class EGLContextLostHandler{
	public interface EGLContextLostListener {
		public void onContextLost();
	}
	
	private static LinkedList<EGLContextLostListener> listeners;
	
	public static void addListener(EGLContextLostListener listener) {
		if (listeners == null){
			listeners = new LinkedList<EGLContextLostListener>();
		}
		listeners.add(listener);
	}
	
	static void contextLost(){
		if (listeners == null) {
			return;
		}
		for (EGLContextLostListener listener : listeners){
			listener.onContextLost();
		}
	}
	
	static void clear() {
		if (listeners != null) {
			listeners.clear();
			listeners = null;		
		}
	}
}
