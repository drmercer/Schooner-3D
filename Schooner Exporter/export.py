#
# Copyright 2013 Dan Mercer
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##

# USAGE NOTES:
# - Meshes must be quads OR tris, not mixed.
# - All rotations must be in quaternions
# - Unused bones (bones with no parented vertices) should not have used children.
# - Bone scale is ignored

import bpy
import sys


# operators
class InfoOperator(bpy.types.Operator):
	""" This Operator notifies the user with an info blurb in the header
	"""
	bl_idname = "ui.info"
	bl_label = "Invoke Info Operator"

	text = bpy.props.StringProperty()

	def execute(self, context):
		self.report({'INFO'}, self.text)
		return {'FINISHED'}

class WarnOperator(bpy.types.Operator):
	""" This Operator notifies the user with a a warning blurb in the header
	"""
	bl_idname = "ui.warn"
	bl_label = "Invoke Warn Operator"

	text = bpy.props.StringProperty()

	def execute(self, context):
		self.report({'WARNING'}, self.text)
		return {'FINISHED'}

bpy.utils.register_class(InfoOperator)
bpy.utils.register_class(WarnOperator)

def info(text=""):
	bpy.ops.ui.info('EXEC_DEFAULT', text=text)

def warn(text=""):
	bpy.ops.ui.warn('EXEC_DEFAULT', text=text)

# End operators


# Blend file and scene cleaner
class Clean:
	def cleanFile():
		import re # regex
		
		# Make sure that curves are in their appropriate groups
		for action in bpy.data.actions:
			for curve in action.fcurves:
				# group bone curves
				isBoneCurve = re.match("pose\.bones\[\".+\"\]\.", curve.data_path)
				if isBoneCurve:
					boneName = curve.data_path.split("\"")[1]
					if curve.group and curve.group.name == boneName:
						continue
					else:
						groupIndex = action.groups.find(boneName)
						if groupIndex + 1:
							curve.group = action.groups[groupIndex]
						else:
							curve.group = action.groups.new(boneName)
			for group in action.groups:
				if not group.channels:
					action.groups.remove(group)
		bpy.context.scene.update()
		
	
	def cleanScene(scene):
		for obj in scene.objects:
			obj.hide = False
		scene.update()

# End class Clean


# Binary output file
class BinFile:
	from struct import Struct
	
	DEBUG = True
	roundToZeroWithin = 0.0001
	
	endian = '>'
	
	signedByte = Struct(endian + 'b')
	unsignedByte = Struct(endian + 'B')
	
	signedShort = Struct(endian + 'h')
	unsignedShort = Struct(endian + 'H')
	
	signedFloat = Struct(endian + 'f')
	
	signedInt = Struct(endian + 'i')
	
	def __init__(self, directory, name):
		# store directory and filename for debugging
		self.DIRECTORY = directory
		self.NAME = name
		if BinFile.DEBUG:
			print("## OPEN " + name)
		
		# open file
		filepath = directory + name
		import os
		if not os.path.exists(directory):
			os.mkdir(directory)
		if os.path.exists(filepath):
			self.file = open(filepath, "wb")
		else:
			self.file = open(filepath, "ab")
		
	def writeFlags(self, bools, bytecount=1):
		if BinFile.DEBUG:
			print("#bool[] : " + str(bools))
		written = 0
		while bytecount > 0:
			flags = 0
			for i in range(min(8, len(bools) - written)):
				if bools[written + i]:
					flags |= 2 ** i
			
			self.file.write(BinFile.unsignedByte.pack(flags))
			
			written += 8
			bytecount -= 1
	
	def writeByte(self, b, signed=False):
		if BinFile.DEBUG:
			if signed:
				print("#byte   : " + str(b))
			else:
				print("#ubyte  : " + str(b))
		if signed:
			self.file.write(BinFile.signedByte.pack(b))
		else:
			self.file.write(BinFile.unsignedByte.pack(b))
	
	def writeShort(self, s, signed=False):
		if BinFile.DEBUG:
			if signed:
				print("#short  : " + str(s))
			else:
				print("#ushort : " + str(s))
		if signed:
			self.file.write(BinFile.signedShort.pack(s))
		else:
			self.file.write(BinFile.unsignedShort.pack(s))
	
	def writeInt(self, i):
		if BinFile.DEBUG:
			print("#int : " + str(i))
		self.file.write(BinFile.signedInt.pack(i))
		
	def writeFloat(self, f):
		f = clampFloat(f)
		if BinFile.DEBUG:
			print("#float  : " + str(f))
		self.file.write(BinFile.signedFloat.pack(f))
		
	def writeString(self, string):
		if BinFile.DEBUG:
			print("#string : " + string)
		self.file.write(bytes(string + chr(0), "UTF-8"))
		
	def writeAllShorts(self, shorts, signed=False):
		if BinFile.DEBUG:
			print("#shorts : " + str(shorts))
		temp = BinFile.DEBUG
		BinFile.DEBUG = False
		for s in shorts:
			self.writeShort(s, signed=signed)
		BinFile.DEBUG = temp
	
	def writeAllShortPairs(self, shortPairs, signed=False):
		if BinFile.DEBUG:
			print("#shorts : " + str(shortPairs))
		temp = BinFile.DEBUG
		BinFile.DEBUG = False
		for pair in shortPairs:
			for s in pair:
				self.writeShort(s, signed=signed)
		BinFile.DEBUG = temp
	
	def writeAllFloats(self, floats):
		if BinFile.DEBUG:
			print("#floats : " + str(floats))
		temp = BinFile.DEBUG
		BinFile.DEBUG = False
		for f in floats:
			self.writeFloat(f)
		BinFile.DEBUG = temp
	
	def close(self):
		self.file.close()
		self.file=None
		if BinFile.DEBUG:
			print("## CLOSE " + self.NAME)
		self.DIRECTORY = None
		self.NAME = None
