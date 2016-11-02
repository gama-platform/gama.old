/*********************************************************************************************
 *
 * 'UserCommandStatement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.FluentIterable;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.architecture.user.UserInputStatement;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.UserCommandStatement.UserCommandValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 7 févr. 2010
 *
 * @todo Description
 *
 */
@symbol(name = {
		IKeyword.USER_COMMAND }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_args = true, concept = {
				IConcept.GUI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL }, symbols = IKeyword.USER_PANEL)
@facets(value = {
		@facet(name = IKeyword.CONTINUE, type = IType.BOOL, optional = true, doc = @doc("Whether or not the button, when clicked, should dismiss the user panel it is defined in. Has no effect in other contexts (menu, parameters, inspectors)")),
		@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true, doc = @doc("The color of the button to display")),
		@facet(name = IKeyword.ACTION, type = IType.ID, optional = true, doc = @doc("the identifier of the action to be executed. This action should be accessible in the context in which it is defined (an experiment, the global section or a species). A special case is allowed to maintain the compatibility with older versions of GAMA, when the user_command is declared in an experiment and the action is declared in 'global'. In that case, all the simulations managed by the experiment will run the action in response to the user executing the command")),
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the user_command")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("the condition that should be fulfilled (in addition to the user clicking it) in order to execute this action")),
		@facet(name = IKeyword.WITH, type = IType.MAP, optional = true, doc = @doc("the map of the parameters::values required by the action")) }, omissible = IKeyword.NAME)
@doc(value = "Anywhere in the global block, in a species or in an (GUI) experiment, user_command statements allows to either call directly an existing action (with or without arguments) or to be followed by a block that describes what to do when this command is run.", usages = {
		@usage(value = "The general syntax is for example:", examples = @example(value = "user_command kill_myself action: some_action with: [arg1::val1, arg2::val2, ...];", isExecutable = false)) }, see = {
				IKeyword.USER_INIT, IKeyword.USER_PANEL, IKeyword.USER_INPUT })
@validator(UserCommandValidator.class)

public class UserCommandStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class UserCommandValidator implements IDescriptionValidator<IDescription> {

		/*
		 * (non-Javadoc)
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.
		 * descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			final String action = description.getLitteral(ACTION);

			final IDescription enclosing = description.getEnclosingDescription();
			if (action != null && enclosing.getAction(action) == null) {
				// 2 cases: we are in a simulation or in a "normal" species and
				// we emit an error, or we are in an experiment, in which case
				// we try to see if the simulations can run it. In that case we
				// emit a warning (see Issue #1595)
				if (enclosing instanceof ExperimentDescription) {
					final ModelDescription model = enclosing.getModelDescription();
					if (model.hasAction(action)) {
						description.warning(
								"Action " + action
										+ " should be defined in the experiment, not in global. To maintain the compatibility with GAMA 1.6.1, the command will execute it on all the simulations managed by this experiment",
								IGamlIssue.WRONG_CONTEXT, ACTION);
					} else {
						description.error("Action " + action + " does not exist in this experiment",
								IGamlIssue.UNKNOWN_ACTION, ACTION);
					}
				} else {
					final String enclosingName = enclosing instanceof ModelDescription ? "global" : enclosing.getName();
					description.error("Action " + action + " does not exist in " + enclosingName,
							IGamlIssue.UNKNOWN_ACTION, ACTION);
				}
			}
		}
	}

	Arguments args;
	Arguments runtimeArgs;
	final String actionName;
	final IExpression when;
	List<UserInputStatement> inputs = new ArrayList<>();

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
	public void setChildren(final Iterable<? extends ISymbol> children) {
		for (final ISymbol c : children) {
			if (c instanceof UserInputStatement) {
				inputs.add((UserInputStatement) c);
			}
		}
		super.setChildren(FluentIterable.from(children).filter(each -> !inputs.contains(each)));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (isEnabled(scope)) {
			if (actionName == null) {
				if (runtimeArgs != null) {
					scope.stackArguments(runtimeArgs);
				}
				// AD 2/1/16 : Addition of this to address Issue #1339
				for (final UserInputStatement s : inputs) {
					s.executeOn(scope);
				}
				final Object result = super.privateExecuteIn(scope);
				runtimeArgs = null;
				return result;
			}
			ISpecies context = scope.getAgent().getSpecies();
			IStatement.WithArgs executer = context.getAction(actionName);
			boolean isWorkaroundForIssue1595 = false;
			if (executer == null) {
				// See Issue #1595
				if (context instanceof ExperimentPlan) {
					context = ((ExperimentPlan) context).getModel();
					executer = context.getAction(actionName);
					isWorkaroundForIssue1595 = true;
				} else {
					throw GamaRuntimeException.error("Unknown action: " + actionName, scope);
				}
			}
			final Arguments tempArgs = new Arguments(args);
			if (runtimeArgs != null) {
				tempArgs.complementWith(runtimeArgs);
			}
			if (isWorkaroundForIssue1595) {
				final SimulationPopulation simulations = scope.getExperiment().getSimulationPopulation();
				for (final SimulationAgent sim : simulations.iterable(scope)) {
					scope.execute(executer, sim, tempArgs);
				}
			} else {
				executer.setRuntimeArgs(tempArgs);
				final Object result = executer.executeOn(scope);
				runtimeArgs = null;
				return result;
			}
		}
		return null;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		this.runtimeArgs = args;
	}

	public boolean isEnabled(final IScope scope) {
		return when == null || Cast.asBool(scope, when.value(scope));
	}

	public GamaColor getColor(final IScope scope) {
		final IExpression exp = getFacet(IKeyword.COLOR);
		if (exp == null)
			return null;
		return Cast.asColor(scope, exp.value(scope));
	}

	public boolean isContinue(final IScope scope) {
		final IExpression exp = getFacet(IKeyword.CONTINUE);
		if (exp == null)
			return false;
		return Cast.asBool(scope, exp.value(scope));
	}

}
