package com.supermercerbros.gameengine.collision;

import java.util.List;

import com.supermercerbros.gameengine.util.LoopingThread;

public class CollisionDetector extends LoopingThread {
	private List<Collidable> objects;
	private OnCollisionCheckFinishedListener listener;

	public CollisionDetector(List<Collidable> objects,
			OnCollisionCheckFinishedListener listener) {
		super();
		this.objects = objects;
		this.listener = listener;
		setIntermittent(true);
	}

	@Override
	protected void loop() {
		// TODO Finish CollisionDetector.loop()
		
		for (int i = 0; i < objects.size(); i++) {
			for (int j = i + 1; j < objects.size(); j++) {
				check(objects.get(i), objects.get(j));
			}
		}
		
		listener.onCollisionCheckFinished();
	}

	private void check(Collidable a, Collidable b) {
		
		
	}

	/**
	 * Call this method to run the CollisionDetector. Can be called repeatedly.
	 * Does nothing if the CollisionDetector is alive and not paused.
	 */
	public void go() {
		if (started) {
			super.resumeLooping();
		} else {
			super.start();
		}
	}

	/**
	 * @deprecated Use {@link #go()} instead.
	 */
	@Override
	public void resumeLooping() {
		throw new UnsupportedOperationException("Use go() instead.");
	}

	/**
	 * @deprecated Use {@link #go()} instead.
	 */
	@Override
	public void start() {
		throw new UnsupportedOperationException("Use go() instead.");
	}

}
