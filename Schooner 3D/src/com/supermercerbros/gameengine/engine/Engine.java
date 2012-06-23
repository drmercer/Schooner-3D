package com.supermercerbros.gameengine.engine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

import android.util.Log;

import com.supermercerbros.gameengine.collision.CollisionDetector;
import com.supermercerbros.gameengine.collision.OnCollisionCheckFinishedListener;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.util.DelayedRunnable;
import com.supermercerbros.gameengine.util.LoopingThread;
import com.supermercerbros.gameengine.util.Toggle;

/**
 * Handles the interactions of game elements in the world space.
 * 
 * @version 1.0
 */
public class Engine extends LoopingThread implements
		OnCollisionCheckFinishedListener {
	static final String TAG = "Engine";
	private final DataPipe pipe;
	private final RenderData outA;
	private final RenderData outB;
	private final Camera cam;
	
	public final CollisionDetector cd; // TODO after debug, revert to private
	private final Toggle cdIsFinished = new Toggle(false);
	
	private boolean aBufs = true;
	
	/**
	 * To be used by subclasses of Engine. Contains the GameObjects currently in
	 * the Engine.
	 */
	protected LinkedList<GameObject> objects;
	private long time;
	
	// Be careful to always synchronize access of these fields:
	private volatile Toggle flush = new Toggle(false);
	private final Light light = new Light();
	
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
		
		this.cd = new CollisionDetector(this);
		
		this.outA = new RenderData(pipe.VBO_capacity / 4, pipe.IBO_capacity / 2);
		this.outB = new RenderData(pipe.VBO_capacity / 4, pipe.IBO_capacity / 2);
		
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
				outA.modelMatrices.add(new float[16]);
				outB.modelMatrices.add(new float[16]);
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
			outA.modelMatrices.add(new float[16]);
			outB.modelMatrices.add(new float[16]);
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
		
		doSpecialStuff(time);
		computeFrame();
		updatePipe();
		aBufs = !aBufs; // Swap aBufs
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
		synchronized (light) {
			light.x = x;
			light.y = y;
			light.z = z;
			light.r = r;
			light.g = g;
			light.b = b;
		}
	}
	
	/**
	 * This method is called every frame, before objects are redrawn. The
	 * default implementation does nothing; subclasses should override this if
	 * they wish to do anything special each frame.
	 * 
	 * @param time
	 *            The time of the current frame.
	 */
	protected void doSpecialStuff(long time) {
		
	}
	
	private void computeFrame() {
		cd.go();
		waitOnToggle(cdIsFinished, true); // TODO put this somewhere else.
		
		for (GameObject object : objects) {
			if (!object.isMarkedForDeletion()) {
				object.drawMatrix(time);
			}
		}
		
		cam.update(time);
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
	
	private int loadToIBO(short[] ibo, GameObject object, int offset,
			int vertexOffset) {
		object.iOffset = offset;
		if (object.isMarkedForDeletion())
			return 0;
		System.arraycopy(object.indices, 0, ibo, offset, object.info.size);
		return object.info.size;
	}
	
	private void updatePipe() {
		final RenderData out = aBufs ? outA : outB;
		synchronized (out) {
			out.ibo_updatePos = out.ibo.length;
			out.primitives.clear();
			
			int vOffset = 0, iOffset = 0, vertexOffset = 0, index = 0;
			for (GameObject object : objects) {
				synchronized (object) {
					int bufferSize = object.info.mtl.loadObjectToVBO(object,
							out.vbo, vOffset);
					vOffset += bufferSize;
					
					iOffset += loadToIBO(out.ibo, object, iOffset, vertexOffset);
					
					vertexOffset += object.info.count;
					
					System.arraycopy(object.modelMatrix, 0,
							out.modelMatrices.get(index++), 0, 16);
					
					out.primitives.add(object.info);
				}
			}
			
			cam.writeToArray(out.viewMatrix, 0);
			
			synchronized (light) {
				light.copyTo(out.light);
			}
		}
		
		time = pipe.putData(time, out);
	}
	
	@Override
	public void onCollisionCheckFinished() {
		synchronized (cdIsFinished) {
			cdIsFinished.setState(true);
			cdIsFinished.notify();
		}
	}
}
