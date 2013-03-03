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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

import android.util.FloatMath;
import android.util.Log;

import com.supermercerbros.gameengine.collision.CollisionDetector;
import com.supermercerbros.gameengine.collision.OnCollisionCheckFinishedListener;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.util.DelayedRunnable;
import com.supermercerbros.gameengine.util.LoopingThread;
import com.supermercerbros.gameengine.util.Toggle;

/**
 * Handles the interactions of game elements in the world space.
 * 
 * @version 1.0
 */
public class Engine extends LoopingThread {
	static final String TAG = "Engine";
	private final DataPipe pipe;
	private final RenderData outA;
	private final RenderData outB;
	private final Camera cam;
	
	private final CollisionDetector cd;
	final Toggle cdIsFinished = new Toggle(false);
	
	private boolean aBufs = true;
	
	/**
	 * To be used by subclasses of Engine. Contains the GameObjects currently in
	 * the Engine.
	 */
	protected LinkedList<GameObject> objects;
	private long time;
	
	// Be careful to always synchronize access of these fields:
	private final Toggle flush = new Toggle(false);
	private final Light light = new Light();
	
	// TODO: remove these queues
	/**
	 * Used for passing commands from the UI thread to the {@link Engine}
	 * thread. This <b>should not</b> be polled by any thread other than the
	 * Engine thread.
	 */
	ConcurrentLinkedQueue<Runnable> actions = new ConcurrentLinkedQueue<Runnable>();
	/**
	 * Used for passing delayed commands from the UI thread to the Engine
	 * thread.
	 */
	DelayQueue<DelayedRunnable> delayedActions = new DelayQueue<DelayedRunnable>();
	/**
	 * Used for passing new GameObjects from the UI thread to the {@link Engine}
	 * thread. This <b>should not</b> be polled by any thread other than the
	 * Engine thread.
	 */
	ConcurrentLinkedQueue<GameObject> newObjects = new ConcurrentLinkedQueue<GameObject>();
	/**
	 * Used for passing GameObjects to be deleted to the {@link Engine} thread.
	 * This <b>should not</b> be polled by any thread other than the Engine
	 * thread.
	 */
	ConcurrentLinkedQueue<GameObject> delObjects = new ConcurrentLinkedQueue<GameObject>();
	
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
			newObjects.addAll(objects);
		}
	}
	
	@Override
	public void end() {
		super.end();
		cd.end();
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
			newObjects.add(object);
		}
	}
	
	/**
	 * Runs a Runnable on the Engine thread
	 * 
	 * @param r
	 *            The Runnable to run on the Engine thread
	 */
	public void doRunnable(Runnable r) {
		actions.add(r);
	}
	
	/**
	 * Runs a {@link Runnable} on the Engine thread with a delay.
	 * 
	 * @param r
	 *            The Runnable to run on the Engine thread.
	 * @param delay
	 *            The amount by which to delay the run, in milliseconds
	 */
	public void doRunnable(Runnable r, long delay) {
		delayedActions.add(new DelayedRunnable(r, delay));
	}
	
	/**
	 * Tells the Engine to actually delete all of its GameObjects that are
	 * marked for deletion
	 */
	public void flushDeletedObjects() {
		synchronized (flush) {
			flush.setState(true);
		}
	}
	
	/**
	 * Removes the given GameObject from the Engine.
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
			delObjects.add(object);
		}
	}
	
	@Override
	protected void loop() {
		// Check for new GameObjects, GameObjects to delete, and actions to
		// perform.
		while (!actions.isEmpty()) {
			actions.poll().run();
		}
		while (!newObjects.isEmpty()) {
			final GameObject newObject = newObjects.poll();
			objects.add(newObject);
			outA.modelMatrices.add(new float[16]);
			outB.modelMatrices.add(new float[16]);
			if (newObject.getBounds() != null) {
				cd.addCollider(newObject);
			}
		}
		while (!delObjects.isEmpty()) {
			delObject(delObjects.poll());
		}
		DelayedRunnable d = delayedActions.poll();
		while (d != null) {
			d.r.run();
			d = delayedActions.poll();
		}
		
		synchronized (flush) {
			if (flush.getState()) {
				flush();
			}
		}
		
		computeFrame();
		final RenderData out;
		if (aBufs) {
			out = outA;
		} else {
			out = outB;
		}
		updatePipe(out);
		aBufs = !aBufs;
//		LoopLog.i(TAG, "Engine is switching to RD " + (aBufs ? 0 : 1));
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
	
	/**
	 * Sets the directional light of the scene
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
	
	private void computeFrame() {
		cd.go();
		
		for (GameObject object : objects) {
			object.drawVerts(time);
		}
		cam.update(time);
		
		waitOnToggle(cdIsFinished, true);
		
		for (GameObject object : objects) {
			object.drawMatrix(time);
		}
		
	}
	
	/**
	 * Marks the given GameObject for deletion.
	 * 
	 * @param object
	 *            The GameObject to remove from the Engine.
	 */
	private synchronized void delObject(GameObject object) {
		if (objects.contains(object)) {
			object.markForDeletion();
		}
	}
	
	private void flush() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).isMarkedForDeletion()) {
				objects.remove(i);
				outA.modelMatrices.remove(i);
				outB.modelMatrices.remove(i);
			}
		}
		flush.setState(false);
	}
	
	private void updatePipe(RenderData out) {
		final int outIndexOffset = out.index * 2;
		out.primitives.clear();
		
		final Iterator<float[]> matrixIter = out.modelMatrices.iterator();
		int vOffset = 0, iOffset = 0;
		for (GameObject object : objects) {
			final Metadata objData = object.info;
			final int[] objBufferLocations = objData.bufferLocations;
			final Material objMaterial = objData.mtl;
			
			synchronized (object) {
				if (!object.isMarkedForDeletion()) {
					final boolean vertsAreDirty = objBufferLocations[outIndexOffset] == -1;
					final boolean indicesAreDirty = objBufferLocations[outIndexOffset + 1] == -1;
					
					// Load verts
					if (vertsAreDirty) {
						objBufferLocations[outIndexOffset] = vOffset;
						if (object.isInstance) {
							final GameObject objParent = object.parent;
							vOffset += objParent.info.mtl.loadObjectToVBO(objParent, out.vbo, vOffset);
						} else {
							vOffset += objMaterial.loadObjectToVBO(object,
									out.vbo, vOffset);
						}
					} else {
						vOffset = objBufferLocations[outIndexOffset] + objData.count * objMaterial.getStride();
					}
					
					// Load indices
					final int size = objData.size;
					if (indicesAreDirty) {
						System.arraycopy(object.indices, 0, out.ibo, iOffset, size);
						objBufferLocations[outIndexOffset + 1] = iOffset;
						iOffset += size;
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
	}
	
	/* (non-Javadoc)
	 *  Sets time to System.currentTimeMillis() for first iteration
	 */
	@Override
	public void start() {
		time = System.currentTimeMillis();
		super.start();
	}
}
