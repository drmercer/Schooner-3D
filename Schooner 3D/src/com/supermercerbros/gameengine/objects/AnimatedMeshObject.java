package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.animation.AnimationData;
import com.supermercerbros.gameengine.animation.MeshAnimation;
import android.util.Log;

/**
 * Represents an animated 3D mesh object.
 */
public class AnimatedMeshObject extends GameObject{
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject";

	private MeshAnimation anim;
	private AnimationData data;

	public AnimatedMeshObject(float[] verts, short[] indices, float[] uvs,
			float[] normals, Material mtl) {
		super(verts, indices, uvs, normals, mtl);
		data = new AnimationData();
	}

	@Override
	public void draw(long time) {
		Log.d(TAG, "AnimatedMeshObject.draw(" + time + ") was called.");
		if (anim != null) {
			anim.getFrame(time, data, this);
		}
		super.draw(time);

	}
	
	public void setAnimation(MeshAnimation anim, long startTime, long duration, int loop){
		this.anim = anim;
		
		this.data.setDuration(duration);
		this.data.setStartTime(startTime);
		this.data.setLoop(loop);
		
		this.data.setInitialState(verts);
		this.data.setCallTime(System.currentTimeMillis());
	}
	
	public void clearAnimation(){
		this.anim = null;
	}
}
