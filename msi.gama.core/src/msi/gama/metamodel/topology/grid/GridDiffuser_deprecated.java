/**
 *
 */
package msi.gama.metamodel.topology.grid;

import java.util.*;
import gnu.trove.map.hash.THashMap;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.operators.fastmaths.*;

class GridDiffuser_deprecated {
	// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.

	private final GamaSpatialMatrix matrix;

	private class GridDiffusion_deprecated {

		IShape[] places;
		double[] values;
		final double proportion;
		final double variation;
		final double range;
		final short type;
		final IContainer<?, IAgent> candidates;

		GridDiffusion_deprecated(final short type, final double proportion, final double variation,
			final double range, final IContainer<?, IAgent> cand) {
			this.type = type;
			this.proportion = proportion;
			this.variation = variation;
			this.range = range;
			places = new IAgent[0];
			values = new double[0];
			candidates = cand;
		}

		// TODO Take candidates into account here as well (by making an intersection ? or better a union)
		void add(final IScope scope, final IAgent p, final double value) {
			if ( candidates != null && !candidates.contains(scope, p) ) { return; }
			for ( int i = 0; i < places.length; i++ ) {
				if ( places[i] == p ) {
					final double v = values[i];
					values[i] = type == IGrid.GRADIENT ? FastMath.max(v, value) : v + value;
					return;
				}
			}
			places = Arrays.copyOf(places, places.length + 1);
			values = Arrays.copyOf(values, values.length + 1);
			places[places.length - 1] = p;
			values[values.length - 1] = value;
		}
	}

	private final int neighboursSize;

	public GridDiffuser_deprecated(final GamaSpatialMatrix gamaSpatialMatrix) {
		matrix = gamaSpatialMatrix;
		neighboursSize = matrix.getNeighbourhood().isVN() ? 4 : 8;
	}

	protected final Map<String, GridDiffusion_deprecated> diffusions_deprecated = new THashMap();

	// public static final short GRADIENT = 1;
	// public static final short DIFFUSION = 0;

	protected void addDiffusion(final IScope scope, final short type, final String var, final IAgent agent,
		final double value, final double proportion, final double variation, final double range,
		final IContainer<?, IAgent> candidates) {
		if ( !diffusions_deprecated.containsKey(var) ) {
			diffusions_deprecated.put(var,
				new GridDiffusion_deprecated(type, proportion, variation, range, candidates));
		}
		diffusions_deprecated.get(var).add(scope, agent, value);
	}

	public void diffuse_deprecated(final IScope scope) throws GamaRuntimeException {
		for ( final String v : diffusions_deprecated.keySet() ) {
			final GridDiffusion_deprecated d = diffusions_deprecated.get(v);
			if ( d.type == IGrid.DIFFUSION ) {
				spreadDiffusion(scope, v, d);
			} else {
				spreadGradient(scope, v, d);
			}
		}
		diffusions_deprecated.clear();
	}

	protected final int getPlaceIndexAt(final ILocation p) {
		final double xx = p.getX() / matrix.cellWidth;
		final double yy = p.getY() / matrix.cellWidth;
		final int x = (int) xx;
		final int y = (int) yy;
		return matrix.getPlaceIndexAt(x, y);
	}

	public void diffuseVariable(final IScope scope, final String name, final double value, final short type,
		final double proportion, final double variation, final ILocation location, final double range,
		final Object candidates) {
		final int p = getPlaceIndexAt(location);
		if ( p == -1 ) { return; }
		IContainer<?, IAgent> cand = candidates instanceof IPopulation ? null
			: candidates instanceof IContainer ? (IContainer) candidates : null;
			addDiffusion(scope, type, name, matrix.matrix[p].getAgent(), value, proportion, variation, range, cand);
	}

