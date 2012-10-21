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

package com.supermercerbros.gameengine.util;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedRunnable implements Delayed {
	/**
	 * The Runnable wrapped in this DelayedRunnable
	 */
	public Runnable r;
	private long endTime;
	private TimeUnit tu = TimeUnit.NANOSECONDS;

	/**
	 * @param r
	 *            The Runnable to be wrapped
	 * @param delay
	 *            The delay in nanoseconds.
	 */
	public DelayedRunnable(Runnable r, long delay) {
		endTime = System.nanoTime() + delay;
		this.r = r;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long delay = endTime - System.nanoTime();
		return unit.convert(delay, TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed another) {
		if (another.getDelay(tu) < (endTime - System.nanoTime()))
			return 1;
		else if (another.getDelay(tu) > (endTime - System.nanoTime()))
			return -1;
		else
			return 0;

	}

}
