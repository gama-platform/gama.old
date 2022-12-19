/*******************************************************************************************************
 *
 * GjkEpaSolver.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.Arrays;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ObjectStackList;

/*
 * GJK-EPA collision solver by Nathanael Presson Nov.2006
 */

/**
 * GjkEpaSolver contributed under zlib by Nathanael Presson.
 *
 * @author jezek2
 */
public class GjkEpaSolver {

	// protected final ArrayPool<float[]> floatArrays = ArrayPool.get(float.class);

	/** The stack mkv. */
	protected final ObjectStackList<Mkv> stackMkv = new ObjectStackList<>(Mkv.class);

	/** The stack he. */
	protected final ObjectStackList<He> stackHe = new ObjectStackList<>(He.class);

	/** The stack face. */
	protected final ObjectStackList<Face> stackFace = new ObjectStackList<>(Face.class);

	/**
	 * Push stack.
	 */
	protected void pushStack() {
		stackMkv.push();
		stackHe.push();
		stackFace.push();
	}

	/**
	 * Pop stack.
	 */
	protected void popStack() {
		stackMkv.pop();
		stackHe.pop();
		stackFace.pop();
	}

	/**
	 * The Enum ResultsStatus.
	 */
	public enum ResultsStatus {

		/** The Separated. */
		Separated,
		/** The Penetrating. */
		/* Shapes doesnt penetrate */
		Penetrating,
		/** The GJ K failed. */
		/* Shapes are penetrating */
		GJK_Failed,
		/** The EP A failed. */
		/* GJK phase fail, no big issue, shapes are probably just 'touching' */
		EPA_Failed, /* EPA phase fail, bigger problem, need to save parameters, and debug */
	}

	/**
	 * The Class Results.
	 */
	public static class Results {

		/** The status. */
		public ResultsStatus status;

		/** The witnesses. */
		public final Vector3f[] witnesses/* [2] */ = { new Vector3f(), new Vector3f() };

		/** The normal. */
		public final Vector3f normal = new Vector3f();

		/** The depth. */
		public float depth;

		/** The epa iterations. */
		public int epa_iterations;

		/** The gjk iterations. */
		public int gjk_iterations;
	}

	////////////////////////////////////////////////////////////////////////////

	/** The Constant cstInf. */
	private static final float cstInf = BulletGlobals.SIMD_INFINITY;

	/** The Constant cst2Pi. */
	// private static final float cstPi = BulletGlobals.SIMD_PI;
	private static final float cst2Pi = BulletGlobals.SIMD_2_PI;

	/** The Constant GJK_maxiterations. */
	private static final int GJK_maxiterations = 128;

	/** The Constant GJK_hashsize. */
	private static final int GJK_hashsize = 1 << 6;

	/** The Constant GJK_hashmask. */
	private static final int GJK_hashmask = GJK_hashsize - 1;

	/** The Constant GJK_insimplex_eps. */
	private static final float GJK_insimplex_eps = 0.0001f;

	/** The Constant GJK_sqinsimplex_eps. */
	private static final float GJK_sqinsimplex_eps = GJK_insimplex_eps * GJK_insimplex_eps;

	/** The Constant EPA_maxiterations. */
	private static final int EPA_maxiterations = 256;

	/** The Constant EPA_inface_eps. */
	private static final float EPA_inface_eps = 0.01f;

	/** The Constant EPA_accuracy. */
	private static final float EPA_accuracy = 0.001f;

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class Mkv.
	 */
	public static class Mkv {

		/** The w. */
		public final Vector3f w = new Vector3f(); // Minkowski vertice

		/** The r. */
		public final Vector3f r = new Vector3f(); // Ray

		/**
		 * Sets the.
		 *
		 * @param m
		 *            the m
		 */
		public void set(final Mkv m) {
			w.set(m.w);
			r.set(m.r);
		}
	}

	/**
	 * The Class He.
	 */
	public static class He {

		/** The v. */
		public final Vector3f v = new Vector3f();

		/** The n. */
		public He n;
	}

	/**
	 * The Class GJK.
	 */
	protected class GJK {
		// protected final BulletStack stack = BulletStack.get();

		// public btStackAlloc sa;
		/** The table. */
		// public Block sablock;
		public final He[] table = new He[GJK_hashsize];

		/** The wrotations. */
		public final Matrix3f[] wrotations/* [2] */ = { new Matrix3f(), new Matrix3f() };

