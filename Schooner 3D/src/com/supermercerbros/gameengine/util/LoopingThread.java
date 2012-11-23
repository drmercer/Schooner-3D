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

/**
 * This is a Thread subclass that provides looping functionality. The
 * {@link #loop()} method is called repeatedly until {@link #end()} is called. A
 * LoopingThread can also be paused with {@link #pause()} and
 * {@link #resumeLooping()}.
 */
public abstract class LoopingThread extends Thread {
	private volatile Toggle paused = new Toggle(false);
	protected volatile boolean started = false;
	private volatile boolean ending = false;
	private boolean intermittent;

	// Constructors (these just go through to Thread's constructors):

	public LoopingThread() {
		super();
	}
	public LoopingThread(Runnable runnable) {
		super(runnable);
	}
	public LoopingThread(String threadName) {
		super(threadName);
	}
	public LoopingThread(Runnable runnable, String threadName) {
		super(runnable, threadName);
	}
	public LoopingThread(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
	}
	public LoopingThread(ThreadGroup group, String threadName) {
		super(group, threadName);
	}
	public LoopingThread(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
	}
	public LoopingThread(ThreadGroup group, Runnable runnable,
			String threadName, long stackSize) {
		super(group, runnable, threadName, stackSize);
	}

	// Methods:

	/**
	 * Terminates the LoopingThread.
	 */
	public void end() {
		if (started) {
			ending = true;
			interrupt();
		}
	}

	/**
	 * @return true if {@link #end()} has been called.
	 */
	public boolean isEnding() {
		return ending;
	}

	/**
	 * Tells the LoopingThread to pause processing. Used with
	 * {@link #resumeLooping()}.
	 */
	public void pause() {
		synchronized (paused) {
			paused.setState(true);
		}
	}

	/**
	 * Tells the LoopingThread to resume processing.
	 */
	public void resumeLooping() {
		synchronized (paused) {
			paused.setState(false);
			paused.notify();
		}
	}

	/**
	 * Do not call this method. Call {@link #start()} to start the
	 * LoopingThread.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (Thread.currentThread() != this) {
			throw new UnsupportedOperationException(
					"Do not call LoopingThread.run()");
		}
		while (!ending) {
			loop();
			if (intermittent) {
				pause();
			}

			if (ending) {
				break;
			}
			final boolean isPaused = paused.getState();
			if (isPaused && !ending) {
				onPause();
				waitOnToggle(paused, false);
				onResume();
			}
		}

		// Thread ends

	}

	/**
	 * Waits for the given {@link Toggle} to have the desired state. Breaks out
	 * of the wait if {@link #end()} is called.
	 */
	protected void waitOnToggle(final Toggle t, final boolean desired) {
		synchronized (t) {
			while (t.getState() != desired) {
				try {
					t.wait();
				} catch (InterruptedException e) {
					if (ending) {
						break;
					}
				}
			}
		}
	}

	/**
	 * Waits for the given amount of time
	 * 
	 * @param millis
	 */
	protected void waitForTime(long millis) {
		final long destinationTime = System.currentTimeMillis() + millis;
		synchronized (this) {
			while (System.currentTimeMillis() < destinationTime) {
				try {
					this.wait(destinationTime - System.currentTimeMillis());
				} catch (InterruptedException e) {
					if (ending) {
						break;
					}
				}
			}
		}
	}

	/**
	 * This method is called when the Thread pauses if it is intermittent.
	 * Override it to do something at that point.
	 */
	protected void onPause() {
		// This is overridden by subclasses
	}
	
	/**
	 * This method is called when the Thread resumes after being paused.
	 * Override it to do something at that point.
	 */
	protected void onResume() {
		// This is overridden by subclasses
	}

	/**
	 * Use this method (<b>not</b> {@link #run()}) to start the LoopingThread.
	 */
	@Override
	public void start() {
		started = true;
		super.start();
	}

	/**
	 * If true, sets this LoopingThread to be intermittent; in other words, it
	 * {@link #pause()}s itself after every call to {@link #loop()}.
	 * 
	 * @param a
	 *            True if this LoopingThread should be intermittent.
	 */
	protected void setIntermittent(boolean a) {
		intermittent = a;
	}

	/**
	 * @return true if this LoopingThread has been started.
	 */
	public boolean hasBeenStarted() {
		return started;
	}

	/**
	 * This method is called repeatedly by {@link #run()} until the Thread is
	 * paused or ended.
	 */
	protected abstract void loop();
}
