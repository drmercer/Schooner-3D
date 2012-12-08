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
	private static final String VARS = 
			"const float c_f0 = 0.0;\n" +
			"const float c_i1 = 1;\n" +
			"const float c_i2 = 2;\n" +
			"const float c_i3 = 3;\n" +
			
			"uniform mat4 u_matrices[%d];\n" +
			
			"attribute ivec4 a_matrixIndices;\n" +
			"attribute vec4 a_matrixWeights;\n";
	private static final String METHODS = 
			"void skin_pos(in vec4 inPos, in float weight, in int index, inout vec3 outPos, inout float weight_sum) {" +
			"  outPos += (u_matrices[index] * inPos).xyz * weight;\n" +
			"  weight_sum += weight;" +
			"}" + 
			
			"void skin_normal(in vec4 inNormal, in float weight, in int index, inout vec3 outNormal, inout float weight_sum) {" +
			"  outNormal += (u_matrices[index] * inNormal).xyz * weight;\n" +
			"  weight_sum += weight;" +
			"}";
	private static final String CODE = 
			"vec4 a_pos4 = vec4(a_pos, c_f0);\n" +
			"vec4 a_normal4 = vec4(a_normal, c_f0);\n" +
			"vec3 pos = vec3(c_f0));\n" +
			"vec3 normal = vec3(c_f0);\n" +
			"float m_weight;\n" +
			"float m_weight_sum = c_f0;\n" +
			"float m_index;\n" +
			
			"m_weight = a_weights[0];\n" +
			"m_index = a_indices[0];\n" +
			"skin_pos(a_pos4, m_weight, m_index, pos, m_weight_sum);\n" +
			"skin_normal(a_normal4, m_weight, m_index, normal, m_weight_sum);\n" +
			
			"m_weight = a_weights[1];\n" +
			"m_index = a_indices[1];\n" +
			"skin_pos(a_pos4, m_weight, m_index, pos, m_weight_sum);\n" +
			"skin_normal(a_normal4, m_weight, m_index, normal, m_weight_sum);\n" +
			
			"m_weight = a_weights[2];\n" +
			"m_index = a_indices[2];\n" +
			"skin_pos(a_pos4, m_weight, m_index, pos, m_weight_sum);\n" +
			"skin_normal(a_normal4, m_weight, m_index, normal, m_weight_sum);\n" +
			
			"m_weight = a_weights[3];\n" +
			"m_index = a_indices[3];\n" +
			"skin_pos(a_pos4, m_weight, m_index, pos, m_weight_sum);\n" +
			"skin_normal(a_normal4, m_weight, m_index, normal, m_weight_sum);\n" +
			
			"pos = pos / m_weight_sum;\n" +
			"normal = normal / m_weight_sum;\n";
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
		mtl.loadArrayToVbo(bonedObject.boneIndices, vbo, (bonesPerVertex + 3) / 4, object.info.count);
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
		sb.append(CODE);
	}

	@Override
	public void getMethods(StringBuilder sb) {
		sb.append(METHODS);
	}

	@Override
	public int getStride() {
		return STRIDE;
	}
	
}
