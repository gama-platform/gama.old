/*******************************************************************************************************
 *
 * GamaRegression.java, in ummisco.gaml.extensions.stats, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.stats;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaRegression.
 */
@vars ({ @variable (
			name = "parameters",
			type = IType.LIST,
			of = IType.FLOAT,
			doc = { @doc ("List of regression coefficients (float) - same order as the variable in the input matrix ") }),
		@variable (
			name = "nb_features",
			type = IType.INT,
			doc = { @doc ("number of variables") }),
		@variable (
			name = "RSquare",
			type = IType.FLOAT,
			doc = { @doc ("Estimated pearson's R-squared statistic") }),
		@variable (
			name = "residuals",
			type = IType.LIST,
			of = IType.FLOAT,
			doc = { @doc ("error terms associated to each observation of the sample") })	
	})
public class GamaRegression implements IValue {

	/** The regression results. */
	RegressionResults regressionResults;
	
	/** The nb features. */
	int nbFeatures;
	
	/** The param. */
	double param[];
	
	/** The error. */
	double error[];
	
	/** The rsquare. */
	double rsquare;

	/**
	 * Instantiates a new gama regression.
	 *
	 * @param scope the scope
	 * @param data the data
	 * @throws Exception the exception
	 */
	public GamaRegression(final IScope scope, final GamaMatrix<?> data) throws Exception {
		final OLSMultipleLinearRegression regressionMethod = new OLSMultipleLinearRegression();
		final int nbFeatures = data.numCols - 1;
		final int nbInstances = data.numRows;

		final double[] instances = new double[data.numCols * data.numRows];

		for (int i = 0; i < data.length(scope); i++) {
			instances[i] = Cast.asFloat(scope, data.getNthElement(i));
		}
		regressionMethod.newSampleData(instances, nbInstances, nbFeatures);
		param = regressionMethod.estimateRegressionParameters();
		rsquare = regressionMethod.calculateAdjustedRSquared();
		error = regressionMethod.estimateResiduals();
	}

	/**
	 * Instantiates a new gama regression.
	 *
	 * @param param the param
	 * @param nbFeatures the nb features
	 * @param regressionResults the regression results
	 */
	public GamaRegression(final double[] param, final int nbFeatures, final RegressionResults regressionResults) {
		super();
		this.regressionResults = regressionResults;
		this.nbFeatures = nbFeatures;
		this.param = param;
	}

	/**
	 * Predict.
	 *
	 * @param scope the scope
	 * @param instance the instance
	 * @return the double
	 */
	public Double predict(final IScope scope, final IList<?> instance) {
		if (param == null) { return null; }
		double val = param[0];
		for (int i = 1; i < param.length; i++) {
			val += param[i] * Cast.asFloat(scope, instance.get(i - 1));
		}
		return val;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@getter ("parameters")
	public IList<Double> getParameters() {
		if (param == null) { return GamaListFactory.create(Types.FLOAT); }
		final IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (final double element : param) {
			vals.add(element);
		}
		return vals;
	}
	
	/**
	 * Gets the residuals.
	 *
	 * @return the residuals
	 */
	@getter ("residuals")
	public IList<Double> getResiduals() {
		IList<Double> res = GamaListFactory.create(Types.FLOAT);
		if (error != null) {for (double e : error) {res.add(e);} }
		return res;
	}
	
	/**
	 * Gets the r square.
	 *
	 * @return the r square
	 */
	@getter ("RSquare")
	public double getRSquare() {
		return rsquare;
	}

	/**
	 * Gets the nb features.
	 *
	 * @return the nb features
	 */
	@getter ("nb_features")
	public Integer getNbFeatures() {
		return nbFeatures;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return stringValue(null);
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(IType.REGRESSION);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		if (param == null) { return "no function"; }
		String st = "y = " + param[0];
		for (int i = 1; i < param.length; i++) {
			st += " + " + param[i] + " x" + i;
		}
		return st;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		final GamaRegression gr = new GamaRegression(param.clone(), nbFeatures, regressionResults);
		return gr;
	}

}
