/*********************************************************************************************
 *
 * 'EventLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
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
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
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
				name = "unused",
				type = IType.ID,
				values = { "mouse_up", "mouse_down", "mouse_move", "mouse_enter", "mouse_exit" },
				optional = true,
				doc = @doc (
						value = "an unused facet that serves only for the purpose of declaring the string values"),
				internal = true),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						// values = { "mouse_up", "mouse_down", "mouse_drag" },
						optional = false,
						doc = @doc ("the type of event captured: can be  \"mouse_up\", \"mouse_down\", \"mouse_move\", \"mouse_exit\", \"mouse_enter\" or a character")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Type of peripheric used to generate events. Defaults to 'default', which encompasses keyboard and mouse")),
				@facet (
						name = IKeyword.ACTION,
						type = IType.ACTION,
						optional = false,
						doc = @doc ("Either a block of statements to execute in the context of the experiment or the identifier of the action to be executed in the context of the simulation. This action needs to be defined in 'global' or in the current experiment, without any arguments. The location of the mouse in the world can be retrieved in this action with the pseudo-constant #user_location")) },
		omissible = IKeyword.NAME)
@validator (EventLayerValidator.class)
@doc (
		value = "`" + IKeyword.EVENT
				+ "` allows to interact with the simulation by capturing mouse or key events and doing an action. This action needs to be defined in 'global' or in the current experiment, without any arguments. The location of the mouse in the world can be retrieved in this action with the pseudo-constant #user_location",
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
										value = "      event mouse_up action: myAction;",
										isExecutable = false),
								@example (
										value = "   }",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE,
				IKeyword.OVERLAY, IKeyword.POPULATION, })
public class EventLayerStatement extends AbstractLayerStatement {

	public static String[] MOUSE_EVENTS = { "mouse_up", "mouse_down", "mouse_move", "mouse_enter", "mouse_exit" };

	public static class EventLayerValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription description) {
			final String actionName = description.getLitteral(ACTION);
			StatementDescription sd = description.getModelDescription().getAction(actionName);
			if (sd == null) {
				// we look into the experiment
				final IDescription superDesc = description.getSpeciesContext();
				sd = superDesc.getAction(actionName);
			}
			if (sd == null) {
				description.error("Action '" + actionName + "' is not defined in neither 'global' nor 'experiment'",
						IGamlIssue.UNKNOWN_ACTION, ACTION);
				return;
			} else if (sd.getPassedArgs().size() > 0) {
				description.error("Action '" + actionName
						+ "' cannot have arguments. Use '#user_location' inside to obtain the location of the mouse, and compute the selected agents in the action using GAML spatial operators",
						IGamlIssue.DIFFERENT_ARGUMENTS, ACTION);
			}
		}
	}

	private final boolean executesInSimulation;
	private final IExpression type;
	private static List<IEventLayerDelegate> delegates = new ArrayList<>();

	/**
	 * @param createExecutableExtension
	 */
	public static void addDelegate(final IEventLayerDelegate delegate) {
		delegates.add(delegate);
	}

	public EventLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(/* context, */desc);
		final String actionName = description.getLitteral(IKeyword.ACTION);
		final StatementDescription sd = description.getSpeciesContext().getAction(actionName);
		executesInSimulation = sd == null;
		type = getFacet(IKeyword.TYPE);
	}

	public boolean executesInSimulation() {
		return executesInSimulation;
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {

		final Object source = getSource(scope);

		for (final IEventLayerDelegate delegate : delegates) {
			if (delegate.acceptSource(scope, source)) {
				delegate.createFrom(scope, source, this);
			}
		}
		return true;
	}

	@Override
	public LayerType getType() {
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

	private Object getSource(final IScope scope) {
		final Object source = type == null ? IKeyword.DEFAULT : type.value(scope);
		return source;
	}
}
