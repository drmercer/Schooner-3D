package com.supermercerbros.gameengine.engine;

import java.util.LinkedList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import com.supermercerbros.gameengine.Schooner3D;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.Metadata;
import com.supermercerbros.gameengine.util.DelayedRunnable;

/**
 * Handles the interactions of game elements in the world space.
 * 
 * @version 1.0
 */
public class Engine extends Thread {
	private static final String TAG = "Engine";
	private DataPipe pipe;
	private RenderData out = new RenderData();
	private Camera cam;

	private int[] vboA;
	private short[] iboA;
	private float[] mmA;
	private float[] lightA;
	private float[] colorA;

	private boolean aBufs = true;

	private int[] vboB;
	private short[] iboB;
	private float[] mmB;
	private float[] lightB;
	private float[] colorB;

	/**
	 * To be used by subclasses of Engine. Contains the GameObjects currently in
	 * the Engine.
	 */
	protected List<GameObject> objects;
	private long time;

	// Be careful to always synchronize access of these fields:
	private volatile Boolean flush = false, paused = false;
	private volatile boolean started = false, ending = false;
	private boolean lightsChanged = false;

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
		this.vboA = new int[pipe.VBO_capacity / 4];
		this.vboB = new int[pipe.VBO_capacity / 4];
		this.iboA = new short[pipe.IBO_capacity / 2];
		this.iboB = new short[pipe.IBO_capacity / 2];
		this.mmA = new float[Schooner3D.maxObjects];
		this.mmB = new float[Schooner3D.maxObjects];
		this.lightA = new float[3];
		this.lightB = new float[3];
		this.colorA = new float[3];
		this.colorB = new float[3];
		this.setDaemon(true);
		Log.d(TAG, "Engine constructed.");
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
		synchronized (lightA) {
			if (aBufs) {
				lightA[0] = x;
				lightA[1] = y;
				lightA[2] = z;
				colorA[0] = r;
				colorA[1] = g;
				colorA[2] = b;
			} else {
				lightB[0] = x;
				lightB[1] = y;
				lightB[2] = z;
				colorB[0] = r;
				colorB[1] = g;
				colorB[2] = b;
			}
			lightsChanged = true;
		}
	}

	/**
	 * TODO Javadoc
	 * @param object
	 */
	public void addObject(GameObject object) {
		if (!started) {
			objects.add(object);
		} else {
			pipe.newObjects.add(object);
		}
	}

	/**
	 * TODO Javadoc
	 * @param objects
	 */
	public void addAllObjects(Collection<GameObject> objects) {
		if (!started) {
			this.objects.addAll(objects);
		} else {
			pipe.newObjects.addAll(objects);
		}
	}

	/**
	 * Removes the given GameObject from the Engine.
	 * @param object
	 */
	public void removeObject(GameObject object) {
		if (!started) {
			objects.remove(object);
		} else {
			pipe.delObjects.add(object);
		}
	}

	private void computeFrame() {
		// Collision detection goes here, whenever I need it.

		for (GameObject object : objects) {
			if (!object.isMarkedForDeletion()) {
				object.draw(time);
			}
		}

		cam.update(time);
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

	/**
	 * Terminates this Engine.
	 */
	public void end() {
		Log.d(TAG, "Engine state before end():" + getState().toString());
		ending = true;
		interrupt();
		Log.d(TAG, "Engine state after end():" + getState().toString());
		
	}

	private void flush() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).isMarkedForDeletion()) {
				objects.remove(i);
			}
		}
		flush = true;
	}

	/**
	 * Tells the Engine to actually delete all of its GameObjects that are
	 * marked for deletion
	 */
	public void flushDeletedObjects() {
		synchronized (flush) {
			flush = true;
		}
	}

	private int loadToIBO(short[] ibo, GameObject object, int offset,
			int vertexOffset) {
		object.iOffset = offset;
		if (object.isMarkedForDeletion())
			return 0;
		System.arraycopy(object.getIndices(), 0, ibo, offset, object.info.size);
		// int length = object.getIndices().length;
		// short[] indices = object.getIndices();
		// for (int i = 0; i < length; i++){
		// ibo[i + offset] = (short) (indices[i] + vertexOffset);
		// }
		return object.info.size;
	}

	/**
	 * Tells this Engine to pause processing.
	 */
	public void pause() {
		synchronized (paused) {
			paused = true;
		}
	}

	/**
	 * Tells this Engine to resume processing.
	 */
	public void resumeEngine() {
		synchronized (paused) {
			paused = false;
			paused.notify();
		}
	}

	/**
	 * Removes the given GameObject from the Engine. Only call this method from
	 * the Engine thread (i.e. in a Runnable in given to
	 * {@link DataPipe#runOnEngineThread(Runnable)}.
	 * 
	 * @param object
	 *            The GameObject to remove from the Engine.
	 */
	private synchronized void delObject(GameObject object) {
		if (objects.contains(object)) {
			object.markForDeletion();
		}
	}

	@Override
	public synchronized void run() {
		while (!ending) {
			// Check for new GameObjects, GameObjects to delete, and actions to
			// perform.
			while (!pipe.actions.isEmpty())
				pipe.actions.poll().run();
			while (!pipe.newObjects.isEmpty())
				objects.add(pipe.newObjects.poll());
			while (!pipe.delObjects.isEmpty())
				delObject(pipe.delObjects.poll());

			DelayedRunnable r = pipe.delayedActions.poll();
			while (r != null) {
				r.run();
				r = pipe.delayedActions.poll();
			}

			synchronized (flush) {
				if (flush) {
					flush();
				}
			}

			doSpecialStuff(time);
			computeFrame();
			updatePipe();
			aBufs = !aBufs; // Swap aBufs

			synchronized (paused) {
				while (paused) {
					try {
						Log.d(TAG, "Waiting to unpause...");
						paused.wait();
					} catch (InterruptedException e) {
						Log.w(TAG, "Interrupted while waiting to unpause.");
						break;
					}
				}
			}
		}

		Log.d(TAG, "end Engine");
		// TODO add any necessary closing code
	}

	/**
	 * Use this method (<b>not</b> {@link #run()}) to start the Engine.
	 */
	public void start() {
		started = true;
		super.start();
	}

	private void updatePipe() {
		if (vboA == null)
			Log.e(TAG, "vboA == null");
		if (vboB == null)
			Log.e(TAG, "vboB == null");

		out.vbo = aBufs ? vboA : vboB;
		out.ibo = aBufs ? iboA : iboB;
		out.modelMatrices = aBufs ? mmA : mmB;
		out.ibo_updatePos = iboA.length;
		out.primitives = new Metadata[objects.size()];

		int vOffset = 0, iOffset = 0, vertexOffset = 0, matrixIndex = 0, i = 0;
		for (GameObject object : objects) {
			int bufferSize = object.info.mtl.loadObjectToVBO(object, out.vbo,
					vOffset);
			vOffset += bufferSize;

			iOffset += loadToIBO(out.ibo, object, iOffset, vertexOffset);

			vertexOffset += object.info.count;

			System.arraycopy(object.modelMatrix, 0, out.modelMatrices,
					matrixIndex++ * 16, 16);

			out.primitives[i++] = object.info;
		}

		cam.copyToArray(out.viewMatrix, 0);
		if (out.viewMatrix == null) {
			Log.e(TAG, "viewMatrix == null");
		}

		if (lightsChanged) {
			synchronized (lightA) {
				out.light = aBufs ? lightA : lightB;
				out.color = aBufs ? colorA : colorB;
			}
		}

		time = pipe.putData(time, out);
		synchronized (lightA) {
			if (aBufs) {
				System.arraycopy(lightA, 0, lightB, 0, 3);
				System.arraycopy(colorA, 0, colorB, 0, 3);
			} else {
				System.arraycopy(lightB, 0, lightA, 0, 3);
				System.arraycopy(colorB, 0, colorA, 0, 3);
			}
		}

	}
}
