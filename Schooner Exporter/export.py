import bpy

from struct import Struct

class BinFile:
	endian = '>'
	shortPack = Struct(endian + 'H')
	floatPack = Struct(endian + 'f')
	
	def __init__(self, dir, name):
		filepath = dir + name
		import os
		if not os.path.exists(dir):
			os.mkdir(dir)
		if os.path.exists(filepath):
			self.file = open(filepath, "wb")
		else:
			self.file = open(filepath, "ab")
		
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
	
	def close(self):
		self.file.close()

def getActionsOfObject(object):
	actions = []
	for action in bpy.data.actions:
		if action.name.rsplit(".",1)[0] == object.name + "Action":
			actions.append(action)
	
	if len(actions):
		return actions
	else:
		return None

def exportActions(directory, name, actions):
	file = BinFile(directory, name + ".sch3Dmovements")
	unnamedCount = 0
	for action in actions:
		actionName = action.name.rsplit(".",1)
		if len(actionName) == 1: # action has no second part
			actionName = "unnamed" + str(unnamedCount)
			print("Action " + action.name + " has no second part. Will be named in file as " + actionName + ".")
			unnamedCount += 1
		else:
			actionName = actionName[len(actionName)-1]
		file.writeString(actionName)
		file.writeString("\n") # TODO write fcurves to file instead of this newline
	
	file.close()

print("BEGIN SCRIPT.")
scene = bpy.context.scene
blendFileName = bpy.data.filepath.rsplit("\\",1)[1].rsplit(".",1)[0]
directory = bpy.data.filepath.rsplit("\\",1)[0] + "\\"  + blendFileName + "_exports\\"
print("Exporting to directory: " + directory)

#Export movements
movementSources = ['MESH',]
for object in scene.objects:
	if movementSources.count(object.type):
		actions = getActionsOfObject(object)
		if actions is not None:
			exportActions(directory, object.name, actions)
		

print("END SCRIPT.")