		/** The positions. */
		public final Vector3f[] positions/* [2] */ = { new Vector3f(), new Vector3f() };

		/** The shapes. */
		public final ConvexShape[] shapes = new ConvexShape[2];

		/** The simplex. */
		public final Mkv[] simplex = new Mkv[5];

		/** The ray. */
		public final Vector3f ray = new Vector3f();

		/** The order. */
		public /* unsigned */ int order;

		/** The iterations. */
		public /* unsigned */ int iterations;

		/** The margin. */
		public float margin;

		/** The failed. */
		public boolean failed;

		{
			for (int i = 0; i < simplex.length; i++) { simplex[i] = new Mkv(); }
		}

		/**
		 * Instantiates a new gjk.
		 */
		public GJK() {}

		/**
		 * Instantiates a new gjk.
		 *
		 * @param wrot0
		 *            the wrot 0
		 * @param pos0
		 *            the pos 0
		 * @param shape0
		 *            the shape 0
		 * @param wrot1
		 *            the wrot 1
		 * @param pos1
		 *            the pos 1
		 * @param shape1
		 *            the shape 1
		 */
		public GJK(/* StackAlloc psa, */
				final Matrix3f wrot0, final Vector3f pos0, final ConvexShape shape0, final Matrix3f wrot1,
				final Vector3f pos1, final ConvexShape shape1) {
			this(wrot0, pos0, shape0, wrot1, pos1, shape1, 0f);
		}

		/**
		 * Instantiates a new gjk.
		 *
		 * @param wrot0
		 *            the wrot 0
		 * @param pos0
		 *            the pos 0
		 * @param shape0
		 *            the shape 0
		 * @param wrot1
		 *            the wrot 1
		 * @param pos1
		 *            the pos 1
		 * @param shape1
		 *            the shape 1
		 * @param pmargin
		 *            the pmargin
		 */
		public GJK(/* StackAlloc psa, */
				final Matrix3f wrot0, final Vector3f pos0, final ConvexShape shape0, final Matrix3f wrot1,
				final Vector3f pos1, final ConvexShape shape1, final float pmargin) {
			init(wrot0, pos0, shape0, wrot1, pos1, shape1, pmargin);
		}

		/**
		 * Inits the.
		 *
		 * @param wrot0
		 *            the wrot 0
		 * @param pos0
		 *            the pos 0
		 * @param shape0
		 *            the shape 0
		 * @param wrot1
		 *            the wrot 1
		 * @param pos1
		 *            the pos 1
		 * @param shape1
		 *            the shape 1
		 * @param pmargin
		 *            the pmargin
		 */
		public void init(/* StackAlloc psa, */
				final Matrix3f wrot0, final Vector3f pos0, final ConvexShape shape0, final Matrix3f wrot1,
				final Vector3f pos1, final ConvexShape shape1, final float pmargin) {
			pushStack();
			wrotations[0].set(wrot0);
			positions[0].set(pos0);
			shapes[0] = shape0;
			wrotations[1].set(wrot1);
			positions[1].set(pos1);
			shapes[1] = shape1;
			// sa =psa;
			// sablock =sa->beginBlock();
			margin = pmargin;
			failed = false;
		}

		/**
		 * Destroy.
		 */
		public void destroy() {
			popStack();
		}

		/**
		 * Hash.
		 *
		 * @param v
		 *            the v
		 * @return the int
		 */
		// vdh: very dummy hash
		public /* unsigned */ int Hash(final Vector3f v) {
			int h = (int) (v.x * 15461) ^ (int) (v.y * 83003) ^ (int) (v.z * 15473);
			return h * 169639 & GJK_hashmask;
		}

		/**
		 * Local support.
		 *
		 * @param d
		 *            the d
		 * @param i
		 *            the i
		 * @param out
		 *            the out
		 * @return the vector 3 f
		 */
		public Vector3f LocalSupport(final Vector3f d, /* unsigned */ final int i, final Vector3f out) {
			Vector3f tmp = VECTORS.get();
			MatrixUtil.transposeTransform(tmp, d, wrotations[i]);

			shapes[i].localGetSupportingVertex(tmp, out);
			wrotations[i].transform(out);
			out.add(positions[i]);
			VECTORS.release(tmp);
			return out;
		}

