package maps.gaml.edpSystem;

import java.util.HashMap;
import java.util.Map.Entry;

import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

import maps.gaml.edpSystem.SystemEDP;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;

public class UserEDPSystem extends SystemEDP {

	private GamaList<ExpressionBuilder> systemEDP;
	private GamaList<String> varName;
	
	public UserEDPSystem(GamaList<String> equations, GamaList<String> _varName, HashMap<String,Double> hmParam){
		super.numberEquation = equations.length() ;
		varName = _varName;
		
		systemEDP = new GamaList<ExpressionBuilder>();
		for(String equation : equations){
			ExpressionBuilder expBuilder = new ExpressionBuilder(equation);
			for(Entry<String, Double> entry : hmParam.entrySet()) {
			    String varName = entry.getKey();
			    Double varValue = Cast.asFloat(null, entry.getValue());
			    expBuilder.withVariable(varName, varValue);
			}				
			systemEDP.add(expBuilder);
		}		
	}
	
	@Override
	public GamaList<Double> compute(GamaList<Double> varValue) {
		GamaList<Double> systemReturn = new GamaList<Double>();
		
		for(ExpressionBuilder exprb : systemEDP) {
			for (int i = 0; i<= varValue.length() - 1; i++){
				exprb.withVariable(varName.get(i), varValue.get(i));
			}
			try {
				systemReturn.add(exprb.build().calculate());
			} catch (UnknownFunctionException e) {
				e.printStackTrace();
				throw new GamaRuntimeException("EDPSystem Computation Exception:  " + e.toString());
			} catch (UnparsableExpressionException e) {
				e.printStackTrace();
				throw new GamaRuntimeException("EDPSystem Computation Exception:  " + e.toString());				
			}
		}		
		return systemReturn;
	}

}
