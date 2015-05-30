package msi.gama.util;

import org.apache.commons.math3.stat.regression.UpdatingMultipleLinearRegression;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "nb_instances", type = IType.INT),
	@var(name = "nb_features", type = IType.INT)})
public class GamaRegression implements IValue{

	UpdatingMultipleLinearRegression regressionMethod;
	int nbFeatures;
	
	
	@getter("nb_instances")
	public Integer getNbData() {
		if (regressionMethod == null) return 0;
		return (int) regressionMethod.getN();
	}
	
	@getter("nb_features")
	public Integer getNbFeatures() {
		return nbFeatures;
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return null;
	}

	@Override
	public IType getType() {
		return Types.get( IType.REGRESSION);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "Regression model with "+ getNbData()+" instances composed of " + nbFeatures + " features";
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		GamaRegression gr = new GamaRegression();
		gr.nbFeatures = nbFeatures;
		gr.regressionMethod = regressionMethod;
		return gr;
	}
	
}
