/*******************************************************************************************************
 *
 * Dbvt.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

// Dbvt implementation by Nathanael Presson

package com.bulletphysics.collision.broadphase;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.IntArrayList;

/**
 *
 * @author jezek2
 */
public class Dbvt {

	/** The Constant SIMPLE_STACKSIZE. */
	public static final int SIMPLE_STACKSIZE = 64;

	/** The Constant DOUBLE_STACKSIZE. */
	public static final int DOUBLE_STACKSIZE = SIMPLE_STACKSIZE * 2;

	/** The root. */
	public Node root = null;

	/** The free. */
	public Node free = null;

	/** The lkhd. */
	public int lkhd = -1;

	/** The leaves. */
	public int leaves = 0;

	/** The opath. */
	public /* unsigned */ int opath = 0;

	/**
	 * Instantiates a new dbvt.
	 */
	public Dbvt() {}

	/**
	 * Clear.
	 */
	public void clear() {
		if (root != null) { recursedeletenode(this, root); }
		// btAlignedFree(m_free);
		free = null;
	}

	/**
	 * Empty.
	 *
	 * @return true, if successful
	 */
	public boolean empty() {
		return root == null;
	}

	/**
	 * Optimize bottom up.
	 */
	public void optimizeBottomUp() {
		if (root != null) {
			ArrayList<Node> leaves = new ArrayList<>(this.leaves);
			fetchleaves(this, root, leaves);
			bottomup(this, leaves);
			root = leaves.get(0);
		}
	}

	/**
	 * Optimize top down.
	 */
	public void optimizeTopDown() {
		optimizeTopDown(128);
	}

	/**
	 * Optimize top down.
	 *
	 * @param bu_treshold
	 *            the bu treshold
	 */
	public void optimizeTopDown(final int bu_treshold) {
		if (root != null) {
			ArrayList<Node> leaves = new ArrayList<>(this.leaves);
			fetchleaves(this, root, leaves);
			root = topdown(this, leaves, bu_treshold);
		}
	}

	/**
	 * Optimize incremental.
	 *
	 * @param passes
	 *            the passes
	 */
	public void optimizeIncremental(int passes) {
		if (passes < 0) { passes = leaves; }

		if (root != null && passes > 0) {
			Node[] root_ref = new Node[1];
			do {
				Node node = root;
				int bit = 0;
				while (node.isinternal()) {
					root_ref[0] = root;
					node = sort(node, root_ref).childs[opath >>> bit & 1];
					root = root_ref[0];

					bit = bit + 1 & 4 * 8 - 1;
				}
				update(node);
				++opath;
			} while (--passes != 0);
		}
	}

