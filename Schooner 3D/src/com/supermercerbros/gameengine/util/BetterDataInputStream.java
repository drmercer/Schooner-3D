package com.supermercerbros.gameengine.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

/**
 * Subclass of {@link DataInputStream} that adds array-reading functions.
 *
 */
public class BetterDataInputStream extends DataInputStream {
	private static final String TAG = "BetterDataInputStream";
	private byte[] array;

	public BetterDataInputStream(InputStream in) {
		super(in);
		array = new byte[0];
	}

	/**
	 * Reads at most <code>length</code> shorts from this stream and stores them
	 * in the <code>short</code> array <code>out</code> starting at
	 * <code>offset</code>. Returns the number of shorts that have been read or
	 * -1 if no shorts have been read and the end of the stream has been
	 * reached.
	 * 
	 * @param out
	 * @param offset
	 * @param length
	 * @return The number of shorts read.
	 * @throws IOException
	 */
	public int readShortArray(short[] out, int offset, int length)
			throws IOException {
		ensureLength(length * 2);
		int bytesRead = super.read(array, 0, length * 2);
		Log.d(TAG, "read(array, 0, " + (length * 2) + ") returns " + bytesRead);
		for (int i = 0; i < bytesRead * 2; i++) {
			out[offset + i] = (short) (array[i * 2] | (array[i * 2 + 1] << 8));
		}
		return bytesRead / 2;
	}

	public int readFloatArray(float[] out, int offset, int length) 
		throws IOException {
			ensureLength(length * 2);
			int bytesRead = super.read(array, 0, length * 2);
			Log.d(TAG, "read(array, 0, " + (length * 2) + ") returns " + bytesRead);
			for (int i = 0; i < bytesRead * 2; i++) {
				out[offset + i] = (short) (array[i * 2] | (array[i * 2 + 1] << 8));
			}
			return bytesRead / 2;
	}
	
	/**
	 * Reads a UTF-8 string whose end has been marked with NUL (0x00)
	 * @return The string read
	 * @throws IOException
	 */
	public String readString() throws IOException{
		StringBuilder builder = new StringBuilder();
		while (true) {
			byte next = super.readByte();
			if (next == 0x00) { // If next char is NUL, the String's end has been reached.
				break;
			} else {
				builder.append((char) next);
			}
		}
		return builder.toString();
	}

	private void ensureLength(int l) {
		if (array.length < l) {
			array = new byte[l];
		}
	}
	
	@Override
	public void close() throws IOException{
		super.close();
		array = null;
	}
}
