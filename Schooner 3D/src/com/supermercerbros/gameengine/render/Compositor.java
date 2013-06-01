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
package com.supermercerbros.gameengine.render;

import static android.opengl.GLES20.*;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

import android.opengl.GLES20;

import com.supermercerbros.gameengine.engine.shaders.Program;
import com.supermercerbros.gameengine.engine.shaders.ShaderLib;

/**
 * Represents a compositor that can perform post-processing on the scene by
 * rendering it off-screen and then rendering that (with any changes) to a
 * full-screen quad.
 */
public abstract class Compositor {

	private int texture = -1;
	private int renderbuffer = -1;
	private int framebuffer = -1;
	private int arrayBuffer = -1;

	private Program p;

	public void onSurfaceChanged(int width, int height) {
		int[] handle = new int[1];

		// Init texture
		if (!glIsTexture(texture)) {
			// Get texture handle
			glGenTextures(1, handle, 0);
			texture = handle[0];
			glBindTexture(GL_TEXTURE_2D, texture);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB,
					GL_UNSIGNED_SHORT_5_6_5, null);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		}

		// Init renderbuffer
		if (!glIsRenderbuffer(renderbuffer)) {
			// Get renderbuffer handle
			glGenRenderbuffers(1, handle, 0);
			renderbuffer = handle[0];

			glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width,
					height);
		}

		// Init framebuffer
		if (!glIsFramebuffer(framebuffer)) {
			// Get framebuffer handle
			glGenFramebuffers(1, handle, 0);
			framebuffer = handle[0];
		}
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
				GL_TEXTURE_2D, texture, 0);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
				GL_RENDERBUFFER, renderbuffer);

		// Init vertex buffer
		if (!glIsBuffer(arrayBuffer)) {
			// Get buffer handle
			glGenBuffers(1, handle, 0);
			arrayBuffer = handle[0];

			glBindBuffer(GL_ARRAY_BUFFER, arrayBuffer);

			byte[] data = { 0, 0, 1, 0, 0, 1, 1, 1 };
			ByteBuffer buf = ByteBuffer.allocateDirect(8).order(
					ByteOrder.nativeOrder());
			buf.put(data);
			buf.clear();

			glBufferData(GL_ARRAY_BUFFER, 8, buf, GL_STATIC_DRAW);
		}

		String vert = getVertexShader();
		String frag = getFragmentShader();
		p = ShaderLib.newProgram(vert, frag);
		p.use();
		loadUniforms(p);
	}

	public void preDraw() {
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer); // Bind texture buffer
	}

	public void postDraw() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0); // Bind screen buffer

		// Bind full-screen quad buffer
		glBindBuffer(GLES20.GL_ARRAY_BUFFER, arrayBuffer);

		// Use Program
		p.use();

		// Attach a_pos attribute
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_BYTE, false, 2, 0);
		attachExtraAttribs(p);

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
	}

	/**
	 * @return The vertex shader source
	 */
	protected String getVertexShader() {
		StringBuilder sb = new StringBuilder();

		InputStream in = getClass().getResourceAsStream(
				"compositor_vert_shader.txt");
		Scanner scan = new Scanner(in);
		while (scan.hasNext()) {
			sb.append(scan.nextLine());
			sb.append("\n");
		}
		scan.close();

		return sb.toString();
	}

	/**
	 * @return The fragment shader source
	 */
	protected abstract String getFragmentShader();

	/**
	 * This is called in {@link #postDraw()}, to attach any extra attributes.
	 */
	private void attachExtraAttribs(Program p) {
		// Nothing by default
	}

	/**
	 * This is called in {@link #onSurfaceChanged(int, int)}, to load any
	 * uniforms for the shader.
	 */
	private void loadUniforms(Program p) {
		// Nothing by default
	}
}
