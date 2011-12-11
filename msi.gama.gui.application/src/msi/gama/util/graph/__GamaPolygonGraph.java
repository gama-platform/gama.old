/**
 * Created by drogoul, 24 nov. 2011
 * 
 */
package msi.gama.util.graph;

import msi.gama.util.GamaGeometry;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 * 
 * @todo Description
 * 
 */
public abstract class __GamaPolygonGraph extends __GamaGeotoolsGraphWrapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaGeotoolsGraphWrapper#add(msi.gama.util.GamaGeometry, double)
	 */
	@Override
	protected void add(final GamaGeometry value, final double weight) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaGeotoolsGraphWrapper#remove(msi.gama.util.GamaGeometry)
	 */
	@Override
	protected void remove(final GamaGeometry value) {}

}