		/**
		 * Support.
		 *
		 * @param d
		 *            the d
		 * @param v
		 *            the v
		 */
		public void Support(final Vector3f d, final Mkv v) {
			v.r.set(d);

			Vector3f tmp1 = LocalSupport(d, 0, VECTORS.get());

			Vector3f tmp = VECTORS.get();
			tmp.set(d);
			tmp.negate();
			Vector3f tmp2 = LocalSupport(tmp, 1, VECTORS.get());

			v.w.sub(tmp1, tmp2);
			v.w.scaleAdd(margin, d, v.w);
			VECTORS.release(tmp1, tmp2, tmp);
		}

		/**
		 * Fetch support.
		 *
		 * @return true, if successful
		 */
		public boolean FetchSupport() {
			int h = Hash(ray);
			He e = table[h];
			while (e != null) {
				if (e.v.equals(ray)) {
					--order;
					return false;
				}
				e = e.n;
			}
			// e = (He*)sa->allocate(sizeof(He));
			// e = new He();
			e = stackHe.get();
			e.v.set(ray);
			e.n = table[h];
			table[h] = e;
			Support(ray, simplex[++order]);
			return ray.dot(simplex[order].w) > 0;
		}

		/**
		 * Solve simplex 2.
		 *
		 * @param ao
		 *            the ao
		 * @param ab
		 *            the ab
		 * @return true, if successful
		 */
		public boolean SolveSimplex2(final Vector3f ao, final Vector3f ab) {
			if (ab.dot(ao) >= 0) {
				Vector3f cabo = VECTORS.get();
				try {
					cabo.cross(ab, ao);
					if (cabo.lengthSquared() <= GJK_sqinsimplex_eps) return true;
					ray.cross(cabo, ab);
				} finally {
					VECTORS.release(cabo);
				}
			} else {
				order = 0;
				simplex[0].set(simplex[1]);
				ray.set(ao);
			}
			return false;
		}

		/**
		 * Solve simplex 3.
		 *
		 * @param ao
		 *            the ao
		 * @param ab
		 *            the ab
		 * @param ac
		 *            the ac
		 * @return true, if successful
		 */
		public boolean SolveSimplex3(final Vector3f ao, final Vector3f ab, final Vector3f ac) {
			Vector3f tmp = VECTORS.get();
			tmp.cross(ab, ac);
			boolean result = SolveSimplex3a(ao, ab, ac, tmp);
			VECTORS.release(tmp);
			return result;
		}

		/**
		 * Solve simplex 3 a.
		 *
		 * @param ao
		 *            the ao
		 * @param ab
		 *            the ab
		 * @param ac
		 *            the ac
		 * @param cabc
		 *            the cabc
		 * @return true, if successful
		 */
		public boolean SolveSimplex3a(final Vector3f ao, final Vector3f ab, final Vector3f ac, final Vector3f cabc) {
			// TODO: optimize

			Vector3f tmp = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();
			try {
				tmp.cross(cabc, ab);
				tmp2.cross(cabc, ac);

				if (tmp.dot(ao) < -GJK_insimplex_eps) {
					order = 1;
					simplex[0].set(simplex[1]);
					simplex[1].set(simplex[2]);
					return SolveSimplex2(ao, ab);
				}
				if (tmp2.dot(ao) > +GJK_insimplex_eps) {
					order = 1;
					simplex[1].set(simplex[2]);
					return SolveSimplex2(ao, ac);
				} else {
					float d = cabc.dot(ao);
					if (Math.abs(d) > GJK_insimplex_eps) {
						if (d > 0) {
							ray.set(cabc);
						} else {
							ray.negate(cabc);

							Mkv swapTmp = new Mkv();
							swapTmp.set(simplex[0]);
							simplex[0].set(simplex[1]);
							simplex[1].set(swapTmp);
						}
						return false;
					} else
						return true;
				}
			} finally {
				VECTORS.release(tmp, tmp2);
			}
		}

