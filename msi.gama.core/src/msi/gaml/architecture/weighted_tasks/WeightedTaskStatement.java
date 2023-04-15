/*******************************************************************************************************
 *
 * WeightedTaskStatement.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.weighted_tasks;

import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.weighted_tasks.WeightedTaskStatement.TaskValidator;
import msi.gaml.compilation.IDescriptionValidator.ValidNameValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class WeightedTaskCommand. A simple definition of a task (set of commands) with a weight that can be computed
 * dynamically. Depending on the architecture in which the tasks are defined, this weight can be used to choose the
 * active task, or to define the order in which they are executed each step.
 *
 * @author drogoul
 */

@symbol (
		name = WeightedTaskStatement.TASK,
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		concept = { IConcept.BEHAVIOR, IConcept.SCHEDULER, IConcept.TASK_BASED, IConcept.ARCHITECTURE })
@inside (
		symbols = { WeightedTasksArchitecture.WT, SortedTasksArchitecture.ST, ProbabilisticTasksArchitecture.PT },
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = WeightedTaskStatement.WEIGHT,
				type = IType.FLOAT,
				optional = false,
				doc = @doc ("the priority level of the task")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = false,
						doc = @doc ("the identifier of the task")) },
		omissible = IKeyword.NAME)
@validator (TaskValidator.class)
@doc ("As reflex, a task is a sequence of statements that can be executed, at each time step, by the agent. If an agent owns several tasks, the scheduler chooses a task to execute based on its current priority weight value.")
public class WeightedTaskStatement extends AbstractStatementSequence {

	/** The Allowed architectures. */
	static List<String> AllowedArchitectures =
			Arrays.asList(SortedTasksArchitecture.ST, WeightedTasksArchitecture.WT, ProbabilisticTasksArchitecture.PT);

	/**
	 * The Class TaskValidator.
	 */
	public static class TaskValidator extends ValidNameValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			if (!Assert.nameIsValid(description)) { return; }
			// Verify that the task is inside a species with task-based control
			final SpeciesDescription species = description.getSpeciesContext();
			final SkillDescription control = species.getControl();
			if (!WeightedTasksArchitecture.class.isAssignableFrom(control.getJavaBase())) {
				description.error("A " + description.getKeyword()
						+ " can only be defined in a task-controlled species  (one of" + AllowedArchitectures + ")",
						IGamlIssue.WRONG_CONTEXT);
			}
		}
	}

	/** The Constant WEIGHT. */
	protected static final String WEIGHT = "weight";
	
	/** The Constant TASK. */
	protected static final String TASK = "task";
	
	/** The weight. */
	protected IExpression weight;

	/**
	 * Instantiates a new weighted task statement.
	 *
	 * @param desc the desc
	 */
	public WeightedTaskStatement(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		weight = getFacet(WEIGHT);
	}

	/**
	 * Compute weight.
	 *
	 * @param scope the scope
	 * @return the double
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public Double computeWeight(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, weight.value(scope));
	}

}
