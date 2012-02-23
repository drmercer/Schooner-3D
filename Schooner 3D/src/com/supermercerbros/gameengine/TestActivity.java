package com.supermercerbros.gameengine;

import java.io.IOException;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.DataPipe;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.GameRenderer;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.objects.BasicMaterial;
import com.supermercerbros.gameengine.objects.GameObject;
import com.supermercerbros.gameengine.objects.TexturedMaterial;

public class TestActivity extends Activity {
	private static final String TAG = "com.supermercerbros.gameengine.TestActivity";
	private GLSurfaceView view;
	private DataPipe pipe;
	private Camera cam;
	private Engine engine;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		float[] bg = { .5f, .5f, .5f, 0.0f };
		Schooner3D.backgroundColor = bg;

		pipe = new DataPipe(this);

		String texName = "";
		try {
			texName = TextureLib.loadTexture(R.drawable.test_texture2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cam = new Camera(1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
		// cam = new Camera();
		if (engine != null) {
			Log.w(TAG, "Engine is not null...");
			if (engine.isAlive()) {
				Log.w(TAG, "and Engine is still alive!");
			}
		} else {
			engine = new Engine(pipe, cam);
			engine.addObject(cube(texName));
			engine.setLight(0.0f, 0.0f, -1.0f, 1.0f, 1.0f, 1.0f);
			engine.start();
		}

		view = new GameView(this);

		view.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
				| GLSurfaceView.DEBUG_LOG_GL_CALLS);
		setContentView(view);
		view.setRenderer(new GameRenderer(pipe));
	}

	private GameObject quad(String texName) {
		float depth = -2.84f;
		float[] verts = { .5f, .5f, depth, .5f, -.5f, depth, -.5f, .5f, depth,
				-.5f, -.5f, depth, };
		float[] uvs = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };

		short[] indices = { 0, 1, 2, 1, 2, 3 };
		float[] normals = {};

		GameObject obj = new GameObject(verts, indices, uvs, normals,
				new TexturedMaterial(texName));
		return obj;
	}

	private GameObject tri() {
		float[] verts = { 0.0f, 0.0f, 0.0f, -0.6f, -0.6f, 0.0f, -0.6f, 0.6f,
				0.0f };
		float[] colors = { 1, 1, 0, 0, 1, 1, 1, 0, 1 };
		short[] indices = { 0, 1, 2 };
		float[] normals = {};

		GameObject tri = new GameObject(verts, indices, colors, normals,
				new BasicMaterial());
		return tri;
	}

	private GameObject cube(String texName) {
		float[] verts = { .5f, .5f, .5f, .5f, -.5f, .5f, -.5f, .5f, .5f, -.5f,
				-.5f, .5f, .5f, .5f, -.5f, .5f, -.5f, -.5f, -.5f, .5f, -.5f,
				-.5f, -.5f, -.5f, };
		float[] uvs = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, };
		float[] colors = { 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, };
		short[] indices = { 0, 1, 2, 1, 2, 3, 0, 1, 5, 0, 4, 5, 1, 5, 7, 1, 3,
				7, 0, 2, 4, 2, 4, 6, 3, 2, 6, 3, 6, 7, 4, 5, 7, 4, 6, 7, };
		float[] normals = {};

		GameObject cube = new GameObject(verts, indices, uvs, normals,
				new TexturedMaterial(texName));
		return cube;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		engine.pause();
		view.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		engine.resumeEngine();
		view.onResume();
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
}