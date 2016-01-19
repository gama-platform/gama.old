/*********************************************************************************************
 *
 *
 * 'AgentInspectView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

public class AgentInspectView extends AttributesEditorsView<IAgent> implements IToolbarDecoratedView.Pausable /* implements GamaSelectionListener */ {

	public static final String ID = IGui.AGENT_VIEW_ID;
	public String firstPartName = null;

	@Override
	public void addOutput(final IDisplayOutput output) {

		if ( output == null ) {
			reset();
			return;
		}
		// System.out.println("Adding output " + output.getName() + " to inspector");
		if ( !(output instanceof InspectDisplayOutput) ) { return; }
		InspectDisplayOutput out = (InspectDisplayOutput) output;
		IAgent[] agents = out.getLastValue();
		if ( agents == null || agents.length == 0 ) {
			reset();
			return;
		}

		IAgent agent = agents[0];
		if ( parent == null ) {
			super.addOutput(out);
		} else if ( editors == null || !editors.getCategories().containsKey(agent) ) {
			super.addOutput(out);
			addItem(agent);
		}
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		// System.out.println("Inspector creating its own part control");
		parent.setBackground(parent.getBackground());
		if ( !outputs.isEmpty() ) {
			IAgent[] init = getOutput().getLastValue();
			if ( init != null && init.length > 0 ) {
				for ( IAgent a : init ) {
					addItem(a);
				}
			}
		}
	}

	@Override
	public InspectDisplayOutput getOutput() {
		return (InspectDisplayOutput) super.getOutput();
	}

	@Override
	public boolean areItemsClosable() {
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final IAgent agent) {

		// add highlight in the expand bar ?

		final Composite attributes = super.createItemContentsFor(agent);
		final AbstractEditor ed = EditorFactory.create(attributes, new ParameterAdapter("highlight", IType.BOOL) {

			@Override
			public void setValue(final IScope scope, final Object value) {
				if ( (Boolean) value ) {
					scope.getGui().setHighlightedAgent(agent);
				} else {
					scope.getGui().setHighlightedAgent(null);
				}
				GAMA.getExperiment().refreshAllOutputs();
			}

			@Override
			public Boolean value() {
				return agent == agent.getScope().getGui().getHighlightedAgent();
			}

			@Override
			public boolean isEditable() {
				return true;
			}

			@Override
			public Boolean value(final IScope iScope) throws GamaRuntimeException {
				return value();
			}

		});
		editors.getCategories().get(agent).put("highlight", ed);
		final ISpecies species = agent.getSpecies();
		final Collection<UserCommandStatement> userCommands = species.getUserCommands();
		if ( userCommands.isEmpty() ) { return attributes; }
		final Composite buttons = new Composite(attributes, SWT.BORDER_SOLID);
		buttons.moveAbove(null);
		buttons.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		buttons.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		final GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		buttons.setLayoutData(data);
		// final GridLayout layout = new GridLayout(3, false);
		// buttons.setLayout(layout);
		buttons.setLayout(new FillLayout());

		for ( final IStatement command : userCommands ) {
			final Button b = new Button(buttons, SWT.PUSH);
			b.setText(command.getName());
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					if ( agent.dead() ) { return; }
					GAMA.getSimulation().getScheduler().executeOneAction(new GamaHelper() {

						@Override
						public Object run(final IScope scope) {
							Object[] result = new Object[1];
							scope.execute(command, agent, null, result);
							return result[0];
						}

					});
				}

			});
		}
		buttons.layout();
		buttons.pack();
		buttons.update();
		return attributes;
	}

	@Override
	public boolean addItem(final IAgent agent) {
		// System.out.println("Adding item " + agent.getName() + " to inspector");
		if ( editors == null ) {
			editors = new AgentAttributesEditorsList();
		}
		updatePartName();
		if ( !editors.getCategories().containsKey(agent) ) {
			editors.add(getParametersToInspect(agent), agent);
			// System.out.println("Asking to create the item " + agent.getName() + " in inspector");
			ParameterExpandItem item = createItem(parent, agent, true, null);
			if ( item == null ) { return false; }
			return true;
		}
		return false;
	}

	@Override
	protected ParameterExpandItem buildConcreteItem(final ParameterExpandBar bar, final IAgent data,
		final GamaUIColor color) {
		return new ParameterExpandItem(bar, data, SWT.None, 0, color);
	}

	private List<IParameter> getParametersToInspect(final IAgent agent) {
		Collection<String> names = getOutput().getAttributes();
		if ( names == null ) {
			names = agent.getSpecies().getVarNames();
		}
		final List<IParameter> params = new ArrayList();
		for ( final IVariable v : agent.getSpecies().getVars() ) {
			if ( names.contains(v.getName()) ) {
				params.add(v);
			}
		}
		return params;
	}

	@Override
	public void removeItem(final IAgent a) {
		InspectDisplayOutput found = null;
		for ( IDisplayOutput out : outputs ) {
			InspectDisplayOutput output = (InspectDisplayOutput) out;
			IAgent[] agents = output.getLastValue();
			if ( agents != null && agents.length > 0 && agents[0] == a ) {
				found = output;
				break;
			}
		}
		if ( found != null ) {
			found.close();
			removeOutput(found);
		}
		updatePartName();
	}

	public void updatePartName() {
		if ( firstPartName == null ) {
			InspectDisplayOutput out = getOutput();
			firstPartName = out == null ? "Inspect: " : out.getName();
		}
		Set<String> names = new LinkedHashSet();
		for ( IOutput o : outputs ) {
			InspectDisplayOutput out = (InspectDisplayOutput) o;
			IAgent a = out.getLastValue()[0];
			if ( a != null ) {
				names.add(a.getName());
			}
		}
		this.setPartName(firstPartName + " " + (names.isEmpty() ? "" : names.toString()));
	}

	/**
	 * Method pauseChanged()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Pausable#pauseChanged()
	 */
	@Override
	public void pauseChanged() {}

	/**
	 * Method synchronizeChanged()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Pausable#synchronizeChanged()
	 */
	@Override
	public void synchronizeChanged() {}

	/**
	 * Method handleMenu()
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final IAgent data, final int x, final int y) {
		return null;
	}

	// /**
	// * Method createToolItem()
	// * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	// */
	// @Override
	// public void createToolItems(final GamaToolbar2 tb) {}

}
