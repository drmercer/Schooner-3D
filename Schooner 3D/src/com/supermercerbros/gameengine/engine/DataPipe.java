package com.supermercerbros.gameengine.engine;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

import android.content.Context;

import com.supermercerbros.gameengine.Schooner3D;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.util.DelayedRunnable;

/**
 * Used for communication between the main thread, the Engine thread, and the
 * renderer thread.
 */
public class DataPipe {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.engine.DataPipe";

	final int VBO_capacity = Schooner3D.vboSize;
	final int IBO_capacity = Schooner3D.iboSize;

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

	private RenderData data;
	private long lastReadTime;
	private boolean isRead = false;
	
	/**
	 * @param context 
	 * @param mtl The material to render GameObjects with.
	 */
	public DataPipe(Context context){
		ShaderLib.init(context);
		TextureLib.init(context);
	}
	
	public void addAllObjects(Collection<GameObject> objects) {
		newObjects.addAll(objects);
	}

	public void addObject(GameObject object) {
		newObjects.add(object);
	}

	/**
	 * Loads the data for the next frame into the DataPipe. Also returns the
	 * time of the frame to compute next.
	 * 
	 * @param frameTime
	 *            the time of the frame represented by the data
	 * @param data
	 *            a RenderData object containing the data to be rendered. This
	 *            is cloned in this method, so changes made to the original are
	 *            not automatically reflected in this DataPipe
	 * @return The time of the next frame that the Engine should calculate
	 */
	public synchronized long putData(long frameTime, RenderData data) {
		if (!isRead)
			while (!isRead) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		this.data = data.copy();

		isRead = false;
		notify();
		return lastReadTime + 1000 / 30;
	}
	
	public void removeObject(GameObject object) {
		delObjects.add(object);
	}

	public synchronized RenderData retrieveData() {
		if (isRead)
			while (isRead) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}

		lastReadTime = System.currentTimeMillis();
		isRead = true;
		notify();
		return data;
	}

	/**
	 * Runs a Runnable on the Engine thread
	 * 
	 * @param r
	 *            The Runnable to run on the Engine thread
	 */
	public void runOnEngineThread(Runnable r) {
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
	public void runOnEngineThread(Runnable r, long delay) {
		delayedActions.add(new DelayedRunnable(r, delay));
	}

}
