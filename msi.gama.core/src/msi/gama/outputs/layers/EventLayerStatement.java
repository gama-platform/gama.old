/*******************************************************************************************************
 *
 * EventLayerStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.EventLayerStatement.EventLayerValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * Written by Marilleau Modified on 16 novembre 2012
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.EVENT,
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		concept = { IConcept.GUI })
@inside (
		symbols = { IKeyword.DISPLAY })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = { IType.STRING },
				optional = false,
				doc = @doc ("the type of event captured: basic events include #mouse_up, #mouse_down, #mouse_move, #mouse_exit, #mouse_enter, #mouse_menu, #arrow_down, #arrow_up, #arrow_left, #arrow_right, #escape, #tab, #enter, #page_up, #page_down or a character")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Type of device used to generate events. Defaults to 'default', which encompasses keyboard and mouse")),
				@facet (
						name = IKeyword.ACTION,
						type = IType.ACTION,
						optional = true,
						doc = @doc ("The identifier of the action to be executed in the context of the simulation. This action needs to be defined in 'global' or in the current experiment, without any arguments. The location of the mouse in the world can be retrieved in this action with the pseudo-constant #user_location")) },
		omissible = IKeyword.NAME)
@validator (EventLayerValidator.class)
@doc (
		value = "`" + IKeyword.EVENT
				+ "` allows to interact with the simulation by capturing mouse or key events and doing an action. The name of this action can be defined with the 'action:' facet, in which case the action needs to be defined in 'global' or in the current experiment, without any arguments."
				+ " The location of the mouse in the world can be retrieved in this action with the pseudo-constant #user_location. The statements to execute can also be defined in the block at the end of this statement, in which case they will be executed in the context of the experiment",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "event [event_type] action: myAction;",
						isExecutable = false) }),
				@usage (
						value = "For instance:",
						examples = { @example (
								value = "global {",
								isExecutable = false),
								@example (
										value = "   // ... ",
										isExecutable = false),
								@example (
										value = "   action myAction () {",
										isExecutable = false),
								@example (
										value = "      point loc <- #user_location; // contains the location of the mouse in the world",
										isExecutable = false),
								@example (
										value = "      list<agent> selected_agents <- agents inside (10#m around loc); // contains agents clicked by the event",
										isExecutable = false),
								@example (
										value = "      ",
										isExecutable = false),
								@example (
										value = "      // code written by modelers",
										isExecutable = false),
								@example (
										value = "   }",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "",
										isExecutable = false),
								@example (
										value = "experiment Simple type:gui {",
										isExecutable = false),
								@example (
										value = "   display my_display {",
										isExecutable = false),
								@example (
										value = "      event #mouse_up action: myAction;",
										isExecutable = false),
								@example (
										value = "   }",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, "graphics", IKeyword.GRID_LAYER,
				IKeyword.IMAGE_LAYER, IKeyword.OVERLAY, IKeyword.SPECIES_LAYER, })
public class EventLayerStatement extends AbstractLayerStatement {

	/**
	 * The Class EventLayerValidator.
	 */
	public static class EventLayerValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription description) {
			IExpressionDescription nameDesc = description.getFacet(NAME);
			final String name = nameDesc != null ? nameDesc.getExpression().literalValue() : null;
			if (name == null) {
				description.error("Impossible to find this action", IGamlIssue.UNKNOWN_ACTION, ACTION);
				return;
			}
			if (name.length() > 1) { // If it is not a char
				StringBuilder error = new StringBuilder();
				boolean foundEventName = false;
				for (final IEventLayerDelegate delegate : delegates) {
					error.append(delegate.getEvents()).append(" ");
					if (delegate.getEvents().contains(name)) { foundEventName = true; }
				}
				if (!foundEventName) {
					description.error("No event can be triggered for '" + name + "'. Acceptable values are "
							+ error.append(" or a character").toString(), IGamlIssue.UNKNOWN_ARGUMENT, NAME);
					return;
				}
			}

			final String actionName = description.getLitteral(ACTION);
			if (actionName != null) {
				if (actionName.contains(IKeyword.SYNTHETIC)) {
					description.warning(
							"This use of 'action' is deprecated. Move the sequence to execute at the end of the 'event' statement instead.",
							IGamlIssue.DEPRECATED, ACTION);
				}
				StatementDescription sd = description.getModelDescription().getAction(actionName);
				if (sd == null) {
					// we look into the experiment
					final IDescription superDesc = description.getSpeciesContext();
					sd = superDesc.getAction(actionName);
				}
				if (sd == null) {
					description.error("Action '" + actionName + "' is not defined in neither 'global' nor 'experiment'",
							IGamlIssue.UNKNOWN_ACTION, ACTION);
				} else if (sd.getPassedArgs().size() > 0) {
					description.error("Action '" + actionName
							+ "' cannot have arguments. Use '#user_location' inside to obtain the location of the mouse, and compute the selected agents in the action using GAML spatial operators",
							IGamlIssue.DIFFERENT_ARGUMENTS, ACTION);
				}

			}
		}
	}

	/** The executes in simulation. */
	private boolean executesInSimulation;

	/** The type. */
	private final IExpression type;

	/** The delegates. */
	public static List<IEventLayerDelegate> delegates = new ArrayList<>();

	/** The action name. */
	private String actionName;

	/** The action. */
	private ActionStatement action;

	/**
	 * @param createExecutableExtension
	 */
	public static void addDelegate(final IEventLayerDelegate delegate) {
		delegates.add(delegate);
	}

	/**
	 * Instantiates a new event layer statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public EventLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(/* context, */desc);
		executesInSimulation = false;
		if (description.hasFacet(IKeyword.ACTION)) {
			actionName = description.getLitteral(IKeyword.ACTION);
			final StatementDescription sd = description.getSpeciesContext().getAction(actionName);
			executesInSimulation = sd == null;
		}

		type = getFacet(IKeyword.TYPE);
	}

	/**
	 * Gets the executer.
	 *
	 * @param scope
	 *            the scope
	 * @return the executer
	 */
	public IAgent getExecuter(final IScope scope) {
		return executesInSimulation ? scope.getSimulation() : scope.getExperiment();
	}

	/**
	 * Executes in simulation.
	 *
	 * @return true, if successful
	 */
	public boolean executesInSimulation() {
		return executesInSimulation;
	}

	/**
	 * Gets the executable.
	 *
	 * @param scope
	 *            the scope
	 * @return the executable
	 */
	public IExecutable getExecutable(final IScope scope) {
		if (action != null) return action;
		IAgent agent = getExecuter(scope);
		return agent == null ? null : agent.getSpecies().getAction(actionName);
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {

		final Object source = getSource(scope);

		for (final IEventLayerDelegate delegate : delegates) {
			if (delegate.acceptSource(scope, source)) { delegate.createFrom(scope, source, this); }
		}
		return true;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.EVENT;
	}

	@Override
	public String toString() {
		return "Event layer: " + this.getFacet(IKeyword.NAME).literalValue();
	}

	/**
	 * Method _step()
	 *
	 * @see msi.gama.outputs.layers.AbstractLayerStatement#_step(msi.gama.runtime.IScope)
	 */
	@Override
	protected boolean _step(final IScope scope) {
		return true;
	}

	/**
	 * Gets the source.
	 *
	 * @param scope
	 *            the scope
	 * @return the source
	 */
	private Object getSource(final IScope scope) {
		return type == null ? IKeyword.DEFAULT : type.value(scope);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		final List<IStatement> statements = new ArrayList<>();
		for (final ISymbol c : commands) { if (c instanceof IStatement) { statements.add((IStatement) c); } }
		if (!statements.isEmpty()) {
			actionName = "inline";
			final IDescription d =
					DescriptionFactory.create(IKeyword.ACTION, getDescription(), IKeyword.NAME, "inline");
			action = new ActionStatement(d);
			action.setChildren(statements);
		}
	}
}
