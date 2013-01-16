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

public enum JankCatcher {
	INSTANCE;
	
	private static final String TAG = JankCatcher.class.getSimpleName();
	
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
