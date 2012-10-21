package com.supermercerbros.gameengine.objects;

import java.util.HashMap;
import java.util.LinkedList;

import android.opengl.Matrix;

import com.supermercerbros.gameengine.collision.Bounds;
import com.supermercerbros.gameengine.collision.Collider;
import com.supermercerbros.gameengine.collision.Collision;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.Normals;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.motion.Movement;
import com.supermercerbros.gameengine.motion.MovementData;
import com.supermercerbros.gameengine.parsers.PreObjectData;

/**
 * Represents a 3D mesh object.
 */
public class GameObject implements Collider {
	public static final String TAG = "GameObject";
	
	/**
	 * Contains the indices of the vertices for the elements (i.e. triangles) in
	 * this object.
	 */
	public final short[] indices;
	
	/**
	 * Contains the current object-space coordinates of the vertices used in
	 * this </code>GameObject</code>. Every three values represent one vertex.
	 */
	public final float[] verts;
	
	/**
	 * Contains either the UV coordinates (float pairs) or colors (three floats
	 * each) of the vertices in this <code>GameObject</code>.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/UV_mapping">UV Mapping</a>
	 *      (Wikipedia)
	 */
	public final float[] mtl;
	
	/**
	 * Contains the normals of the vertices of this <code>GameObject</code>.
	 */
	public final float[] normals;
	
	/**
	 * Contains the indices of the vertex pairs that are identical
	 * geometrically. Used in normal calculation.
	 */
	public final short[][] doubles;
	
	/**
	 * The Metadata about this GameObject.
	 */
	public final Metadata info;
	
	/**
	 * The model transformation matrix for this GameObject
	 */
	public final float[] modelMatrix;
	
	public final GameObject parent;
	public final boolean isInstance;
	
	/**
	 * The current Movement of the GameObject
	 */
	protected Movement motion;
	/**
	 * The MovementData containing GameObject-specific information about
	 * {@link #motion}
	 */
	protected final MovementData motionData;
	
	/**
	 * 
	 * @param verts
	 *            The object-space coordinates of the object's vertices. Every
	 *            three values represent one vertex (x-, y-, and z-coordinates).
	 * @param indices
	 *            The indices of the vertices for the triangles in this object.
	 * @param normals
	 *            The coordinates of the normal vectors of the vertices.
	 * @param mtl
	 *            The UV texture coordinates of the triangles.
	 * @param mtl
	 *            A Material object to use when for rendering
	 */
	public GameObject(float[] verts, short[] indices, float[] normals,
			float[] uvs, short[][] doubles, Material mtl) {
		this.verts = verts;
		this.indices = indices;
		this.mtl = uvs;
		this.normals = (normals != null) ? normals : new float[verts.length];
		this.doubles = doubles;
		
		// Instance information
		this.isInstance = false;
		this.parent = null;
		
		// Metadata
		info = new Metadata();
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = mtl;
		
		// Movements
		motionData = new MovementData();
		
		// Collision detection
		collisions = new HashMap<Collision, Collider>();
		
		// Model Matrix
		modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		
		// Normals
		if (normals == null) {
			Normals.calculate(this);
		}
	}
	
	public GameObject(PreObjectData data, Material material) {
		this.verts = data.verts;
		this.indices = data.indices;
		this.mtl = data.uvs;
		this.normals = new float[verts.length];
		this.doubles = data.doubles;
		Normals.calculate(this);
		
		// Instance information
		this.parent = data.parent;
		if (parent != null) {
			isInstance = true;
			info = new Metadata(parent.info.bufferLocations);
		} else {
			isInstance = false;
			info = new Metadata();
		}
		
		// Metadata
		info.size = indices.length;
		info.count = verts.length / 3;
		info.mtl = material;
		
		// Movements
		motionData = new MovementData();
		
		// Collision Detection
		collisions = new HashMap<Collision, Collider>();
		
		// Model matrix
		if (data.matrix == null) {
			modelMatrix = new float[16];
			Matrix.setIdentityM(modelMatrix, 0);
		} else {
			modelMatrix = data.matrix;
		}
	}

	/**
	 * Returns a LinkedList containing one instance of this GameObject per
	 * Material in <code>materials</code>.
	 * 
	 * @param materials
	 *            An array of the Materials to use for the instances.
	 * @return a LinkedList of GameObjects, or null if no materials are given.
	 */
	public LinkedList<GameObject> instances(Material... materials) {
		if (materials.length == 0) {
			return null;
		}
		LinkedList<GameObject> instances = new LinkedList<GameObject>();
		if (isInstance) {
			for (Material material : materials) {
				final GameObject instance = parent.getInstance(material);
				material.makeProgram();
				instances.add(instance);
			}
		} else {
			for (Material material : materials) {
				final GameObject instance = getInstance(material);
				material.makeProgram();
				instances.add(instance);
			}
		}
		return instances;
	}
	
	public GameObject instance(Material material) {
		if (isInstance) {
			final GameObject instance = parent.getInstance(material);
			material.makeProgram();
			return instance;
		} else {
			final GameObject instance = getInstance(material);
			material.makeProgram();
			return instance;
		}
	}
	
	/**
	 * Subclasses should override this to return an instance of that class.
	 * @param material
	 * @return
	 */
	protected GameObject getInstance(Material material) {
		PreObjectData preData = new PreObjectData(verts, indices, mtl, doubles, null, null);
		preData.parent = this;
		return new GameObject(preData, material);
	}

	/**
	 * This method is called to tell the object to recalculate its
	 * transformation matrix for the given point in time, in milliseconds.
	 * 
	 * @param time
	 *            The time of the frame currently being calculated,
	 *            in milliseconds.
	 * 
	 * @see AnimatedMeshObject#drawMatrix(long)
	 */
	public void drawMatrix(long time) {
		synchronized (motionData) {
			if (motion != null) {
				motion.getFrame(this, motionData, time);
			}
		}
	}
	
	/**
	 * This method is called to tell the object to update its vertices for the
	 * given point in time, in milliseconds.
	 * 
	 * <p>
	 * The default implementation does nothing. To do something with the
	 * object-space (local) vertices every frame, override this method in a
	 * subclass.
	 * </p>
	 * 
	 * @param time
	 *            The time of the frame currently being calculated,
	 *            in milliseconds.
	 */
	public void drawVerts(long time) {
		// Subclasses can do something here.
	}
	
	/**
	 * TODO change to isVisible()
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
	 * @param time
	 *            The current time, in milliseconds.
	 * @param duration
	 *            The duration of the movement, in milliseconds.
	 */
	public void startMovement(Movement motion, long time, long duration) {
		synchronized (motionData) {
			this.motion = motion;
			this.motionData.set(time, duration, modelMatrix);
		}
	}
	
	public void endMovement() {
		synchronized (motionData) {
			this.motion = null;
		}
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
	
	/**
	 * Returns the number of extra matrices used by this GameObject. The default
	 * implementation returns zero.
	 * 
	 * @return The number of extra matrices used by this GameObject.
	 */
	public int getExtraMatrixCount() {
		return 0;
	}
	
	public void writeMatrices(float[] matrixArray){
		System.arraycopy(modelMatrix, 0, matrixArray, 0, 16);
	}
	
}
