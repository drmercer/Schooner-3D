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
		Log.d(TAG, "onCreate() called");
		float[] bg = {0.5f, 1.0f, 0.7f, 1.0f};
		Schooner3D.backgroundColor = bg;
		
		pipe = new DataPipe(this);
		
		String texName = "";
		try {
			texName = TextureLib.loadTexture(R.drawable.test_texture);
		} catch (IOException e) {
			e.printStackTrace();
		}

		view = new GameView(this); 
		view.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);

		cam = new Camera();
		engine = new Engine(pipe, cam);
		engine.addObject(quad(texName));
		engine.addObject(tri());
		engine.setLight(0.0f, 0.0f, -1.0f, 1.0f, 1.0f, 1.0f);
		engine.start();
		Log.d(TAG, "Initializing GameView");
		view.setRenderer(new GameRenderer(pipe));
		setContentView(view);
	}

	private GameObject quad(String texName) {
		float[] verts = {0.1f, -0.6f, 0.0f, 0.6f, -0.6f, 0.0f, 0.6f,
				0.6f, 0.0f, 0.1f, 0.6f, 0.0f};
		float[] uvs = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};

		short[] indices = { 0, 1, 2, 0, 2, 3};
		float[] normals = {};

		GameObject obj = new GameObject(verts, indices, uvs, normals, new TexturedMaterial(texName));
		return obj;
	}
	
	private GameObject tri(){
		float[] verts = {0.0f, 0.0f, 0.0f, -0.6f, -0.6f, 0.0f, -0.6f, 0.6f, 0.0f};
		float[] colors = {1, 1, 0, 0, 1, 1, 1, 0, 1};
		short[] indices = {0, 1, 2};
		float[] normals = {};
		
		GameObject tri = new GameObject(verts, indices, colors, normals, new BasicMaterial());
		return tri;
	}

	@Override
	public void onPause() {
		super.onPause();
		engine.pause();
		view.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		engine.resume();
		view.onResume();
	}
}