/*********************************************************************************************
 * 
 *
 * 'UserCommandStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserInputStatement;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 7 févr. 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.USER_COMMAND }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_args = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL }, symbols = IKeyword.USER_PANEL)
@facets(value = { @facet(name = IKeyword.ACTION, type = IType.ID, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.WITH, type = IType.MAP, optional = true) }, omissible = IKeyword.NAME)
public class UserCommandStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	Arguments args;
	final String actionName;
	final IExpression when;
	List<UserInputStatement> inputs = new ArrayList();

	public UserCommandStatement(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		actionName = getLiteral(IKeyword.ACTION);
		when = getFacet(IKeyword.WHEN);
	}

	public List<UserInputStatement> getInputs() {
		return inputs;
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		this.args = args;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( final ISymbol c : children ) {
			if ( c instanceof UserInputStatement ) {
				inputs.add((UserInputStatement) c);
			}
		}
		children.removeAll(inputs);
		super.setChildren(children);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( isEnabled(scope) ) {
			if ( actionName == null ) { return super.privateExecuteIn(scope); }
			final ISpecies context = scope.getAgentScope().getSpecies();
			final IStatement.WithArgs executer = context.getAction(actionName);
			executer.setRuntimeArgs(args);
			final Object result = executer.executeOn(scope);
			return result;
		}
		// scope.setStatus(ExecutionStatus.success);
		return null;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	// @Override
	// public IType getType() {
	// if ( actionName == null ) { return super.getType(); }
	// final StatementDescription executer = description.getSpeciesContext().getAction(actionName);
	// return executer.getType();
	// }

	// @Override
	// public IType getContentType() {
	// if ( actionName == null ) { return super.getContentType(); }
	// final StatementDescription executer = description.getSpeciesContext().getAction(name);
	// return executer.getContentType();
	// }
	//
	// @Override
	// public IType getKeyType() {
	// if ( actionName == null ) { return Types.NO_TYPE; }
	// final StatementDescription executer = description.getSpeciesContext().getAction(name);
	// return executer.getKeyType();
	// }

	public boolean isEnabled(final IScope scope) {
		return when == null || Cast.asBool(scope, when.value(scope));
	}

}