# End class BinFile

# Mesh exporter object
class MeshExporter:
	def __init__(self, mesh_object, tris=True, textured=False):
		self.setMode('OBJECT')
		bpy.ops.object.select_all(action='DESELECT')
		bpy.context.scene.objects.active = mesh_object
		mesh_object.select = True
		self.setMode('EDIT')
		
		mesh = mesh_object.data
		
		# Convert to tris or quads
		bpy.ops.mesh.select_all(action='SELECT')
		if tris:
			bpy.ops.mesh.quads_convert_to_tris()
		else:
			bpy.ops.mesh.tris_convert_to_quads()
		bpy.ops.mesh.remove_doubles()
		
		# Set to Edge select mode
		bpy.ops.object.mode_set(mode='EDIT')
		bpy.ops.mesh.select_all(action='DESELECT')
		bpy.ops.mesh.remove_doubles()
		bpy.context.tool_settings.mesh_select_mode=(False,True,False)
		
		# split sharp edges
		bpy.ops.object.mode_set(mode='OBJECT')
		sharpEdges = [edge for edge in mesh.edges if edge.use_edge_sharp]
		for edge in sharpEdges:
			edge.select=True
		bpy.ops.object.mode_set(mode='EDIT')
		bpy.ops.mesh.edge_split()
		
		# get the vertex indices in the sharp edges
		self.sharps = []
		for edge in mesh.edges:
			if edge.use_edge_sharp:
				for index in edge.vertices:
					if not self.sharps.count(index):
						self.sharps.append(index)
		self.sharps.sort()
		
		bpy.ops.mesh.select_all(action='DESELECT')
		# split seam edges
		bpy.ops.object.mode_set(mode='OBJECT')
		seams = [edge for edge in mesh.edges if edge.use_seam and not edge.use_edge_sharp]
		for edge in seams:
			edge.select=True
		bpy.ops.object.mode_set(mode='EDIT')
		bpy.ops.mesh.edge_split()
		bpy.ops.object.mode_set(mode='OBJECT')
		
		# store flags
		self.tris = tris
		self.textured = textured
		self.armature_indexed = (mesh_object.parent!=None and mesh_object.parent.type=='ARMATURE')
		print("armature_indexed = " + str(self.armature_indexed))
		
		# store index data
		self.indices = [index for face in mesh.polygons for index in face.vertices]
		
		# store vertex data
		self.vertices = [coord for vert in mesh.vertices for coord in vert.co]
		
		# store UV data
		if textured:
			#mesh.calc_tessface()
			mesh.update(calc_tessface=True)
			self.uvs = []
			for vertIndex in range(len(mesh.vertices)):
				for face in mesh.polygons:
					uvs_found = False
					i = 0
					for value in face.vertices:
						if value == vertIndex:
							faceIndex = face.index
							uv = self.getUVs(mesh, faceIndex, i)
							self.uvs.extend(uv)
							uvs_found = True
							break
						i += 1
					if uvs_found:
						break
		
		# store armature weight data
		if self.armature_indexed:
			armature_bones = filterBoneList(mesh_object.parent)
			self.bone_weights = []
			for vertex, vert_index in zip(mesh.vertices, range(len(mesh.vertices))):
				bones = []
				for g in vertex.groups:
					if clampFloat(g.weight) != 0.0:
						name = mesh_object.vertex_groups[g.group].name
						bone_index = -1
						for armature_bone in armature_bones:
							if armature_bone.name == name:
								bone_index = armature_bones.index(armature_bone)
								break
						else:
							continue
						bone_weight = g.weight
						bone = (bone_index, bone_weight)
						bones.append(bone)
				if not len(bones):
					print("canceling armature_indexed")
					self.armature_indexed = False
					self.bone_weights = None
					break
				self.bone_weights.append(bones)
	
	def export(self, directory, name):
		file = BinFile(directory, name + ".sch3Dmesh")
		file.writeInt(1)
		
		# write flags
		file.writeFlags((self.tris, self.textured, self.armature_indexed))
		
		# write number of faces
		if self.tris:
			file.writeShort(int(len(self.indices) / 3))
		else:
			file.writeShort(int(len(self.indices) / 4))
			
		# write number of vertices
		file.writeShort(int(len(self.vertices) / 3))
		
		# write indices
		file.writeAllShorts(self.indices)
		
		# write vertices
		file.writeAllFloats(self.vertices)
		
		if self.textured:
			# write sharp verts
			file.writeShort(len(self.sharps))
			file.writeAllShorts(self.sharps)
			# write UVs
			file.writeAllFloats(self.uvs)
		
		if self.armature_indexed:
			# write armature weights
			for vertex_bones in self.bone_weights:
				file.writeByte(len(vertex_bones))
				for index, weight in vertex_bones:
					file.writeByte(index)
					file.writeFloat(weight)
		
		# close file
		file.close()
	
	def getUVs(self, mesh, faceIndex=0, uvIndex=0):
		#mesh = bpy.context.active_object.to_mesh(bpy.context.scene, True, 'PREVIEW')
		
		#tessface = mesh.tessface_uv_textures.active
		#face_data = tessface.data[faceIndex]
		#if uvIndex == 0:
		#   return face_data.uv1
		#elif uvIndex == 1:
		#   return face_data.uv2
		#elif uvIndex == 2:
		#   return face_data.uv3
		
		uv_data = mesh.uv_layers.active.data
		return uv_data[faceIndex * 3 + uvIndex].uv
	
	def select(self, edges, extend=False):
		originalMode = bpy.context.active_object.mode
		if not extend:
			self.setMode(mode='EDIT')
			bpy.ops.mesh.select_all(action='DESELECT')
		self.setMode(mode='OBJECT')
		for edge in edges:
			edge.select=True
		self.setMode(originalMode)
	
	def setMode(self, mode):
		currentMode = bpy.context.active_object.mode
		import sys
		print("currentMode = " + str(currentMode))
		print("mode = " + str(mode))
		if currentMode != mode:
			bpy.ops.object.mode_set(mode=mode)
