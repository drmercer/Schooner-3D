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

import java.io.IOException;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.supermercerbros.gameengine.engine.Camera;
import com.supermercerbros.gameengine.engine.Engine;
import com.supermercerbros.gameengine.engine.TextureLib;
import com.supermercerbros.gameengine.material.TexturedMaterial;
import com.supermercerbros.gameengine.objects.GameObject;

public class TestActivity extends GameActivity {
	private static String TAG = GameActivity.class.getSimpleName();
	private static final float NEAR_CLIP_DISTANCE = 0.1f;
	private static final float FAR_CLIP_DISTANCE = 20.0f;
	private static final int CAMERA_MOVE_DURATION = 1500;
	private Engine engine;
	private Camera cam;
	private boolean iso = true;
	private int bg;
	private GameObject object;
	
	/**
	 * This provides an example of a subclass of GameActivity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bg = 0x00000000;
		setBackgroundColor(bg);
		engine = getEngine();
		cam = getCamera();
		cam.set(10.0f, 10.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
		
		object = getObject();
		
		engine.setLight(0.0f, 0.0f, 1.0f, ((float) Color.red(bg) / 256 + 1.0f) / 2.0f, ((float) Color.green(bg) / 256 + 1.0f) / 2.0f, ((float) Color.blue(bg) / 256 + 1.0f) / 2.0f);
		engine.addObject(object);
		start(NEAR_CLIP_DISTANCE, FAR_CLIP_DISTANCE);
	}

	protected GameObject getObject() {
		String testTexture2 = "";
		try {
			testTexture2 = TextureLib.loadTexture(R.drawable.test_texture2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GameObject object = TestObjects.cube(new TexturedMaterial(testTexture2));
		
		return object;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
		switchCamPosition();
		Random rand = new Random();
		setBG(event.getX() / getWidth(), event.getY(0) / getHeight(), rand.nextFloat());
		Log.d("onTouch", "x: " + event.getX() + " y: " + event.getY());
		return true;
	}
	
	private void switchCamPosition(){
		if (iso) {
			cam.moveTo(2.0f, -10.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, CAMERA_MOVE_DURATION);
			iso = false;
		} else {
			cam.moveTo(10.0f, 10.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, CAMERA_MOVE_DURATION);
			iso = true;
		}
	}
	
	private void setBG(float r, float g, float b){
		bg = Color.argb(255, (int) (255 * r), (int) (255 * g), (int) (255 * b));
		engine.setLight(0.0f, 0.0f, 1.0f, 1 - r/2, 1 - g/2, 1 - b/2);
		setBackgroundColor(bg);
	}
	
	private int getWidth(){
		return getWindowManager().getDefaultDisplay().getWidth(); 
	}
	
	private int getHeight(){
		return getWindowManager().getDefaultDisplay().getHeight();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		onMenuButtonClick();
		Log.d(TAG, "onMenuButtonClick");
		return super.onPrepareOptionsMenu(menu);
	}

	protected void onMenuButtonClick() {
	}
}
