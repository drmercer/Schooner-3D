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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import android.util.FloatMath;
import android.util.Log;

import com.supermercerbros.gameengine.collision.CollisionDetector;
import com.supermercerbros.gameengine.collision.OnCollisionCheckFinishedListener;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.util.LoopingThread;
import com.supermercerbros.gameengine.util.Toggle;

/**
 * Handles the interactions of game elements in the world space.
 * 
 * @version 1.0
 */
public class Engine extends LoopingThread {
	private static final String TAG = "Engine";

	/**
	 * To be used by subclasses of Engine. Contains the GameObjects currently in
	 * the Engine.
	 */
	protected final LinkedList<GameObject> objects;
	private boolean aBufs = true;
	private final Camera cam;
	private final CollisionDetector cd;
	
	private final Toggle cdIsFinished = new Toggle(false);
	// Be careful to always synchronize access of these fields:
	private final Light light = new Light();
	
	private Scene newScene;
	
	private final RenderData outA;
	private final RenderData outB;
	
	private final DataPipe pipe;
	
	private Scene scene;
	private long time;

	/**
	 * @param pipe
	 *            The DataPipe that this Engine will use to communicate with the
	 *            renderer and UI threads.
	 * @param cam
	 *            A pointer to the Camera object that will be used by the Game
	 *            Engine.
	 */
	public Engine(DataPipe pipe, Camera cam) {
		super("Schooner3D Engine thread");
		Log.d(TAG, "Constructing Engine...");
		this.pipe = pipe;
		this.cam = cam;
		this.objects = new LinkedList<GameObject>();
		
		final OnCollisionCheckFinishedListener listener = new OnCollisionCheckFinishedListener() {
			@Override
			public void onCollisionCheckFinished() {
				synchronized (cdIsFinished) {
					cdIsFinished.setState(true);
					cdIsFinished.notify();
				}
			}
		};
		this.cd = new CollisionDetector(listener);
		
		outA = new RenderData(0, pipe.VBO_capacity / 4, pipe.IBO_capacity / 2);
		outB = new RenderData(1, pipe.VBO_capacity / 4, pipe.IBO_capacity / 2);
		
		Log.d(TAG, "Engine constructed.");
	}
	
	/**
	 * Adds the collection of GameObjects to the Engine
	 * 
	 * @param objects
	 */
	public void addAllObjects(Collection<GameObject> objects) {
		if (!started) {
			this.objects.addAll(objects);
			for (final GameObject object : objects) {
				if (object.getBounds() != null) {
					cd.addCollider(object);
				}
				outA.modelMatrices.add(new float[16 + (16 * object
						.getExtraMatrixCount())]);
				outB.modelMatrices.add(new float[16 + (16 * object
						.getExtraMatrixCount())]);
			}
		} else {
			throw new IllegalStateException("Do not add GameObjects to the Engine while it is running.");
		}
	}
	
	/**
	 * Adds the given GameObject to the Engine
	 * 
	 * @param object
	 */
	public void addObject(GameObject object) {
		if (!started) {
			objects.add(object);
			if (object.getBounds() != null) {
				cd.addCollider(object);
			}
			outA.modelMatrices.add(new float[16 + (16 * object
					.getExtraMatrixCount())]);
			outB.modelMatrices.add(new float[16 + (16 * object
					.getExtraMatrixCount())]);
		} else {
			throw new IllegalStateException("Do not add GameObjects to the Engine while it is running.");
		}
	}
	
	@Override
	public void end() {
		super.end();
		cd.end();
	}
	
	/**
	 * Removes the given GameObject from the Engine. TODO remove this?
	 * 
	 * @param object
	 */
	public void removeObject(GameObject object) {
		if (!started) {
			final int index = objects.indexOf(object);
			objects.remove(object);
			outA.modelMatrices.remove(index);
			outB.modelMatrices.remove(index);
		} else {
			throw new IllegalStateException("Do not remove GameObjects from the Engine while it is running.");
		}
	}
	
	/**
	 * Sets the directional light of the scene. The given xyz coordinates do not need to be normalized.
	 * 
	 * @param x
	 *            The x-coordinate of the light vector
	 * @param y
	 *            The x-coordinate of the light vector
	 * @param z
	 *            The x-coordinate of the light vector
	 * @param r
	 *            The red value of the light's color
	 * @param g
	 *            The green value of the light's color
	 * @param b
	 *            The blue value of the light's color
	 */
	public void setLight(float x, float y, float z, float r, float g, float b) {
		final float length = FloatMath.sqrt(x*x + y*y + z*z);
		x /= length;
		y /= length;
		z /= length;
		synchronized (light) {
			light.x = x;
			light.y = y;
			light.z = z;
			light.r = r;
			light.g = g;
			light.b = b;
		}
	}
	
