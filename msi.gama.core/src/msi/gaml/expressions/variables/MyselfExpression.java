package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

public class MyselfExpression extends TempVariableExpression {

	public MyselfExpression(final IType<?> type, final IDescription definitionDescription) {
		super(IKeyword.MYSELF, type, definitionDescription);
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {}

	@Override
	public String getTitle() {
		return "pseudo variable " + getName() + " of type " + getGamlType().getTitle();
	}

	@Override
	public String getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		return "pseudo variable " + getName() + " of type " + getGamlType().getTitle()
				+ (desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle());
	}

}