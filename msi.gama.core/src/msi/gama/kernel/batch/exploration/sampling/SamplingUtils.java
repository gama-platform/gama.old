package msi.gama.kernel.batch.exploration.sampling;


import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;

/**
 * 
 * @author tomroy
 *
 */
/**
 * 
 * This abstract class gives methods for all sampling methods.
 *
 */
public abstract class SamplingUtils {
	
	public SamplingUtils() {
	}
	
	/**
	 * This scale the sampling value for discrete values
	 * @param scope
	 * @param set
	 * @param var
	 * @param ValFromSampling
	 * @return
	 */
	private ParametersSet ScaleSamplingAmongValue(IScope scope, ParametersSet set, Batch var, double ValFromSampling) {
		int size= var.getAmongValue(scope).size();
		if(ValFromSampling>=0 && ValFromSampling< 1/(size+1)) {
			set.put(var.getName(),var.getAmongValue(scope).get(0));
			return set;
		}else {
			for(int i=1;i<size;i++) {
				if(ValFromSampling >= (i/(size+1)) && ValFromSampling < (i+1)/(size+1)) {
					set.put(var.getName(), var.getAmongValue(scope).get(i));
					return set;
				}
			} 

		}
		throw GamaRuntimeException.error("Fail during the scale sampling method for Among Value", scope);
	}
	
	/**
	 * This function scale the sampling value
	 * @param scope
	 * @param set
	 * @param var
	 * @param ValFromSampling
	 * @return
	 */
	private ParametersSet ScaleSampling(IScope scope, ParametersSet set, Batch var, double ValFromSampling) {
		switch (var.getType().id()) {
		case IType.INT:
			int intValue = Cast.asInt(scope, var.getMinValue(scope));
			int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
			int sampleIValue = Math.round(Math.round(intValue + ValFromSampling * (maxIntValue - intValue)));
			set.put(var.getName(), sampleIValue);
			return set;
		case IType.FLOAT:
			double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
			double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
			double sampleFValue = floatValue + ValFromSampling * (maxFloatValue - floatValue);
			set.put(var.getName(), sampleFValue);
			return set;
		case IType.DATE:
			GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
			GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
			GamaDate sampleDValue = dateValue.plus( dateValue.getTemporal().until(maxDateValue, ChronoUnit.SECONDS) 
					* ValFromSampling, ChronoUnit.SECONDS );
			set.put(var.getName(), sampleDValue);
			return set;
		case IType.POINT:
			GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
			GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
			
			double samplePXValue = pointValue.getX() + ValFromSampling * (maxPointValue.getX() - pointValue.getX());
			double samplePYValue = pointValue.getY() + ValFromSampling * (maxPointValue.getY() - pointValue.getY());
			double samplePZValue = pointValue.getZ() + ValFromSampling * (maxPointValue.getZ() - pointValue.getZ());
			set.put(var.getName(), new GamaPoint(samplePXValue,samplePYValue,samplePZValue));
			return set;
		case IType.BOOL:
			set.put(var.getName(), ValFromSampling>0.5?true:false);
			return set;
		default:
			throw GamaRuntimeException.error("Trying to add a variable of unknown type "+var.getType().id()+" to a parameter set", scope);
		}
	}
	
	/**
	 * Build the parameters set from samples between 0 and 1, and scale it between min and max value of the parameters
	 * @param scope
	 * @param parameters
	 * @param SamplingData
	 * @return
	 */
	public List<ParametersSet> BuildParametersSetfromSample(IScope scope,List<Batch> parameters,List<Map<String,Double>> SamplingData){
		List<ParametersSet> ParameterSet=new ArrayList<>();
		for(int i=0;i<SamplingData.size();i++) {
			ParametersSet origi = new ParametersSet();
			for(int y=0;y<parameters.size();y++) {
				if(parameters.get(y).getAmongValue(scope)!=null) {
					origi=ScaleSamplingAmongValue(scope,origi,parameters.get(y),SamplingData.get(i).get(parameters.get(y).getName()));
				}else {
					origi=ScaleSampling(scope,origi,parameters.get(y),SamplingData.get(i).get(parameters.get(y).getName()));
				}
			}
			ParameterSet.add(origi);
		}
		return ParameterSet;
	}
}