	public void setScene(Scene scene) {
		if (scene == null) {
			throw new NullPointerException("scene cannot be null, try NullScene for debugging");
		}
		if (!started) {
			this.scene = scene;
			scene.loadObjects(this);
		} else {
			synchronized(this) {
				this.newScene = scene;
			}
		}
	}
	
	/* (non-Javadoc)
	 *  Sets time to System.currentTimeMillis() for first iteration
	 */
	@Override
	public void start() {
		if (this.scene == null) {
			throw new IllegalStateException("Engine.setScene() has not been called.");
		}
		time = System.currentTimeMillis();
		super.start();
	}
	
	@Override
	protected void loop() {
		// Change scene if necessary
		synchronized (this) {
			if (newScene != null) {
				this.scene = newScene;
				newScene = null;
				objects.clear();
				scene.loadObjects(this);
			}
		}
		
		cd.go(); // Start collision detection (in background)
		
		// While collision detection is running
		scene.onBeginFrame(time);
		for (GameObject object : objects) {
			object.drawVerts(time);
		}
		cam.update(time);
		
		waitOnToggle(cdIsFinished, true); // Wait for collision detection
		
		// After collision detection has finished
		scene.onCollisionDetectorFinished();
		for (GameObject object : objects) {
			object.drawMatrix(time);
		}
		
		// Swap RenderData
		final RenderData out;
		if (aBufs) {
			out = outA;
		} else {
			out = outB;
		}
		
		// Update pipe
		final int outIndexOffset = out.index * 2;
		out.primitives.clear();
		
		final Iterator<float[]> matrixIter = out.modelMatrices.iterator();
		int vOffset = 0, iOffset = 0;
		for (GameObject object : objects) {
			final Metadata objData = object.info;
			final int[] objBufferLocations = objData.bufferLocations;
			final Material objMaterial = objData.mtl;
			
			synchronized (object) {
				if (object.isVisible()) {
					final boolean vertsAreDirty = objBufferLocations[outIndexOffset] == -1;
					final boolean indicesAreDirty = objBufferLocations[outIndexOffset + 1] == -1;
					
					// Load verts
					if (vertsAreDirty) {
						final int rangeStart = vOffset;
						objBufferLocations[outIndexOffset] = rangeStart;
						if (object.isInstance) {
							final GameObject objParent = object.parent;
							vOffset += objParent.info.mtl.loadObjectToVBO(objParent, out.vbo, vOffset);
						} else {
							vOffset += objMaterial.loadObjectToVBO(object,
									out.vbo, vOffset);
						}
						out.vboRange.include(rangeStart, vOffset);
					} else {
						vOffset = objBufferLocations[outIndexOffset] + objData.count * objMaterial.getStride();
					}
					
					// Load indices
					final int size = objData.size;
					if (indicesAreDirty) {
						final int rangeStart = iOffset;
						System.arraycopy(object.indices, 0, out.ibo, iOffset, size);
						objBufferLocations[outIndexOffset + 1] = iOffset;
						iOffset += size;
						out.iboRange.include(rangeStart, iOffset);
					} else {
						iOffset = objBufferLocations[outIndexOffset + 1] + size;
					}
					
					// Load matrices
					object.writeMatrices(matrixIter.next());
					
					out.primitives.add(objData);
				}
			}
		}
		
		cam.writeToArray(out.viewMatrix, 0);
		
		synchronized (light) {
			light.copyTo(out.light);
		}
		
		time = pipe.putData(this, out);
		aBufs = !aBufs;
//		LoopLog.i(TAG, "Engine is switching to RD " + (aBufs ? 0 : 1));
	}
	
	@Override
	protected void onBegin() {
		scene.onBegin();
	}
	
	@Override
	protected void onEnd() {
		Log.i(TAG, "Engine end.");
	}
	
	@Override
	protected void onPause() {
		Time.INSTANCE.pause();
	}
	
	@Override
	protected void onResume() {
		Time.INSTANCE.resume();
	}
}
