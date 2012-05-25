package com.supermercerbros.gameengine.engine;


import android.content.Context;

import com.supermercerbros.gameengine.Schooner3D;

/**
 * Used for communication between the main thread, the Engine thread, and the
 * renderer thread.
 */
public class DataPipe {
	@SuppressWarnings("unused")
	private static final String TAG = "com.supermercerbros.gameengine.engine.DataPipe";

	final int VBO_capacity = Schooner3D.vboSize;
	final int IBO_capacity = Schooner3D.iboSize;

	private RenderData data;
	private long lastReadTime;
	private boolean isRead = false;

	/**
	 * Constructs a new DataPipe. This also initializes <code>ShaderLib</code>
	 * and <code>TextureLib</code>
	 * 
	 * @param context
	 *            The app Context
	 * @param mtl
	 *            The material to render GameObjects with.
	 */
	public DataPipe(Context context) {
		ShaderLib.init(context);
		TextureLib.init(context);
	}

	public void close() {
		TextureLib.close();
		ShaderLib.close();
		EGLContextLostHandler.clear();
	}

	/**
	 * Loads the data for the next frame into the DataPipe. Also returns the
	 * time of the frame to compute next.
	 * 
	 * @param frameTime
	 *            the time of the frame represented by the data
	 * @param data
	 *            a RenderData object containing the data to be rendered.
	 * @return The time of the next frame that the Engine should calculate
	 */
	public synchronized long putData(long frameTime, RenderData data) {
		while (!isRead) {
			try {
				wait(1000 / 30);
				if (isRead){
					break;
				} else {
					return lastReadTime + 3*(1000 / 30);
				}
			} catch (InterruptedException e) {
				return lastReadTime + 3*(1000 / 30);
			}
		}
		this.data = data;

		isRead = false;
		notify();
		return lastReadTime + 2*(1000/30);
	}

	public synchronized RenderData retrieveData() {
		while (isRead) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		lastReadTime = System.currentTimeMillis();
		isRead = true;
		notify();
		return data;
	}
}
