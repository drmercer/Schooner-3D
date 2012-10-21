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

package com.supermercerbros.gameengine.shaders;

public class ProgramSource {
	public static final String PRECISION_LOW = "precision lowp float;\n";
	public static final String PRECISION_MEDIUM = "precision mediump float;\n";
	public static final String PRECISION_HIGH = "precision highp float;\n";
	
	public static final String MAIN_HEADER = "void main() {\n";
	public static final String MAIN_FOOTER = "}\n";
	
	private static final String EMPTY = "";
	
	public final String varyings;
	public final String vertPrecision;
	public final String vertVars;
	public final String vertMain;
	public final String vertMethods;
	public final String fragMain;
	public final String fragVars;
	public final String fragPrecision;
	public final String fragMethods;
	
	public ProgramSource(String varyings,
			String vertPrecision, String vertVars, String vertSource,
			String fragPrecision, String fragVars, String fragSource) {
		this(varyings, vertPrecision, vertVars, vertSource, null, fragPrecision, fragVars, fragSource, null);
	}
	
	public ProgramSource(String varyings, String vertPrecision,
			String vertVars, String vertSource, String vertMethods,
			String fragPrecision, String fragVars, String fragSource,
			String fragMethods) {
		if (vertVars == null) {
			throw new IllegalArgumentException("vertVars == null");
		}
		
		if (varyings == null) {
			this.varyings = EMPTY;
		} else {
			this.varyings = varyings;
		}
		
		this.vertVars = vertVars;
		
		if (fragVars == null) {
			this.fragVars = EMPTY;
		} else {
			this.fragVars = fragVars;
		}
		
		this.vertMain = vertSource;
		this.fragMain = fragSource;
		
		if (vertPrecision == null) {
			this.vertPrecision = EMPTY;
		} else {
			this.vertPrecision = vertPrecision;
		}
		
		if (fragPrecision == null) {
			this.fragPrecision = PRECISION_MEDIUM;
		} else {
			this.fragPrecision = fragPrecision;
		}
		
		if (vertMethods == null) {
			this.vertMethods = EMPTY;
		} else {
			this.vertMethods = vertMethods;
		}
		if (fragMethods == null) {
			this.fragMethods = EMPTY;
		} else {
			this.fragMethods = fragMethods;
		}
	}
}
