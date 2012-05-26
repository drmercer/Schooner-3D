package com.supermercerbros.gameengine.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.opengl.Matrix;

import com.supermercerbros.gameengine.animation.Movable;
import com.supermercerbros.gameengine.animation.Movement;
import com.supermercerbros.gameengine.collision.Bounds;
import com.supermercerbros.gameengine.collision.Collider;
import com.supermercerbros.gameengine.collision.Collision;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.Normals;

/**
 * Represents a 3D mesh object.
 */
public class GameObject implements Movable, Collider {
	public static final String TAG = "GameObject";

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
	/**
	 * The model transformation matrix for this GameObject
	 */
	public float[] modelMatrix = new float[16];
	protected Movement motion;
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
	private boolean stationary;

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
		this.verts = verts;
		this.indices = indices;
		this.mtl = uvs;
		this.normals = normals;
		this.doubles = (doubles2 != null) ? doubles2 : new short[2][0];
		info = new Metadata();
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = mtl;

		collisions = new HashMap<Collision, Collider>();

		Matrix.setIdentityM(modelMatrix, 0);
		stationary = false;
		
		if (normals == null) {
			Normals.calculate(this);
		}
	}

	private GameObject(float[] verts, short[] indices, float[] uvs,
			float[] normals, int[] instanceLoaded, Material mtl,
			short[][] doubles) {
		this.verts = verts;
		this.indices = indices;
		this.mtl = uvs;
		this.normals = normals;
		this.doubles = (doubles != null) ? doubles : new short[2][0];
		info = new Metadata();
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = mtl;

		collisions = new HashMap<Collision, Collider>();

		Matrix.setIdentityM(modelMatrix, 0);
		stationary = false;

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
			motion.getFrame(modelMatrix, 0, time);
		}
		lastDrawTime = time;

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
	 * @return true if this object is stationary.
	 */
	public boolean isStationary() {
		return stationary;
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
	 * @param stationary
	 *            <code>true</code> if this <code>GameObject</code> has no
	 *            motion. Note that <code>modelMatrix</code> can still be
	 *            modified, and will still affect the <code>GameObject</code>'s
	 *            position, but the object will not be translated or rotated
	 *            when <code>draw()</code> is called.
	 */
	public void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	/**
	 * Sets and starts the Movement that is used to animate this GameObject's
	 * location.
	 * 
	 * @param speed
	 *            the speed of the Movement
	 * @param time
	 *            the current time
	 * 
	 */
	public void startMotion(Movement motion, long time, float speed) {
		this.motion = motion;
		motion.start(time, modelMatrix, speed);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	private final HashMap<Collision, Collider> collisions;
	private Bounds bounds;

	@Override
	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public float[] getMatrix() {
		return modelMatrix;
	}

	@Override
	public void clearCollisions() {
		collisions.clear();
	}

	@Override
	public void addCollision(Collider other, Collision collision) {
		collisions.put(collision, other);
	}

}
