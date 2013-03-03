/*
 * Copyright 2013 Dan Mercer
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
package com.supermercerbros.gameengine.armature;

import java.util.Locale;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.VertexModifier;
import com.supermercerbros.gameengine.objects.BonedObject;
import com.supermercerbros.gameengine.objects.GameObject;

/**
 * A VertexModifier that is optimized for single-bone (binary) vertex weights.
 */
public class BinarySkeletalVertexModifier extends VertexModifier {
	
	// VARIABLES
	private static final String VARS = 
			"uniform mat4 u_matrices[%d];\n" +
			"attribute float a_index;\n";
	
	// CODE SNIPPETS
	private static final String POS_CODE = 
			"vec4 a_pos4 = vec4(a_pos.xyz, 1.0);\n" +
			"vec3 mod_pos = (u_matrices[int(a_index)] * a_pos4).xyz;\n";
	private static final String NORMAL_CODE = 
			"vec4 a_normal4 = vec4(a_normal.xyz, 0.0);\n" +
			"vec3 mod_normal = (u_matrices[int(a_index)] * a_normal4).xyz;\n";
	
	
	private final int boneCount;
	
	private int a_index = -1;
	
	/**
	 * Constructs a new BinarySkeletalVertexModifier
	 */
	public BinarySkeletalVertexModifier(int boneCount) {
		this.boneCount = boneCount;
	}
	
	@Override
	public void onLoadObject(Material mtl, GameObject object, float[] vbo) {
		BonedObject bo = (BonedObject) object;
		mtl.loadArrayToVbo(bo.boneIndices, vbo, 1, object.info.count);
	}
	
	@Override
	public void onAttachAttribs(Material mtl, Program program) {
		if (a_index == -1) {
			a_index = program.getAttribLocation("a_index");
		}
		mtl.attachAttrib(a_index, 1, GLES20.GL_BYTE);
	}
	
	// Getters =================================
	
	@Override
	public void getVars(StringBuilder sb) {
		sb.append(String.format(Locale.US, VARS, boneCount));
	}
	
	@Override
	public void getCode(StringBuilder sb) {
		sb.append(POS_CODE);
		if (containsNormalAttrib(sb)) {
			sb.append(NORMAL_CODE);
		}
	}
	
	@Override
	public void getMethods(StringBuilder sb) {
		// No methods for this modifier
	}
	
	@Override
	public int getStride() {
		return 1;
	}
	
}
