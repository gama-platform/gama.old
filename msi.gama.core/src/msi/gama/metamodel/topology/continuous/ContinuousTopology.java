/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.continuous;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Maths;

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
	public ContinuousTopology(final IScope scope, final IShape environment, final boolean torus) {
		super(scope, environment, torus);
		places = GamaList.with(environment);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "Continuous topology in " + environment.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return IKeyword.TOPOLOGY + "(" + environment.toGaml() + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(IScope scope) {
		return new ContinuousTopology(scope, environment, isTorus);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return environment.covers(p);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IShape g) {
		return environment.intersects(g);
	}

	@Override
	public Integer directionInDegreesTo(IScope scope, final IShape g1, final IShape g2) {
		// TODO Attention : calcul fait uniquement sur les locations. Il conviendrait plutot de
		// faire une DistanceOp().getNearestPoints()
		ILocation source = g1.getLocation();
		ILocation target = g2.getLocation();
		if ( isTorus ) {
			source = normalizeLocation(source, false);
			target = normalizeLocation(target, false);
		}

		// TODO for the moment, the direction to unreachable places can be determined
		// if ( !isValidLocation(source) ) {
		// ; // Necessary ?
		// return null;
		// }
		// if ( !isValidLocation(target) ) {
		// ;// Necessary ?
		// return null;
		// }
		final double x2 = /* translateX(source.x, target.x); */target.getX();
		final double y2 = /* translateY(source.y, target.y); */target.getY();
		final double dx = x2 - source.getX();
		final double dy = y2 - source.getY();
		final double result = Maths.aTan2(dy, dx) * Maths.toDeg;
		return Maths.checkHeading((int) result);
	}

	@Override
	public Double distanceBetween(IScope scope, final IShape g1, final IShape g2) {
		// if ( !isValidGeometry(g1) ) { return Double.MAX_VALUE; }
		// TODO is it useful to keep these tests ?
		// if ( !isValidGeometry(g2) ) { return Double.MAX_VALUE; }
		if ( g1 == g2 ) { return 0d; }
		if ( isTorus ) { return returnToroidalGeom(g1).distance(returnToroidalGeom(g2)); }
		return g1.euclidianDistanceTo(g2);
	}

	@Override
	public Double distanceBetween(IScope scope, final ILocation g1, final ILocation g2) {
		// if ( !isValidLocation(g1) ) { return Double.MAX_VALUE; }
		// TODO is it useful to keep these tests ?
		// if ( !isValidLocation(g2) ) { return Double.MAX_VALUE; }
		if ( g1 == g2 ) { return 0d; }
		if ( isTorus ) { return returnToroidalGeom(g1).distance(returnToroidalGeom(g2)); }
		return g1.euclidianDistanceTo(g2);
	}

	@Override
	public boolean isTorus() {
		// TODO Auto-generated method stub
		return isTorus;
	}

}