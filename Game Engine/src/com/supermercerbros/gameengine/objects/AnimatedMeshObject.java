package com.supermercerbros.gameengine.objects;

import com.supermercerbros.gameengine.animation.Animation;
import com.supermercerbros.gameengine.animation.MeshAnimation;
import com.supermercerbros.gameengine.util.Log;

/**
 * Represents an animated 3D mesh object.
 */
public class AnimatedMeshObject extends GameObject implements AnimatedObject {
	private static final String TAG = "com.supermercerbros.gameengine.objects.GameObject";
	
	/**
	 * Contains the Animations that this object can perform.
	 */
	protected MeshAnimation[] anims;
	/**
	 * Contains the index of the current <code>MeshAnimation</code>.
	 */
	protected int currentAnim = 0;
	
	private int animState = Animation.STOPPED;

	/**
	 * Creates a new AnimatedMeshObject with the given animations, indices, UV
	 * coordinates, and normals.
	 * 
	 * @param anims
	 *            Contains the animations that this object can do.
	 * @param animNames
	 *            Contains the names of the Animations in <code>anims</code>
	 * @param initialAnim
	 *            Contains the index of the animation who's first keyframe is
	 *            used as the object's initial shape.
	 * @param indices
	 * @param mtl
	 * @param normals
	 */
	public AnimatedMeshObject(MeshAnimation[] anims, int initialAnim,
			short[] indices, float[] uvs, float[] normals, Material material) {
		super(anims[initialAnim].getKeyframe(0), indices, uvs, normals, material);
		this.anims = anims.clone();
		this.currentAnim = initialAnim;
	}

	@Override
	public void draw(long time) {
		Log.d(TAG, "AnimatedMeshObject.draw(" + time + ") was called.");
		switch (animState) {
		case Animation.RUNNING:
			this.verts = anims[currentAnim].getFrame(time);
			break;

		case Animation.PAUSED:
			long timePassed = time - this.getLastDrawTime();
			anims[currentAnim].lag(timePassed);
			break;
		}
		super.draw(time);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.supermercerbros.gameengine.objects.AnimatedObject#start(int,
	 * long, float, long)
	 */
	public boolean start(int index, long delay, float speed, long currentTime) {
		if (index > anims.length - 1)
			return false;
		currentAnim = index;
		anims[index].start(currentTime, delay, this.verts, speed);
		animState = Animation.RUNNING;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.supermercerbros.gameengine.objects.AnimatedObject#setSpeed(float)
	 */
	public void setSpeed(float speed) {
		anims[currentAnim].setSpeed(speed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.supermercerbros.gameengine.objects.AnimatedObject#getSpeed()
	 */
	public float getSpeed() {
		return anims[currentAnim].getSpeed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.supermercerbros.gameengine.objects.AnimatedObject#stop()
	 */
	public void stop() {
		animState = Animation.STOPPED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.supermercerbros.gameengine.objects.AnimatedObject#pause()
	 */
	public void pause() {
		animState = Animation.PAUSED;
	}

	/**
	 * @return a clone of the array containing the {@link MeshAnimation}s of
	 *         this object.
	 */
	public MeshAnimation[] getAnims() {
		return anims.clone();
	}

	/**
	 * @return the index of the current MeshAnimation.
	 */
	public int getCurrentAnim() {
		return currentAnim;
	}

	/**
	 * @return the current state of this object's animation:
	 *         {@link MeshAnimation#STOPPED}, {@link MeshAnimation#PAUSED}, or
	 *         {@link MeshAnimation#RUNNING}.
	 */
	public int getAnimState() {
		return animState;
	}

}