		/**
		 * Solve simplex 4.
		 *
		 * @param ao
		 *            the ao
		 * @param ab
		 *            the ab
		 * @param ac
		 *            the ac
		 * @param ad
		 *            the ad
		 * @return true, if successful
		 */
		public boolean SolveSimplex4(final Vector3f ao, final Vector3f ab, final Vector3f ac, final Vector3f ad) {
			// TODO: optimize

			Vector3f crs = VECTORS.get();
			Vector3f tmp = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();
			Vector3f tmp3 = VECTORS.get();
			tmp.cross(ab, ac);
			tmp2.cross(ac, ad);
			tmp3.cross(ad, ab);

			try {
				if (tmp.dot(ao) > GJK_insimplex_eps) {
					crs.set(tmp);
					order = 2;
					simplex[0].set(simplex[1]);
					simplex[1].set(simplex[2]);
					simplex[2].set(simplex[3]);
					return SolveSimplex3a(ao, ab, ac, crs);
				}
				if (tmp2.dot(ao) > GJK_insimplex_eps) {
					crs.set(tmp2);
					order = 2;
					simplex[2].set(simplex[3]);
					return SolveSimplex3a(ao, ac, ad, crs);
				} else if (tmp3.dot(ao) > GJK_insimplex_eps) {
					crs.set(tmp3);
					order = 2;
					simplex[1].set(simplex[0]);
					simplex[0].set(simplex[2]);
					simplex[2].set(simplex[3]);
					return SolveSimplex3a(ao, ad, ab, crs);
				} else
					return true;
			} finally {
				VECTORS.release(tmp, tmp2, tmp3);
			}
		}

		/**
		 * Search origin.
		 *
		 * @return true, if successful
		 */
		public boolean SearchOrigin() {
			Vector3f tmp = VECTORS.get();
			tmp.set(1f, 0f, 0f);
			boolean result = SearchOrigin(tmp);
			VECTORS.release(tmp);
			return result;
		}

		/**
		 * Search origin.
		 *
		 * @param initray
		 *            the initray
		 * @return true, if successful
		 */
		public boolean SearchOrigin(final Vector3f initray) {
			Vector3f tmp1 = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();
			Vector3f tmp3 = VECTORS.get();
			Vector3f tmp4 = VECTORS.get();

			try {
				iterations = 0;
				order = -1;
				failed = false;
				ray.set(initray);
				ray.normalize();
				Arrays.fill(table, null);
				FetchSupport();
				ray.negate(simplex[0].w);
				for (; iterations < GJK_maxiterations; ++iterations) {
					float rl = ray.length();
					ray.scale(1f / (rl > 0f ? rl : 1f));
					if (!FetchSupport()) return false;
					boolean found = false;
					switch (order) {
						case 1: {
							tmp1.negate(simplex[1].w);
							tmp2.sub(simplex[0].w, simplex[1].w);
							found = SolveSimplex2(tmp1, tmp2);
							break;
						}
						case 2: {
							tmp1.negate(simplex[2].w);
							tmp2.sub(simplex[1].w, simplex[2].w);
							tmp3.sub(simplex[0].w, simplex[2].w);
							found = SolveSimplex3(tmp1, tmp2, tmp3);
							break;
						}
						case 3: {
							tmp1.negate(simplex[3].w);
							tmp2.sub(simplex[2].w, simplex[3].w);
							tmp3.sub(simplex[1].w, simplex[3].w);
							tmp4.sub(simplex[0].w, simplex[3].w);
							found = SolveSimplex4(tmp1, tmp2, tmp3, tmp4);
							break;
						}
					}
					if (found) return true;
				}
				failed = true;
				return false;
			} finally {
				VECTORS.release(tmp1, tmp2, tmp3, tmp4);
			}
		}

