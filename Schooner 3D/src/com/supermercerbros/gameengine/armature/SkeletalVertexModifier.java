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

package com.supermercerbros.gameengine.armature;

import java.util.Locale;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.shaders.Material;
import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.VertexModifier;
import com.supermercerbros.gameengine.objects.BonedObject;
import com.supermercerbros.gameengine.objects.GameObject;

public class SkeletalVertexModifier extends VertexModifier {
	// METHODS
	private static final String METHOD_SKIN_POS =
			"void skin_pos(in vec4 inPos, in float weight, in int index, inout vec3 outPos, inout float weight_sum) {\n" +
			"  outPos += (u_matrices[index] * inPos).xyz * weight;\n" +
			"  weight_sum += weight;\n" +
			"}\n";
	private static final String METHOD_SKIN_NORMAL =
			"void skin_normal(in vec4 inNormal, in float weight, in int index, inout vec3 outNormal, inout float weight_sum) {\n" +
			"  outNormal += (u_matrices[index] * inNormal).xyz * weight;\n" +
			"  weight_sum += weight;\n" +
			"}\n";
	
	// METHOD CALLS
	private static final String SKIN_POS = "skin_pos(a_pos4, m_weight, m_index, pos, m_weight_sum);\n";
	private static final String SKIN_NORMAL = "skin_normal(a_normal4, m_weight, m_index, normal, m_weight_sum);\n";
	
	// VARIABLES
	private static final String VARS = 
			"uniform mat4 u_matrices[%d];\n" +
			
			"attribute vec4 a_indices;\n" +
			"attribute vec4 a_weights;\n";
	
	// STRIDE
	private static final int STRIDE = 5;
	
	private final int bonesPerVertex;
	private final int boneCount;
	
	private int a_weights = -2;
	private int a_indices;
	
	public SkeletalVertexModifier(int bonesPerVertex, int boneCount) {
		this.boneCount = boneCount;
		this.bonesPerVertex = bonesPerVertex;
	}
	
	@Override
	public void onLoadObject(Material mtl, GameObject object, float[] vbo) {
		BonedObject bonedObject = (BonedObject) object;
		mtl.loadArrayToVbo(bonedObject.boneWeights, vbo, bonesPerVertex, object.info.count);
		mtl.loadArrayToVbo(bonedObject.boneIndices, vbo, bonesPerVertex, object.info.count);
	}
	
	@Override
	public void onAttachAttribs(Material mtl, Program program) {
		if (a_weights == -2) {
			a_weights = program.getAttribLocation("a_weights");
			a_indices = program.getAttribLocation("a_indices");
		}
		mtl.attachAttrib(a_weights, bonesPerVertex);
		mtl.attachAttrib(a_indices, bonesPerVertex, GLES20.GL_BYTE); 
	}

	@Override
	public void getVars(StringBuilder sb) {
		sb.append(String.format(Locale.US, VARS, boneCount));
	}

	@Override
	public void getCode(StringBuilder sb) {
		final boolean normal = containsNormalAttrib(sb);
		
		final String SKIN;
		if (normal) {
			SKIN = SKIN_POS + SKIN_NORMAL;
		} else {
			SKIN = SKIN_POS;
		}
		
		// "initialization" code
		sb.append(
				"float m_weight;\n" +
				"float m_weight_sum = 0.0;\n" +
				"int m_index;\n" +
				"vec4 a_pos4 = vec4(a_pos.xyz, 1.0);\n" +
				"vec3 pos = vec3(0.0);\n");
		if (normal) {
			sb.append(
					"vec4 a_normal4 = vec4(a_normal, 0.0);\n" +
					"vec3 normal = vec3(0.0);\n");
		}
		
		// "skinning" code
		sb.append(
				"m_weight = a_weights[0];\n" +
				"m_index = int(a_indices[0]);\n");
		sb.append(SKIN);
		
		sb.append(
				"m_weight = a_weights[1];\n" +
				"m_index = int(a_indices[1]);\n");
		sb.append(SKIN);
		
		sb.append(
				"m_weight = a_weights[2];\n" +
				"m_index = int(a_indices[2]);\n");
		sb.append(SKIN);
		
		sb.append(
				"m_weight = a_weights[3];\n" +
				"m_index = int(a_indices[3]);\n");
		sb.append(SKIN);
		
		// "finishing" code
		sb.append("pos = pos / m_weight_sum;\n");
		if (normal) {
			sb.append("normal = normal / m_weight_sum;\n");
		}
	}

	@Override
	public void getMethods(StringBuilder sb) {
		sb.append(METHOD_SKIN_POS);
		if (containsNormalAttrib(sb)) {
			sb.append(METHOD_SKIN_NORMAL);
		}
	}

	@Override
	public int getStride() {
		return STRIDE;
	}
	
}
