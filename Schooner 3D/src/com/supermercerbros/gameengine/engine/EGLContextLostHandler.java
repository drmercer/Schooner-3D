package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;

class EGLContextLostHandler{
	interface EGLContextLostListener {
		public void onContextLost();
	}
	
	private static LinkedList<EGLContextLostListener> listeners;
	
	static void addListener(EGLContextLostListener listener) {
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
		listeners.clear();
		listeners = null;		
	}
}
