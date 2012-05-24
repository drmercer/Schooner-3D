package com.supermercerbros.gameengine;

import java.io.IOException;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.objects.TexturedMaterial;

public class TestActivity extends GameActivity {
	@SuppressWarnings("unused")
	private static String TAG = "GameActivity";
	private static final float NEAR_CLIP_DISTANCE = 0.1f;
	private static final float FAR_CLIP_DISTANCE = 10.0f;
	private static final int CAMERA_MOVE_DURATION = 1500;
	private Engine engine;
	private Camera cam;
	private boolean iso = true;
	private int bg;
	
	/**
	 * This provides an example of a subclass of GameActivity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bg = Color.argb(255, 128, 255, 255);
		setBackgroundColor(bg);
		engine = getEngine();
		cam = getCamera();
		
		String testTexture2 = "";
		try {
			testTexture2 = TextureLib.loadTexture(R.drawable.test_texture2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cam.set(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
		
		engine.setLight(0.0f, 0.0f, 1.0f, ((float) Color.red(bg) / 256 + 1.0f) / 2.0f, ((float) Color.green(bg) / 256 + 1.0f) / 2.0f, ((float) Color.blue(bg) / 256 + 1.0f) / 2.0f);
//		engine.addObject(TestObjects.tetra());
		engine.addObject(TestObjects.cube(new TexturedMaterial(testTexture2)));
		start(NEAR_CLIP_DISTANCE, FAR_CLIP_DISTANCE);
		
	}

	@Override
	public boolean onTouch(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
		switchCamPosition();
		Random rand = new Random();
		setBG(event.getX() / getWidth(), event.getY(0) / getHeight(), rand.nextFloat());
		Log.d("onTouch", "x: " + event.getX() + " y: " + event.getY());
		return true;
	}
	
	private void switchCamPosition(){
		if (iso) {
			cam.moveTo(1.0f, -5.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, CAMERA_MOVE_DURATION);
			iso = false;
		} else {
			cam.moveTo(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, CAMERA_MOVE_DURATION);
			iso = true;
		}
	}
	
	private void setBG(float r, float g, float b){
		bg = Color.argb(255, (int) (255 * r), (int) (255 * g), (int) (255 * b));
		engine.setLight(0.0f, 0.0f, 1.0f, ((float) Color.red(bg) / 256 + 1.0f) / 2.0f, ((float) Color.green(bg) / 256 + 1.0f) / 2.0f, ((float) Color.blue(bg) / 256 + 1.0f) / 2.0f);
		setBackgroundColor(bg);
	}
	
	private int getWidth(){
		return getWindowManager().getDefaultDisplay().getWidth(); 
	}
	
	private int getHeight(){
		return getWindowManager().getDefaultDisplay().getHeight();
	}

}