	/**
	 * Insert.
	 *
	 * @param box
	 *            the box
	 * @param data
	 *            the data
	 * @return the node
	 */
	public Node insert(final DbvtAabbMm box, final Object data) {
		Node leaf = createnode(this, null, box, data);
		insertleaf(this, root, leaf);
		leaves++;
		return leaf;
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 */
	public void update(final Node leaf) {
		update(leaf, -1);
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 * @param lookahead
	 *            the lookahead
	 */
	public void update(final Node leaf, final int lookahead) {
		Node root = removeleaf(this, leaf);
		if (root != null) {
			if (lookahead >= 0) {
				for (int i = 0; i < lookahead && root.parent != null; i++) { root = root.parent; }
			} else {
				root = this.root;
			}
		}
		insertleaf(this, root, leaf);
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 * @param volume
	 *            the volume
	 */
	public void update(final Node leaf, final DbvtAabbMm volume) {
		Node root = removeleaf(this, leaf);
		if (root != null) {
			if (lkhd >= 0) {
				for (int i = 0; i < lkhd && root.parent != null; i++) { root = root.parent; }
			} else {
				root = this.root;
			}
		}
		leaf.volume.set(volume);
		insertleaf(this, root, leaf);
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 * @param volume
	 *            the volume
	 * @param velocity
	 *            the velocity
	 * @param margin
	 *            the margin
	 * @return true, if successful
	 */
	public boolean update(final Node leaf, final DbvtAabbMm volume, final Vector3f velocity, final float margin) {
		if (leaf.volume.Contain(volume)) return false;
		Vector3f tmp = VECTORS.get();
		tmp.set(margin, margin, margin);
		volume.Expand(tmp);
		volume.SignedExpand(velocity);
		update(leaf, volume);
		return true;
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 * @param volume
	 *            the volume
	 * @param velocity
	 *            the velocity
	 * @return true, if successful
	 */
	public boolean update(final Node leaf, final DbvtAabbMm volume, final Vector3f velocity) {
		if (leaf.volume.Contain(volume)) return false;
		volume.SignedExpand(velocity);
		update(leaf, volume);
		return true;
	}

	/**
	 * Update.
	 *
	 * @param leaf
	 *            the leaf
	 * @param volume
	 *            the volume
	 * @param margin
	 *            the margin
	 * @return true, if successful
	 */
	public boolean update(final Node leaf, final DbvtAabbMm volume, final float margin) {
		if (leaf.volume.Contain(volume)) return false;
		Vector3f tmp = VECTORS.get();
		tmp.set(margin, margin, margin);
		volume.Expand(tmp);
		update(leaf, volume);
		return true;
	}

	/**
	 * Removes the.
	 *
	 * @param leaf
	 *            the leaf
	 */
	public void remove(final Node leaf) {
		removeleaf(this, leaf);
		deletenode(this, leaf);
		leaves--;
	}

	/**
	 * Write.
	 *
	 * @param iwriter
	 *            the iwriter
	 */
	public void write(final IWriter iwriter) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Clone.
	 *
	 * @param dest
	 *            the dest
	 */
	public void clone(final Dbvt dest) {
		clone(dest, null);
	}

	/**
	 * Clone.
	 *
	 * @param dest
	 *            the dest
	 * @param iclone
	 *            the iclone
	 */
	public void clone(final Dbvt dest, final IClone iclone) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Count leaves.
	 *
	 * @param node
	 *            the node
	 * @return the int
	 */
	public static int countLeaves(final Node node) {
		if (node.isinternal()) return countLeaves(node.childs[0]) + countLeaves(node.childs[1]);
		return 1;
	}

	/**
	 * Extract leaves.
	 *
	 * @param node
	 *            the node
	 * @param leaves
	 *            the leaves
	 */
	public static void extractLeaves(final Node node, final ArrayList<Node> leaves) {
		if (node.isinternal()) {
			extractLeaves(node.childs[0], leaves);
			extractLeaves(node.childs[1], leaves);
		} else {
			leaves.add(node);
		}
	}

	/**
	 * Enum nodes.
	 *
	 * @param root
	 *            the root
	 * @param policy
	 *            the policy
	 */
	public static void enumNodes(final Node root, final ICollide policy) {
		// DBVT_CHECKTYPE
		policy.Process(root);
		if (root.isinternal()) {
			enumNodes(root.childs[0], policy);
			enumNodes(root.childs[1], policy);
		}
	}

	/**
	 * Enum leaves.
	 *
	 * @param root
	 *            the root
	 * @param policy
	 *            the policy
	 */
	public static void enumLeaves(final Node root, final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root.isinternal()) {
			enumLeaves(root.childs[0], policy);
			enumLeaves(root.childs[1], policy);
		} else {
			policy.Process(root);
		}
	}

	/**
	 * Collide TT.
	 *
	 * @param root0
	 *            the root 0
	 * @param root1
	 *            the root 1
	 * @param policy
	 *            the policy
	 */
	public static void collideTT(final Node root0, final Node root1, final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root0 != null && root1 != null) {
			ArrayList<sStkNN> stack = new ArrayList<>(DOUBLE_STACKSIZE);
			stack.add(new sStkNN(root0, root1));
			do {
				sStkNN p = stack.remove(stack.size() - 1);
				if (p.a == p.b) {
					if (p.a.isinternal()) {
						stack.add(new sStkNN(p.a.childs[0], p.a.childs[0]));
						stack.add(new sStkNN(p.a.childs[1], p.a.childs[1]));
						stack.add(new sStkNN(p.a.childs[0], p.a.childs[1]));
					}
				} else if (DbvtAabbMm.Intersect(p.a.volume, p.b.volume)) {
					if (p.a.isinternal()) {
						if (p.b.isinternal()) {
							stack.add(new sStkNN(p.a.childs[0], p.b.childs[0]));
							stack.add(new sStkNN(p.a.childs[1], p.b.childs[0]));
							stack.add(new sStkNN(p.a.childs[0], p.b.childs[1]));
							stack.add(new sStkNN(p.a.childs[1], p.b.childs[1]));
						} else {
							stack.add(new sStkNN(p.a.childs[0], p.b));
							stack.add(new sStkNN(p.a.childs[1], p.b));
						}
					} else if (p.b.isinternal()) {
						stack.add(new sStkNN(p.a, p.b.childs[0]));
						stack.add(new sStkNN(p.a, p.b.childs[1]));
					} else {
						policy.Process(p.a, p.b);
					}
				}
			} while (stack.size() > 0);
		}
	}

	/**
	 * Collide TT.
	 *
	 * @param root0
	 *            the root 0
	 * @param root1
	 *            the root 1
	 * @param xform
	 *            the xform
	 * @param policy
	 *            the policy
	 */
	public static void collideTT(final Node root0, final Node root1, final Transform xform, final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root0 != null && root1 != null) {
			ArrayList<sStkNN> stack = new ArrayList<>(DOUBLE_STACKSIZE);
			stack.add(new sStkNN(root0, root1));
			do {
				sStkNN p = stack.remove(stack.size() - 1);
				if (p.a == p.b) {
					if (p.a.isinternal()) {
						stack.add(new sStkNN(p.a.childs[0], p.a.childs[0]));
						stack.add(new sStkNN(p.a.childs[1], p.a.childs[1]));
						stack.add(new sStkNN(p.a.childs[0], p.a.childs[1]));
					}
				} else if (DbvtAabbMm.Intersect(p.a.volume, p.b.volume, xform)) {
					if (p.a.isinternal()) {
						if (p.b.isinternal()) {
							stack.add(new sStkNN(p.a.childs[0], p.b.childs[0]));
							stack.add(new sStkNN(p.a.childs[1], p.b.childs[0]));
							stack.add(new sStkNN(p.a.childs[0], p.b.childs[1]));
							stack.add(new sStkNN(p.a.childs[1], p.b.childs[1]));
						} else {
							stack.add(new sStkNN(p.a.childs[0], p.b));
							stack.add(new sStkNN(p.a.childs[1], p.b));
						}
					} else if (p.b.isinternal()) {
						stack.add(new sStkNN(p.a, p.b.childs[0]));
						stack.add(new sStkNN(p.a, p.b.childs[1]));
					} else {
						policy.Process(p.a, p.b);
					}
				}
			} while (stack.size() > 0);
		}
	}

	/**
	 * Collide TT.
	 *
	 * @param root0
	 *            the root 0
	 * @param xform0
	 *            the xform 0
	 * @param root1
	 *            the root 1
	 * @param xform1
	 *            the xform 1
	 * @param policy
	 *            the policy
	 */
	public static void collideTT(final Node root0, final Transform xform0, final Node root1, final Transform xform1,
			final ICollide policy) {
		Transform xform = TRANSFORMS.get();
		xform.inverse(xform0);
		xform.mul(xform1);
		collideTT(root0, root1, xform, policy);
		TRANSFORMS.release(xform);
	}

	/**
	 * Collide TV.
	 *
	 * @param root
	 *            the root
	 * @param volume
	 *            the volume
	 * @param policy
	 *            the policy
	 */
	public static void collideTV(final Node root, final DbvtAabbMm volume, final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root != null) {
			ArrayList<Node> stack = new ArrayList<>(SIMPLE_STACKSIZE);
			stack.add(root);
			do {
				Node n = stack.remove(stack.size() - 1);
				if (DbvtAabbMm.Intersect(n.volume, volume)) {
					if (n.isinternal()) {
						stack.add(n.childs[0]);
						stack.add(n.childs[1]);
					} else {
						policy.Process(n);
					}
				}
			} while (stack.size() > 0);
		}
	}

	/**
	 * Collide RAY.
	 *
	 * @param root
	 *            the root
	 * @param origin
	 *            the origin
	 * @param direction
	 *            the direction
	 * @param policy
	 *            the policy
	 */
	public static void collideRAY(final Node root, final Vector3f origin, final Vector3f direction,
			final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root != null) {
			Vector3f normal = VECTORS.get();
			normal.normalize(direction);
			Vector3f invdir = VECTORS.get();
			invdir.set(1f / normal.x, 1f / normal.y, 1f / normal.z);
			int[] signs = { direction.x < 0 ? 1 : 0, direction.y < 0 ? 1 : 0, direction.z < 0 ? 1 : 0 };
			ArrayList<Node> stack = new ArrayList<>(SIMPLE_STACKSIZE);
			stack.add(root);
			do {
				Node node = stack.remove(stack.size() - 1);
				if (DbvtAabbMm.Intersect(node.volume, origin, invdir, signs)) {
					if (node.isinternal()) {
						stack.add(node.childs[0]);
						stack.add(node.childs[1]);
					} else {
						policy.Process(node);
					}
				}
			} while (stack.size() != 0);
		}
	}

