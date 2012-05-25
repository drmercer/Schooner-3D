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
