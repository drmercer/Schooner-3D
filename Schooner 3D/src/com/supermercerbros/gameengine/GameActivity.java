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

package com.supermercerbros.gameengine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.DataPipe;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.hud.GameHud;

/**
 * An Activity that handles much of setting up the Engine and Renderer. At the
 * end of {@link #onCreate(Bundle)}, subclasses should call
 * {@link #setClipDistances(float, float)} and {@link #start()} like so:
 * 
 * <pre>
 * public void onCreate(Bundle savedInstanceState) {
 *     super.onCreate(savedInstanceState);
 *     
 *     ... // Set Camera, load objects, textures, etc.
 *     
 *     setClipDistances(NEAR_CLIP_DISTANCE, FAR_CLIP_DISTANCE);
 *     
 *     ... // Set GameHud if desired
 *     
 *     start();
 * }
 * </pre>
 */
public abstract class GameActivity extends Activity {
	private static final String TAG = "com.supermercerbros.gameengine.GameActivity";

	private GameView gameView;
	private GameRenderer renderer;
	private DataPipe pipe;
	private Camera cam;
	private Engine engine;
	private boolean created = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.setBackgroundDrawable(null);

		created = true;
		Log.d(TAG, "onCreate");

		pipe = new DataPipe(this);
		cam = new Camera();
		engine = new Engine(pipe, cam);

		gameView = new GameView(this);
	}

	/**
	 * Returns the Engine being used by this GameActivity.
	 * 
	 * @return The Engine used by this GameActivity.
	 */
	protected Engine getEngine() {
		if (!created) {
			throw new IllegalStateException(
					"GameActivity subclass must call super.onCreate() first!");
		}
		return engine;
	}

	/**
	 * Returns the Camera being used by this GameActivity.
	 * 
	 * @return The Camera used by this GameActivity.
	 */
	protected Camera getCamera() {
		if (!created) {
			throw new IllegalStateException(
					"GameActivity subclass must call super.onCreate() first!");
		}
		return cam;
	}

	/**
	 * Subclasses must call this at the end of their onCreate() call.
	 * 
	 * @deprecated Use {@link #setClipDistances(float, float)} and
	 *             {@link #start()} instead.
	 * 
	 * @param near
	 *            The near clipping distance.
	 * @param far
	 *            The far clipping distance.
	 */
	protected void start(float near, float far) {
		// TODO remove for first release version
		setClipDistances(near, far);
		start();
	}

	protected void start() {
		if (!created) {
			throw new IllegalStateException(
					"GameActivity subclass must call super.onCreate() first");
		} else if (renderer == null) {
			throw new IllegalStateException(
					"GameActivity subclass must call setClipDistances() first");
		}
		Log.d(TAG, "GameActivity Start!");
		gameView.setRenderer(renderer);
		setContentView(gameView);
		engine.start();
	}

	/**
	 * Sets the near and far clipping distances. Calling this method initializes
	 * the renderer.
	 * 
	 * @param near
	 *            The near clipping distance.
	 * @param far
	 *            The far clipping distance.
	 */
	protected void setClipDistances(float near, float far) {
		renderer = new GameRenderer(pipe, near, far);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		pauseGame();
	}

	/**
	 * Call this to pause the game (this is called by GameActivity during
	 * {@link #onPause()}).
	 */
	protected void pauseGame() {
		if (gameView != null) {
			// Pause the GameView if it exists
			gameView.onPause();
		} else {
			Log.w(TAG, "pausing while gameView is null!");
		}
		// Pause the Engine
		engine.pause();
	}

	/**
	 * Call this to resume the paused game.
	 */
	protected void resumeGame() {
		// Resume the Engine
		engine.resumeLooping();
		if (gameView != null) {
			// Resume the GameView if it exists
			gameView.onResume();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		engine.end();

		if (engine.isAlive()) {
			Log.w(TAG, "Engine is still alive!");
		} else {
			Log.i(TAG, "Engine is dead.");
			engine = null;
		}
		pipe.close();
		pipe = null;
	}

	/**
	 * Sets the GameHud to superimpose over the game.
	 * 
	 * @param hud
	 *            The GameHud to use.
	 */
	protected void setHud(GameHud hud) {
		renderer.setHud(hud);
		gameView.setHud(hud);
		int width = getWidth();
		int height = getHeight();
		Log.d("GameActivity", "(" + width + ", " + height + ")");
		hud.setDimensions(width, height);
	}

	/**
	 * Sets the render background color.
	 * 
	 * @param color
	 *            The RGB color-int to use on the render background.
	 */
	public static void setBackgroundColor(int color) {
		Schooner3D.backgroundColor[0] = (float) Color.red(color) / 256;
		Schooner3D.backgroundColor[1] = (float) Color.green(color) / 256;
		Schooner3D.backgroundColor[2] = (float) Color.blue(color) / 256;
		Schooner3D.backgroundColor[3] = (float) Color.alpha(color) / 256;
	}

	/**
	 * Utility function to get the width of the display.
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public int getWidth() {
		final Display display = getWindowManager().getDefaultDisplay();
		int width;
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			// Only available in API 13+
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			// Deprecated in API 13+
			width = display.getWidth();
		}
		return width;
	}

	/**
	 * Utility function to get the height of the display.
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public int getHeight() {
		final Display display = getWindowManager().getDefaultDisplay();
		int height;
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			// Only available in API 13+
			Point size = new Point();
			display.getSize(size);
			height = size.y;
		} else {
			// Deprecated in API 13+
			height = display.getHeight();
		}
		return height;
	}

}
