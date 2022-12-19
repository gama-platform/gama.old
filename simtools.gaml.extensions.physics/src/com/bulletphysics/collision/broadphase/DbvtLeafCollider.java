/*******************************************************************************************************
 *
 * DbvtLeafCollider.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

// Dbvt implementation by Nathanael Presson

package com.bulletphysics.collision.broadphase;

/**
 *
 * @author jezek2
 */
public class DbvtLeafCollider extends Dbvt.ICollide {

	/** The pbp. */
	public DbvtBroadphase pbp;
	
	/** The ppx. */
	public DbvtProxy ppx;

	/**
	 * Instantiates a new dbvt leaf collider.
	 *
	 * @param p the p
	 * @param px the px
	 */
	public DbvtLeafCollider(DbvtBroadphase p, DbvtProxy px) {
		this.pbp = p;
		this.ppx = px;
	}

	@Override
	public void Process(Dbvt.Node na) {
		Dbvt.Node nb = ppx.leaf;
		if (nb != na) {
			DbvtProxy pa = (DbvtProxy) na.data;
			DbvtProxy pb = (DbvtProxy) nb.data;
			
			//#if DBVT_BP_DISCRETPAIRS
			if (DbvtAabbMm.Intersect(pa.aabb, pb.aabb))
			//#endif
			{
				//if(pa>pb) btSwap(pa,pb);
				if (pa.hashCode() > pb.hashCode()) {
					DbvtProxy tmp = pa;
					pa = pb;
					pb = tmp;
				}
				pbp.paircache.addOverlappingPair(pa, pb);
			}
		}
	}

}
