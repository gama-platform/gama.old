package maps.gaml.edpSystem;

import java.util.*;
import java.util.Map.Entry;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import de.congrace.exp4j.*;

public class UserEDPSystem extends SystemEDP {

	private final GamaList<ExpressionBuilder> systemEDP;
	private final GamaList<String> varName;

	public UserEDPSystem(final IScope scope, final GamaList<String> equations,
		final GamaList<String> _varName, final HashMap<String, Double> hmParam) {
		super.numberEquation = equations.length(scope);
		varName = _varName;

		systemEDP = new GamaList<ExpressionBuilder>();
		for ( String equation : equations ) {
			ExpressionBuilder expBuilder = new ExpressionBuilder(equation);
			for ( Entry<String, Double> entry : hmParam.entrySet() ) {
				String varName = entry.getKey();
				Double varValue = Cast.asFloat(null, entry.getValue());
				expBuilder.withVariable(varName, varValue);
			}
			systemEDP.add(expBuilder);
		}
	}

	@Override
	public GamaList<Double> compute(final GamaList<Double> varValue) {
		GamaList<Double> systemReturn = new GamaList<Double>();

		for ( ExpressionBuilder exprb : systemEDP ) {
			for ( int i = 0; i <= varValue.length(null) - 1; i++ ) { // VERIFY NULL SCOPE
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
