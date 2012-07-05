package com.supermercerbros.gameengine.debug;

import android.util.Log;

public class LoopLog {
	private static final String TAG = LoopLog.class.getSimpleName();
	private static final int CALLS_TO_LOG = 100;
	private static LoopLog instance;
	
	private synchronized static LoopLog instance() {
		if (instance == null) {
			instance = new LoopLog();
		}
		return instance;
	}
	
	private int callCount = 0;
	
	public static synchronized void e(String tag, String msg) {
		instance().logE(tag, msg);
	}
	
	public static synchronized void d(String tag, String msg) {
		instance().logD(tag, msg);
	}
	
	public static synchronized void w(String tag, String msg) {
		instance().logW(tag, msg);
	}
	
	public static synchronized void i(String tag, String msg) {
		instance().logI(tag, msg);
	}
	
	public synchronized void logE(String tag, String msg) {
		if (callCount < CALLS_TO_LOG) {
			Log.e(tag, msg);
			callCount++;
			if (callCount == CALLS_TO_LOG) {
				Log.v(TAG, "No more calls will be logged.");
			}
		}
	}
	
	public synchronized void logD(String tag, String msg) {
		if (callCount < CALLS_TO_LOG) {
			Log.d(tag, msg);
			callCount++;
			if (callCount == CALLS_TO_LOG) {
				Log.v(TAG, "No more calls will be logged.");
			}
		}
	}
	
	public synchronized void logI(String tag, String msg) {
		if (callCount < CALLS_TO_LOG) {
			Log.i(tag, msg);
			callCount++;
			if (callCount == CALLS_TO_LOG) {
				Log.v(TAG, "No more calls will be logged.");
			}
		}
	}
	
	public synchronized void logW(String tag, String msg) {
		if (callCount < CALLS_TO_LOG) {
			Log.w(tag, msg);
			callCount++;
			if (callCount == CALLS_TO_LOG) {
				Log.v(TAG, "No more calls will be logged.");
			}
		}
	}
}