		/**
		 * Enclose origin.
		 *
		 * @return true, if successful
		 */
		public boolean EncloseOrigin() {
			Vector3f tmp = VECTORS.get();
			Vector3f tmp1 = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();

			try {
				switch (order) {
					// Point
					case 0:
						break;
					// Line
					case 1: {
						Vector3f ab = VECTORS.get();
						ab.sub(simplex[1].w, simplex[0].w);

						Vector3f[] b = { VECTORS.get(), VECTORS.get(), VECTORS.get() };
						b[0].set(1f, 0f, 0f);
						b[1].set(0f, 1f, 0f);
						b[2].set(0f, 0f, 1f);

						b[0].cross(ab, b[0]);
						b[1].cross(ab, b[1]);
						b[2].cross(ab, b[2]);

						float m[] = { b[0].lengthSquared(), b[1].lengthSquared(), b[2].lengthSquared() };

						Quat4f tmpQuat = QUATS.get();
						tmp.normalize(ab);
						QuaternionUtil.setRotation(tmpQuat, tmp, cst2Pi / 3f);

						Matrix3f r = MATRICES.get();
						MatrixUtil.setRotation(r, tmpQuat);

						Vector3f w = VECTORS.get();
						w.set(b[m[0] > m[1] ? m[0] > m[2] ? 0 : 2 : m[1] > m[2] ? 1 : 2]);

						tmp.normalize(w);
						Support(tmp, simplex[4]);
						r.transform(w);
						tmp.normalize(w);
						Support(tmp, simplex[2]);
						r.transform(w);
						tmp.normalize(w);
						Support(tmp, simplex[3]);
						r.transform(w);
						order = 4;
						VECTORS.release(w, ab, b[0], b[1], b[2]);
						QUATS.release(tmpQuat);
						MATRICES.release(r);
						return true;
					}
					// Triangle
					case 2: {
						tmp1.sub(simplex[1].w, simplex[0].w);
						tmp2.sub(simplex[2].w, simplex[0].w);
						Vector3f n = VECTORS.get();
						n.cross(tmp1, tmp2);
						n.normalize();

						Support(n, simplex[3]);

						tmp.negate(n);
						Support(tmp, simplex[4]);
						order = 4;
						VECTORS.release(n);
						return true;
					}
					// Tetrahedron
					case 3:
						return true;
					// Hexahedron
					case 4:
						return true;
				}
				return false;
			} finally {
				VECTORS.release(tmp, tmp2, tmp1);
			}
		}

	}

	////////////////////////////////////////////////////////////////////////////

	/** The mod 3. */
	private static int[] mod3 = { 0, 1, 2, 0, 1 };

	/** The Constant tetrahedron_fidx. */
	private static final int[][] tetrahedron_fidx/* [4][3] */ = { { 2, 1, 0 }, { 3, 0, 1 }, { 3, 1, 2 }, { 3, 2, 0 } };

	/** The Constant tetrahedron_eidx. */
	private static final int[][] tetrahedron_eidx/* [6][4] */ =
			{ { 0, 0, 2, 1 }, { 0, 1, 1, 1 }, { 0, 2, 3, 1 }, { 1, 0, 3, 2 }, { 2, 0, 1, 2 }, { 3, 0, 2, 2 } };

	/** The Constant hexahedron_fidx. */
	private static final int[][] hexahedron_fidx/* [6][3] */ =
			{ { 2, 0, 4 }, { 4, 1, 2 }, { 1, 4, 0 }, { 0, 3, 1 }, { 0, 2, 3 }, { 1, 3, 2 } };

	/** The Constant hexahedron_eidx. */
	private static final int[][] hexahedron_eidx/* [9][4] */ = { { 0, 0, 4, 0 }, { 0, 1, 2, 1 }, { 0, 2, 1, 2 },
			{ 1, 1, 5, 2 }, { 1, 0, 2, 0 }, { 2, 2, 3, 2 }, { 3, 1, 5, 0 }, { 3, 0, 4, 2 }, { 5, 1, 4, 1 } };

	/**
	 * The Class Face.
	 */
	public static class Face {

		/** The v. */
		public final Mkv[] v = new Mkv[3];

		/** The f. */
		public final Face[] f = new Face[3];

		/** The e. */
		public final int[] e = new int[3];

		/** The n. */
		public final Vector3f n = new Vector3f();

		/** The d. */
		public float d;

		/** The mark. */
		public int mark;

		/** The prev. */
		public Face prev;

		/** The next. */
		public Face next;
	}

	/**
	 * The Class EPA.
	 */
	protected class EPA {
		// protected final BulletStack stack = BulletStack.get();

		/** The gjk. */
		public GJK gjk;

		/** The root. */
		// public btStackAlloc* sa;
		public Face root;

		/** The nfaces. */
		public int nfaces;

		/** The iterations. */
		public int iterations;

		/** The features. */
		public final Vector3f[][] features = new Vector3f[2][3];

		/** The nearest. */
		public final Vector3f[] nearest/* [2] */ = { new Vector3f(), new Vector3f() };

		/** The normal. */
		public final Vector3f normal = new Vector3f();

		/** The depth. */
		public float depth;

		/** The failed. */
		public boolean failed;

		{
			for (int i = 0; i < features.length; i++) {
				for (int j = 0; j < features[i].length; j++) { features[i][j] = new Vector3f(); }
			}
		}

