package com.supermercerbros.gameengine;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.widget.FrameLayout;

/**
 * A ViewGroup that holds the UI views on top of the GLSurfaceView which renders
 * a game. Only the GLSurfaceView is drawn by the system call to
 * {@link #dispatchDraw(Canvas)}.
 * FIXME: remove this. Won't help
 */
class GameLayout extends FrameLayout {
	private GLSurfaceView gameView;

	/**
	 * Constructs a new GameLayout with the given Context and GLSurfaceView.
	 * 
	 * @param context
	 * @param view
	 */
	public GameLayout(Context context, GLSurfaceView view) {
		super(context);
		gameView = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.FrameLayout#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		gameView.layout(l, t, r, b);
	}

	/**
	 * Draws the GameLayout's GLSurfaceView to a Canvas.
	 * 
	 * @param canvas
	 *            The Canvas to draw to.
	 * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		gameView.draw(canvas);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// This method should do nothing.
	}

	/**
	 * Draws the GameLayout's children (not including the gameView) to the given
	 * Canvas.
	 * 
	 * @param canvas
	 *            The Canvas to draw to.
	 */
	public void drawChildren(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

}
