package com.supermercerbros.gameengine;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {
	public Renderer mRenderer;

	public GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
				| GLSurfaceView.DEBUG_LOG_GL_CALLS);
	}

}
