package com.supermercerbros.gameengine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

public class Utils {
	/** Appends multiple arrays onto the end of another array of the same type. I found this code 
	 * <a href=http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java/784842#784842>here</a>, 
	 * and I claim no personal credit for it.
	 * 
	 * @author <a href=http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java/784842#784842>Joachim Sauer</a>
	 * @param first The array to add to
	 * @param rest The arrays to append onto the end of <code>first</code>
	 * @param <T> The object class
	 * @return The concatenation of the arrays.
	 */
//	public static <T> T[] concatAll(T[] first, T[]... rest){
//		int totalLength = first.length;
//		for (T[] array : rest) {
//			totalLength += array.length;
//		}
//		T[] result = Arrays.copyOf(first, totalLength);
//		int offset = first.length;
//		for (T[] array : rest) {
//			System.arraycopy(array, 0, result, offset, array.length);
//			offset += array.length;
//		}
//		return result; 
//	}
	
	/** Returns the length of a vector given the vector's three coordinates. This uses 
	 * the Pythagorean theorem (hence it's name).
	 * @see <a href="http://en.wikipedia.org/wiki/Pythagorean_theorem">Pythagorean Theorem</a> (Wikipedia)
	 * 
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 * @param z The z-coordinate.
	 * @return The length of the vector.
	 */
	public static float pythagF(float x, float y, float z){
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	/**
	 * Returns the length of a vector given the vector's three coordinates. This uses 
	 * the Pythagorean theorem (hence it's name).
	 * @see <a href="http://en.wikipedia.org/wiki/Pythagorean_theorem">Pythagorean Theorem</a> (Wikipedia)
	 * 
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 * @param z The z-coordinate.
	 * @return The length of the vector.
	 */
	public static double pythagD(double x, double y, double z){
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	
	/** Appends multiple float arrays onto the end of another float array. I found this code 
	 * <a href=http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java/784842#784842>here</a> 
	 * and adapted it to work with float arrays.
	 * 
	 * @author <a href=http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java/784842#784842>Joachim Sauer</a>
	 * @param first The array to add to
	 * @param rest The arrays to append onto the end of <code>first</code>
	 * @return The concatenation of the arrays.
	 */
	public static float[] concatAll(float[] first, float[]... rest) {
		int totalLength = first.length;
		for (float[] array : rest) {
			totalLength += array.length;
		}
		float[] result = new float[totalLength];
		System.arraycopy(first, 0, result, 0, first.length);
		
		int offset = first.length;
		for (float[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result; 
	}
	
	/**
	 * Creates a perspective projection matrix.
	 * @param m The float array to write the matrix to
	 * @param offset The offset into array m where the matrix is written
	 * @param fov The field-of-view angle, in degrees.
	 * @param aspect The aspect ratio
	 * @param near The near clip plane
	 * @param far The far clip plane
	 */
	public static void perspectiveM(float[] m, int offset, float fov, float aspect, float near, float far) {
		fov = (float) Math.toRadians(fov);
        float f = (float)Math.tan(0.5 * (Math.PI - fov));
        float range = near - far;

        m[0] = f / aspect;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = f;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0; 
        m[10] = far / range;
        m[11] = -1;

        m[12] = 0;
        m[13] = 0;
        m[14] = near * far / range;
        m[15] = 0;
    }
	
	public static String readAssetAsString(AssetManager assets, String filename) throws IOException{
		StringBuilder sb = new StringBuilder();
		Scanner reader = new Scanner(assets.open(filename));
		while(reader.hasNextLine()) {
			sb.append(reader.nextLine() + "\n");
		}
		return sb.toString();
	}
	
	public static String readResourceAsString(Resources res, int resId) throws NotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner reader = new Scanner(res.openRawResource(resId));
		while(reader.hasNextLine()) {
			sb.append(reader.nextLine() + "\n");
		}
		return sb.toString();
	}
	
	public static String readInputStreamAsString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		Scanner reader = new Scanner(is);
		while(reader.hasNextLine()) {
			sb.append(reader.nextLine() + "\n");
		}
		return sb.toString();
	}

	public static boolean checkByte(byte flags, int place) {
		return (flags & (1 << place)) != 0;
	}
}