		/**
		 * Instantiates a new epa.
		 *
		 * @param pgjk
		 *            the pgjk
		 */
		public EPA(final GJK pgjk) {
			gjk = pgjk;
			// sa = pgjk->sa;
		}

		/**
		 * Gets the coordinates.
		 *
		 * @param face
		 *            the face
		 * @param out
		 *            the out
		 * @return the vector 3 f
		 */
		public Vector3f GetCoordinates(final Face face, final Vector3f out) {
			Vector3f tmp = VECTORS.get();
			Vector3f tmp1 = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();

			Vector3f o = VECTORS.get();
			o.scale(-face.d, face.n);

			// float[] a = floatArrays.getFixed(3);

			float[] temp = new float[3];
			tmp1.sub(face.v[0].w, o);
			tmp2.sub(face.v[1].w, o);
			tmp.cross(tmp1, tmp2);
			temp[0] = tmp.length();

			tmp1.sub(face.v[1].w, o);
			tmp2.sub(face.v[2].w, o);
			tmp.cross(tmp1, tmp2);
			temp[1] = tmp.length();

			tmp1.sub(face.v[2].w, o);
			tmp2.sub(face.v[0].w, o);
			tmp.cross(tmp1, tmp2);
			temp[2] = tmp.length();

			float sm = temp[0] + temp[1] + temp[2];

			out.set(temp[1], temp[2], temp[0]);
			out.scale(1f / (sm > 0f ? sm : 1f));

			// floatArrays.release(a);
			VECTORS.release(tmp, tmp1, tmp2, o);
			return out;
		}

		/**
		 * Find best.
		 *
		 * @return the face
		 */
		public Face FindBest() {
			Face bf = null;
			if (root != null) {
				Face cf = root;
				float bd = cstInf;
				do {
					if (cf.d < bd) {
						bd = cf.d;
						bf = cf;
					}
				} while (null != (cf = cf.next));
			}
			return bf;
		}

		/**
		 * Sets the.
		 *
		 * @param f
		 *            the f
		 * @param a
		 *            the a
		 * @param b
		 *            the b
		 * @param c
		 *            the c
		 * @return true, if successful
		 */
		public boolean Set(final Face f, final Mkv a, final Mkv b, final Mkv c) {
			Vector3f tmp1 = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();
			Vector3f tmp3 = VECTORS.get();

			Vector3f nrm = VECTORS.get();
			tmp1.sub(b.w, a.w);
			tmp2.sub(c.w, a.w);
			nrm.cross(tmp1, tmp2);

			float len = nrm.length();

			tmp1.cross(a.w, b.w);
			tmp2.cross(b.w, c.w);
			tmp3.cross(c.w, a.w);

			boolean valid = tmp1.dot(nrm) >= -EPA_inface_eps && tmp2.dot(nrm) >= -EPA_inface_eps
					&& tmp3.dot(nrm) >= -EPA_inface_eps;

			f.v[0] = a;
			f.v[1] = b;
			f.v[2] = c;
			f.mark = 0;
			f.n.scale(1f / (len > 0f ? len : cstInf), nrm);
			f.d = Math.max(0, -f.n.dot(a.w));
			VECTORS.release(tmp3, tmp1, tmp2, nrm);
			return valid;
		}

		/**
		 * New face.
		 *
		 * @param a
		 *            the a
		 * @param b
		 *            the b
		 * @param c
		 *            the c
		 * @return the face
		 */
		public Face NewFace(final Mkv a, final Mkv b, final Mkv c) {
			// Face pf = new Face();
			Face pf = stackFace.get();
			if (Set(pf, a, b, c)) {
				if (root != null) { root.prev = pf; }
				pf.prev = null;
				pf.next = root;
				root = pf;
				++nfaces;
			} else {
				pf.prev = pf.next = null;
			}
			return pf;
		}

		/**
		 * Detach.
		 *
		 * @param face
		 *            the face
		 */
		public void Detach(final Face face) {
			if (face.prev != null || face.next != null) {
				--nfaces;
				if (face == root) {
					root = face.next;
					root.prev = null;
				} else if (face.next == null) {
					face.prev.next = null;
				} else {
					face.prev.next = face.next;
					face.next.prev = face.prev;
				}
				face.prev = face.next = null;
			}
		}

