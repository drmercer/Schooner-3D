package com.supermercerbros.gameengine.debug;

import android.util.Log;

public class JankCatcher {
	private static final String TAG = JankCatcher.class.getSimpleName();
	
	private static JankCatcher instance;
	
	public synchronized static JankCatcher instance() {
		if (instance == null) {
			instance = new JankCatcher();
		}
		return instance;
	}
	
	private int updating = -1;
	private int rendering = -1;
	
	public synchronized void onBeginUpdate(int index) {
		updating = index;
		if (updating == rendering) {
			LoopLog.e(TAG, "onBeginUpdate(" + index + ") - JANK!");
		} else {
			LoopLog.d(TAG, "onBeginUpdate(" + index + ")");
		}
	}
	
	public synchronized void onFinishUpdate(int index) {
		LoopLog.d(TAG, "onFinishUpdate(" + index + ")");
		if (updating != index) {
			Log.wtf(TAG, "\'Finished\' updating RD " + index + ", but was updating RD " + updating);
		}
		updating = -1;
	}
	
	public synchronized void onBeginRender(int index) {
		rendering = index;
		if (rendering == updating) {
			LoopLog.e(TAG, "onBeginRender(" + index + ") - JANK!");
		} else {
			LoopLog.d(TAG, "onBeginRender(" + index + ")");
		}
	}
	
	public synchronized void onFinishRender(int index) {
		LoopLog.d(TAG, "onFinishRender(" + index + ")");
		if (rendering != index) {
			Log.wtf(TAG, "\'Finished\' rendering RD " + index + ", but was rendering RD " + updating);
		}
		rendering = -1;
	}
}
