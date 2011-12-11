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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;
import msi.gaml.operators.Spatial;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The class ExpandableTopology.
 * 
 * @author drogoul
 * @since 2 déc. 2011
 * 
 */
public class AmorphousTopology implements ITopology {

	IGeometry expandableEnvironment = GamaGeometry.createPoint(new GamaPoint(0, 0));

	/**
	 * @see msi.gama.interfaces.IValue#type()
	 */
	@Override
	public IType type() {
		return Types.get(IType.TOPOLOGY);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "Expandable topology";
	}

	/**
	 * @see msi.gama.interfaces.IValue#toGaml()
	 */
	@Override
	public String toGaml() {
		return "topology {0,0}";
	}

	/**
	 * @see msi.gama.interfaces.IValue#toJava()
	 */
	@Override
	public String toJava() {
		return null;
	}

	/**
	 * @see msi.gama.interfaces.IValue#copy()
	 */
	@Override
	public Object copy() throws GamaRuntimeException {
		return new AmorphousTopology();
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IPopulation pop) throws GamaRuntimeException {}

	/**
	 * @see msi.gama.environment.ITopology#updateAgent(msi.gama.interfaces.IAgent, boolean,
	 *      msi.gama.util.GamaPoint, com.vividsolutions.jts.geom.Envelope)
	 */
	@Override
	public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
		final GamaPoint previousLoc, final Envelope previousEnv) {
		GamaGeometry ng =
			Spatial.Operators.opUnion(expandableEnvironment.getGeometry(), agent.getGeometry());
		expandableEnvironment.setGeometry(ng.getGeometricEnvelope());
	}

	/**
	 * @see msi.gama.environment.ITopology#removeAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void removeAgent(final IAgent agent) {}

	/**
	 * @see msi.gama.environment.ITopology#getAgentClosestTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter)
	 */
	@Override
	public IAgent getAgentClosestTo(final IGeometry source, final IAgentFilter filter) {
		return null;
	}

	/**
	 * @see msi.gama.environment.ITopology#getNeighboursOf(msi.gama.interfaces.IGeometry,
	 *      java.lang.Double, msi.gama.environment.IAgentFilter)
	 */
	@Override
	public GamaList<IAgent> getNeighboursOf(final IGeometry source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		return GamaList.EMPTY_LIST;
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public GamaList<IAgent> getAgentsIn(final IGeometry source, final IAgentFilter f,
		final boolean covered) {
		return GamaList.EMPTY_LIST;
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Double distanceBetween(final IGeometry source, final IGeometry target) {
		return source.euclidianDistanceTo(target);
	}

	/**
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaPath pathBetween(final IGeometry source, final IGeometry target)
		throws GamaRuntimeException {
		return new GamaPath(this, GamaList.with(source, target));
	}

	/**
	 * @see msi.gama.environment.ITopology#getDestination(msi.gama.util.GamaPoint, int, double,
	 *      boolean)
	 */
	@Override
	public GamaPoint getDestination(final GamaPoint source, final int direction,
		final double distance, final boolean nullIfOutside) {
		double cos = distance * MathUtils.cos(direction);
		double sin = distance * MathUtils.sin(direction);
		return new GamaPoint(source.x + cos, source.y + sin);

	}

	/**
	 * @see msi.gama.environment.ITopology#getRandomLocation()
	 */
	@Override
	public GamaPoint getRandomLocation() {
		return new GamaPoint(GAMA.getRandom().next(), GAMA.getRandom().next());
	}

	/**
	 * @see msi.gama.environment.ITopology#getPlaces()
	 */
	@Override
	public IContainer<?, IGeometry> getPlaces() {
		return GamaList.with(expandableEnvironment);
	}

	/**
	 * @see msi.gama.environment.ITopology#getEnvironment()
	 */
	@Override
	public IGeometry getEnvironment() {
		return expandableEnvironment;
	}

	/**
	 * @see msi.gama.environment.ITopology#normalizeLocation(msi.gama.util.GamaPoint, boolean)
	 */
	@Override
	public GamaPoint normalizeLocation(final GamaPoint p, final boolean nullIfOutside) {
		return p;
	}

	/**
	 * @see msi.gama.environment.ITopology#shapeChanged(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void shapeChanged(final IPopulation pop) {}

	/**
	 * @see msi.gama.environment.ITopology#getWidth()
	 */
	@Override
	public double getWidth() {
		return expandableEnvironment.getEnvelope().getWidth();
	}

	/**
	 * @see msi.gama.environment.ITopology#getHeight()
	 */
	@Override
	public double getHeight() {
		return expandableEnvironment.getEnvelope().getHeight();
	}

	/**
	 * @see msi.gama.environment.ITopology#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final GamaPoint p) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IGeometry g) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IGeometry g1, final IGeometry g2) {
		GamaPoint source = g1.getLocation();
		GamaPoint target = g2.getLocation();
		final double x2 = /* translateX(source.x, target.x); */target.x;
		final double y2 = /* translateY(source.y, target.y); */target.y;
		final double dx = x2 - source.x;
		final double dy = y2 - source.y;
		final double result = MathUtils.aTan2(dy, dx) * MathUtils.toDeg;
		return MathUtils.checkHeading((int) result);
	}

}