# End class MeshExporter

class ArmatureExporter:
	def __init__(self, armature_object, actions, exportMovements=True):
		self.actions = actions
		self.armature = armature_object.data
		self.bones = filterBoneList(armature_object)
		
	def export(self, directory, name, options):
		file = BinFile(directory, name + ".sch3Darmature")
		file.writeInt(1)
		
		file.writeByte(len(self.bones) - 1)
		
		# For each bone
		for bone in self.bones:
			# Write bone Center of Rotation coordinates
			file.writeFloat(bone.head_local[0])
			file.writeFloat(bone.head_local[1])
			file.writeFloat(bone.head_local[2])
			
			# Write bone parent index + 1, or 0 if bone has no parent.
			if bone.parent:
				file.writeByte(self.bones.index(bone.parent) + 1)
			else:
				file.writeByte(0)
		
		# For each action
		for action in self.actions:
			if options.moveScale:
				scale='UNIFORM'
			elif options.moveScaleAxis:
				scale='AXIS'
			else:
				scale='NONE'
			
			# write movement part of action
			writeMovementToFile(action, file, options.moveLoc, options.moveRot, scale)
			
			# write bones part of action
			for bone in self.bones:
				group = action.groups.get(bone.name)
				if group and len(group.channels) >= 4:
					print("  " + bone.name + " group exists and has channels")
					for array_index in range(4):
						for fcurve in group.channels:
							if (fcurve.data_path.find("rotation_quaternion") + 1) and fcurve.array_index == array_index:
								file.writeByte(len(fcurve.keyframe_points))
								writeFCurveToFile(fcurve, file)
								break
						else:
							file.writeByte(0)
				else:
					print("  " + bone.name + " group does not exist or does not have channels")
					# no points for w, x, y, or z curves
					file.writeByte(0)
					file.writeByte(0)
					file.writeByte(0)
					file.writeByte(0)
		file.close()


