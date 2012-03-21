package com.supermercerbros.gameengine.util;

import android.util.Log;

import com.supermercerbros.gameengine.util.Toggle;

public abstract class LoopingThread extends Thread {
	private static final String TAG = "LoopingThread";
	private volatile Toggle paused = new Toggle(false);
	protected volatile boolean started = false;
	private volatile boolean ending = false;
	private boolean intermittent;

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

	/**
	 * Terminates the LoopingThread.
	 */
	public void end() {
		Log.d(TAG, "LoopingThread state before end():" + getState().toString());
		ending = true;
		interrupt();
		Log.d(TAG, "LoopingThread state after end():" + getState().toString());

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
	public synchronized void run() {
		if (Thread.currentThread() != this) {
			throw new UnsupportedOperationException(
					"Do not call LoopingThread.run()");
		}
		while (!ending) {
			loop();

			if (ending) {
				break;
			}
			if (intermittent) {
				pause();
			}
			synchronized (paused) {
				while (paused.getState()) {
					try {
						Log.d(TAG, "Waiting to unpause...");
						paused.wait();
					} catch (InterruptedException e) {
						Log.w(TAG, "Interrupted while waiting to unpause.");
						if (ending) {
							break;
						}
					}
				}
			}
		}

		Log.d(TAG, "end LoopingThread");
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
	 * This method is called repeatedly by {@link #run()} until the Thread is
	 * paused or ended.
	 */
	protected abstract void loop();

}