		/**
		 * Link.
		 *
		 * @param f0
		 *            the f 0
		 * @param e0
		 *            the e 0
		 * @param f1
		 *            the f 1
		 * @param e1
		 *            the e 1
		 */
		public void Link(final Face f0, final int e0, final Face f1, final int e1) {
			f0.f[e0] = f1;
			f1.e[e1] = e0;
			f1.f[e1] = f0;
			f0.e[e0] = e1;
		}

		/**
		 * Support.
		 *
		 * @param w
		 *            the w
		 * @return the mkv
		 */
		public Mkv Support(final Vector3f w) {
			// Mkv v = new Mkv();
			Mkv v = stackMkv.get();
			gjk.Support(w, v);
			return v;
		}

		/**
		 * Builds the horizon.
		 *
		 * @param markid
		 *            the markid
		 * @param w
		 *            the w
		 * @param f
		 *            the f
		 * @param e
		 *            the e
		 * @param cf
		 *            the cf
		 * @param ff
		 *            the ff
		 * @return the int
		 */
		public int BuildHorizon(final int markid, final Mkv w, final Face f, final int e, final Face[] cf,
				final Face[] ff) {
			int ne = 0;
			if (f.mark != markid) {
				int e1 = mod3[e + 1];
				if (f.n.dot(w.w) + f.d > 0) {
					Face nf = NewFace(f.v[e1], f.v[e], w);
					Link(nf, 0, f, e);
					if (cf[0] != null) {
						Link(cf[0], 1, nf, 2);
					} else {
						ff[0] = nf;
					}
					cf[0] = nf;
					ne = 1;
				} else {
					int e2 = mod3[e + 2];
					Detach(f);
					f.mark = markid;
					ne += BuildHorizon(markid, w, f.f[e1], f.e[e1], cf, ff);
					ne += BuildHorizon(markid, w, f.f[e2], f.e[e2], cf, ff);
				}
			}
			return ne;
		}

		/**
		 * Evaluate PD.
		 *
		 * @return the float
		 */
		public float EvaluatePD() {
			return EvaluatePD(EPA_accuracy);
		}

		/**
		 * Evaluate PD.
		 *
		 * @param accuracy
		 *            the accuracy
		 * @return the float
		 */
		@SuppressWarnings ("null")
		public float EvaluatePD(final float accuracy) {
			pushStack();
			Vector3f tmp = VECTORS.get();
			try {

				// btBlock* sablock = sa->beginBlock();
				Face bestface = null;
				int markid = 1;
				depth = -cstInf;
				normal.set(0f, 0f, 0f);
				root = null;
				nfaces = 0;
				iterations = 0;
				failed = false;
				/* Prepare hull */
				if (gjk.EncloseOrigin()) {
					// const U* pfidx = 0;
					int[][] pfidx_ptr = null;
					int pfidx_index = 0;

					int nfidx = 0;
					// const U* peidx = 0;
					int[][] peidx_ptr = null;
					int peidx_index = 0;

					int neidx = 0;
					Mkv[] basemkv = new Mkv[5];
					Face[] basefaces = new Face[6];
					switch (gjk.order) {
						// Tetrahedron
						case 3: {
							// pfidx=(const U*)fidx;
							pfidx_ptr = tetrahedron_fidx;
							pfidx_index = 0;

							nfidx = 4;

							// peidx=(const U*)eidx;
							peidx_ptr = tetrahedron_eidx;
							peidx_index = 0;

							neidx = 6;
						}
							break;
						// Hexahedron
						case 4: {
							// pfidx=(const U*)fidx;
							pfidx_ptr = hexahedron_fidx;
							pfidx_index = 0;

							nfidx = 6;

							// peidx=(const U*)eidx;
							peidx_ptr = hexahedron_eidx;
							peidx_index = 0;

							neidx = 9;
						}
							break;
					}
					int i;

					for (i = 0; i <= gjk.order; ++i) {
						basemkv[i] = new Mkv();
						basemkv[i].set(gjk.simplex[i]);
					}
					for (i = 0; i < nfidx; ++i, pfidx_index++) {
						basefaces[i] = NewFace(basemkv[pfidx_ptr[pfidx_index][0]], basemkv[pfidx_ptr[pfidx_index][1]],
								basemkv[pfidx_ptr[pfidx_index][2]]);
					}
					for (i = 0; i < neidx; ++i, peidx_index++) {
						Link(basefaces[peidx_ptr[peidx_index][0]], peidx_ptr[peidx_index][1],
								basefaces[peidx_ptr[peidx_index][2]], peidx_ptr[peidx_index][3]);
					}
				}
				if (0 == nfaces) return depth;
				/* Expand hull */
				for (; iterations < EPA_maxiterations; ++iterations) {
					Face bf = FindBest();
					if (bf == null) { break; }
					tmp.negate(bf.n);
					Mkv w = Support(tmp);
					float d = bf.n.dot(w.w) + bf.d;
					bestface = bf;
					if (d < -accuracy) {
						Face[] cf = { null };
						Face[] ff = { null };
						int nf = 0;
						Detach(bf);
						bf.mark = ++markid;
						for (int i = 0; i < 3; ++i) { nf += BuildHorizon(markid, w, bf.f[i], bf.e[i], cf, ff); }
						if (nf <= 2) { break; }
						Link(cf[0], 1, ff[0], 2);
					} else {
						break;
					}
				}
				/* Extract contact */
				if (bestface != null) {
					Vector3f b = GetCoordinates(bestface, VECTORS.get());
					normal.set(bestface.n);
					depth = Math.max(0, bestface.d);
					for (int i = 0; i < 2; ++i) {
						float s = i != 0 ? -1f : 1f;
						for (int j = 0; j < 3; ++j) {
							tmp.scale(s, bestface.v[j].r);
							gjk.LocalSupport(tmp, i, features[i][j]);
						}
					}

					Vector3f tmp1 = VECTORS.get();
					Vector3f tmp2 = VECTORS.get();
					Vector3f tmp3 = VECTORS.get();

					tmp1.scale(b.x, features[0][0]);
					tmp2.scale(b.y, features[0][1]);
					tmp3.scale(b.z, features[0][2]);
					VectorUtil.add(nearest[0], tmp1, tmp2, tmp3);

					tmp1.scale(b.x, features[1][0]);
					tmp2.scale(b.y, features[1][1]);
					tmp3.scale(b.z, features[1][2]);
					VectorUtil.add(nearest[1], tmp1, tmp2, tmp3);
					VECTORS.release(tmp1, tmp2, tmp3, b);
				} else {
					failed = true;
				}
				// sa->endBlock(sablock);
				return depth;
			} finally {
				popStack();
				VECTORS.release(tmp);
			}
		}

	}

