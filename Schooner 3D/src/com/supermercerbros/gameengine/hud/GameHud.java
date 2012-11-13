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
package com.supermercerbros.gameengine.hud;

import java.util.LinkedList;

import android.view.MotionEvent;

/**
 * Represents the Heads-Up-Display-style UI of a game.
 */
public class GameHud {
	private final LinkedList<HudElement> elements;

	/**
	 * Constructs a new GameHud.
	 */
	public GameHud() {
		this.elements = new LinkedList<HudElement>();
	}

	/**
	 * Adds the given {@link HudElement} to this GameHud
	 * 
	 * @param element
	 *            The <code>HudElement</code> to add.
	 */
	public void addElement(HudElement element) {
		final LinkedList<HudElement> localElements = this.elements;
		synchronized (localElements) {
			localElements.add(element);
		}
	}

	/**
	 * Called by GameRenderer
	 */
	public void render() {
		final LinkedList<HudElement> localElements = this.elements;
		// TODO GL Buffer stuff
		synchronized (localElements) {
			for (HudElement element : localElements) {
				element.writeIndicesToBuffer()
			}
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent event) {
		event.
		// TODO GameHud.onTouchEvent()
		return false;
	}
}
