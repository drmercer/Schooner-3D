package com.supermercerbros.gameengine.handlers;

import java.util.LinkedList;

public class OnAnimationCompleteDispatcher {
	private LinkedList<OnAnimationCompleteListener> listeners;
	
	public void addListener(OnAnimationCompleteListener listener){
		if (listeners == null) {
			listeners = new LinkedList<OnAnimationCompleteListener>();
		}
		listeners.add(listener);
	}
	public void fire(String id) {
		if (listeners != null) {
			for (OnAnimationCompleteListener listener : listeners) {
				listener.onAnimationComplete(id);
			}
		}
	}
}
