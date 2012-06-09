package com.supermercerbros.gameengine.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Subclass of {@link DataInputStream} that adds array-reading functions.
 *
 */
public class BetterDataInputStream extends DataInputStream {

	public BetterDataInputStream(final InputStream in) {
		super(in);
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
	 * @throws IOException
	 */
	public void readShortArray(short[] out, int offset, int length)
			throws IOException {
		for (int i = 0; i < length; i++){
			out[offset+i] = super.readShort();
		}
		return;
	}

	public void readFloatArray(float[] out, int offset, int length) 
		throws IOException {
		for (int i = 0; i < length; i++) {
			out[offset + i] = super.readFloat();
		}
//		int floatsRead = 0;
//		int yetToBeRead;
//		while (floatsRead < length){
//			yetToBeRead = length - floatsRead;
//			final int bytesToRead = (yetToBeRead * 4 < array.length) ? yetToBeRead * 4 : array.length;
//			final int bytesRead = super.read(array, 0, bytesToRead);
//			final int floats = bytesRead / 4;
//			for (int i = 0; i < floats; i++) {
//				out[offset + floatsRead + i] = Float.intBitsToFloat(
//						(array[i*4] << 24) |
//						((array[i*4+1] & 0xff) << 16) |
//						((array[i*4+2] & 0xff) << 8 |
//								(array[i*4+3] & 0xff)));
//			}
//		}
		return;
	}
	
	public void readByteArray(final byte[] out, final int offset, final int length) throws IOException{
		for (int i = 0; i < length; i++) {
			out[offset + i] = super.readByte();
		}
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
	
	@Override
	public void close() throws IOException{
		super.close();
	}
	
	public boolean hasNext() throws IOException {
		return super.available() > 0;
	}
}


