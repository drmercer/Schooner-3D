package com.supermercerbros.gameengine;

import java.io.IOException;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.TextureLib;

import android.os.Bundle;
import android.util.Log;

public class TestActivity extends GameActivity {
	private static String TAG = "GameActivity";
	private Engine engine;
	private Camera cam;
	
	/**
	 * This provides an example of an extension of GameActivity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "super.onCreate finished");
		setBackgroundColor(.5f, .5f, .5f);
		
		engine = getEngine();
		
		cam = getCamera();
		cam.set(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
		
		engine.addObject(TestObjects.tetra());
		start(.5f, 10.f);
		
	}

}
