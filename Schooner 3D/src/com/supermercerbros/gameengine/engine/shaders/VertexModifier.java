package com.supermercerbros.gameengine.engine.shaders;

import com.supermercerbros.gameengine.objects.GameObject;

/**
 * Represents a vertex position modifier that is applied in the GPU
 */
public abstract class VertexModifier {
	public abstract void onLoadObject(Material mtl, GameObject object, int[] vbo);
	
	public abstract void onAttachAttribs(Material mtl, Program program);
	
	/**
	 * @param sb 
	 * @return Additional variables (uniforms and attributes) for the vertex
	 *         shader. These should all be set in
	 *         {@link #onAttachAttribs(Material)}.
	 */
	public abstract void getVars(StringBuilder sb);
	
	/**
	 * @return The vertex position modifier code to be inserted at the
	 *         beginning of the main method. Should write <code>vec3 pos</code>
	 *         and <code>vec3 normal</code> variables containing the modified
	 *         position and
	 *         normal respectively.
	 */
	public abstract void getCode(StringBuilder sb);
	
	/**
	 * @return Methods declared (and used) by the modifier. Inserted before the main
	 *         method declaration.
	 */
	public abstract void getMethods(StringBuilder sb);
	
	/**
	 * @return The number of additional floats per vertex.
	 */
	public abstract int getStride();
}
