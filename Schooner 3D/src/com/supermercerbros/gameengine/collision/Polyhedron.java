package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.supermercerbros.gameengine.util.Utils;

/**
 * Represents a <a href=http://en.wikipedia.org/wiki/Polyhedron>Polyhedron</a>.
 * Also contains utilities for partitioning <a
 * href=http://en.wikipedia.org/wiki/Convex_polyhedra>convex</a> polyhedra.
 */
public class Polyhedron {
	public static final float PLANE_CHECK_TOLERANCE = .001f;

	/**
	 * Partitions a closed mesh of quads into convex subparts.
	 * 
	 * @param verts
	 *            The vertices of the mesh.
	 * @param indices
	 *            The indices of the vertices of each quad in the mesh.
	 * @return A list containing the convex parts.
	 */
	public static LinkedList<Polyhedron> partitionMesh(float[] verts,
			short[] indices) {
		ArrayList<Integer> workingList = new ArrayList<Integer>();
		workingList.add(0);
		int workingListIndex = 0;

		int groupCount = 1, faceCount = indices.length / 4;
		int[] faceGroups = new int[faceCount];
		faceGroups[0] = 1;
		while (workingList.size() > workingListIndex) {
			// Get next face (f1) to study
			int face = workingList.get(workingListIndex);

			// If the face (f1) has not been grouped
			if (faceGroups[face] != 0) {
				continue;
			}

			float[] groupTestResults = new float[groupCount];
			// For each face (f2)
			for (int f2 = 0; f2 < faceCount; f2++) {
				int groupOfFaceToCheck = faceGroups[f2];
				if (groupOfFaceToCheck == 0) {
					// If f2 has not been grouped, continue to next f2
					continue;
				} else {
					float currentResult = groupTestResults[groupOfFaceToCheck];
					if (currentResult >= 0.0f) {
						// If f2's group has not already failed for f1,
						// check with f1
						if (doFacesAgree(verts, indices, f2, face)) {
							float distance = faceDistance(verts, indices, face,
									f2);
							if (distance < currentResult) {
								groupTestResults[groupOfFaceToCheck] = distance;
							}
						} else {
							groupTestResults[groupOfFaceToCheck] = -1.0f;
						}
					}
				}
			}

			int bestGroup = -1;
			// Put the f1 in the group that it is closest to
			for (int i = 0; i < groupTestResults.length; i++) {

				if (groupTestResults[i] == -1.0f) {
					// the face (f1) did not fit in Group [i + 1]
					continue;

				} else if (bestGroup == -1) {
					// Group [i + 1] is the first group that fits.
					bestGroup = i;

				} else if (groupTestResults[i] < groupTestResults[bestGroup]) {
					// Group [i + 1] is the new best fit.
					bestGroup = i;
				}
			}

			if (bestGroup == -1) {
				// If f1 didn't fit in any group, make a new one for it.
				groupCount++;
				faceGroups[face] = groupCount;

			} else {
				// If it did, put it in the best group.
				faceGroups[face] = bestGroup + 1;
			}

			// Add neighbors to workingList if not already contained
			for (int tail = 0; tail < 4; tail++) {
				int head = (tail < 3) ? tail + 1 : 0;
				int neighbor = findFaceWithEdge(face, indices, tail, head);
				if (!workingList.contains(neighbor)) {
					workingList.add(neighbor);
				}
			}

			workingListIndex++;

		}
		LinkedList<ArrayList<Short>> groupIndices = new LinkedList<ArrayList<Short>>();
		for (int i = 0; i < groupCount; i++) {
			// For each group, create an ArrayList to contain the indices of the
			// faces in that group

			groupIndices.add(new ArrayList<Short>());
		}
		for (int i = 0; i < faceCount; i++) {
			// For each face, add that face's indices to its group's index list

			ArrayList<Short> groupIndexList = groupIndices.get(faceGroups[i]);
			groupIndexList.add(indices[i * 4 + 0]);
			groupIndexList.add(indices[i * 4 + 1]);
			groupIndexList.add(indices[i * 4 + 2]);
			groupIndexList.add(indices[i * 4 + 3]);
		}

		LinkedList<Polyhedron> subPolyhedra = new LinkedList<Polyhedron>();
		for (ArrayList<Short> groupIndexList : groupIndices) {
			// For each group, create a short array containing its indices

			int size = groupIndexList.size();
			short[] groupIndexArray = new short[size];
			for (int i = 0; i < size; i++) {
				groupIndexArray[i] = groupIndexList.get(i).shortValue();
			}

			// Use that index array and the verts array to create a list of
			// features. Seal the holes in that list of features, wrap it in a
			// Polyhedron, and add that to the subPolyhedra list
			ArrayList<Feature> subPartFeatures = featureMesh(verts,
					groupIndexArray);
			subPolyhedra.add(new Polyhedron(makeClosed(subPartFeatures)));
		}

		return subPolyhedra;
	}

