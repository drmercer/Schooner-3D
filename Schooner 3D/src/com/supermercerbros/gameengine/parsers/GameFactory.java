package com.supermercerbros.gameengine.parsers;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.supermercerbros.gameengine.armature.Action;
import com.supermercerbros.gameengine.armature.Skeleton;
import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.motion.CurveMovement;
import com.supermercerbros.gameengine.objects.BonedObject;
import com.supermercerbros.gameengine.objects.GameObject;

/**
 * Creates Movements and GameObjects, including BonedObjects.
 */
public class GameFactory {
	private final AssetManager am;
	private final Resources res;
	
	private HashMap<String, Action> actions;
	private PreObjectData data;
	private Skeleton skeleton;
	private Material material;
	
	/**
	 * Creates a new GameFactory for the given Context.
	 * 
	 * @param context
	 */
	public GameFactory(Context context) {
		this.am = context.getAssets();
		this.res = context.getResources();
	}
	
	protected void setActions(HashMap<String, Action> actions) {
		this.actions = actions;
	}

	/**
	 * @param fileName
	 *            The asset path of the file to read.
	 * @return A HashMap of movements and their names
	 * @throws IOException
	 *             If an error occurs opening or reading the file, i.e. if it
	 *             does not exist or is corrupt.
	 */
	public HashMap<String, CurveMovement> getMovements(String fileName)
			throws IOException {
		return Sch3D.parseMovements(am.open(fileName));
	}
	
	public void setObjectData(String filename) throws IOException {
		data = Sch3D.parseMesh(am.open(filename));
	}
	
	public void setObjectData(int resId) throws IOException {
		data = Sch3D.parseMesh(res.openRawResource(resId));
	}
	
	public void setSkeleton(String filename) throws IOException {
		if (filename != null) {
			skeleton = Sch3D.parseSkeleton(this, am.open(filename), "@a:" + filename);
		} else {
			skeleton = null;
		}
	}
	
	public void setMatrixSource(GameObject obj) {
		if (obj != null) {
			data.matrix = obj.modelMatrix;
		} else {
			data.matrix = null;
		}
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public GameObject bakeGameObject() {
		if (skeleton != null && data.boneIndices != null) {
			BonedObject object = new BonedObject(data, material, skeleton);
			material.makeProgram();
			return object;
		} else {
			GameObject object = new GameObject(data, material);
			material.makeProgram();
			return object;
		}
	}
	
	public HashMap<String, Action> getActions() {
		return actions;
	}
	
	public void clear() {
		data = null;
		material = null;
		actions = null;
		skeleton = null;
	}
	
	/**
	 * Closes the GameFactory
	 */
	public void close() {
		clear();
	}
	
}