class ArmatureOptions:
	
	def __init__(self, moveLoc=True, moveRot=True, moveScaleAxis=False, moveScale=True):
		self.moveLoc = moveLoc
		self.moveRot = moveRot
		self.moveScaleAxis = moveScaleAxis
		self.moveScale = moveScale

def getActionsOfArmature(armature_object):
	if verbose:
		print("Finding actions of " + armature_object.name)
	armature = armature_object.data
	actions = []
	for action in bpy.data.actions:
		if verbose:
			print("Check Action " + action.name)
		if action.name.rsplit(".",1)[0] != armature_object.name + "Action":
			if verbose:
				print("Not this action.")
			continue
		for group in action.groups:
			if verbose:
				print("Check group " + group.name)
			if (not armature.bones.get(group.name)) or (not len(group.channels)):
				# If group does not represent bone or group has no curves,
				continue # Skip to next group
			for fcurve in group.channels:
				if fcurve.data_path.find('quaternion') + 1:
					if verbose:
						print("Quat rotation found. :)")
					break
			else:
				if verbose:
					print("No quat rotation found. :(")
				break
		else:
			actions.append(action)
	return actions

def getActionsOfObject(obj):
	actions = []
	for action in bpy.data.actions:
		if action.name.rsplit(".",1)[0] == obj.name + "Action":
			for fcurve in action.fcurves:
				if fcurve.data_path.find('quaternion') + 1:
					actions.append(action)
					break
	
	if len(actions):
		return actions
	else:
		return None


def exportActions(directory, name, actions):
	# Exports the given list of actions to a .sch3Dmovements file with the given name in the given directory
	file = BinFile(directory, name + ".sch3Dmovements")
	file.writeInt(1)
	for action in actions:
		writeMovementToFile(action, file, loc=True, rot=True, scale='UNIFORM') # Only supports uniform scale for now
	file.close()


def writeMovementToFile(action, file, loc=True, rot=True, scale='UNIFORM'):
	# name
	nameParts = action.name.rsplit(".",1)
	if len(nameParts) == 1: # action has no second part
		name = action.name
		warn("Action " + action.name + " has no second part. Will be named in file as \"" + name + "\".")
	else:
		name = nameParts[1]
	file.writeString(name) # write name
	print("NAME: " + name)
	
	# Check to make sure that necessary curves for location, rotation and scale exist.
	# Note that this messes up if the curves are out of order. Hopefully that'll never happen.
	# It may be neater to do this with filter(function, iterable)
	if loc:
		for index in range(3):
			for fcurve in action.fcurves:
				if fcurve.data_path == "location" and fcurve.array_index == index:
					break # Component was found, advance to next index
			else: # Component wasn't found
				print("Curve " + str(index) + " not found for location")
				loc = False
	
	if rot:
		for index in range(4):
			for fcurve in action.fcurves:
				if fcurve.data_path == "rotation_quaternion" and fcurve.array_index == index:
					break # Component was found, advance to next index
			else: # Component wasn't found
				print("Curve " + str(index) + " not found for rotation_quaternion")
				rot = False
	
	if scale == 'AXIS':
		for index in range(3):
			for fcurve in action.fcurves:
				if fcurve.data_path == "scale" and fcurve.array_index == index:
					break # Component was found, advance to next index
			else: # Component wasn't found
				print("Curve " + str(index) + " not found for scale")
				scale = 'UNIFORM'
	
	if scale == 'UNIFORM':
		for fcurve in action.fcurves:
			if fcurve.data_path == "scale" and fcurve.array_index == 0:
				break # Component was found
		else: # Component wasn't found
			print("Curve 0 not found for (uniform) scale")
			scale = ''
	
	# flags
	flags = (loc, rot, scale=='UNIFORM', scale=='AXIS')
	file.writeFlags(flags) # write flags
	print("FLAGS: " + str(flags))
	if not (loc or rot or scale != ''):
		return # If flags == 0, movement is empty.
	
	# FCurve data_paths and array_indices to export
	curveNames = {}
	curveKeys = []
	if loc:
		curveNames["location"] = (0, 1, 2)
		curveKeys.append("location")
	if rot:
		curveNames["rotation_quaternion"] = (0, 1, 2, 3)
		curveKeys.append("rotation_quaternion")
	if scale=='UNIFORM':
		curveNames["scale"] = (0,)
		curveKeys.append("scale")
	elif scale=='AXIS':
		curveNames["scale"] = (0, 1, 2)
		curveKeys.append("scale")
	
	curveSets = {}
	# For each data path...
	for key in curveKeys:
		curves = []
		# Get the curves for that path
		for index in curveNames[key]:
			for fcurve in action.fcurves:
				if fcurve.data_path==key and fcurve.array_index==index:
					curves.append(fcurve)
					break
		if len(curves) == len(curveNames[key]):
			curveSets[key] = curves
		else:
			print("Something went wrong with the " + key + " curves");
	
	# Get the left-most keyframe in the movement
	offset = None
	for key in curveKeys:
		for curve in curveSets[key]:
			first_keyframe = curve.keyframe_points[0].co[0]
			if offset == None or first_keyframe < offset:
				offset = first_keyframe
	
	# write the curves to the file
	for key in curveKeys:
		curves = curveSets[key]
		file.writeByte(len(curves[0].keyframe_points))
		for curve in curves:
			print("write curve " + curve.data_path + "[" + str(curve.array_index) + "]")
			writeFCurveToFile(curve, file, offset)


