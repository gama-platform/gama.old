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
package msi.gaml.operators;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import org.uncommons.maths.number.NumberGenerator;

/**
 * Written by drogoul Modified on 10 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Random {

	@operator(value = { "TGauss", "truncated_gauss" })
	public static Double opTGauss(final IScope scope, final GamaPoint p) {
		return opTGauss(scope, p.listValue(scope));
	}

	@operator(value = { "TGauss", "truncated_gauss" })
	public static Double opTGauss(final IScope scope, final List list) {
		if ( list.size() < 2 ) { return 0d; }
		final double mean = Cast.asFloat(scope, list.get(0));
		final double range = Cast.asFloat(scope, list.get(1));
		/*
		 * We want to have a real gamma like distribution though it s truncated one to do so we set
		 * that 2 stdDevation = deviation which means we will have 95% of the random generated
		 * number within ]mean - deviation; mean + deviation[ , thus in 5% of the time we will redo
		 * regenerate the number
		 */
		// double internalRange = bound / 2;
		double tmpResult = 0;
		NumberGenerator<Double> gen = GAMA.getRandom().createGaussian(mean, range / 2);
		// 'do while' does the truncature

		do {
			// we use bound / 2 as a standard deviation because we want to have
			// stdDeviation = 2 * bound
			tmpResult = gen.nextValue();
		} while (tmpResult > mean + range || tmpResult < mean - range);
		return tmpResult;

	}

	@operator(value = "gauss")
	public static Double opGauss(final IScope scope, final GamaPoint point) {
		final double mean = point.x;
		final double sd = point.y;
		return GAMA.getRandom().createGaussian(mean, sd).nextValue();
	}

	@operator(value = "poisson")
	public static Integer opPoisson(final IScope scope, final Double mean) {
		return GAMA.getRandom().createPoisson(mean).nextValue();
	}

	@operator(value = "binomial")
	public static Integer opBinomial(final IScope scope, final GamaPoint point) {
		final int n = (int) point.x;
		final double p = point.y;
		return GAMA.getRandom().createBinomial(n, p).nextValue();
	}

	@operator(value = "shuffle", content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static List opShuffle(final IScope scope, final List target) {
		final GamaList list = new GamaList(target);
		GAMA.getRandom().shuffle(list);
		return list;
	}

	@operator(value = "shuffle", content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static List opShuffle(final IScope scope, final ISpecies target) {
		return opShuffle(scope, scope.getAgentScope().getPopulationFor(target).getAgentsList());
	}

	@operator(value = "shuffle", content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IMatrix opShuffle(final IScope scope, final IMatrix target)
		throws GamaRuntimeException {
		return GAMA.getRandom().shuffle(target);
	}

	@operator(value = "shuffle", content_type = IType.STRING)
	public static String opShuffle(final IScope scope, final String target) {
		return GAMA.getRandom().shuffle(target);
	}

	@operator(value = { "any", "one_of" }, type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IAgent opAny(final IScope scope, final ISpecies l) {
		return scope.getAgentScope().getPopulationFor(l).getAgentsList().any();
	}

	@operator(value = "rnd")
	public static Integer opRnd(final IScope scope, final Integer max) {
		RandomAgent r = GAMA.getRandom();
		return r.between(0, max);
	}

	@operator(value = "rnd")
	public static Integer opRnd(final IScope scope, final Double max) {
		return GAMA.getRandom().between(0, max.intValue());
	}

	@operator(value = "rnd")
	public static GamaPoint opRnd(final IScope scope, final GamaPoint max) {
		final Integer x = GAMA.getRandom().between(0, (int) max.x);
		final Integer y = GAMA.getRandom().between(0, (int) max.y);
		return new GamaPoint(x, y);
	}

	@operator(value = "flip")
	public static Boolean opFlip(final IScope scope, final Double probability) {
		return probability > GAMA.getRandom().between(0., 1.);
	}

}
