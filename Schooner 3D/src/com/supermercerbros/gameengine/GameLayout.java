package com.supermercerbros.gameengine;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;

class GameLayout extends FrameLayout {
	private View gameView;

	public GameLayout(Context context) {
		super(context);
	}
	
	public void setGameView(View v){
		gameView = v;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		gameView.layout(l, t, r, b);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		gameView.draw(canvas);
    }
	
	public void drawChildren(Canvas canvas){
		super.dispatchDraw(canvas);
	}

}
