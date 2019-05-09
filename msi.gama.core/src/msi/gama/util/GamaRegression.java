/*******************************************************************************************************
 *
 * msi.gama.util.GamaRegression.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ 
	@variable(name = "parameters", type = IType.LIST, of = IType.FLOAT,
			doc = { @doc ("List of regression coefficients (float) - same order as the variable in the input matrix ")}), 
	@variable(name = "nb_features", type = IType.INT, 
		doc = { @doc ("number of variables")})
	})
public class GamaRegression implements IValue {

	RegressionResults regressionResults;
	int nbFeatures;
	double param[];

	public GamaRegression(final IScope scope, final GamaMatrix<?> data) throws Exception {
		AbstractMultipleLinearRegression regressionMethod = new OLSMultipleLinearRegression();
		final int nbFeatures = data.numCols - 1;
		final int nbInstances = data.numRows;
		
		final double[] instances = new double[data.numCols * data.numRows];
		
		for (int i = 0; i < data.length(scope); i++) {
			instances[i] = Cast.asFloat(scope, data.getNthElement(i));
		}
		regressionMethod.newSampleData(instances, nbInstances, nbFeatures);
		param = regressionMethod.estimateRegressionParameters();
	}

	public GamaRegression(final double[] param, final int nbFeatures, final RegressionResults regressionResults) {
		super();
		this.regressionResults = regressionResults;
		this.nbFeatures = nbFeatures;
		this.param = param;
	}

	public Double predict(final IScope scope, final GamaList<?> instance) {
		if (param == null)
			return null;
		double val = param[0];
		for (int i = 1; i < param.length; i++) {
			val += param[i] * Cast.asFloat(scope, instance.get(i - 1));
		}
		return val;
	}

	@getter("parameters")
	public IList<Double> getParameters() {
		if (param == null)
			return GamaListFactory.create(Types.FLOAT);
		final IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (int i = 0; i < param.length; i++)
			vals.add(param[i]);
		return vals;
	}

	@getter("nb_features")
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
		if (param == null)
			return "no function";
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