	////////////////////////////////////////////////////////////////////////////

	/** The gjk. */
	private final GJK gjk = new GJK();

	/**
	 * Collide.
	 *
	 * @param shape0
	 *            the shape 0
	 * @param wtrs0
	 *            the wtrs 0
	 * @param shape1
	 *            the shape 1
	 * @param wtrs1
	 *            the wtrs 1
	 * @param radialmargin
	 *            the radialmargin
	 * @param results
	 *            the results
	 * @return true, if successful
	 */
	public boolean collide(final ConvexShape shape0, final Transform wtrs0, final ConvexShape shape1,
			final Transform wtrs1, final float radialmargin/*
															 * , btStackAlloc* stackAlloc
															 */, final Results results) {

		// Initialize
		results.witnesses[0].set(0f, 0f, 0f);
		results.witnesses[1].set(0f, 0f, 0f);
		results.normal.set(0f, 0f, 0f);
		results.depth = 0;
		results.status = ResultsStatus.Separated;
		results.epa_iterations = 0;
		results.gjk_iterations = 0;
		/* Use GJK to locate origin */
		gjk.init(/* stackAlloc, */
				wtrs0.basis, wtrs0.origin, shape0, wtrs1.basis, wtrs1.origin, shape1, radialmargin + EPA_accuracy);
		try {
			boolean collide = gjk.SearchOrigin();
			results.gjk_iterations = gjk.iterations + 1;
			if (collide) {
				/* Then EPA for penetration depth */
				EPA epa = new EPA(gjk);
				float pd = epa.EvaluatePD();
				results.epa_iterations = epa.iterations + 1;
				if (pd > 0) {
					results.status = ResultsStatus.Penetrating;
					results.normal.set(epa.normal);
					results.depth = pd;
					results.witnesses[0].set(epa.nearest[0]);
					results.witnesses[1].set(epa.nearest[1]);
					return true;
				}
				if (epa.failed) { results.status = ResultsStatus.EPA_Failed; }
			} else if (gjk.failed) { results.status = ResultsStatus.GJK_Failed; }
			return false;
		} finally {
			gjk.destroy();
		}
	}

}
