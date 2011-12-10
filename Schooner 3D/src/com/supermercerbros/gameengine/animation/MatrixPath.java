package com.supermercerbros.gameengine.animation;

import android.opengl.Matrix;

import com.supermercerbros.gameengine.util.IPO;
import com.supermercerbros.gameengine.util.Log;

public class MatrixPath implements Movement {
	private static final String TAG = null;

	private float[] keyframes;
	private float[] relativeMatrix;
	
	private long startTime;
	private long[] times;
	private float speed; 
	private long lastTime;
	private boolean loop;
	private int currentKeyframe = 0;
	private int numOfKeyframes;
	private boolean finished;
	
	/**
	 * Creates a new MatrixPath.
	 * 
	 * @param keyMatrices The keyframes of this movement. The length of this array is equal to the 
	 * number of matrices * 16.
	 * @param times The times that each of the keyframes should happen at. Since keyframe 0 starts 
	 * at 0 milliseconds on the first loop, <code>times[0]</code> should contain the time at which 
	 * keyframe 0 would happen when the movement loops back to the beginning. Note that this first 
	 * element is unimportant if the movement is non-looping.
	 * @param loop true if the movement should loop back to the beginning upon reaching the 
	 * last keyframe.
	 * 
	 * @throws IllegalArgumentException if keyframes.length is not a multiple of three.
	 */
	public MatrixPath(float[] keyMatrices, long[] times, boolean loop){
		this.times = times;
		this.keyframes = keyMatrices;
		this.loop = loop;
		
		this.numOfKeyframes = keyMatrices.length / 16;
	}

	@Override
	public void getFrame(float[] matrix, int mOffset, long frameTime) {
		if (finished){
			System.arraycopy(keyframes, getKeyframeOffset(numOfKeyframes-1), matrix, 0, 16);
			return;
		}
		float framePoint;
		try {
			framePoint = getFramePoint(frameTime);
		} catch (EndOfAnimation e) {
			finished = true;
			System.arraycopy(keyframes, getKeyframeOffset(numOfKeyframes-1), matrix, 0, 16);
			return;
		}
		IPO.matrix(matrix, 0, keyframes, getKeyframeOffset(currentKeyframe), keyframes, getKeyframeOffset(currentKeyframe+1), framePoint);
		Matrix.multiplyMM(matrix, 0, relativeMatrix, 0, matrix, 0);
	}

	/**
	 * @param frameTime the time of the frame that is currently being calculated
	 * @return the current location between currentKeyframe and currentKeyframe+1
	 * @throws EndOfAnimation if the Movement is non-looping and has reached the last keyframe.
	 */
	private float getFramePoint(long frameTime) throws EndOfAnimation{
		long preSpeedTime = frameTime - startTime;
		long time = (long) ((preSpeedTime - lastTime) * getSpeed()) + lastTime;
		startTime += preSpeedTime - time;
		
		Log.d(TAG, "getFramePoint.time = " + time);
		float framePoint; //This value holds the current position between keyframes.
		
		Log.d(TAG, "times[times.length-1] = " + times[times.length-1]);
		while (time > times[times.length-1]) {
			Log.d(TAG, "time > times[times.length-1]");
			if (loop){
				this.start(startTime + times[times.length-1], relativeMatrix, getSpeed());
			}else{
				throw new EndOfAnimation();
			}
			time = frameTime - startTime;
			Log.d(TAG, "time = " + time);
		}
		
		while (time > getKeyframeTime(currentKeyframe +1)){
			currentKeyframe++;
			if (currentKeyframe > numOfKeyframes) currentKeyframe = 0;
			Log.d(TAG, "Suspect looped. currentKeyframe = " + currentKeyframe);
		}
		Log.d(TAG, "loop exited! ");
		
		lastTime = time;
		
		framePoint = (float) (time - getKeyframeTime(currentKeyframe)) / (getKeyframeTime(currentKeyframe+1) - getKeyframeTime(currentKeyframe));
		return framePoint;
	}

	public int getKeyframeOffset(int i){
		return 16*i;
	}

	public long getKeyframeTime(int i){
		long result;
		if (i <= 0) {
			result = 0;
		} else {
			result = times[i];
		}
		Log.d(TAG, "getKeyframeTime(" + i + ") returns ");
		Log.d(TAG, "" + result);
		return result;
	}

	@Override
	public float getSpeed() {
		return speed;
	}
	
	@Override
	public void lag(long lagTime) {
		startTime += lagTime;

	}
	
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;

	}
	
	@Override
	public void start(long time, float[] relativeMatrix, float speed) {
		this.startTime = time;
		this.relativeMatrix = relativeMatrix;
		this.speed = speed;
	}

}
