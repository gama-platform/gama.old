/*******************************************************************************************************
 *
 * DbvtBroadphase.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

// Dbvt implementation by Nathanael Presson

package com.bulletphysics.collision.broadphase;

import static com.bulletphysics.Pools.VECTORS;

import java.util.List;

import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
public class DbvtBroadphase implements BroadphaseInterface {

	/** The Constant DBVT_BP_MARGIN. */
	public static final float DBVT_BP_MARGIN = 0.05f;

	/** The Constant DYNAMIC_SET. */
	public static final int DYNAMIC_SET = 0; // Dynamic set index
	
	/** The Constant FIXED_SET. */
	public static final int FIXED_SET = 1; // Fixed set index
	
	/** The Constant STAGECOUNT. */
	public static final int STAGECOUNT = 2; // Number of stages

	/** The sets. */
	public final Dbvt[] sets = new Dbvt[2]; // Dbvt sets
	
	/** The stage roots. */
	public DbvtProxy[] stageRoots = new DbvtProxy[STAGECOUNT + 1]; // Stages list
	
	/** The paircache. */
	public OverlappingPairCache paircache; // Pair cache
	
	/** The predictedframes. */
	public float predictedframes; // Frames predicted
	
	/** The stage current. */
	public int stageCurrent; // Current stage
	
	/** The fupdates. */
	public int fupdates; // % of fixed updates per frame
	
	/** The dupdates. */
	public int dupdates; // % of dynamic updates per frame
	
	/** The pid. */
	public int pid; // Parse id
	
	/** The gid. */
	public int gid; // Gen id
	
	/** The releasepaircache. */
	public boolean releasepaircache; // Release pair cache on delete

	// #if DBVT_BP_PROFILE
	// btClock m_clock;
	// struct {
	// unsigned long m_total;
	// unsigned long m_ddcollide;
	// unsigned long m_fdcollide;
	// unsigned long m_cleanup;
	// unsigned long m_jobcount;
	// } m_profiling;
	// #endif

	/**
	 * Instantiates a new dbvt broadphase.
	 */
	public DbvtBroadphase() {
		this(null);
	}

	/**
	 * Instantiates a new dbvt broadphase.
	 *
	 * @param paircache the paircache
	 */
	public DbvtBroadphase(final OverlappingPairCache paircache) {
		sets[0] = new Dbvt();
		sets[1] = new Dbvt();

		// Dbvt.benchmark();
		releasepaircache = paircache != null ? false : true;
		predictedframes = 2;
		stageCurrent = 0;
		fupdates = 1;
		dupdates = 1;
		this.paircache = paircache != null ? paircache : new HashedOverlappingPairCache();
		gid = 0;
		pid = 0;

		for (int i = 0; i <= STAGECOUNT; i++) {
			stageRoots[i] = null;
		}
		// #if DBVT_BP_PROFILE
		// clear(m_profiling);
		// #endif
	}

	/**
	 * Collide.
	 *
	 * @param dispatcher the dispatcher
	 */
	public void collide( final Dispatcher dispatcher) {
		// SPC(m_profiling.m_total);

		// optimize:
		sets[0].optimizeIncremental(1 + sets[0].leaves * dupdates / 100);
		sets[1].optimizeIncremental(1 + sets[1].leaves * fupdates / 100);

		// dynamic -> fixed set:
		stageCurrent = (stageCurrent + 1) % STAGECOUNT;
		DbvtProxy current = stageRoots[stageCurrent];
		if (current != null) {
			DbvtTreeCollider collider = new DbvtTreeCollider(this);
			do {
				DbvtProxy next = current.links[1];
				stageRoots[current.stage] = listremove(current, stageRoots[current.stage]);
				stageRoots[STAGECOUNT] = listappend(current, stageRoots[STAGECOUNT]);
				Dbvt.collideTT(sets[1].root, current.leaf, collider);
				sets[0].remove(current.leaf);
				current.leaf = sets[1].insert(current.aabb, current);
				current.stage = STAGECOUNT;
				current = next;
			} while (current != null);
		}

		// collide dynamics:
		{
			DbvtTreeCollider collider = new DbvtTreeCollider(this);
			{
				// SPC(m_profiling.m_fdcollide);
				Dbvt.collideTT(sets[0].root, sets[1].root, collider);
			}
			{
				// SPC(m_profiling.m_ddcollide);
				Dbvt.collideTT(sets[0].root, sets[0].root, collider);
			}
		}

		// clean up:
		{
			// SPC(m_profiling.m_cleanup);
			List<BroadphasePair> pairs = paircache.getOverlappingPairArray();
			if (pairs.size() > 0) {
				for (int i = 0, ni = pairs.size(); i < ni; i++) {
					BroadphasePair p = pairs.get(i);
					DbvtProxy pa = (DbvtProxy) p.pProxy0;
					DbvtProxy pb = (DbvtProxy) p.pProxy1;
					if (!DbvtAabbMm.Intersect(pa.aabb, pb.aabb)) {
						// if(pa>pb) btSwap(pa,pb);
						if (pa.hashCode() > pb.hashCode()) {
							DbvtProxy tmp = pa;
							pa = pb;
							pb = tmp;
						}
						paircache.removeOverlappingPair( pa, pb, dispatcher);
						ni--;
						i--;
					}
				}
			}
		}
		pid++;
	}

	/**
	 * Listappend.
	 *
	 * @param item the item
	 * @param list the list
	 * @return the dbvt proxy
	 */
	private static DbvtProxy listappend(final DbvtProxy item, DbvtProxy list) {
		item.links[0] = null;
		item.links[1] = list;
		if (list != null) { list.links[0] = item; }
		list = item;
		return list;
	}

	/**
	 * Listremove.
	 *
	 * @param item the item
	 * @param list the list
	 * @return the dbvt proxy
	 */
	private static DbvtProxy listremove(final DbvtProxy item, DbvtProxy list) {
		if (item.links[0] != null) {
			item.links[0].links[1] = item.links[1];
		} else {
			list = item.links[1];
		}

		if (item.links[1] != null) { item.links[1].links[0] = item.links[0]; }
		return list;
	}

	@Override
	public BroadphaseProxy createProxy( final Vector3f aabbMin, final Vector3f aabbMax,
			final BroadphaseNativeType shapeType, final Object userPtr, final short collisionFilterGroup,
			final short collisionFilterMask, final Dispatcher dispatcher, final Object multiSapProxy) {
		DbvtProxy proxy = new DbvtProxy(userPtr, collisionFilterGroup, collisionFilterMask);
		DbvtAabbMm.FromMM(aabbMin, aabbMax, proxy.aabb);
		proxy.leaf = sets[0].insert(proxy.aabb, proxy);
		proxy.stage = stageCurrent;
		proxy.uniqueId = ++gid;
		stageRoots[stageCurrent] = listappend(proxy, stageRoots[stageCurrent]);
		return proxy;
	}

	@Override
	public void destroyProxy( final BroadphaseProxy absproxy, final Dispatcher dispatcher) {
		DbvtProxy proxy = (DbvtProxy) absproxy;
		if (proxy.stage == STAGECOUNT) {
			sets[1].remove(proxy.leaf);
		} else {
			sets[0].remove(proxy.leaf);
		}
		stageRoots[proxy.stage] = listremove(proxy, stageRoots[proxy.stage]);
		paircache.removeOverlappingPairsContainingProxy( proxy, dispatcher);
		// btAlignedFree(proxy);
	}

	@Override
	public void setAabb( final BroadphaseProxy absproxy, final Vector3f aabbMin,
			final Vector3f aabbMax, final Dispatcher dispatcher) {
		DbvtProxy proxy = (DbvtProxy) absproxy;
		DbvtAabbMm aabb = DbvtAabbMm.FromMM(aabbMin, aabbMax, new DbvtAabbMm());
		if (proxy.stage == STAGECOUNT) {
			// fixed -> dynamic set
			sets[1].remove(proxy.leaf);
			proxy.leaf = sets[0].insert(aabb, proxy);
		} else {
			// dynamic set:
			if (DbvtAabbMm.Intersect(proxy.leaf.volume, aabb)) {/* Moving */
				Vector3f delta = VECTORS.get();
				delta.add(aabbMin, aabbMax);
				delta.scale(0.5f);
				Vector3f temp = proxy.aabb.Center(VECTORS.get());
				delta.sub(temp);
				// #ifdef DBVT_BP_MARGIN
				delta.scale(predictedframes);
				sets[0].update(proxy.leaf, aabb, delta, DBVT_BP_MARGIN);
				// #else
				// m_sets[0].update(proxy->leaf,aabb,delta*m_predictedframes);
				// #endif
				VECTORS.release(delta, temp);
			} else {
				// teleporting:
				sets[0].update(proxy.leaf, aabb);
			}
		}

		stageRoots[proxy.stage] = listremove(proxy, stageRoots[proxy.stage]);
		proxy.aabb.set(aabb);
		proxy.stage = stageCurrent;
		stageRoots[stageCurrent] = listappend(proxy, stageRoots[stageCurrent]);
	}

	@Override
	public void calculateOverlappingPairs( final Dispatcher dispatcher) {
		collide( dispatcher);

		// #if DBVT_BP_PROFILE
		// if(0==(m_pid%DBVT_BP_PROFILING_RATE))
		// {
		// printf("fixed(%u) dynamics(%u)
		// pairs(%u)\r\n",m_sets[1].m_leafs,m_sets[0].m_leafs,m_paircache->getNumOverlappingPairs());
		// printf("mode: %s\r\n",m_mode==MODE_FULL?"full":"incremental");
		// printf("cleanup: %s\r\n",m_cleanupmode==CLEANUP_FULL?"full":"incremental");
		// unsigned int total=m_profiling.m_total;
		// if(total<=0) total=1;
		// printf("ddcollide: %u%%
		// (%uus)\r\n",(50+m_profiling.m_ddcollide*100)/total,m_profiling.m_ddcollide/DBVT_BP_PROFILING_RATE);
		// printf("fdcollide: %u%%
		// (%uus)\r\n",(50+m_profiling.m_fdcollide*100)/total,m_profiling.m_fdcollide/DBVT_BP_PROFILING_RATE);
		// printf("cleanup: %u%%
		// (%uus)\r\n",(50+m_profiling.m_cleanup*100)/total,m_profiling.m_cleanup/DBVT_BP_PROFILING_RATE);
		// printf("total: %uus\r\n",total/DBVT_BP_PROFILING_RATE);
		// const unsigned long sum=m_profiling.m_ddcollide+
		// m_profiling.m_fdcollide+
		// m_profiling.m_cleanup;
		// printf("leaked: %u%% (%uus)\r\n",100-((50+sum*100)/total),(total-sum)/DBVT_BP_PROFILING_RATE);
		// printf("job counts:
		// %u%%\r\n",(m_profiling.m_jobcount*100)/((m_sets[0].m_leafs+m_sets[1].m_leafs)*DBVT_BP_PROFILING_RATE));
		// clear(m_profiling);
		// m_clock.reset();
		// }
		// #endif
	}

	@Override
	public OverlappingPairCache getOverlappingPairCache() {
		return paircache;
	}

	@Override
	public void getBroadphaseAabb(final Vector3f aabbMin, final Vector3f aabbMax) {
		DbvtAabbMm bounds = new DbvtAabbMm();
		if (!sets[0].empty()) {
			if (!sets[1].empty()) {
				DbvtAabbMm.Merge(sets[0].root.volume, sets[1].root.volume, bounds);
			} else {
				bounds.set(sets[0].root.volume);
			}
		} else if (!sets[1].empty()) {
			bounds.set(sets[1].root.volume);
		} else {
			DbvtAabbMm.FromCR(new Vector3f(0f, 0f, 0f), 0f, bounds);
		}
		aabbMin.set(bounds.Mins());
		aabbMax.set(bounds.Maxs());
	}

	@Override
	public void printStats() {}

}
