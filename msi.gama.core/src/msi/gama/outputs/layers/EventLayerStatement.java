/*********************************************************************************************
 *
 *
 * 'EventLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

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
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
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
@symbol(name = IKeyword.EVENT, kind = ISymbolKind.LAYER, with_sequence = true, concept = { IConcept.GUI })
@inside(symbols = { IKeyword.DISPLAY })
@facets(value = {
		@facet(name = "unused", type = IType.ID, values = { "mouse_up", "mouse_down", "mouse_move", "mouse_enter",
				"mouse_exit" }, optional = true, doc = @doc(value = "an unused facet that serves only for the purpose of declaring the string values"), internal = true),
		@facet(name = IKeyword.NAME, type = IType.ID,
				// values = { "mouse_up", "mouse_down", "mouse_drag" },
				optional = false, doc = @doc("the type of event captured: can be  \"mouse_up\", \"mouse_down\", \"mouse_move\", \"mouse_exit\", \"mouse_enter\" or a character")),
		@facet(name = IKeyword.ACTION, type = IType.NONE, optional = false, doc = @doc("Either a block of statements to execute in the context of the simulation or the identifier of the action to be executed. This action needs to be defined in 'global' and will receive two possible arguments: the location of the mouse in the environment and the agents under the mouse. For instance:`action myAction (point location, list selected_agents)`")) }, omissible = IKeyword.NAME)
@validator(EventLayerValidator.class)
@doc(value = "`" + IKeyword.EVENT
		+ "` allows to interact with the simulation by capturing mouse or key events and doing an action. This action needs to be defined in 'global' and will receive two possible arguments: the location of the mouse in the environment and the agents under the mouse. The names of these arguments need not to be fixed: instead, the first argument of type 'point' will receive the location of the mouse, while the first argument whose type is compatible with 'container<agent>' will receive the list of agents selected.", usages = {
				@usage(value = "The general syntax is:", examples = {
						@example(value = "event [event_type] action: myAction;", isExecutable = false) }),
				@usage(value = "For instance:", examples = { @example(value = "global {", isExecutable = false),
						@example(value = "   // ... ", isExecutable = false),
						@example(value = "   action myAction (point location, list<agent> selected_agents) {", isExecutable = false),
						@example(value = "      // location: contains le location of the click in the environment", isExecutable = false),
						@example(value = "      // selected_agents: contains agents clicked by the event", isExecutable = false),
						@example(value = "      ", isExecutable = false),
						@example(value = "      // code written by modelers", isExecutable = false),
						@example(value = "   }", isExecutable = false), @example(value = "}", isExecutable = false),
						@example(value = "", isExecutable = false),
						@example(value = "experiment Simple type:gui {", isExecutable = false),
						@example(value = "   display my_display {", isExecutable = false),
						@example(value = "      event mouse_up action: myAction;", isExecutable = false),
						@example(value = "   }", isExecutable = false),
						@example(value = "}", isExecutable = false) }) }, see = { IKeyword.DISPLAY, IKeyword.AGENTS,
								IKeyword.CHART, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE, IKeyword.OVERLAY,
								IKeyword.POPULATION, })
public class EventLayerStatement extends AbstractLayerStatement {

	public static class EventLayerValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription description) {
			final IExpression exp = description.getFacets().getExpr(ACTION);
			final String actionName = exp.literalValue();
			StatementDescription sd = description.getModelDescription().getAction(actionName);
			if (sd == null) {
				// display
				IDescription superDesc = description.getEnclosingDescription();
				// output or permanent
				superDesc = superDesc.getEnclosingDescription();
				if (superDesc.getKeyword() == IKeyword.PERMANENT) {
					// we look into experiment
					sd = superDesc.getEnclosingDescription().getAction(actionName);
				}
			}
			if (sd == null) {
				description.error("Action '" + actionName + "' is not defined in neither 'global' nor 'experiment'",
						IGamlIssue.UNKNOWN_ACTION, ACTION);
				return;
			} else if (sd.getArgs().size() > 0) {
				description.error(
						"Action '" + actionName
								+ "' cannot have arguments. Use '#user_location' inside to obtain the location of the mouse, and compute the selected agents in the action using GAML spatial operators",
						IGamlIssue.DIFFERENT_ARGUMENTS, ACTION);
			}
		}
	}

	public EventLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(/* context, */desc);
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	@Override
	public short getType() {
		return EVENT;
	}

	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
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
}