	/**
	 * Collide KDOP.
	 *
	 * @param root
	 *            the root
	 * @param normals
	 *            the normals
	 * @param offsets
	 *            the offsets
	 * @param count
	 *            the count
	 * @param policy
	 *            the policy
	 */
	public static void collideKDOP(final Node root, final Vector3f[] normals, final float[] offsets, final int count,
			final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root != null) {
			int inside = (1 << count) - 1;
			ArrayList<sStkNP> stack = new ArrayList<>(SIMPLE_STACKSIZE);
			int[] signs = new int[4 * 8];
			assert count < 128 / /* sizeof(signs[0]) */ 4;
			for (int i = 0; i < count; ++i) {
				signs[i] = (normals[i].x >= 0 ? 1 : 0) + (normals[i].y >= 0 ? 2 : 0) + (normals[i].z >= 0 ? 4 : 0);
			}
			stack.add(new sStkNP(root, 0));
			do {
				sStkNP se = stack.remove(stack.size() - 1);
				boolean out = false;
				for (int i = 0, j = 1; !out && i < count; ++i, j <<= 1) {
					if (0 == (se.mask & j)) {
						int side = se.node.volume.Classify(normals[i], offsets[i], signs[i]);
						switch (side) {
							case -1:
								out = true;
								break;
							case +1:
								se.mask |= j;
								break;
						}
					}
				}
				if (!out) {
					if (se.mask != inside && se.node.isinternal()) {
						stack.add(new sStkNP(se.node.childs[0], se.mask));
						stack.add(new sStkNP(se.node.childs[1], se.mask));
					} else if (policy.AllLeaves(se.node)) { enumLeaves(se.node, policy); }
				}
			} while (stack.size() != 0);
		}
	}

	/**
	 * Collide OCL.
	 *
	 * @param root
	 *            the root
	 * @param normals
	 *            the normals
	 * @param offsets
	 *            the offsets
	 * @param sortaxis
	 *            the sortaxis
	 * @param count
	 *            the count
	 * @param policy
	 *            the policy
	 */
	public static void collideOCL(final Node root, final Vector3f[] normals, final float[] offsets,
			final Vector3f sortaxis, final int count, final ICollide policy) {
		collideOCL(root, normals, offsets, sortaxis, count, policy, true);
	}

	/**
	 * Collide OCL.
	 *
	 * @param root
	 *            the root
	 * @param normals
	 *            the normals
	 * @param offsets
	 *            the offsets
	 * @param sortaxis
	 *            the sortaxis
	 * @param count
	 *            the count
	 * @param policy
	 *            the policy
	 * @param fullsort
	 *            the fullsort
	 */
	public static void collideOCL(final Node root, final Vector3f[] normals, final float[] offsets,
			final Vector3f sortaxis, final int count, final ICollide policy, final boolean fullsort) {
		// DBVT_CHECKTYPE
		if (root != null) {
			int srtsgns = (sortaxis.x >= 0 ? 1 : 0) + (sortaxis.y >= 0 ? 2 : 0) + (sortaxis.z >= 0 ? 4 : 0);
			int inside = (1 << count) - 1;
			ArrayList<sStkNPS> stock = new ArrayList<>();
			IntArrayList ifree = new IntArrayList();
			IntArrayList stack = new IntArrayList();
			int[] signs = new int[/* sizeof(unsigned)*8 */4 * 8];
			assert count < 128 / /* sizeof(signs[0]) */ 4;
			for (int i = 0; i < count; i++) {
				signs[i] = (normals[i].x >= 0 ? 1 : 0) + (normals[i].y >= 0 ? 2 : 0) + (normals[i].z >= 0 ? 4 : 0);
			}
			// stock.reserve(SIMPLE_STACKSIZE);
			// stack.reserve(SIMPLE_STACKSIZE);
			// ifree.reserve(SIMPLE_STACKSIZE);
			stack.add(allocate(ifree, stock, new sStkNPS(root, 0, root.volume.ProjectMinimum(sortaxis, srtsgns))));
			do {
				// JAVA NOTE: check
				int id = stack.remove(stack.size() - 1);
				sStkNPS se = stock.get(id);
				ifree.add(id);
				if (se.mask != inside) {
					boolean out = false;
					for (int i = 0, j = 1; !out && i < count; ++i, j <<= 1) {
						if (0 == (se.mask & j)) {
							int side = se.node.volume.Classify(normals[i], offsets[i], signs[i]);
							switch (side) {
								case -1:
									out = true;
									break;
								case +1:
									se.mask |= j;
									break;
							}
						}
					}
					if (out) { continue; }
				}
				if (policy.Descent(se.node)) {
					if (se.node.isinternal()) {
						Node[] pns = { se.node.childs[0], se.node.childs[1] };
						sStkNPS[] nes = { new sStkNPS(pns[0], se.mask, pns[0].volume.ProjectMinimum(sortaxis, srtsgns)),
								new sStkNPS(pns[1], se.mask, pns[1].volume.ProjectMinimum(sortaxis, srtsgns)) };
						int q = nes[0].value < nes[1].value ? 1 : 0;
						int j = stack.size();
						if (fullsort && j > 0) {
							/* Insert 0 */
							j = nearest(stack, stock, nes[q].value, 0, stack.size());
							stack.add(0);
							// #if DBVT_USE_MEMMOVE
							// memmove(&stack[j+1],&stack[j],sizeof(int)*(stack.size()-j-1));
							// #else
							for (int k = stack.size() - 1; k > j; --k) {
								stack.set(k, stack.get(k - 1));
								// #endif
							}
							stack.set(j, allocate(ifree, stock, nes[q]));
							/* Insert 1 */
							j = nearest(stack, stock, nes[1 - q].value, j, stack.size());
							stack.add(0);
							// #if DBVT_USE_MEMMOVE
							// memmove(&stack[j+1],&stack[j],sizeof(int)*(stack.size()-j-1));
							// #else
							for (int k = stack.size() - 1; k > j; --k) {
								stack.set(k, stack.get(k - 1));
								// #endif
							}
							stack.set(j, allocate(ifree, stock, nes[1 - q]));
						} else {
							stack.add(allocate(ifree, stock, nes[q]));
							stack.add(allocate(ifree, stock, nes[1 - q]));
						}
					} else {
						policy.Process(se.node, se.value);
					}
				}
			} while (stack.size() != 0);
		}
	}

	/**
	 * Collide TU.
	 *
	 * @param root
	 *            the root
	 * @param policy
	 *            the policy
	 */
	public static void collideTU(final Node root, final ICollide policy) {
		// DBVT_CHECKTYPE
		if (root != null) {
			ArrayList<Node> stack = new ArrayList<>(SIMPLE_STACKSIZE);
			stack.add(root);
			do {
				Node n = stack.remove(stack.size() - 1);
				if (policy.Descent(n)) {
					if (n.isinternal()) {
						stack.add(n.childs[0]);
						stack.add(n.childs[1]);
					} else {
						policy.Process(n);
					}
				}
			} while (stack.size() > 0);
		}
	}

	/**
	 * Nearest.
	 *
	 * @param i
	 *            the i
	 * @param a
	 *            the a
	 * @param v
	 *            the v
	 * @param l
	 *            the l
	 * @param h
	 *            the h
	 * @return the int
	 */
	public static int nearest(final IntArrayList i, final ArrayList<sStkNPS> a, final float v, int l, int h) {
		int m = 0;
		while (l < h) {
			m = l + h >> 1;
			if (a.get(i.get(m)).value >= v) {
				l = m + 1;
			} else {
				h = m;
			}
		}
		return h;
	}

	/**
	 * Allocate.
	 *
	 * @param ifree
	 *            the ifree
	 * @param stock
	 *            the stock
	 * @param value
	 *            the value
	 * @return the int
	 */
	public static int allocate(final IntArrayList ifree, final ArrayList<sStkNPS> stock, final sStkNPS value) {
		int i;
		if (ifree.size() > 0) {
			i = ifree.get(ifree.size() - 1);
			ifree.remove(ifree.size() - 1);
			stock.get(i).set(value);
		} else {
			i = stock.size();
			stock.add(value);
		}
		return i;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indexof.
	 *
	 * @param node
	 *            the node
	 * @return the int
	 */
	private static int indexof(final Node node) {
		return node.parent.childs[1] == node ? 1 : 0;
	}

	/**
	 * Merge.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param out
	 *            the out
	 * @return the dbvt aabb mm
	 */
	private static DbvtAabbMm merge(final DbvtAabbMm a, final DbvtAabbMm b, final DbvtAabbMm out) {
		DbvtAabbMm.Merge(a, b, out);
		return out;
	}

	/**
	 * Size.
	 *
	 * @param a
	 *            the a
	 * @return the float
	 */
	// volume+edge lengths
	private static float size(final DbvtAabbMm a) {
		Vector3f edges = a.Lengths(VECTORS.get());
		float result = edges.x * edges.y * edges.z + edges.x + edges.y + edges.z;
		VECTORS.release(edges);
		return result;
	}

	/**
	 * Deletenode.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param node
	 *            the node
	 */
	private static void deletenode(final Dbvt pdbvt, final Node node) {
		// btAlignedFree(pdbvt->m_free);
		pdbvt.free = node;
	}

	/**
	 * Recursedeletenode.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param node
	 *            the node
	 */
	private static void recursedeletenode(final Dbvt pdbvt, final Node node) {
		if (!node.isleaf()) {
			recursedeletenode(pdbvt, node.childs[0]);
			recursedeletenode(pdbvt, node.childs[1]);
		}
		if (node == pdbvt.root) { pdbvt.root = null; }
		deletenode(pdbvt, node);
	}

	/**
	 * Createnode.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param parent
	 *            the parent
	 * @param volume
	 *            the volume
	 * @param data
	 *            the data
	 * @return the node
	 */
	private static Node createnode(final Dbvt pdbvt, final Node parent, final DbvtAabbMm volume, final Object data) {
		Node node;
		if (pdbvt.free != null) {
			node = pdbvt.free;
			pdbvt.free = null;
		} else {
			node = new Node();
		}
		node.parent = parent;
		node.volume.set(volume);
		node.data = data;
		node.childs[1] = null;
		return node;
	}

	/**
	 * Insertleaf.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param root
	 *            the root
	 * @param leaf
	 *            the leaf
	 */
	private static void insertleaf(final Dbvt pdbvt, Node root, final Node leaf) {
		if (pdbvt.root == null) {
			pdbvt.root = leaf;
			leaf.parent = null;
		} else {
			if (!root.isleaf()) {
				do {
					if (DbvtAabbMm.Proximity(root.childs[0].volume, leaf.volume) < DbvtAabbMm
							.Proximity(root.childs[1].volume, leaf.volume)) {
						root = root.childs[0];
					} else {
						root = root.childs[1];
					}
				} while (!root.isleaf());
			}
			Node prev = root.parent;
			Node node = createnode(pdbvt, prev, merge(leaf.volume, root.volume, new DbvtAabbMm()), null);
			if (prev != null) {
				prev.childs[indexof(root)] = node;
				node.childs[0] = root;
				root.parent = node;
				node.childs[1] = leaf;
				leaf.parent = node;
				do {
					if (prev.volume.Contain(node.volume)) { break; }
					DbvtAabbMm.Merge(prev.childs[0].volume, prev.childs[1].volume, prev.volume);
					node = prev;
				} while (null != (prev = node.parent));
			} else {
				node.childs[0] = root;
				root.parent = node;
				node.childs[1] = leaf;
				leaf.parent = node;
				pdbvt.root = node;
			}
		}
	}

	/**
	 * Removeleaf.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param leaf
	 *            the leaf
	 * @return the node
	 */
	private static Node removeleaf(final Dbvt pdbvt, final Node leaf) {
		if (leaf == pdbvt.root) {
			pdbvt.root = null;
			return null;
		}
		Node parent = leaf.parent;
		Node prev = parent.parent;
		Node sibling = parent.childs[1 - indexof(leaf)];
		if (prev == null) {
			pdbvt.root = sibling;
			sibling.parent = null;
			deletenode(pdbvt, parent);
			return pdbvt.root;
		}
		prev.childs[indexof(parent)] = sibling;
		sibling.parent = prev;
		deletenode(pdbvt, parent);
		while (prev != null) {
			DbvtAabbMm pb = prev.volume;
			DbvtAabbMm.Merge(prev.childs[0].volume, prev.childs[1].volume, prev.volume);
			if (!DbvtAabbMm.NotEqual(pb, prev.volume)) { break; }
			prev = prev.parent;
		}
		return prev != null ? prev : pdbvt.root;
	}

	/**
	 * Fetchleaves.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param root
	 *            the root
	 * @param leaves
	 *            the leaves
	 */
	private static void fetchleaves(final Dbvt pdbvt, final Node root, final ArrayList<Node> leaves) {
		fetchleaves(pdbvt, root, leaves, -1);
	}

	/**
	 * Fetchleaves.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param root
	 *            the root
	 * @param leaves
	 *            the leaves
	 * @param depth
	 *            the depth
	 */
	private static void fetchleaves(final Dbvt pdbvt, final Node root, final ArrayList<Node> leaves, final int depth) {
		if (root.isinternal() && depth != 0) {
			fetchleaves(pdbvt, root.childs[0], leaves, depth - 1);
			fetchleaves(pdbvt, root.childs[1], leaves, depth - 1);
			deletenode(pdbvt, root);
		} else {
			leaves.add(root);
		}
	}

	/**
	 * Split.
	 *
	 * @param leaves
	 *            the leaves
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param org
	 *            the org
	 * @param axis
	 *            the axis
	 */
	private static void split(final ArrayList<Node> leaves, final ArrayList<Node> left, final ArrayList<Node> right,
			final Vector3f org, final Vector3f axis) {
		Vector3f tmp = VECTORS.get();
		MiscUtil.resize(left, 0, Node.class);
		MiscUtil.resize(right, 0, Node.class);
		for (int i = 0, ni = leaves.size(); i < ni; i++) {
			leaves.get(i).volume.Center(tmp);
			tmp.sub(org);
			if (axis.dot(tmp) < 0f) {
				left.add(leaves.get(i));
			} else {
				right.add(leaves.get(i));
			}
		}
	}

	/**
	 * Bounds.
	 *
	 * @param leaves
	 *            the leaves
	 * @return the dbvt aabb mm
	 */
	private static DbvtAabbMm bounds(final ArrayList<Node> leaves) {
		DbvtAabbMm volume = new DbvtAabbMm(leaves.get(0).volume);
		for (int i = 1, ni = leaves.size(); i < ni; i++) { merge(volume, leaves.get(i).volume, volume); }
		return volume;
	}

	/**
	 * Bottomup.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param leaves
	 *            the leaves
	 */
	private static void bottomup(final Dbvt pdbvt, final ArrayList<Node> leaves) {
		DbvtAabbMm tmpVolume = new DbvtAabbMm();
		while (leaves.size() > 1) {
			float minsize = BulletGlobals.SIMD_INFINITY;
			int[] minidx = { -1, -1 };
			for (int i = 0; i < leaves.size(); i++) {
				for (int j = i + 1; j < leaves.size(); j++) {
					float sz = size(merge(leaves.get(i).volume, leaves.get(j).volume, tmpVolume));
					if (sz < minsize) {
						minsize = sz;
						minidx[0] = i;
						minidx[1] = j;
					}
				}
			}
			Node[] n = { leaves.get(minidx[0]), leaves.get(minidx[1]) };
			Node p = createnode(pdbvt, null, merge(n[0].volume, n[1].volume, new DbvtAabbMm()), null);
			p.childs[0] = n[0];
			p.childs[1] = n[1];
			n[0].parent = p;
			n[1].parent = p;
			// JAVA NOTE: check
			leaves.set(minidx[0], p);
			Collections.swap(leaves, minidx[1], leaves.size() - 1);
			leaves.remove(leaves.size() - 1);
		}
	}

	/** The axis. */
	private static Vector3f[] axis = { new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1) };

	/**
	 * Topdown.
	 *
	 * @param pdbvt
	 *            the pdbvt
	 * @param leaves
	 *            the leaves
	 * @param bu_treshold
	 *            the bu treshold
	 * @return the node
	 */
	private static Node topdown(final Dbvt pdbvt, final ArrayList<Node> leaves, final int bu_treshold) {
		if (leaves.size() > 1) {
			if (leaves.size() <= bu_treshold) {
				bottomup(pdbvt, leaves);
				return leaves.get(0);
			}
			DbvtAabbMm vol = bounds(leaves);
			Vector3f org = vol.Center(VECTORS.get());
			@SuppressWarnings ("unchecked") ArrayList<Node>[] sets = new ArrayList[2];
			for (int i = 0; i < sets.length; i++) { sets[i] = new ArrayList<>(); }
			int bestaxis = -1;
			int bestmidp = leaves.size();
			int[][] splitcount = { { 0, 0 }, { 0, 0 }, { 0, 0 } };

			Vector3f x = VECTORS.get();

			for (Node element : leaves) {
				element.volume.Center(x);
				x.sub(org);
				for (int j = 0; j < 3; j++) { splitcount[j][x.dot(axis[j]) > 0f ? 1 : 0]++; }
			}
			for (int i = 0; i < 3; i++) {
				if (splitcount[i][0] > 0 && splitcount[i][1] > 0) {
					int midp = Math.abs(splitcount[i][0] - splitcount[i][1]);
					if (midp < bestmidp) {
						bestaxis = i;
						bestmidp = midp;
					}
				}
			}
			if (bestaxis >= 0) {
				// sets[0].reserve(splitcount[bestaxis][0]);
				// sets[1].reserve(splitcount[bestaxis][1]);
				split(leaves, sets[0], sets[1], org, axis[bestaxis]);
			} else {
				// sets[0].reserve(leaves.size()/2+1);
				// sets[1].reserve(leaves.size()/2);
				for (int i = 0, ni = leaves.size(); i < ni; i++) { sets[i & 1].add(leaves.get(i)); }
			}
			Node node = createnode(pdbvt, null, vol, null);
			node.childs[0] = topdown(pdbvt, sets[0], bu_treshold);
			node.childs[1] = topdown(pdbvt, sets[1], bu_treshold);
			node.childs[0].parent = node;
			node.childs[1].parent = node;
			VECTORS.release(org, x);
			return node;
		}
		return leaves.get(0);
	}

	/**
	 * Sort.
	 *
	 * @param n
	 *            the n
	 * @param r
	 *            the r
	 * @return the node
	 */
	private static Node sort(final Node n, final Node[] r) {
		Node p = n.parent;
		assert n.isinternal();
		// JAVA TODO: fix this
		if (p != null && p.hashCode() > n.hashCode()) {
			int i = indexof(n);
			int j = 1 - i;
			Node s = p.childs[j];
			Node q = p.parent;
			assert n == p.childs[i];
			if (q != null) {
				q.childs[indexof(p)] = n;
			} else {
				r[0] = n;
			}
			s.parent = n;
			p.parent = n;
			n.parent = q;
			p.childs[0] = n.childs[0];
			p.childs[1] = n.childs[1];
			n.childs[0].parent = p;
			n.childs[1].parent = p;
			n.childs[i] = p;
			n.childs[j] = s;

			DbvtAabbMm.swap(p.volume, n.volume);
			return p;
		}
		return n;
	}

	/**
	 * Walkup.
	 *
	 * @param n
	 *            the n
	 * @param count
	 *            the count
	 * @return the node
	 */
	private static Node walkup(Node n, int count) {
		while (n != null && count-- != 0) { n = n.parent; }
		return n;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class Node.
	 */
	public static class Node {

		/** The volume. */
		public final DbvtAabbMm volume = new DbvtAabbMm();

		/** The parent. */
		public Node parent;

		/** The childs. */
		public final Node[] childs = new Node[2];

		/** The data. */
		public Object data;

		/**
		 * Checks if is leaf.
		 *
		 * @return true, if is leaf
		 */
		public boolean isleaf() {
			return childs[1] == null;
		}

		/**
		 * Checks if is internal.
		 *
		 * @return true, if is internal
		 */
		public boolean isinternal() {
			return !isleaf();
		}
	}

	/** Stack element */
	public static class sStkNN {

		/** The a. */
		public Node a;

		/** The b. */
		public Node b;

		/**
		 * Instantiates a new s stk NN.
		 *
		 * @param na
		 *            the na
		 * @param nb
		 *            the nb
		 */
		public sStkNN(final Node na, final Node nb) {
			a = na;
			b = nb;
		}
	}

	/**
	 * The Class sStkNP.
	 */
	public static class sStkNP {

		/** The node. */
		public Node node;

		/** The mask. */
		public int mask;

		/**
		 * Instantiates a new s stk NP.
		 *
		 * @param n
		 *            the n
		 * @param m
		 *            the m
		 */
		public sStkNP(final Node n, final int m) {
			node = n;
			mask = m;
		}
	}

	/**
	 * The Class sStkNPS.
	 */
	public static class sStkNPS {

		/** The node. */
		public Node node;

		/** The mask. */
		public int mask;

		/** The value. */
		public float value;

		/**
		 * Instantiates a new s stk NPS.
		 */
		public sStkNPS() {}

		/**
		 * Instantiates a new s stk NPS.
		 *
		 * @param n
		 *            the n
		 * @param m
		 *            the m
		 * @param v
		 *            the v
		 */
		public sStkNPS(final Node n, final int m, final float v) {
			node = n;
			mask = m;
			value = v;
		}

		/**
		 * Sets the.
		 *
		 * @param o
		 *            the o
		 */
		public void set(final sStkNPS o) {
			node = o.node;
			mask = o.mask;
			value = o.value;
		}
	}

	/**
	 * The Class sStkCLN.
	 */
	public static class sStkCLN {

		/** The node. */
		public Node node;

		/** The parent. */
		public Node parent;

		/**
		 * Instantiates a new s stk CLN.
		 *
		 * @param n
		 *            the n
		 * @param p
		 *            the p
		 */
		public sStkCLN(final Node n, final Node p) {
			node = n;
			parent = p;
		}
	}

	/**
	 * The Class ICollide.
	 */
	public static class ICollide {

		/**
		 * Process.
		 *
		 * @param n1
		 *            the n 1
		 * @param n2
		 *            the n 2
		 */
		public void Process(final Node n1, final Node n2) {}

		/**
		 * Process.
		 *
		 * @param n
		 *            the n
		 */
		public void Process(final Node n) {}

		/**
		 * Process.
		 *
		 * @param n
		 *            the n
		 * @param f
		 *            the f
		 */
		public void Process(final Node n, final float f) {
			Process(n);
		}

		/**
		 * Descent.
		 *
		 * @param n
		 *            the n
		 * @return true, if successful
		 */
		public boolean Descent(final Node n) {
			return true;
		}

		/**
		 * All leaves.
		 *
		 * @param n
		 *            the n
		 * @return true, if successful
		 */
		public boolean AllLeaves(final Node n) {
			return true;
		}
	}

	/**
	 * The Class IWriter.
	 */
	public static abstract class IWriter {

		/**
		 * Prepare.
		 *
		 * @param root
		 *            the root
		 * @param numnodes
		 *            the numnodes
		 */
		public abstract void Prepare(Node root, int numnodes);

		/**
		 * Write node.
		 *
		 * @param n
		 *            the n
		 * @param index
		 *            the index
		 * @param parent
		 *            the parent
		 * @param child0
		 *            the child 0
		 * @param child1
		 *            the child 1
		 */
		public abstract void WriteNode(Node n, int index, int parent, int child0, int child1);

		/**
		 * Write leaf.
		 *
		 * @param n
		 *            the n
		 * @param index
		 *            the index
		 * @param parent
		 *            the parent
		 */
		public abstract void WriteLeaf(Node n, int index, int parent);
	}

	/**
	 * The Class IClone.
	 */
	public static class IClone {
		/**
		 * Clone leaf.
		 *
		 * @param n
		 *            the n
		 */
		public void CloneLeaf(final Node n) {}
	}

}
