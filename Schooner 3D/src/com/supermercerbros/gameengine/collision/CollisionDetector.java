package com.supermercerbros.gameengine.collision;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import com.supermercerbros.gameengine.util.LoopingThread;
import com.supermercerbros.gameengine.util.Utils;

public class CollisionDetector extends LoopingThread {
	public static final double CONSTRAINT_CHECK_TOLERANCE = -0.000;
	
	public static final boolean DEBUGGING = true;

	static CollisionDetector instance;

	private final LinkedList<Collider> objects;
	private final OnCollisionCheckFinishedListener listener;

	private Matrix mA, mB;
	/**
	 * The closest point on A
	 */
	private Point cpA;
	/**
	 * The closest point on B
	 */
	private Point cpB;
	private double distance;
	private boolean a = true;
	
	private DebugListener dbg;
	
	/**
	 * For debugging purposes. TODO remove debug.
	 * @param listener
	 */
	public void setDebugCallback(DebugListener listener) {
		dbg = listener;
	}

	public CollisionDetector(OnCollisionCheckFinishedListener listener) {
		super();
		this.setName("CollisionDetector");
		this.objects = new LinkedList<Collider>();
		this.listener = listener;
		super.setIntermittent(true);
		instance = this;
	}
	
	public synchronized void addCollider(Collider c) {
		System.out.println("adding Collider " + c);
		objects.add(c);
	}
	
	public synchronized void removeCollider(Collider c) {
		objects.remove();
	}

	@Override
	protected synchronized void loop() {
		final long startTime = System.currentTimeMillis();

		for (ListIterator<Collider> i = objects.listIterator(); i.hasNext();) {

			final Collider a = i.next();
			if (!i.hasNext()) {
				break;
			}
			a.clearCollisions();
			final Bounds boundsA = a.getBounds();
			mA = new Matrix(a.getMatrix());

			for (ListIterator<Collider> j = objects.listIterator(i.nextIndex()); j
					.hasNext();) {

				final Collider b = j.next();
				final Bounds boundsB = b.getBounds();

				mB = new Matrix(b.getMatrix());

				final Matrix ab = new Matrix(a.getMatrix(), b.getMatrix());
				final Matrix ba = new Matrix(b.getMatrix(), a.getMatrix());

				// For every pair of objects...
				
				if (DEBUGGING) {
					if (boundsA == null) {
						System.err.println("boundsA == null");
						System.out.println("a == " + a);
					}
					if (boundsB == null) {
						System.err.println("boundsB == null");
						System.out.println("b == " + b);
					}
				}

				for (final Polyhedron polyA : boundsA.parts) {
					for (final Polyhedron polyB : boundsB.parts) {
						try {
							double dist = check(a, b, polyA, polyB, ab, ba);
							if (dist < boundsA.buffer + boundsB.buffer) {
								final LinkedList<Point> points = new LinkedList<Point>();
								points.add(Point.average(cpA, cpB));
								throw new Collision(new Vector(cpA, cpB, 1.0f), a, points);
							}
							if (dbg != null) {
								dbg.closestPoints(cpA, cpB);
							}
						} catch (Collision collision) {
							System.out.println(collision);
							a.addCollision(b, collision);
							b.addCollision(a, collision);
						}
					}
				}

			}
		}
		
		if (dbg != null) {
			dbg.onFrameComplete(System.currentTimeMillis() - startTime);
		}
	}

	@Override
	public void afterPause() {
		listener.onCollisionCheckFinished();
	}

	/**
	 * Should be called whenever a Feature.test(Feature) implementation (except
	 * Face.test(Face)) returns null.
	 * 
	 * @param p
	 */
	private void setCP(Point p) {
		System.out.println("Setting closest point to " + p);
		if (a) {
			System.out.println("point is in polyA's axes");
			cpB = mA.transform(p.x, p.y, p.z);
			a = false;
		} else {
			System.out.println("point is in polyB's axes");
			cpA = mB.transform(p.x, p.y, p.z);
			a = true;
		}
	}

