package com.supermercerbros.gameengine.engine;

import android.opengl.GLES20;
import android.util.Log;

/**
 * A patched copy of {@link android.opengl.GLES20}. In Android
 * 
 */
public class GLES2 extends GLES20 {
	private static final String TAG = "com.supermercerbros.gameengine.engine.GLES2";
	private static final boolean USE_FIX = android.os.Build.VERSION.SDK_INT < 9;

	/**
	 * @param index
	 *            Specifies the index of the generic vertex attribute to be
	 *            modified.
	 * @param size
	 *            Specifies the number of components per generic vertex
	 *            attribute. Must be 1, 2, 3, or 4. The initial value is 4.
	 * @param type
	 *            Specifies the data type of each component in the array.
	 *            Symbolic constants GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,
	 *            GL_UNSIGNED_SHORT, GL_FIXED, or GL_FLOAT are accepted. The
	 *            initial value is GL_FLOAT.
	 * @param normalized
	 *            Specifies whether fixed-point data values should be normalized
	 *            (true) or converted directly as fixed-point values
	 *            (false) when they are accessed.
	 * @param stride
	 *            The byte offset between consecutive generic vertex
	 *            attributes. If stride is 0, the generic vertex attributes are
	 *            understood to be tightly packed in the array. The initial
	 *            value is 0.
	 * @param offset The byte offset into the buffer object's data store
	 */
	public static void glVertexAttribPointer(int index, int size, int type,
			boolean normalized, int stride, int offset) {
		if (USE_FIX) {

			Log.e(TAG, "glVertexAttribPointer is not patched yet!");
			// Put patched method here

		} else {
			GLES20.glVertexAttribPointer(index, size, type, normalized, stride,
					offset);
		}
	}

	public static void glDrawElements(int mode, int count, int type, int offset) {

	}
	// TODO Use NDK to patch GLES20

}
