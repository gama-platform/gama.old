/*********************************************************************************************
 * 
 *
 * 'WeightedTaskStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.weighted_tasks;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.weighted_tasks.WeightedTaskStatement.TaskValidator;
import msi.gaml.compilation.IDescriptionValidator.ValidNameValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class WeightedTaskCommand. A simple definition of a task (set of commands) with a weight that
 * can be computed dynamically. Depending on the architecture in which the tasks are defined, this
 * weight can be used to choose the active task, or to define the order in which they are executed
 * each step.
 * 
 * @author drogoul
 */

@symbol(name = WeightedTaskStatement.TASK, kind = ISymbolKind.BEHAVIOR, with_sequence = true)
@inside(symbols = WeightedTasksArchitecture.WT, kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT,
	ISymbolKind.MODEL })
@facets(value = { @facet(name = WeightedTaskStatement.WEIGHT, type = IType.FLOAT, optional = false),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, omissible = IKeyword.NAME)
@validator(TaskValidator.class)
public class WeightedTaskStatement extends AbstractStatementSequence {

	static List<String> AllowedArchitectures = Arrays.asList(SortedTasksArchitecture.ST, WeightedTasksArchitecture.WT,
		ProbabilisticTasksArchitecture.PT);

	public static class TaskValidator extends ValidNameValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			if ( !Assert.nameIsValid(description) ) { return; }
			// Verify that the task is inside a species with task-based control
			SpeciesDescription species = description.getSpeciesContext();
			String control = species.getControlName();
			if ( !AllowedArchitectures.contains(control) ) {
				description.error("A " + description.getKeyword() +
					" can only be defined in a task-controlled species  (one of" + AllowedArchitectures + ")",
					IGamlIssue.WRONG_CONTEXT);
				return;
			}
		}
	}

	protected static final String WEIGHT = "weight";
	protected static final String TASK = "task";
	protected IExpression weight;

	public WeightedTaskStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME));
		weight = getFacet(WEIGHT);
	}

	public Double computeWeight(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, weight.value(scope));
	}

}
