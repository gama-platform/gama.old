package msi.gama.util;

import org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.GLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "parameters", type = IType.LIST, of = IType.FLOAT),
	@var(name = "nb_features", type = IType.INT)})
public class GamaRegression implements IValue{

	RegressionResults regressionResults;
	int nbFeatures;
	double param[] ;
	
	public GamaRegression(IScope scope, GamaFloatMatrix data, String method) throws Exception{
		AbstractMultipleLinearRegression regressionMethod = null;
		if (method.equals("GLS"))
			regressionMethod = new GLSMultipleLinearRegression();
		else regressionMethod = new OLSMultipleLinearRegression();
		int nbFeatures = data.numCols -1;
		int nbInstances = data.numRows ;
		double[] instances = new double[data.numCols * data.numRows];
		for (int i = 0; i < data.length(scope); i++ ) {
			instances[i] = data.getMatrix()[i];
		}
		regressionMethod.newSampleData(instances, nbInstances,nbFeatures);
		param = regressionMethod.estimateRegressionParameters();
	}
	
	
	
	public GamaRegression(double[] param, int nbFeatures,RegressionResults regressionResults) {
		super();
		this.regressionResults = regressionResults;
		this.nbFeatures = nbFeatures;
		this.param = param;
	}

	


	public Double predict(IScope scope, GamaList<Double> instance) {
		if (param == null)
			return null;
		double val = param[0];
		for (int i = 1; i < param.length; i++) {
			val += param[i]*Cast.asFloat(scope, instance.get(i-1));
		}
		return val;
	}
	
	@getter("parameters")
	public IList<Double> getParameters() {
		if (param == null) return GamaListFactory.create();
		IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (int i = 0; i < param.length; i++) vals.add(param[i]);
		return vals;
	}
	
	@getter("nb_features")
	public Integer getNbFeatures() {
		return nbFeatures;
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return stringValue(null);
	}
	
	

	@Override
	public IType getType() {
		return Types.get( IType.REGRESSION);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		if (param == null) return "no function";
		String st = "y = " + param[0];
		for (int i = 1; i < param.length; i++) {
			st += " + " + param[i] + " x" + i;
		}
		return st;
	}
	

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		GamaRegression gr = new GamaRegression(param.clone(),nbFeatures,regressionResults);
		return gr;
	}
	
}
