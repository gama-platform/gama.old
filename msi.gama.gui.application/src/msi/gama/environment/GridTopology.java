package msi.gama.environment;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;
import msi.gama.util.matrix.GamaSpatialMatrix;

public class GridTopology extends AbstractTopology {

	public GridTopology(final IScope scope, final GamaSpatialMatrix matrix) {
		super(scope, matrix.environmentFrame);
		places = matrix;
	}

	@Override
	protected boolean createAgents() {
		return true;
	}

	public GridTopology(final IScope scope, final IGeometry environment, final int rows,
		final int columns, /* final boolean isTorus, */final boolean usesVN) {
		super(scope, environment/* , isTorus */);
		places = new GamaSpatialMatrix(environment, rows, columns,/* , isTorus, */usesVN);
	}

	@Override
	public IAgent getAgentClosestTo(final IGeometry source, final IAgentFilter filter) {
		return ((GamaSpatialMatrix) places).getAgentAt(source.getLocation());
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "Grid topology in " + environment.toString() + " as " + places.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return ISymbol.TOPOLOGY + " (" + places.toGaml() + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy() {
		return new GridTopology(scope, environment, ((GamaSpatialMatrix) places).getRows(),
			((GamaSpatialMatrix) places).getCols(),
			((GamaSpatialMatrix) places).neighbourhood.isVN());
	}

	@Override
	public GamaSpatialMatrix getPlaces() {
		return (GamaSpatialMatrix) super.getPlaces();
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaPath pathBetween(final IGeometry source, final IGeometry target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final GamaPoint p) {
		return ((GamaSpatialMatrix) places).getPlaceAt(p) != null;

	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IGeometry g) {
		return isValidLocation(g.getLocation());
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry, java.lang.Double)
	 */
	@Override
	public Double distanceBetween(final IGeometry source, final IGeometry target) {
		if ( !isValidGeometry(source) || !isValidGeometry(target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IGeometry source, final IGeometry target) {
		// TODO compute from the path
		return null;
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsInEnvelope(msi.gama.interfaces.IGeometry,
	 *      com.vividsolutions.jts.geom.Envelope, msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public GamaList<IAgent> getAgentsIn(final IGeometry source, final IAgentFilter f,
		final boolean covered) {
		GamaList<IAgent> agents = super.getAgentsIn(source, f, covered);
		GamaList<IAgent> result = new GamaList();
		for ( IAgent ag : agents ) {
			if ( isValidGeometry(ag) ) {
				result.add(ag);
			}
		}
		return result;
	}

	@Override
	public GamaList<IAgent> getNeighboursOf(final IGeometry source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		return getPlaces().getNeighboursOf(scope, this, source, distance); // AgentFilter ?
	}

}
