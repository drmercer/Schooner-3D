Schooner 3D
===========

Schooner 3D is an open-source game engine for the Android platform. It is licensed under the [Apache License][]. 
I have successfully rendered both textured and vertex-colored primitives. 

Things left to do:
-----------------

*  Patch GLES20 with NDK
*  Test the Blender exporter and the parsing code ([Sch3D][])
*  Rewrite Movements (use Euler Angles, coordinates, and maybe scale)
*  Finish making GameObjects instanceable
*  Finish Android UI mod

Things to do after first stable release:
---------------------------------------

*  Collision detection (in progress: see collision branch)
*  Add support for quads?

[Apache License]: www.apache.org/licenses/LICENSE-2.0.html "Apache License v2.0"
[Sch3D]: https://github.com/drmercer/Schooner-3D/blob/master/Schooner%203D/src/com/supermercerbros/gameengine/parsers/Sch3D.java "Sch3D.java"