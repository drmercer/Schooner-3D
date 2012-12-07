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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.supermercerbros.gameengine.engine.GameRenderer;

/**
 * Represents the Heads-Up-Display-style UI of a game.
 */
public class GameHud {
	// Constants
	private static final int DEFAULT_VBO_SIZE = 8000; // 400 verts
	private static final int DEFAULT_IBO_SIZE = 2400; // 800 verts

	private final int vboSize;
	private final int iboSize;
	private final LinkedList<HudElement> elements;

	private volatile boolean initialized = false;

	// Coordinate converter
	private CoordsConverter converter;

	// Buffer handles
	private int arrayBuffer;
	private int elementBuffer;

	/**
	 * Constructs a new GameHud.
	 */
	public GameHud() {
		this.elements = new LinkedList<HudElement>();
		this.vboSize = DEFAULT_VBO_SIZE;
		this.iboSize = DEFAULT_IBO_SIZE;
	}

	/**
	 * Constructs a new GameHud.
	 * 
	 * @param vboSize
	 * @param iboSize
	 */
	public GameHud(int vboSize, int iboSize) {
		this.elements = new LinkedList<HudElement>();
		this.vboSize = vboSize;
		this.iboSize = iboSize;
	}

	/**
	 * Adds the given {@link HudElement} to this GameHud
	 * 
	 * @param element
	 *            The <code>HudElement</code> to add.
	 */
	public void addElement(HudElement element) {
		if (initialized) {
			throw new IllegalStateException(
					"Cannot addElement to already initialized gameHud");
		}
		final LinkedList<HudElement> localElements = this.elements;
		synchronized (localElements) {
			localElements.add(element);
		}
	}

	/**
	 * Called by GameRenderer
	 */
	public void render() {
		// GL Buffer stuff
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, arrayBuffer);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

		// Disable depth test and face culling
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE); // TODO delete this line
		GameRenderer.logError("debugging");

		// Render elements
		final LinkedList<HudElement> localElements = this.elements;
		synchronized (localElements) {
			for (HudElement element : localElements) {
				element.render();
			}
		}
	}

	/**
	 * Called by GameRenderer
	 */
	public void init() {
		// Generate buffers
		final int[] buffers = new int[2];
		GLES20.glGenBuffers(2, buffers, 0);
		final int localArrayBuffer = buffers[0];
		final int localElementBuffer = buffers[1];
		arrayBuffer = localArrayBuffer;
		elementBuffer = localElementBuffer;

		// Bind buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, localArrayBuffer);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, localElementBuffer);

		// Construct ByteBuffers
		final ByteBuffer vbo = ByteBuffer.allocateDirect(vboSize).order(
				ByteOrder.nativeOrder());
		final ByteBuffer ibo = ByteBuffer.allocateDirect(iboSize).order(
				ByteOrder.nativeOrder());

		// Fill Buffers and load Programs
		final LinkedList<HudElement> localElements = this.elements;
		synchronized (localElements) {
			for (HudElement element : localElements) {
				element.writeIndicesToBuffer(ibo);
				element.writeVertsToBuffer(vbo);
				element.loadProgram();
			}
		}

		vbo.rewind();
		ibo.rewind();

		// Initialize buffers
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vboSize, vbo,
				GLES20.GL_STATIC_DRAW);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboSize, ibo,
				GLES20.GL_STATIC_DRAW);

	}

	/**
	 * Called when a MotionEvent occurs. This calls <code>onTouchEvent</code> on
	 * all elements contained in this GameHud.
	 * 
	 * @param event
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent event) {
		for (HudElement element : elements) {
			if (element.onTouchEvent(event, converter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param width
	 * @param height
	 */
	public void setDimensions(int width, int height) {
		this.converter = new CoordsConverter(width, height);
	}
}
