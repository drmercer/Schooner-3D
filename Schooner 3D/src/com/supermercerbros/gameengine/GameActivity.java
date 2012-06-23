package com.supermercerbros.gameengine;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.DataPipe;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.GameRenderer;

/**
 * An Activity that handles much of setting up the Engine and Renderer. At the
 * end of {@link #onCreate(Bundle)}, subclasses should call
 * {@link #start(float, float)} like so:
 * 
 * <pre>
 * public void onCreate(Bundle savedInstanceState) {
 *     super.onCreate(savedInstanceState);
 *     
 *     ... // Set Camera, load objects, textures, etc.
 *     
 *     start(NEAR_CLIP_DISTANCE, FAR_CLIP_DISTANCE);
 * }
 */
public abstract class GameActivity extends Activity implements OnTouchListener {
	private static final String TAG = "com.supermercerbros.gameengine.GameActivity";
	private GLSurfaceView gameView;
	private GameRenderer renderer;
	private GameLayout content;
	private DataPipe pipe;
	private Camera cam;
	private Engine engine;
	private boolean created = false;

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

		gameView = new GLSurfaceView(this);
		gameView.setEGLContextClientVersion(2);

		gameView.setOnTouchListener(this);
	}

	/**
	 * Sets the content to be displayed. The layout given is superimposed over
	 * the game.
	 * 
	 * @param layoutResID
	 *            The resource ID of the layout to display over the game.
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(int layoutResID) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(getLayoutInflater().inflate(layoutResID, null), params);
	}

	/**
	 * Sets the content to be displayed. The layout given is superimposed over
	 * the game.
	 * 
	 * @param v
	 *            The view to display over the game.
	 * 
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(View v) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(v, params);
	}

	/**
	 * Sets the content to be displayed. The layout given is superimposed over
	 * the game.
	 * 
	 * @param v
	 *            The view to display over the game.
	 * @param params
	 *            The ViewGroup params to use.
	 * 
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(View v, ViewGroup.LayoutParams params) {
		if (content == null) {
			content = new GameLayout(this, gameView);
		} else {
			content.removeAllViews();
		}
		content.addView(v, params);
		super.setContentView(content);
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
	 * @param near
	 *            The near clipping distance.
	 * @param far
	 *            The far clipping distance.
	 */
	protected void start(float near, float far) {
		if (!created) {
			throw new IllegalStateException(
					"GameActivity subclass must call super.onCreate() first!");
		}
		Log.d(TAG, "GameActivity Start!");
		engine.start();
		renderer = new GameRenderer(pipe, near, far);
		gameView.setRenderer(renderer);
		super.setContentView(gameView); // TODO change to super.setContentView(content);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		if (gameView != null) {
			gameView.onPause();
		} else {
			Log.w(TAG, "pausing while gameView is null!");
		}
		engine.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (engine.isAlive()) {
			engine.resumeLooping();
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
	 *            The RGB color-int to use on the render background.
	 */
	public static void setBackgroundColor(int color) {
		Schooner3D.backgroundColor[0] = (float) Color.red(color) / 256;
		Schooner3D.backgroundColor[1] = (float) Color.green(color) / 256;
		Schooner3D.backgroundColor[2] = (float) Color.blue(color) / 256;
		Schooner3D.backgroundColor[3] = (float) Color.alpha(color) / 256;
	}

	@Override
	public final boolean onTouch(View v, MotionEvent e) {
		if (v == gameView) {
			return onTouch(e);
		} else {
			return false;
		}
	}

	/**
	 * Called when the background (the rendered scene) receives a touch event.
	 * 
	 * @param e
	 *            The MotionEvent received.
	 * @return True if the handling has consumed the event, false if otherwise.
	 */
	protected abstract boolean onTouch(MotionEvent e);
}