	static void setClosestPoint(Point p) {
		instance.setCP(p);
	}

	/**
	 * Should only be called by Face.test(Face)
	 * 
	 * @param d
	 */
	private void setDist(double d) {
		if (a) {
			distance = d;
		} else {
			distance = (distance + d) / 2;
		}
	}

	static void setDistance(double d) {
		instance.setDist(d);
	}

	private double check(Collider colA, Collider colB, Polyhedron polyA, Polyhedron polyB, Matrix ab,
			Matrix ba) throws Collision {
		Feature closestA = polyA.getLastClosest(polyB);
		Feature closestB = polyB.getLastClosest(polyA);
		Feature lastClosestA = closestA, lastClosestB = closestB;

		while (true) {
			try {
				System.out.println(closestA + " : " + closestB);

				a = true;
				Feature nextA;
				try {
					nextA = closestA.test(closestB, ab);
				} catch (LocalDistMinimum ldm) {
					closestA = handleLocalMinimum(closestA, ldm.p, polyA);
					continue;
				}

				a = false;
				Feature nextB;
				try {
					nextB = closestB.test(closestA, ba);
				} catch (LocalDistMinimum ldm) {
					closestB = handleLocalMinimum(closestB, ldm.p, polyB);
					continue;
				}

				if (nextA == null && nextB == null) {
					polyA.setClosest(polyB, closestA);
					polyB.setClosest(polyA, closestB);
					System.out.println("Closest Features: " + closestA + ", "
							+ closestB);
					System.out.println("cpA = " + cpA + ", cpB = " + cpB);
					break; // Closest points have been found
				} else {
					if (lastClosestA == nextA && lastClosestB == nextB) {
						// We're not getting anywhere, so let's momentarily stop
						// closestA from advancing.
						lastClosestB = closestB;
						closestB = nextB;
					} else {
						if (nextA != null) {
							lastClosestA = closestA;
							closestA = nextA;
						}
						if (nextB != null) {
							lastClosestB = closestB;
							closestB = nextB;
						}
					}
				}

			} catch (Intersection i) {
				if (closestA instanceof Face) {
					throw findIntersection(colA, (Face) closestA, polyA, mA, mB,
							polyB, closestB, (i.point != null ) ? i.point.transform(mA) : null);
				} else {
					throw findIntersection(colB, (Face) closestB, polyB, mB, mA,
							polyA, closestA, (i.point != null ) ? i.point.transform(mB) : null);
				}
			}
		}

		if (distance != 0.0) {
			return distance;
		} else {
			return Utils.pythagD(cpA.x - cpB.x, cpA.y - cpB.y, cpA.z - cpB.z);
		}
	}

