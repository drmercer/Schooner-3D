import bpy
import struct
from struct import Struct

class Options:
	"""
	Contains export options.
	"""
	def __init__(
				self, filepath, textured, still, animations, 
				animNames, verbose=False, scene=0):
		"""
		@filepath(string): contains the path of the file to export into
	
		@textured(boolean): True if the mesh being exported has texture coordinates
		
		@still(boolean): True if no animations should be exported. In this case,
		[animations] is ignored.
		
		@animations(int[]): contains the start frames of the animations to 
		export. They do not have to be exact, they only must be before the 
		first keyframe of each seperate animation. If only one animation is to be 
		exported, this should be None. (The first animation is always assumed to start 
		at zero.)
	
		@verbose(boolean): True if verbose (debugging) output is desired. (optional)
		
		@scene(int or string): The index or name of the current scene. (optional)
	
		"""
		
		self.filepath = filepath
		self.textured = textured
		self.still = still
		self.animations = animations
		self.animNames = animNames
		self.verbose = verbose
		self.scene = scene

class BinFile:
	endian = '>'
	shortPack = Struct(endian + 'H')
	floatPack = Struct(endian + 'f')
	
	def __init__(self, filepath):
		self.file = open(filepath, 'wb')
		
	def writeFlags(self, *bools):
		flags = 0
		for b, j in bools, range(len(bools)):
			if j > 63:
				print("Too many booleans to store in 64 bits!")
				break
			if b:
				flags |= 2 ** j
		if len(bools) <= 8:
			self.file.write(struct.pack(BinFile.endian + 'B', flags))
		elif len(bools) <= 16:
			self.file.write(struct.pack(BinFile.endian + 'H', flags))
		elif len(bools) <= 32:
			self.file.write(struct.pack(BinFile.endian + 'I', flags))
		else:
			self.file.write(struct.pack(BinFile.endian + 'Q', flags))
	
	def writeByte(self, b):
		self.file.write(struct.pack(BinFile.endian + 'B', b))
	
	def writeShort(self, s):
		self.file.write(BinFile.shortPack.pack(s))
		
	def writeFloat(self, f):
		self.file.write(BinFile.floatPack.pack(f))
		
	def writeString(self, string):
		self.file.write(bytes(string, "UTF-8"))
		
	def writeAllShorts(self, shorts):
		for s in shorts:
			self.file.write(BinFile.shortPack.pack(s))
	
	def writeAllShortPairs(self, shorts):
		for t in shorts:
			for s in t:
				self.file.write(BinFile.shortPack.pack(s))
	
	def writeAllFloats(self, *floats):
		for f in floats:
			self.file.write(BinFile.floatPack.pack(f))
		
class Face:
	def _init_(self, obj, indices, uvs):
		self.verts = []
		self.uvs = []
		self.vertexInIndices = indices
		self.verts.append(obj.vertices[indices[0]].co)
		self.uvs.append(uvs[0])
		
		self.verts.append(obj.vertices[indices[1]].co)
		self.uvs.append(uvs[1])
		
		self.verts.append(obj.vertices[indices[2]].co)
		self.uvs.append(uvs[2])
	
	def loadToArrays(self, indices, verts, uvs, duplicates, vertexInIndices=None):
		self.indices = [None, None, None]
		for vert, uv, j in self.verts, self.uvs, range(3):			
			match = -1
			uvMatch = False
			
			for i in range(len(verts)):
				if verts[(i * 3) + 0] != vert[0]:
					continue
				if verts[(i * 3) + 1] != vert[1]:
					continue
				if verts[(i * 3) + 2] != vert[2]:
					continue
				match = i
				if (uvs[(i * 2) + 0] == uv[0] and uvs[(i * 2) + 1] == uv[1]) or not Face.textured:
					uvMatch = True
					self.indices[j] = i
					break
			
			if not uvMatch:
				self.indices[j] = len(verts)
				if match > -1:
					duplicates.append([self.indices[j], match])
				verts.append(vert)
				uvs.extend(uv)
				if vertexInIndices is not None:
					vertexInIndices.append(self.vertexInIndices[j])

		indices.extend(self.indices)

def writeAnimations(file, sceneIndex, mesh, times, names, indices):
	scene = bpy.data.scenes[sceneIndex]
	times = [0, ] + times + [scene.frame_end]
	
	for i in range(len(times) - 1):
		scene.frame_set(times[i])
		bpy.ops.screen.keyframe_jump()
		animBeginFrame = scene.frame_current
		
		#Write animation name
		file.writeString(names[i]);
		
		frameCount = 0;
		while scene.frame_current < times[i + 1]:
			frameCount += 1
			currentFrame = scene.frame_current
			bpy.ops.screen.keyframe_jump()
			if currentFrame == scene.frame_current:
				break
		
		#Write keyframe count
		file.writeShort(frameCount)
		
		#reset to start of animation
		scene.frame_set(animBeginFrame)
		
		for j in range(frameCount):
			mesh.update()
			
			#Write keyframe time
			file.writeShort(scene.frame_current - animBeginFrame)
			
			#Write vertex positions
			for index in indices:
				file.writeAllFloats(mesh.vertices[index].co)
				
			#Next keyframe
			bpy.ops.screen.keyframe_jump()
			
			

def exportSchooner3D(opts):
	bpy.ops.mesh.quads_convert_to_tris()
	bpy.ops.mesh.remove_doubles()
	obj = bpy.data.scenes[opts.scene].objects.active
	if obj.type != 'MESH':
		print("The active object is not a mesh.")
		return
	elif opts.verbose:
		print("Good: the active object is a mesh.")
		
	Face.textured = opts.textured
	
	if not opts.still:
		bpy.ops.screen.frame_jump()
	
	uvCoords = None
	for uv in obj.uv_textures:
		if uv.active_render:
			uvCoords = uv.data
			break
	
	faces = []
	for i in range(len(obj.faces)):
		face = Face(obj, obj.faces[i].vertices, (uvCoords[i].uv1, uvCoords[i].uv2, uvCoords[i].uv3, uvCoords[i].uv4))
		faces.append(face)
	
	indices = []
	verts = []
	uvs = []
	duplicates = []
	
	for face in faces:
		face.loadToArrays(indices, verts, uvs, duplicates)
	
	file = BinFile(opts.filepath)
	
	#Write version, flags, number of indices, and number of vertices
	file.writeShort(1)
	file.writeFlags(opts.textured, (not opts.still))
	file.writeShort(len(indices) / 3)
	file.writeShort(len(verts) / 3)
	
	#Write indices
	file.writeAllShorts(indices)
	
	#Write vertex positions
	file.writeAllFloats([coord for vertex in verts for coord in vertex])
	
	#Write duplicate pairs
	file.writeShort(len(duplicates) / 2)
	duplicates.sort()
	for pair in duplicates:
		pair.sort()
	file.writeAllShorts(duplicates)
	
	if opts.textured: #Write UV coordinates
		file.writeAllFloats([coord for pair in uvs for coord in pair])
		
	if not opts.still:
		if opts.animations is not None: #Write number of animations
			file.writeShort(len(opts.animations) + 1)
		else:
			file.writeShort(1)
		
		writeAnimations(file, opts.scene, obj, opts.animations, opts.animNames, duplicates)

opts = Options(
			# v== Modify these. Read the docstring (at head of Options class --^ )
			# to understand what is expected here.
			
			False,
			{0, 100, 200},
			None,
			
			# You're done! Make sure you don't modify anything else.
			)
exportSchooner3D(opts)
