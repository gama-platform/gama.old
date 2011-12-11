/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaPoint;

public class GridDiffuser {

	public static class GridDiffusion {

		IGeometry[] places;
		double[] values;
		final double proportion;
		final double variation;
		final double range;
		final short type;

		private GridDiffusion(final short type, final double proportion, final double variation,
			final double range) {
			this.type = type;
			this.proportion = proportion;
			this.variation = variation;
			this.range = range;
			places = new IAgent[0];
			values = new double[0];
		}

		void add(final IAgent p, final double value) {
			for ( int i = 0; i < places.length; i++ ) {
				if ( places[i] == p ) {
					final double v = values[i];
					values[i] = type == GridDiffuser.GRADIENT ? Math.max(v, value) : v + value;
					return;
				}
			}
			places = Arrays.copyOf(places, places.length + 1);
			values = Arrays.copyOf(values, values.length + 1);
			places[places.length - 1] = p;
			values[values.length - 1] = value;
		}
	}

	private final IGeometry[] agents;
	private final GridNeighbourhood neighbourhood;
	private final double cellWidth;
	private final int neighboursSize;

	/**
	 * 
	 */
	// private final GridAgentManager gridAgentManager;

	/**
	 * @param gridAgentManager
	 */
	public GridDiffuser(final IGeometry[] agents, final GridNeighbourhood neighbourhood,
		final double cellWidth) {
		this.agents = agents;
		this.neighbourhood = neighbourhood;
		this.cellWidth = cellWidth;
		neighboursSize = neighbourhood.isVN() ? 4 : 8;
	}

	protected final Map<String, GridDiffusion> diffusions = new HashMap();
	public static final short GRADIENT = 1;
	public static final short DIFFUSION = 0;

	protected void addDiffusion(final short type, final String var, final IAgent agent,
		final double value, final double proportion, final double variation, final double range) {
		if ( !diffusions.containsKey(var) ) {
			diffusions.put(var, new GridDiffusion(type, proportion, variation, range));
		}
		diffusions.get(var).add(agent, value);
	}

	public void diffuse(final IScope scope) throws GamaRuntimeException {
		for ( final String v : diffusions.keySet() ) {
			final GridDiffusion d = diffusions.get(v);
			if ( d.type == GridDiffuser.DIFFUSION ) {
				spreadDiffusion(scope, v, d);
			} else {
				spreadGradient(scope, v, d);
			}
		}
		diffusions.clear();
	}

	protected final int getPlaceIndexAt(final GamaPoint p) {
		final double xx = p.x / cellWidth;
		final double yy = p.y / cellWidth;
		final int x = (int) xx;
		final int y = (int) yy;
		return neighbourhood.getPlaceIndexAt(x, y);
	}

	public void diffuseVariable(final String name, final double value, final short type,
		final double proportion, final double variation, final GamaPoint location,
		final double range) {
		final int p = getPlaceIndexAt(location);
		if ( p == -1 ) { return; }
		// IVariable var = this.gridAgentManager.species.getVar(name);
		addDiffusion(type, name, agents[p].getAgent(), value, proportion, variation, range);
	}

	private void spreadDiffusion(final IScope scope, final String v,
		final GridDiffusion gridDiffusion) throws GamaRuntimeException {
		int[] neighbours;
		IAgent p;
		final double proportion = gridDiffusion.proportion;
		final double variation = gridDiffusion.variation;
		int range = (int) (gridDiffusion.range / cellWidth);
		if ( range < 0 ) {
			range = 1000;
		}
		if ( range == 0 ) { return; }

		int n;
		double r0, rn;
		final double prop = proportion / neighboursSize;
		final double propInit = 1 - proportion;
		for ( int i = 0, halt = gridDiffusion.places.length; i < halt; i++ ) {
			p = gridDiffusion.places[i].getAgent();
			int placeIndex = p.getIndex();
			r0 = gridDiffusion.values[i];
			scope.setAgentVarValue(p, v, (Double) scope.getAgentVarValue(p, v) + r0 * propInit);
			rn = r0 * prop - variation;
			int max_range = 1;
			double vn = rn;
			while (vn > 0.1) {
				vn = vn * prop - variation;
				max_range++;
			}
			range = Math.min(range, max_range);
			neighbours = neighbourhood.getRawNeighboursIncluding(placeIndex, range);
			for ( n = 1; n <= range; n++ ) {
				final int begin = neighbourhood.neighboursIndexOf(placeIndex, n);
				final int end = neighbourhood.neighboursIndexOf(placeIndex, n + 1);
				for ( int k = begin; k < end; k++ ) {
					final IAgent z = agents[neighbours[k]].getAgent();
					// v.addDirectFloat(z, rn);
					scope.setAgentVarValue(z, v, (Double) scope.getAgentVarValue(z, v) + rn);
				}
				rn = rn * prop - variation;
			}
		}
	}

	private void spreadGradient(final IScope scope, final String v,
		final GridDiffusion gridDiffusion) throws GamaRuntimeException {
		int[] neighbours;
		IAgent p;
		final double proportion = gridDiffusion.proportion;
		final double variation = gridDiffusion.variation;
		int range = (int) (gridDiffusion.range / cellWidth);
		if ( range < 0 ) {
			range = 1000;
		}
		if ( range == 0 ) { return; }

		int n;
		double r0, rn;
		for ( int i = 0, halt = gridDiffusion.places.length; i < halt; i++ ) {
			p = gridDiffusion.places[i].getAgent();
			int placeIndex = p.getIndex();
			r0 = gridDiffusion.values[i];

			if ( (Double) scope.getAgentVarValue(p, v) > r0 ) { return; }
			scope.setAgentVarValue(p, v, r0);
			rn = r0 * proportion - variation;
			int max_range = 1;
			double vn = rn;
			while (vn > 0.1) {
				vn = vn * proportion - variation;
				max_range++;
			}
			range = Math.min(range, max_range);
			neighbours = neighbourhood.getRawNeighboursIncluding(placeIndex, range);
			boolean cont = true;
			for ( n = 1; n <= range; n++ ) {
				final int begin = neighbourhood.neighboursIndexOf(placeIndex, n);
				final int end = neighbourhood.neighboursIndexOf(placeIndex, n + 1);
				cont = false;
				for ( int k = begin; k < end; k++ ) {
					final IAgent z = agents[neighbours[k]].getAgent();

					if ( (Double) scope.getAgentVarValue(z, v) < rn ) {
						scope.setAgentVarValue(z, v, rn);
						cont = true;
					}
				}
				if ( !cont ) {
					break;
				}
				rn = rn * proportion - variation;
			}
		}
	}

}