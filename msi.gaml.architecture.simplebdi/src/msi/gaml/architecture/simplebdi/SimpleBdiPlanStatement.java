/*********************************************************************************************
 * 
 *
 * 'SimpleBdiStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = { SimpleBdiArchitecture.PLAN}, kind = ISymbolKind.BEHAVIOR, with_sequence = true)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true),
	@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = SimpleBdiPlanStatement.INTENTION, type = IType.NONE, optional = true),
	@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true)}, omissible = IKeyword.NAME)
//Ajouter un facet "intention" qui complèterait le when.
public class SimpleBdiPlanStatement extends AbstractStatementSequence {
	
	public static final String INTENTION = "intention";

	 final IExpression _when;
	 final IExpression _priority;
	 final IExpression _executedwhen;
	 final IExpression _instantaneous;
	 final IExpression _intention;

	public IExpression getPriorityExpression() {
		return _priority;
	}

	public IExpression getContextExpression() {
		return _when;
	}

	public IExpression getExecutedExpression() {
		return _executedwhen;
	}

	public IExpression getInstantaneousExpression() {
		return _instantaneous;
	}

	public IExpression getIntentionExpression(){
		return _intention;
	}
	
	public SimpleBdiPlanStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(SimpleBdiPlanStatement.INTENTION);
		if ( hasFacet(IKeyword.NAME) ) {
			setName(getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( _when == null || Cast.asBool(scope, _when.value(scope)) ) { return super.privateExecuteIn(scope); }
		// scope.setStatus(ExecutionStatus.skipped);
		return null;
	}

	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, _priority.value(scope));
	}
}

