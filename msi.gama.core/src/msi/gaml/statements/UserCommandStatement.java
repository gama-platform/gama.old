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
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
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
@facets(value = { @facet(name = IKeyword.ACTION, type = IType.ID, optional = true, doc = @doc("the identifier of the action to be executed")),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the user_command")),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("the condition that should be fulfille in order that the action is executed")),
	@facet(name = IKeyword.WITH, type = IType.MAP, optional = true, doc = @doc("the map of the parameters::values that requires the action")) }, omissible = IKeyword.NAME)
@doc(value="Anywhere in the global block, in a species or in an (GUI) experiment, user_command statements allows to either call directly an existing action (with or without arguments) or to be followed by a block that describes what to do when this command is run.", usages={
		@usage(value="The general syntax is for example:", examples = @example(value="user_command kill_myself action: some_action with: [arg1::val1, arg2::val2, ...];", isExecutable=false))
	}, see = {IKeyword.USER_INIT,IKeyword.USER_PANEL, IKeyword.USER_INPUT})
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
