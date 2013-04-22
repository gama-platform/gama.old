package msi.gaml.architecture.user;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;

// FIXME Implement this class to allow expressions like
// int aa <- user_input("Enter value", 10);

public class UserInputExpression extends AbstractNAryOperator {

	@Override
	public IOperator copy() {
		return null;
	}

	@Override
	public IOperator init(String operator, IDescription context, IExpression ... args) {
		return null;
	}

	@Override
	public Object value(IScope scope) throws GamaRuntimeException {
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