	private void spreadDiffusion(final IScope scope, final String v, final GridDiffusion_deprecated gridDiffusion)
		throws GamaRuntimeException {
		int[] neighbours;
		IAgent p;
		final double proportion = gridDiffusion.proportion;
		final double variation = gridDiffusion.variation;
		int range = (int) (gridDiffusion.range / matrix.cellWidth);
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
			// if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, p) ) {
			// continue;
			// }
			final int placeIndex = p.getIndex();
			r0 = gridDiffusion.values[i];
			final Double previous = (Double) scope.getAgentVarValue(p, v);
			// If we cant get access to the value of the variable, it means probably that the agent is dead. Better
			// to stop spreading !
			if ( previous == null ) { return; }
			scope.setAgentVarValue(p, v, previous + r0 * propInit);
			rn = r0 * prop - variation;
			int max_range = 1;
			double vn = rn;
			while (vn > 0.1) {
				vn = vn * prop - variation;
				max_range++;
			}
			range = CmnFastMath.min(range, max_range);
			try {
				neighbours = matrix.neighbourhood.getRawNeighboursIncluding(scope, placeIndex, range);
			} catch (final GamaRuntimeException e) {
				// We change the neighbourhood to a cached version dynamically
				GAMA.reportError(scope, e, false);
				matrix.useNeighboursCache = true;
				matrix.neighbourhood = null;
				neighbours = matrix.getNeighbourhood().getRawNeighboursIncluding(scope, placeIndex, range);
			}
			for ( n = 1; n <= range; n++ ) {
				final int begin = matrix.neighbourhood.neighboursIndexOf(scope, placeIndex, n);
				final int end = matrix.neighbourhood.neighboursIndexOf(scope, placeIndex, n + 1);
				for ( int k = begin; k < end; k++ ) {
					final IAgent z = matrix.matrix[neighbours[k]].getAgent();
					if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, z) ) {
						continue;
					}
					// v.addDirectFloat(z, rn);
					final Double value = (Double) scope.getAgentVarValue(z, v);
					// If we cant get access to the value of the variable, it means probably that the agent is dead.
					// Better to stop spreading !
					if ( value == null ) { return; }
					scope.setAgentVarValue(z, v, value + rn);
				}
				rn = rn * prop - variation;
			}
		}
	}

	private void spreadGradient(final IScope scope, final String v, final GridDiffusion_deprecated gridDiffusion)
		throws GamaRuntimeException {
		int[] neighbours;
		IAgent p;
		final double proportion = gridDiffusion.proportion;
		final double variation = gridDiffusion.variation;
		int range = (int) (gridDiffusion.range / matrix.cellWidth);
		if ( range < 0 ) {
			range = 1000;
		}
		if ( range == 0 ) { return; }

		int n;
		double r0, rn;
		for ( int i = 0, halt = gridDiffusion.places.length; i < halt; i++ ) {
			p = gridDiffusion.places[i].getAgent();
			final int placeIndex = p.getIndex();
			r0 = gridDiffusion.values[i];
			final Double previous = (Double) scope.getAgentVarValue(p, v);
			// If we cant get access to the value of the variable, it means probably that the agent is dead. Better
			// to stop spreading !
			if ( previous == null || previous > r0 ) { return; }
			scope.setAgentVarValue(p, v, r0);
			rn = r0 * proportion - variation;
			int max_range = 1;
			double vn = rn;
			while (vn > 0.1) {
				vn = vn * proportion - variation;
				max_range++;
			}
			range = CmnFastMath.min(range, max_range);
			try {
				neighbours = matrix.neighbourhood.getRawNeighboursIncluding(scope, placeIndex, range);
			} catch (final GamaRuntimeException e) {
				// We change the neighbourhood to a cached version dynamically
				GAMA.reportError(scope, e, false);
				matrix.useNeighboursCache = true;
				matrix.neighbourhood = null;
				neighbours = matrix.getNeighbourhood().getRawNeighboursIncluding(scope, placeIndex, range);
			}
			boolean cont = true;
			for ( n = 1; n <= range; n++ ) {
				final int begin = matrix.neighbourhood.neighboursIndexOf(scope, placeIndex, n);
				final int end = matrix.neighbourhood.neighboursIndexOf(scope, placeIndex, n + 1);
				cont = false;
				for ( int k = begin; k < end; k++ ) {
					final IAgent z = matrix.matrix[neighbours[k]].getAgent();
					if ( gridDiffusion.candidates != null && !gridDiffusion.candidates.contains(scope, z) ) {
						continue;
					}
					final Double value = (Double) scope.getAgentVarValue(z, v);
					// If we cant get access to the value of the variable, it means probably that the agent is dead.
					// Better to stop spreading !
					if ( value == null ) { return; }
					if ( value < rn ) {
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