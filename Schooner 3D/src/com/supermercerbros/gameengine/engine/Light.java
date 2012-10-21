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

package com.supermercerbros.gameengine.engine;

public class Light {
	public float x;
	public float y;
	public float z;
	public float r;
	public float g;
	public float b;

	public void copyTo(Light light) {
		if (light == this) {
			return;
		}
		if (light.x != x) {
			light.x = x;
		}
		if (light.y != y) {
			light.y = y;
		}
		if (light.z != z) {
			light.z = z;
		}
		if (light.r != r) {
			light.r = r;
		}
		if (light.g != g) {
			light.g = g;
		}
		if (light.b != b) {
			light.b = b;
		}
	}
}
