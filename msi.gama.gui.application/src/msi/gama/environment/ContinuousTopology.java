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

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

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
	public ContinuousTopology(final IScope scope, final IGeometry environment/* , final boolean torus */) {
		super(scope, environment/* , torus */);
		places = GamaList.with(environment);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "Continuous topology in " + environment.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return ISymbol.TOPOLOGY + "(" + environment.toGaml() + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy() {
		return new ContinuousTopology(scope, environment/* , isTorus */);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final GamaPoint p) {
		return environment.covers(p);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IGeometry g) {
		return environment.intersects(g);
	}

	@Override
	public Integer directionInDegreesTo(final IGeometry g1, final IGeometry g2) {
		// TODO Attention : calcul fait uniquement sur les locations. Il conviendrait plutot de
		// faire une DistanceOp().getNearestPoints()
		GamaPoint source = g1.getLocation();
		GamaPoint target = g2.getLocation();
		// TODO for the moment, the direction to unreachable places can be determined
		// if ( !isValidLocation(source) ) {
		// ; // Necessary ?
		// return null;
		// }
		// if ( !isValidLocation(target) ) {
		// ;// Necessary ?
		// return null;
		// }
		final double x2 = /* translateX(source.x, target.x); */target.x;
		final double y2 = /* translateY(source.y, target.y); */target.y;
		final double dx = x2 - source.x;
		final double dy = y2 - source.y;
		final double result = MathUtils.aTan2(dy, dx) * MathUtils.toDeg;
		return MathUtils.checkHeading((int) result);
	}

	@Override
	public Double distanceBetween(final IGeometry g1, final IGeometry g2) {
		if ( !isValidGeometry(g1) ) { return Double.MAX_VALUE; }
		if ( !isValidGeometry(g2) ) { return Double.MAX_VALUE; }
		if ( g1 == g2 ) { return 0d; }
		return g1.euclidianDistanceTo(g2);
	}

}