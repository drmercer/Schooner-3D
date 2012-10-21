/*
 * Copyright 2012 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
