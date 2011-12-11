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
package msi.gaml.operators;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import org.uncommons.maths.statistics.DataSet;

/**
 * Written by drogoul Modified on 15 janv. 2011
 * 
 * @todo Description
 * 
 */
public class Stats {

	private static DataSet from(final GamaList values) {
		DataSet d = new DataSet(values.size());
		for ( Object o : values ) {
			if ( o instanceof Number ) {
				d.addValue(((Number) o).doubleValue());
			}
		}
		return d;
	}

	@operator(value = "mean", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static Object getMean(final IScope scope, final IContainer l)
		throws GamaRuntimeException {
		if ( l.length() == 0 ) { return Double.valueOf(0d); }
		Object s = l.sum();
		if ( s instanceof Number ) { return ((Number) s).doubleValue() / l.length(); }
		if ( s instanceof GamaPoint ) { return Points.divide(((GamaPoint) s), l.length()); }
		return Cast.asFloat(scope, s) / l.length();
	}

	// Penser ˆ faire ces calculs sur les points, Žgalement (et les entiers ?)

	@operator(value = "median")
	public static Double opMedian(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getMedian();
	}

	@operator(value = "standard_deviation")
	public static Double opStDev(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getStandardDeviation();
	}

	@operator(value = "geometric_mean")
	public static Double opGeomMean(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getGeometricMean();
	}

	@operator(value = "harmonic_mean")
	public static Double opHarmonicMean(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getHarmonicMean();
	}

	@operator(value = "variance")
	public static Double opVariance(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getVariance();
	}

	@operator(value = "mean_deviation")
	public static Double opMeanDeviation(final IScope scope, final GamaList values) {
		DataSet d = from(values);
		return d.getMeanDeviation();
	}

	@operator(value = { "frequency_of" }, priority = IPriority.ITERATOR, iterator = true)
	public static GamaMap frequencyOf(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap result = new GamaMap();
		for ( Object each : original ) {
			scope.setEach(each);
			Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, 1);
			} else {
				result.put(key, (Integer) result.get(key) + 1);
			}
		}
		return result;
	}

}