	/**
	 * Closes any holes in the mesh formed by the list of features.
	 * 
	 * @param features
	 *            A list of features to close.
	 * @return The list of features given, with new Features added where
	 *         necessary.
	 */
	private static ArrayList<Feature> makeClosed(ArrayList<Feature> features) {
		LinkedList<ArrayList<Edge>> holes = new LinkedList<ArrayList<Edge>>();

		// Find and trace all holes in mesh
		for (Feature feature : features) {
			if (!(feature instanceof Edge)) {
				continue;
			}
			Edge edge = (Edge) feature;

			// For every edge in features...

			boolean isNewHole = true;
			for (ArrayList<Edge> hole : holes) {
				// Make sure that the edge's hole has not already been traced.
				if (hole.contains(edge)) {
					isNewHole = false;
					break;
				}
			}

			if (isNewHole && edge.isOpen()) {
				// If this is a new hole, trace it and add it to our list of
				// holes
				holes.add(traceHole(edge));
			}
		}

		// Close the holes
		for (ArrayList<Edge> hole : holes) {
			while (hole.size() > 3) {
				Edge a = hole.remove(0);
				Edge b = hole.remove(1);
				Edge newEdge = web(a, b, features);
				hole.add(0, newEdge);

				if (hole.size() > 3) {
					int size = hole.size();
					Edge c = hole.remove(size - 1);
					Edge d = hole.remove(size - 2);
					Edge newEdge2 = web(c, d, features);
					hole.add(0, newEdge2);
				}
			}

			// Close last three edges of hole
			Edge a = hole.remove(0);
			Edge b = hole.remove(1);
			Edge c = hole.remove(2);
			Vector normal;
			if (a.getHead() == b.getHead() || a.getHead() == b.getTail()) {
				if (a.getRight() != null) {
					// A is on left of new triangle from top.
					normal = b.asVector().cross(a.asVector()).normalize();
				} else {
					// A is on right of new triangle from top.
					normal = a.asVector().cross(b.asVector()).normalize();
				}
			} else {
				if (a.getRight() != null) {
					// A is on right of new triangle from top.
					normal = a.asVector().cross(b.asVector()).normalize();
				} else {
					// A is on left of new triangle from top.
					normal = b.asVector().cross(a.asVector()).normalize();
				}
			}
			features.add(new Face(normal, a, b, c));
		}
		return features;
	}

	/**
	 * Traces the border of a hole in a mesh.
	 * 
	 * @param edge
	 *            An edge in the border of the hole
	 * @return An ArrayList of the Edges that form the hole's boundary
	 */
	private static ArrayList<Edge> traceHole(Edge edge) {
		if (!edge.isOpen()) {
			throw new IllegalArgumentException(
					"Given edge does not border a hole.");
		}
		ArrayList<Edge> holeEdges = new ArrayList<Edge>();

		Vertex head = edge.getHead(), next = head;

		for (int i = 0; i < next.getEdges().size(); i++) {
			Edge e = next.getEdges().get(i);

			boolean thisEdgeFound = false;
			if (e.isOpen()) {

				if (!holeEdges.contains(e)) {
					holeEdges.add(e);
					next = e.getOpposite(next);
					i = -1; // Reset counter
					continue;
				} else {
					// If this block is reached twice, both open edges of next
					// have already been traced, so we're done tracing the hole
					if (thisEdgeFound) {
						break;
					} else {
						thisEdgeFound = true;
					}
				}

			} else if (i + 1 == next.getEdges().size()) {
				throw new IllegalArgumentException(
						"Hole border could not be traced: dead end reached.");
			}
		}

		return holeEdges;
	}

