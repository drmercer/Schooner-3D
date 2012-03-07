package com.supermercerbros.gameengine;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.DataPipe;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.GameRenderer;

public class GameActivity extends Activity {
	private static final String TAG = "com.supermercerbros.gameengine.GameActivity";
	private GLSurfaceView gameView;
	private GameLayout content;
	private DataPipe pipe;
	private Camera cam;
	private Engine engine;
	private boolean started = false, created = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		created = true;
		Log.d(TAG, "onCreate");

		pipe = new DataPipe(this);
		cam = new Camera();
		engine = new Engine(pipe, cam);
	}

	@Override
	public void setContentView(int layoutResID) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(getLayoutInflater().inflate(layoutResID, null), params);
	}

	@Override
	public void setContentView(View v) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(v, params);
	}

	@Override
	public void setContentView(View v, ViewGroup.LayoutParams params) {
		if (content == null) {
			content = new GameLayout(this);
		}
		super.setContentView(content);
		content.setGameView(gameView);
		content.addView(v, params);
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
	 * Subclasses must call this at the end of their onCreate() call
	 */
	protected void start(float near, float far) {
		if (!created) {
			throw new IllegalStateException(
					"GameActivity subclass must call super.onCreate() first!");
		}
		Log.d(TAG, "GameActivity Start!");
		started = true;
		engine.start();

		gameView = new GLSurfaceView(this);
		gameView.setEGLContextClientVersion(2);
		gameView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
				| GLSurfaceView.DEBUG_LOG_GL_CALLS);
		gameView.setRenderer(new GameRenderer(pipe, near, far));
		super.setContentView(gameView);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		engine.pause();
		if (gameView != null) {
			gameView.onPause();
		} else {
			Log.w(TAG, "pausing while gameView is null!");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (engine.isAlive()) {
			engine.resumeEngine();
		}
		if (gameView != null) {
			gameView.onResume();
		}
		Log.d(TAG, "finished resuming");
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
	 * Sets the render background color.
	 * 
	 * @param color
	 *            The RGB color to use as the render background.
	 */
	public static void setBackgroundColor(float... color) {
		System.arraycopy(color, 0, Schooner3D.backgroundColor, 0,
				(color.length < 4) ? color.length : 4);
	}
}