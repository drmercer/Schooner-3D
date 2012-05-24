package com.supermercerbros.gameengine.objects;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.Normals;
import com.supermercerbros.gameengine.motion.Movement;
import com.supermercerbros.gameengine.motion.MovementData;

/**
 * Represents a 3D mesh object.
 */
public class GameObject {
	public static final String TAG = "com.supermercerbros.gameengine.objects.GameObject";

	/**
	 * Contains the indices of the vertices for the elements (i.e. triangles) in
	 * this object.
	 */
	public short[] indices;

	/**
	 * Contains the current object-space coordinates of the vertices used in
	 * this </code>GameObject</code>. Every three values represent one vertex.
	 */
	public float[] verts;

	/**
	 * Contains either the UV coordinates (float pairs) or colors (three floats
	 * each) of the vertices in this <code>GameObject</code>.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/UV_mapping">UV Mapping</a>
	 *      (Wikipedia)
	 */
	protected float[] mtl;

	/**
	 * Contains the normals of the vertices of this <code>GameObject</code>.
	 */
	public float[] normals;

	/**
	 * Contains the indices of the vertex pairs that are identical
	 * geometrically. Used in normal calculation.
	 */
	public short[][] doubles;

	/**
	 * The Metadata about this GameObject.
	 */
	public final Metadata info;
	public final float[] modelMatrix;
	protected Movement motion;
	private final MovementData motionData;

	/**
	 * Contains the VBO offset at which this GameObject's data is loaded. This
	 * is used for multiple instances of the same primitive.
	 */
	protected int[] instanceLoaded = { -1 };

	private long lastDrawTime;
	/**
	 * Used by the Engine class when loading the GameObject into buffers.
	 */
	public int iOffset = -1;

	private boolean debug = false;

	/**
	 * 
	 * @param verts
	 *            The object-space coordinates of the object's vertices. Every
	 *            three values represent one vertex (x-, y-, and z-coordinates).
	 * @param indices
	 *            The indices of the vertices for the triangles in this object.
	 * @param mtl
	 *            The UV texture coordinates of the triangles.
	 * @param normals
	 *            The coordinates of the normal vectors of the vertices.
	 * @param mtl
	 *            A Material object to use when for rendering
	 */
	public GameObject(float[] verts, short[] indices, float[] uvs,
			float[] normals, Material mtl, short[][] doubles2) {
		Log.d(TAG, "Constructing GameObject...");
		this.verts = verts;
		this.indices = indices;
		this.mtl = uvs;
		this.normals = normals;
		this.doubles = (doubles2 != null) ? doubles2 : new short[2][0];
		info = new Metadata();
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = mtl;
		
		modelMatrix = new float[16];
		motionData = new MovementData();

		Log.d(TAG, Arrays.toString(normals));
		if (normals == null) {
			Normals.calculate(this);
		}
	}

	private GameObject(float[] verts, short[] indices, float[] uvs,
			float[] normals, int[] instanceLoaded, Material mtl,
			short[][] doubles) {
		Log.d(TAG, "Constructing GameObject...");
		this.verts = verts;
		this.indices = indices;
		this.mtl = uvs;
		this.normals = normals;
		this.doubles = (doubles != null) ? doubles : new short[2][0];
		info = new Metadata();
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = mtl;
		
		modelMatrix = new float[16];
		motionData = new MovementData();

		if (normals == null) {
			Normals.calculate(this);
		}

		this.instanceLoaded = instanceLoaded;
	}

	/**
	 * Returns an ArrayList of <code>quantity</code> instances of this
	 * GameObject.
	 * 
	 * @param quantity
	 *            The number of duplicates to make.
	 * @return an ArrayList of GameObjects, or null if
	 *         <code>quantity <= 0</code>.
	 */
	public List<GameObject> instance(int quantity) {
		if (quantity <= 0)
			return null;
		LinkedList<GameObject> instances = new LinkedList<GameObject>();
		for (int i = 0; i < quantity; i++) {
			instances.add(new GameObject(verts, indices, mtl, normals,
					instanceLoaded, info.mtl, doubles));
		}
		return instances;
	}

	/**
	 * This method is called to tell the object to recalculate it's vertices
	 * and/or transformation matrix for the given point in time, in
	 * milliseconds. To do something with the object-space (local) vertices
	 * every frame, override this method in a <code>GameObject</code> subclass.
	 * 
	 * @param time
	 *            The (estimated) time of the frame currently being calculated,
	 *            in milliseconds.
	 * 
	 * @see AnimatedMeshObject#draw(long)
	 */
	public void draw(long time) {
		if (motion != null) {
			motion.getFrame(this, motionData, time);
		}
		lastDrawTime = time;

		if (debug) {
			Log.d(TAG, Arrays.toString(normals));
		}
	}

	/**
	 * @return the last time given to {@link #draw(long)}.
	 */
	protected long getLastDrawTime() {
		return lastDrawTime;
	}

	/**
	 * @return true if this GameObject has been marked for deletion.
	 * 
	 * @see #markForDeletion()
	 */
	public boolean isMarkedForDeletion() {
		return info.delete;
	}

	/**
	 * Marks this GameObject for deletion. The Engine doesn't update this
	 * GameObject for rendering anymore, but it is not actually deleted from the
	 * Engine until {@link Engine#flushDeletedObjects()} is called. Should only
	 * be called from the Engine thread
	 */
	public void markForDeletion() {
		info.delete = true;
	}

	/**
	 * Sets and starts the Movement that is used to animate this GameObject's
	 * location.
	 * 
	 * @param motion
	 *            The Movement to start
	 * @param time
	 *            The current time in milliseconds.
	 * @param duration
	 *            The duration of the Movement, in milliseconds.
	 * 
	 */
	public void startMotion(Movement motion, long time, long duration) {
		this.motion = motion;
		motionData.startTime = time;
		motionData.duration = duration;
		System.arraycopy(modelMatrix, 0, motionData.matrix, 0, 16);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
