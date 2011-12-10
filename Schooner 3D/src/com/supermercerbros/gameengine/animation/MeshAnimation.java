package com.supermercerbros.gameengine.animation;


import com.supermercerbros.gameengine.objects.AnimatedMeshObject;
import com.supermercerbros.gameengine.util.IPO;
import com.supermercerbros.gameengine.util.Log;

/**
 * Contains the data of one animation of an {@link AnimatedMeshObject}.
 *
 */
public class MeshAnimation implements Animation {
	private static final String TAG = "com.supermercerbros.gameengine.objects.MeshAnimation";
	
	private float[] keyframes;
	private int offset;
	private float[] prevFrame;
	
	
	private float speed;
	private boolean loop;
	private boolean finished = false;
	private int currentKeyframe = 0;
	private int numOfKeyframes;
	
	// "Local time" is relative to startTime. "World time" is relative to January 1st, 1970.
	private long[] times; // Local time
	private long startTime; // World time
	private long beginAnimTime; //Local time
	private long lastTime = 0; // Local time
	
	

	/**
	 * Creates a new MeshAnimation.
	 * 
	 * @param keyframes The keyframes of this animation. The length of this array is equal to the 
	 * number of frames * the number of vertices (<code>size</code>) * 3 (float values per vertex).
	 * @param size The number of vertices in the animated mesh. 
	 * @param times The times that each of the keyframes should happen at. Since keyframe 0 starts 
	 * at 0 milliseconds on the first loop, <code>times[0]</code> should contain the time at which 
	 * keyframe 0 would happen when the animation loops back to the beginning. Note that this first 
	 * element is unimportant if the animation is non-looping.
	 * @param loop States if the animation should loop back to the beginning upon reaching the 
	 * last keyframe.
	 * 
	 * @throws IllegalArgumentException if keyframes.length is not a multiple of three.
	 */
	public MeshAnimation(float[] keyframes, int size, long[] times, boolean loop){
		if (keyframes.length % 3 != 0) throw new IllegalArgumentException("keyframes.length is not a multiple of three");
		this.offset = size*3;
		this.times = times;
		this.numOfKeyframes = keyframes.length / offset;
		this.keyframes = keyframes;
		this.loop = loop;
	}
	
	@Override
	public float[] getFrame(long frameTime){ // frameTime is world time
		Log.d(TAG, "MeshAnimation.getFrame(" + frameTime + ") was called.");
		if (finished) return getKeyframe(numOfKeyframes-1);
		float framePoint;
		try {
			framePoint = getFramePoint(frameTime);
		} catch (EndOfAnimation e) {
			return getKeyframe(numOfKeyframes-1);
		}
		float[] frame = new float[offset];
		IPO.mesh(frame, 0, offset, getKeyframe(currentKeyframe), 0, getKeyframe(currentKeyframe+1), 0, framePoint);
		return frame;
	}

	/**
	 * @param frameTime
	 * @return
	 * @throws EndOfAnimation 
	 */
	protected float getFramePoint(long frameTime) throws EndOfAnimation {
		long preSpeedTime = frameTime - startTime;
		long time = (long) ((preSpeedTime - lastTime) * getSpeed()) + lastTime;
		startTime += preSpeedTime - time;
		
		Log.d(TAG, "time = " + time);
		float framePoint; //This value holds the current position between keyframes.
		
		Log.d(TAG, "times[times.length-1] = " + times[times.length-1]);
		while (time > times[times.length-1]) {
			Log.d(TAG, "time > times[times.length-1]");
			if (loop){
				this.start(startTime + times[times.length-1], times[0] - times[times.length-1], getKeyframe(numOfKeyframes-1), getSpeed());
			}else{
				throw new EndOfAnimation();
			}
			time = frameTime - startTime;
			Log.d(TAG, "time = " + time);
		}
		
		while (time > getKeyframeTime(currentKeyframe+1)){
			currentKeyframe++;
			if (currentKeyframe > numOfKeyframes) currentKeyframe = 0;
			Log.d(TAG, "Suspect looped. currentKeyframe = " + currentKeyframe);
		}
		Log.d(TAG, "loop exited! ");
		
		lastTime = time;
		
		framePoint = (float) (time - getKeyframeTime(currentKeyframe)) / (getKeyframeTime(currentKeyframe+1) - getKeyframeTime(currentKeyframe));
		return framePoint;
	}

	public final float[] getKeyframe(int i) {
		if (i < 0) return prevFrame;
		float[] keyframe = new float[offset];
		System.arraycopy(keyframes, offset*i, keyframe, 0, offset);
		return keyframe;
	}
	
	/*package*/ final long getKeyframeTime(int i){ 
		long result;
		if (i < 0) {
			result = 0;
		} else if (i == 0) {
			result = beginAnimTime;
		} else {
			result = beginAnimTime + times[i];
		}
		Log.d(TAG, "getKeyframeTime(" + i + ") returns ");
		Log.d(TAG, "" + result);
		return result;
	}
	
	@Override
	public void start(long time, long delay, float[] currentFrame, float speed){
		startTime = time;
		beginAnimTime = delay;
		prevFrame = currentFrame.clone();
		this.speed = speed;
		currentKeyframe = -1;
		
		Log.d(TAG, "start() was called.\n" +
				"\tstartTime = " + time + "\n" + 
				"\tbeginAnimTime = " + delay + "\n" +  
				"\tspeed = " + speed);
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
	public float getSpeed() {
		return speed;
	}
	
}