	/**
	 * Finds all intersections between <code>Edge</code>s and <code>Face</code>s
	 * in <code>facePoly</code> and <code>otherPoly</code>. The collision
	 * normals are taken relative to the faces of aPoly.
	 * 
	 * @param a
	 *            The <code>Face</code> behind which <code>b</code> lies.
	 * @param aPoly
	 *            The <code>Polyhedron</code> containing <code>a</code>
	 * @param aM
	 *            The Matrix that transforms a's coordinates into world
	 *            coordinates.
	 * @param bM
	 *            The Matrix that transforms b's coordinates into world
	 *            coordinates.
	 * @param bPoly
	 *            The <code>Polyhedron</code> containing <code>b</code>
	 * @param b
	 *            The <code>Feature</code> that was found to be behind
	 *            <code>a</code>
	 * @param point
	 *            The point of intersection between <code>a</code> and
	 *            <code>b</code>, if <code>b</code> is an
	 *            <code>Edge</code>. In <code>a</code>'s coordinates.
	 * 
	 * @return The <code>Collision</code> between the polyhedra
	 */
	private static Collision findIntersection(final Collider aCollider, final Face a,
			final Polyhedron aPoly, final Matrix aM, final Matrix bM,
			final Polyhedron bPoly, final Feature b, final Point point) {
		
		System.out.println("fI(" + a + ", " + b + ", " + point + ")");

		final ArrayList<Point> collisionPoints = new ArrayList<Point>();
		final ArrayList<Vector> collisionVectors = new ArrayList<Vector>();

		// Contains the verts that still need to be continued through. These are
		// inside the other poly
		final ArrayList<Vertex> vertsToCheck = new ArrayList<Vertex>();

		// Contains the verts that have already been continued through. Also
		// inside the other poly
		final ArrayList<Vertex> checkedVerts = new ArrayList<Vertex>();

		final ArrayList<Edge> checkedEdges = new ArrayList<Edge>();

		if (b instanceof Vertex) { // if b is a Vertex
			vertsToCheck.add((Vertex) b);

		} else if (b instanceof Edge) { // if b is an Edge
			final Edge edge = (Edge) b;
			
			if (point != null) {
				final Vertex potentialInside;
				if (a.pointIsInFront(edge.head)) {
					potentialInside = edge.tail;
				} else {
					potentialInside = edge.head;
				}
				final Point worldPotIn = potentialInside.transform(bM);
				
				boolean inside = true;
				for (final Feature feature : aPoly.features) {
					if (feature instanceof Face) {
						final Face face = (Face) feature;
						final Plane worldFace = face.transform(aM);
						
						if (worldFace.pointIsInFront(worldPotIn)) {
							inside = false;
							final Point intersection = worldFace.intersect(edge
									.transform(bM));
							if (face.pointPasses(intersection, aM)) {
								collisionPoints.add(intersection);
								collisionVectors.add(worldFace.normal);
								break;
							}
						}
					}
				}
				
				if (inside) {
					collisionPoints.add(point.transform(aM));
					collisionVectors.add(a.normal.transform(aM));
				}
			} else {
				
			}


		} else if (b instanceof Face) { // if b is a Face
			for (final Edge edge : ((Face) b).getEdges()) {
				final Point p = edge.head.transform(bM);

				boolean inside = true;
				for (final Feature f : aPoly.features) {
					if (f instanceof Face && ((Face) f).transform(aM).pointIsInFront(p)) {
						inside = false;
						break;
					}
				}

				if (inside) {
					vertsToCheck.add(edge.head);
				}
			}
		}

		while (!vertsToCheck.isEmpty()) {
			final int size = vertsToCheck.size();

			for (int i = 0; i < size; i++) {
				final Vertex v = vertsToCheck.remove(0);
				checkedVerts.add(v);

				for (final Edge edge : v.getEdges()) {
					final Vertex opposite = edge.getOpposite(v);
					if (checkedEdges.contains(edge)
							|| checkedVerts.contains(opposite)) {
						continue;
					}

					// For each edge in v's coboundary that has not already been
					// checked

					boolean outside = false;
					for (final Feature f : aPoly.features) {
						// for each Face in aPoly...
						if (!(f instanceof Face)) {
							continue;
						}
						Face face2 = (Face) f;

						final Plane worldFace2 = face2.transform(aM);

						if (worldFace2.pointIsInFront(opposite.transform(bM))) {
							final Point intersection = worldFace2
									.intersect(edge.transform(bM));
							outside = true;

							if (intersection != null) {
								// edge is intersecting face2

								if (face2.pointPasses(intersection, aM)) {
									collisionPoints.add(intersection);
									collisionVectors.add(worldFace2.normal);
									checkedEdges.add(edge);
									break;
								}
							}
						}
					}

					// If opposite is not outside, add it to the list to be
					// checked.
					if (!outside) {
						vertsToCheck.add(opposite);
					}
				}
			}
		}

		vertsToCheck.clear();
		checkedVerts.clear();
		checkedEdges.clear();
		
		// Now we do it again, check aPoly's edges against bPoly's faces.

		for (final Edge faceAEdge : a.getEdges()) {
			final Point p = faceAEdge.head.transform(aM);

			boolean inside = true;
			for (final Feature f : bPoly.features) {
				if (f instanceof Face && ((Face) f).transform(bM).pointIsInFront(p)) {
					inside = false;
					break;
				}
			}

			if (inside) {
				vertsToCheck.add(faceAEdge.head);
			}
		}

		while (!vertsToCheck.isEmpty()) {
			final int size = vertsToCheck.size();

			for (int i = 0; i < size; i++) {
				final Vertex v = vertsToCheck.remove(0);
				checkedVerts.add(v);

				for (final Edge edge : v.getEdges()) {
					final Vertex opposite = edge.getOpposite(v);
					if (checkedEdges.contains(edge)
							|| checkedVerts.contains(opposite)) {
						continue;
					}
					final Point tOpposite = opposite.transform(bM);

					// For each edge in v's coboundary that has not already been
					// checked

					boolean outside = false;
					for (final Feature f : bPoly.features) {
						// for each Face in bPoly...
						if (!(f instanceof Face)) {
							continue;
						}
						Face face2 = (Face) f;

						final Plane worldFace2 = face2.transform(bM);

						if (worldFace2.pointIsInFront(tOpposite)) {
							final Point intersection = worldFace2
									.intersect(edge.transform(aM));
							outside = true;

							if (intersection != null) {
								// edge is intersecting face2

								if (face2.pointPasses(intersection, bM)) {
									collisionPoints.add(intersection);
									collisionVectors.add(worldFace2.normal
											.flip());
									checkedEdges.add(edge);
									break;
								}
							}
						}
					}

					// If opposite is not outside, add it to the list to be
					// checked.
					if (!outside) {
						vertsToCheck.add(opposite);
					}
				}
			}
		}
		
		float x = 0, y = 0, z = 0;
		for (final Vector vec : collisionVectors) {
			x += vec.x;
			y += vec.y;
			z += vec.z;
		}
		return new Collision(new Vector(x, y, z, 1), aCollider, collisionPoints);

	}

