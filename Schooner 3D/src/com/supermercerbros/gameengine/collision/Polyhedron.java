package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a polyhedron.
 * @author 118514
 *
 */
public class Polyhedron {
	/**
	 * Partitions a closed mesh of quads into convex subparts.
	 * 
	 * @param verts
	 *            The vertices of the mesh.
	 * @param indices
	 *            The indices of the vertices of each quad in the mesh.
	 * @return A list containing the convex parts.
	 */
	public static List<Polyhedron> partitionMesh(float[] verts,
			short[] indices) {
		// TODO write mesh partitioning code
		return null;
	}

	/**
	 * Partitions a surface of quads into convex subparts.
	 * 
	 * @param verts
	 *            The vertices of the mesh.
	 * @param indices
	 *            The indices of the vertices of each quad in the mesh.
	 * @param thickness
	 *            The distance to extrude vertices where necessary.
	 * @return A list containing the convex parts.
	 */
	public static List<Polyhedron> partitionSurface(float[] verts,
			short[] indices, float thickness) {
		// TODO write surface partitioning code
		return null;
	}
	
	/**
	 * Parses the given mesh data into a list of features.
	 * @param verts The vertices of the mesh.
	 * @param indices The indices of the vertices of each quad in the mesh.
	 * @return A list containing the features of the mesh.
	 */
	public static List<Feature> featureMesh(float[] verts, short[] indices){
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Feature> features = new ArrayList<Feature>();
		int count = indices.length;
		short[] edgeIndices = new short[count];
		
		for (int i = 0; i < verts.length; i += 3){
			Vertex vert = new Vertex(verts[i], verts[i+1], verts[i+2]);
			vertices.add(vert);
			features.add(vert);
		}
		
		for (int i = 0; i < count; i += 4){
			Vertex[] faceVert = new Vertex[4];
			faceVert[0] = vertices.get(indices[i + 0]);
			faceVert[1] = vertices.get(indices[i + 1]);
			faceVert[2] = vertices.get(indices[i + 2]);
			faceVert[3] = vertices.get(indices[i + 3]);
			for (int j = 0; j < 4; j++) {
				Edge edge;
				if (i < 3){
					edge = new Edge(faceVert[j], faceVert[j + 1]);
				} else {
					edge = new Edge(faceVert[j], faceVert[0]);
				}
				if(!features.contains(edge)){
					edgeIndices[i + j] = (short) features.size();
					features.add(edge);
				} else {
					edgeIndices[i + j] = (short) features.indexOf(edge);
				}
			}
			
		}
		
		for (int i = 0; i < count; i += 4){
			Vertex point0 = new Vertex(verts[indices[i*3 + 0]], verts[indices[i*3 + 1]], verts[indices[i*3 + 2]]);
			Vertex point1 = new Vertex(verts[indices[(i+1)*3]], verts[indices[(i+1)*3 + 1]], verts[indices[(i+1)*3 + 2]]);
			Vertex point2 = new Vertex(verts[indices[(i+3)*3]], verts[indices[(i+3)*3 + 1]], verts[indices[(i+3)*3 + 2]]);
			Vector normal = new Vector(point0, point1, true)
							.cross(
							new Vector(point0, point2, true));
			Face face = new Face(normal,
					(Edge) features.get(edgeIndices[i + 0]),
					(Edge) features.get(edgeIndices[i + 1]),
					(Edge) features.get(edgeIndices[i + 2]),
					(Edge) features.get(edgeIndices[i + 3]));
			features.add(face);
		}
		return features;
	}

	@SuppressWarnings("unused")
	private List<Feature> features;

	/**
	 * @param features
	 *            A <code>List</code> of the <code>Feature</code>s in this
	 *            polyhedron.
	 */
	public Polyhedron(List<Feature> features) {
		this.features = features;
	}

}
