package com.supermercerbros.gameengine;

import java.io.IOException;

import android.os.Bundle;
import android.view.MotionEvent;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.TextureLib;

public class TestActivity extends GameActivity {
	private static String TAG = "GameActivity";
	private static final float NEAR_CLIP_DISTANCE = 0.1f;
	private static final float FAR_CLIP_DISTANCE = 10.0f;
	private Engine engine;
	private Camera cam;
	
	/**
	 * This provides an example of a subclass of GameActivity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setBackgroundColor(.5f, .5f, .5f);
		engine = getEngine();
		cam = getCamera();
		
		String testTexture2 = "";
		try {
			testTexture2 = TextureLib.loadTexture(R.drawable.test_texture2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cam.set(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
		cam.moveTo(1.0f, -5.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 5000);
		
		engine.setLight(0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
//		engine.addObject(TestObjects.tetra());
		engine.addObject(TestObjects.cube(testTexture2));
		start(NEAR_CLIP_DISTANCE, FAR_CLIP_DISTANCE);
		
	}

	@Override
	public boolean onTouch(MotionEvent event) {
		// Handle touch events here if desired.
		return false;
	}

}
