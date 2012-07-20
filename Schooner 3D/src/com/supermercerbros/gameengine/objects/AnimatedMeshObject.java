package com.supermercerbros.gameengine.objects;

import android.util.Log;

import com.supermercerbros.gameengine.animation.AnimationData;
import com.supermercerbros.gameengine.animation.MeshAnimation;
import com.supermercerbros.gameengine.engine.Normals;
import com.supermercerbros.gameengine.engine.shaders.Material;

/**
 * Represents an animated 3D mesh object.
 */
public class AnimatedMeshObject extends GameObject {
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject";

	private MeshAnimation anim;
	private AnimationData data;

	/**
	 * Contains {@link MeshAnimation}s associated with this GameObject. The
	 * GameObject itself doesn't do anything with them; this is merely for
	 * transportation to the client.
	 */
	private MeshAnimation[] anims;

	public AnimatedMeshObject(float[] verts, short[] indices, float[] uvs,
			float[] normals, Material mtl, short[][] doubles) {
		super(verts, indices, normals, uvs, doubles, mtl);
		data = new AnimationData();
	}

	@Override
	public void drawVerts(long time) {
		Log.d(TAG, "AnimatedMeshObject.drawVerts(" + time + ") was called.");
		if (anim != null) {
			anim.getFrame(time, data, this);
			Normals.calculate(this);
		}
		super.drawVerts(time);

	}

	public void setAnimation(MeshAnimation anim, long startTime, long duration,
			int loop) {
		this.anim = anim;

		this.data.setDuration(duration);
		this.data.setStartTime(startTime);
		this.data.setLoop(loop);

		this.data.setInitialState(verts);
		this.data.setCallTime(System.currentTimeMillis());
	}

	public void clearAnimation() {
		this.anim = null;
	}

	/**
	 * Attaches the given {@link MeshAnimation}s to this AnimatedMeshObject.
	 * These animations are not used by the GameObject in any way - this is
	 * used solely for transportation to the client.
	 * 
	 * @param anims The animations to attach.
	 */
	public void attachAnims(MeshAnimation[] anims) {
		this.anims = anims;
	}

	/**
	 * Gets the {@link MeshAnimation}s attached to this AnimatedMeshObject.
	 * These animations are not used by the GameObject in any way - this is
	 * used solely for transportation to the client.
	 * 
	 * @return The {@link MeshAnimation}s attached to this AnimatedMeshObject.
	 */
	public MeshAnimation[] getAnims() {
		return this.anims;
	}
}
