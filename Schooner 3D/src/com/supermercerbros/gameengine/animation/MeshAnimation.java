package com.supermercerbros.gameengine.animation;

import java.util.List;

import com.supermercerbros.gameengine.objects.AnimatedMeshObject;
import com.supermercerbros.gameengine.util.IPO;
import android.util.Log;

/**
 * Contains the data of one animation of an {@link AnimatedMeshObject}.
 */
public class MeshAnimation {
	private static final String TAG = "com.supermercerbros.gameengine.objects.MeshAnimation";

	public static final int STOPPED = 0;
	public static final int PAUSED = 1;
	public static final int RUNNING = 2;

	private final List<Keyframe> keyframes;
	private final int numOfKeyframes;
	private float[] times;

	/**
	 * @param keyframes
	 * @param ratios
	 */
	public MeshAnimation(List<Keyframe> keyframes, float[] ratios) {
		times = new float[ratios.length];
		float last = ratios[ratios.length - 1];
		if (last > 1.0) {
			for (int i = 0; i < ratios.length; i++) {
				times[i] = ratios[i] / last;
			}
		} else {
			this.times = ratios;
		}

		this.numOfKeyframes = keyframes.size();
		this.keyframes = keyframes;
	}

	public void getFrame(long frameTime, AnimationData data,
			AnimatedMeshObject object) { // frameTime is world time
		Log.d(TAG, "MeshAnimation.getFrame() was called.");

		double framePoint = ((float) (frameTime - data.startTime)) / (float) data.duration;
		Log.d(TAG, framePoint + " = (" + (frameTime - data.startTime) + ") / " + data.duration);

		if (framePoint < 0.0) {
			framePoint = (frameTime - data.callTime)
					/ (data.startTime - data.callTime);
			IPO.mesh(object.verts, data.initialState, keyframes.get(0).verts,
					framePoint);

		} else if (framePoint >= data.loop && data.loop > 0) {
			keyframes.get(numOfKeyframes - 1).loadTo(object.verts);
			object.clearAnimation();

		} else {
			framePoint %= 1.0;
			int nextKey, lastKey = times.length - 1;
			
			while (framePoint < times[lastKey]) {
				lastKey--;
			}
			
			if (lastKey < times.length - 1) {
				nextKey = lastKey + 1;
				framePoint = (framePoint - times[lastKey])
						/ (times[nextKey] - times[lastKey]);
			} else {
				nextKey = 0;
				framePoint = (framePoint - times[lastKey])
						/ (1 + times[0] - times[lastKey]);
			}

			IPO.mesh(object.verts, keyframes.get(lastKey).verts, keyframes
					.get(nextKey).verts, framePoint);
		}

	}

	public int getCount() {
		return keyframes.get(0).count();
	}
}