	/**
	 * Puts a triangle between the two edges.
	 * 
	 * @param a
	 *            Edge A
	 * @param b
	 *            Edge B
	 * @param features
	 *            The new face and edge are added to this List.
	 * @return The new edge that was created between the separate endpoints of
	 *         the edges.
	 */
	private static Edge web(Edge a, Edge b, List<Feature> features) {
		final Vertex aHead = a.getHead(), aTail = a.getTail(), bHead = b
				.getHead(), bTail = b.getTail();
		Vertex vA, vB;
		Vector normal;

		if (aHead == bHead) {
			vA = aTail;
			vB = bTail;
			if (a.getRight() != null) {
				// A is on left of new triangle from top.
				normal = b.asVector().cross(a.asVector()).normalize();
			} else {
				// A is on right of new triangle from top.
				normal = a.asVector().cross(b.asVector()).normalize();
			}
		} else if (aHead == bTail) {
			vA = aTail;
			vB = bHead;
			if (a.getRight() != null) {
				// A is on left of new triangle from top.
				normal = b.asVector().cross(a.asVector()).normalize();
			} else {
				// A is on right of new triangle from top.
				normal = a.asVector().cross(b.asVector()).normalize();
			}
		} else if (aTail == bHead) {
			vA = aHead;
			vB = bTail;
			if (a.getRight() != null) {
				// A is on right of new triangle from top.
				normal = a.asVector().cross(b.asVector()).normalize();
			} else {
				// A is on left of new triangle from top.
				normal = b.asVector().cross(a.asVector()).normalize();
			}
		} else if (aTail == bTail) {
			vA = aHead;
			vB = bHead;
			if (a.getRight() != null) {
				// A is on right of new triangle from top.
				normal = a.asVector().cross(b.asVector()).normalize();
			} else {
				// A is on left of new triangle from top.
				normal = b.asVector().cross(a.asVector()).normalize();
			}
		} else {
			throw new IllegalArgumentException("Edges are not neighboring.");
		}

		Edge newEdge = new Edge(vA, vB);
		features.add(newEdge);
		Face newFace = new Face(normal, a, b, newEdge);
		features.add(newFace);
		return newEdge;
	}

