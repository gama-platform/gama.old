/*******************************************************************************************************
 *
 * HatchBoundaryLoop.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.Bounds;
import msi.gama.ext.kabeja.dxf.DXFEntity;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class HatchBoundaryLoop {
	
	/** The edges. */
	private final List<DXFEntity> edges = new ArrayList<>();
	
	/** The outermost. */
	private boolean outermost = true;

	/**
	 * @return Returns the outermost.
	 */
	public boolean isOutermost() { return outermost; }

	/**
	 * @param outermost
	 *            The outermost to set.
	 */
	public void setOutermost(final boolean outermost) { this.outermost = outermost; }

	/**
	 * Gets the boundary edges iterator.
	 *
	 * @return the boundary edges iterator
	 */
	public Iterator getBoundaryEdgesIterator() { return edges.iterator(); }

	/**
	 * Adds the boundary edge.
	 *
	 * @param edge the edge
	 */
	public void addBoundaryEdge(final DXFEntity edge) {
		edges.add(edge);
	}

	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	public Bounds getBounds() {
		Bounds bounds = new Bounds();

		// System.out.println("edges="+edges.size());
		if (edges.size() <= 0) {
			bounds.setValid(false);

			return bounds;
		}
		Iterator i = edges.iterator();

		while (i.hasNext()) {
			DXFEntity entity = (DXFEntity) i.next();
			Bounds b = entity.getBounds();

			if (b.isValid()) { bounds.addToBounds(b); }
		}

		return bounds;
	}

	/**
	 * Gets the edge count.
	 *
	 * @return the edge count
	 */
	public int getEdgeCount() { return this.edges.size(); }
}
