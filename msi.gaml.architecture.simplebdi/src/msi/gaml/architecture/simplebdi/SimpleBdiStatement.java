package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.ExecutionStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = {SimpleBdiArchitecture.PLAN, SimpleBdiArchitecture.PERCEIVE}, kind = ISymbolKind.BEHAVIOR, with_sequence = true)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL_STR, optional = true),
		@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class SimpleBdiStatement extends AbstractStatementSequence{
	
	private final IExpression _when;
	private final IExpression _priority;
	
	public IExpression getPriorityExpression(){
		return _priority;
	}
	
	public IExpression getContextExpression(){
		return _when;
	}
	
	public SimpleBdiStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		if ( hasFacet(IKeyword.NAME) ) {
			setName(getLiteral(IKeyword.NAME));
		}
	}
	
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( _when == null || Cast.asBool(scope, _when.value(scope)) ) { return super
			.privateExecuteIn(scope); }
		scope.setStatus(ExecutionStatus.skipped);
		return null;
	}
	
	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, _priority.value(scope));
	}
}