	/**
	 * Finds a face that shares an edge
	 * 
	 * @param currentFace
	 * @param indices
	 * @param tailIndex
	 * @param headIndex
	 * @return
	 */
	private static int findFaceWithEdge(int currentFace, short[] indices,
			int tailIndex, int headIndex) {
		for (int i = 0; i < indices.length / 4; i++) {
			if (i == currentFace) {
				continue;
			}
			boolean containsHead = false, containsTail = false;
			containsHead |= indices[i * 4 + 0] == headIndex;
			containsHead |= indices[i * 4 + 1] == headIndex;
			containsHead |= indices[i * 4 + 2] == headIndex;
			containsHead |= indices[i * 4 + 3] == headIndex;

			containsTail |= indices[i * 4 + 0] == tailIndex;
			containsTail |= indices[i * 4 + 1] == tailIndex;
			containsTail |= indices[i * 4 + 2] == tailIndex;
			containsTail |= indices[i * 4 + 3] == tailIndex;

			if (containsHead && containsTail) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if two quads are facing away from each other.
	 * 
	 * @param verts
	 * @param indices
	 * @param faceA
	 * @param faceB
	 * @return
	 */
	private static boolean doFacesAgree(float[] verts, short[] indices,
			int faceA, int faceB) {
		float[] a = { verts[indices[faceA * 4 + 0] * 3 + 0],
				verts[indices[faceA * 4 + 0] * 3 + 1],
				verts[indices[faceA * 4 + 0] * 3 + 2],
				verts[indices[faceA * 4 + 1] * 3 + 0],
				verts[indices[faceA * 4 + 1] * 3 + 1],
				verts[indices[faceA * 4 + 1] * 3 + 2],
				verts[indices[faceA * 4 + 2] * 3 + 0],
				verts[indices[faceA * 4 + 2] * 3 + 1],
				verts[indices[faceA * 4 + 2] * 3 + 2],
				verts[indices[faceA * 4 + 3] * 3 + 0],
				verts[indices[faceA * 4 + 3] * 3 + 1],
				verts[indices[faceA * 4 + 3] * 3 + 2], };

		float[] b = { verts[indices[faceB * 4 + 0] * 3 + 0],
				verts[indices[faceB * 4 + 0] * 3 + 1],
				verts[indices[faceB * 4 + 0] * 3 + 2],
				verts[indices[faceB * 4 + 1] * 3 + 0],
				verts[indices[faceB * 4 + 1] * 3 + 1],
				verts[indices[faceB * 4 + 1] * 3 + 2],
				verts[indices[faceB * 4 + 2] * 3 + 0],
				verts[indices[faceB * 4 + 2] * 3 + 1],
				verts[indices[faceB * 4 + 2] * 3 + 2],
				verts[indices[faceB * 4 + 3] * 3 + 0],
				verts[indices[faceB * 4 + 3] * 3 + 1],
				verts[indices[faceB * 4 + 3] * 3 + 2], };

		Vector normalA = new Vector(a[0] - a[3], a[1] - a[4], a[2] - a[5], true)
				.cross(new Vector(a[6] - a[3], a[7] - a[4], a[8] - a[5], true));

		Vector normalB = new Vector(b[0] - b[3], b[1] - b[4], b[2] - b[5], true)
				.cross(new Vector(b[6] - b[3], b[7] - b[4], b[8] - b[5], true));

		Point pointA = new Vertex(a[0], a[1], a[2]);
		Point pointB = new Vertex(b[0], b[1], b[2]);

		Plane planeA = new Plane(pointA, normalA);
		Plane planeB = new Plane(pointB, normalB);

		for (int i = 0; i < 4; i++) {
			if (!planeB.pointIsInFront(a[i * 3 + 0], a[i * 3 + 1],
					a[i * 3 + 2], PLANE_CHECK_TOLERANCE)) {
				return false;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (!planeA.pointIsInFront(b[i * 3 + 0], b[i * 3 + 1],
					b[i * 3 + 2], PLANE_CHECK_TOLERANCE)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Finds the distance between the centerpoints of the given faces.
	 * 
	 * @param verts
	 * @param indices
	 * @param faceA
	 * @param faceB
	 * @return
	 */
	private static float faceDistance(float[] verts, short[] indices,
			int faceA, int faceB) {

		float centerAX = 0.0f, centerAY = 0.0f, centerAZ = 0.0f, centerBX = 0.0f, centerBY = 0.0f, centerBZ = 0.0f;

		// Compute center points of faces
		for (int i = 0; i < 4; i++) {
			int vertexA = faceA * 4 + i, vertexB = faceB * 4 + i;
			centerAX += verts[indices[vertexA] * 3 + 0];
			centerAY += verts[indices[vertexA] * 3 + 1];
			centerAZ += verts[indices[vertexA] * 3 + 2];

			centerBX += verts[indices[vertexB] * 3 + 0];
			centerBY += verts[indices[vertexB] * 3 + 1];
			centerBZ += verts[indices[vertexB] * 3 + 2];
		}

		centerAX /= 4;
		centerAY /= 4;
		centerAZ /= 4;

		centerBX /= 4;
		centerBY /= 4;
		centerBZ /= 4;

		// Return distance between centerpoints.
		return Utils.pythagF(centerAX - centerBX, centerAY - centerBY, centerAZ
				- centerBZ);
	}

	/**
	 * Parses the given quad mesh data into a list of features.
	 * 
	 * @param verts
	 *            The vertices of the mesh.
	 * @param indices
	 *            The indices of the vertices of each quad in the mesh.
	 * @return A list containing the features of the mesh.
	 */
	public static ArrayList<Feature> featureMesh(float[] verts, short[] indices) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Feature> features = new ArrayList<Feature>();
		int count = indices.length;
		short[] edgeIndices = new short[count];

		for (int i = 0; i < verts.length; i += 3) {
			Vertex vert = new Vertex(verts[i], verts[i + 1], verts[i + 2]);
			vertices.add(vert);
			features.add(vert);
		}

		for (int i = 0; i < count; i += 4) {
			Vertex[] faceVert = new Vertex[4];
			faceVert[0] = vertices.get(indices[i + 0]);
			faceVert[1] = vertices.get(indices[i + 1]);
			faceVert[2] = vertices.get(indices[i + 2]);
			faceVert[3] = vertices.get(indices[i + 3]);
			for (int j = 0; j < 4; j++) {
				Edge edge;
				if (i < 3) {
					edge = new Edge(faceVert[j], faceVert[j + 1]);
				} else {
					edge = new Edge(faceVert[j], faceVert[0]);
				}
				if (!features.contains(edge)) {
					edgeIndices[i + j] = (short) features.size();
					features.add(edge);
				} else {
					edgeIndices[i + j] = (short) features.indexOf(edge);
				}
			}

		}

		for (int i = 0; i < count; i += 4) {
			Vertex point0 = new Vertex(verts[indices[i * 3 + 0]],
					verts[indices[i * 3 + 1]], verts[indices[i * 3 + 2]]);
			Vertex point1 = new Vertex(verts[indices[(i + 1) * 3]],
					verts[indices[(i + 1) * 3 + 1]],
					verts[indices[(i + 1) * 3 + 2]]);
			Vertex point2 = new Vertex(verts[indices[(i + 3) * 3]],
					verts[indices[(i + 3) * 3 + 1]],
					verts[indices[(i + 3) * 3 + 2]]);
			Vector normal = new Vector(point0, point1, true).cross(new Vector(
					point0, point2, true));
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
	 *            A {@link List} of the {@link Feature} in this polyhedron.
	 */
	public Polyhedron(List<Feature> features) {
		this.features = features;
		for (Feature feature : features) {
			feature.lock();
		}
	}

}
