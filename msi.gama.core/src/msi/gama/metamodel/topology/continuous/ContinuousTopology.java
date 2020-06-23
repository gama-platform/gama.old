/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.continuous.ContinuousTopology.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.continuous;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.AbstractTopology;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gaml.operators.Maths;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 4 juil. 2011
 *
 * @todo Description
 *
 */
public class ContinuousTopology extends AbstractTopology {

	/**
	 * Initializes inner environment for agents other than "world".
	 *
	 * @param directMacro
	 * @param torus
	 */
	public ContinuousTopology(final IScope scope, final IShape environment) {
		super(scope, environment, null);
		places = GamaListFactory.wrap(Types.GEOMETRY, environment);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Continuous topology in " + environment.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml(final boolean includingBuiltIn) {
		return IKeyword.TOPOLOGY + "(" + environment.serialize(includingBuiltIn) + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(final IScope scope) {
		return new ContinuousTopology(scope, environment);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final IScope scope, final ILocation p) {
		return environment.covers(p);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IScope scope, final IShape g) {
		return environment.intersects(g);
	}

	@Override
	public Double directionInDegreesTo(final IScope scope, final IShape g1, final IShape g2) {
		// TODO Attention : calcul fait uniquement sur les locations. Il
		// conviendrait plutot de
		// faire une DistanceOp().getNearestPoints()
		if (g1 == null || g2 == null) { return null; }
		ILocation source = g1.getLocation();
		ILocation target = g2.getLocation();
		if (isTorus()) {
			source = normalizeLocation(source, false);
			target = normalizeLocation(target, false);
		}

		final double x2 = /* translateX(source.x, target.x); */target.getX();
		final double y2 = /* translateY(source.y, target.y); */target.getY();
		final double dx = x2 - source.getX();
		final double dy = y2 - source.getY();
		// AD 21/03/15: Fixes two long-standing bugs (see Issue 1177) + problems
		// in MovingSkill.move().
		final double result = Maths.atan2(dy, dx);
		return Maths.checkHeading(result);
	}

	@Override
	public boolean isContinuous() {
		return true;
	}

	@Override
	public Double distanceBetween(final IScope scope, final IShape g1, final IShape g2) {
		if (g1 == g2) { return 0d; }
		if (isTorus()) { return returnToroidalGeom(g1).distance(returnToroidalGeom(g2)); }
		return g1.euclidianDistanceTo(g2);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation g1, final ILocation g2) {
		if (g1 == g2) { return 0d; }
		if (isTorus()) { return returnToroidalGeom(g1).distance(returnToroidalGeom(g2)); }
		return g1.euclidianDistanceTo(g2);
	}

}