	/**
	 * Handles a local distance minimum.
	 * 
	 * @param face
	 *            The {@link Face} behind which point <code>p</code> lies.
	 * @param p
	 *            The "closest" point on the other polyhedron, transformed to
	 *            <code>face</code>'s coordinate system.
	 * @param poly
	 *            The {@link Polyhedron} containing <code>face</code>.
	 * @return A <code>Face</code> in <code>poly</code> that is facing
	 *         <code>p</code>.
	 * 
	 * @throws Intersection
	 *             if no such <code>Face</code> can be found.
	 * 
	 * @see {@link LocalDistMinimum}
	 */
	private static Feature handleLocalMinimum(Feature face, Point p,
			Polyhedron poly) throws Intersection {
		System.out.println("Local Distance Minimum: " + p + " is behind "
				+ face);

		for (final Feature feature : poly.features) {
			if (feature instanceof Face && ((Face) feature).pointIsInFront(p)) {
				System.out.println(p + " is in front of " + feature);
				return feature;
			}
		}

		throw new Intersection();
	}

	/**
	 * Call this method to run the CollisionDetector. Can be called repeatedly.
	 * Does nothing if the CollisionDetector is alive and not paused.
	 */
	public synchronized void go() {
		if (super.hasBeenStarted()) {
			super.resumeLooping();
		} else {
			super.start();
		}
	}

	/**
	 * @deprecated Use {@link #go()} instead.
	 */
	@Deprecated
	@Override
	public void resumeLooping() {
		throw new UnsupportedOperationException("Use go() instead.");
	}

	/**
	 * @deprecated Use {@link #go()} instead.
	 */
	@Deprecated
	@Override
	public void start() {
		throw new UnsupportedOperationException("Use go() instead.");
	}

}