def writeFCurveToFile(fcurve, file, offset=0.0):
	last_index = len(fcurve.keyframe_points)-1
	i = 0
	for keyframe in fcurve.keyframe_points:
		# Write left handle
		if i > 0:
			file.writeFloat(keyframe.handle_left[0] - offset)
			file.writeFloat(keyframe.handle_left[1])
		# Write point
		file.writeFloat(keyframe.co[0] - offset)
		file.writeFloat(keyframe.co[1])
		# Write right handle
		if i < last_index:
			file.writeFloat(keyframe.handle_right[0] - offset)
			file.writeFloat(keyframe.handle_right[1])
		i+= 1


# Clamps floats to zero if they are within a certain tolerance.
def clampFloat(f):
	import math
	if math.fabs(f) < BinFile.roundToZeroWithin:
		f = 0.0
	return f


def filterBoneList(armature_object):
	bones = []
	for bone in armature_object.data.bones:
		for child in armature_object.children:
			if child.vertex_groups and child.vertex_groups.get(bone.name):
				bones.append(bone)
				break
	return bones

logToFile = True
logFileName = "log.txt"
verbose = True

# Begin script.
if verbose:
	print("\n\n")
	print("BEGIN SCRIPT.")

Clean.cleanFile() # Clean file

scene = bpy.context.scene
if bpy.data.filepath == "" :
	warn("You must save the .blend first!")
blendFileName = bpy.data.filepath.rsplit("\\",1)[1].rsplit(".",1)[0]
directory = bpy.data.filepath.rsplit("\\",1)[0] + "\\"  + blendFileName + "_exports\\"

if verbose:
	print("Exporting to directory: " + directory)
if logToFile:
	print("Writing log data to " + directory + logFileName)
	import os
	if not os.path.exists(directory):
		os.mkdir(directory)
	log = open(directory + logFileName, "w")
	sys.stdout = log
	

bpy.ops.object.mode_set(mode='OBJECT')

#verbose = False
# Export movements.
movementSources = [obj for obj in scene.objects if ['MESH',].count(obj.type)]
for obj in movementSources:
	if obj.rotation_mode != 'QUATERNION':
		warn(obj.name + " is not in quaternion rotation mode. " +
			"Make sure all objects and actions use quaternions for rotations.")
		continue
	actions = getActionsOfObject(obj)
	if actions is not None:
		if verbose:
			print("Exporting " + obj.name + "'s actions.")
		exportActions(directory, obj.name, actions)

# Export meshes.
originalScene = bpy.context.scene
bpy.ops.scene.new(type='LINK_OBJECT_DATA')
scene = bpy.context.scene
Clean.cleanScene(scene) # Clean scene

mesh_objects = [obj for obj in scene.objects if obj.type == 'MESH']
bpy.ops.object.mode_set(mode='OBJECT')
for obj in scene.objects:
	obj.select=False
for obj in mesh_objects:
	exporter = MeshExporter(obj, tris=True, textured=False)
	exporter.export(directory, obj.name.rsplit(".",1)[0])
bpy.ops.scene.delete()
scene = originalScene

verbose = True

# Export armatures
armature_objects = [obj for obj in scene.objects if obj.type == 'ARMATURE']
print("ARMATURES: " + str(armature_objects))
for obj in armature_objects:
	actions = getActionsOfArmature(obj)
	print(actions)
	if actions:
		exporter = ArmatureExporter(obj, actions)
		exporter.export(directory, obj.name, ArmatureOptions())

if logToFile:
	sys.stdout.close()
	sys.stdout = sys.__stdout__

# End script	
if verbose:
	print("END SCRIPT.")