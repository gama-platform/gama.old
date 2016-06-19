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
package ummisco.gama.ui.views.inspectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.experiment.parameters.AgentAttributesEditorsList;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

public class AgentInspectView extends AttributesEditorsView<IAgent> implements
		IToolbarDecoratedView.Pausable /* implements GamaSelectionListener */ {

	public static final String ID = IGui.AGENT_VIEW_ID;
	public String firstPartName = null;

	@Override
	public void addOutput(final IDisplayOutput output) {

		if (output == null) {
			reset();
			return;
		}
		// System.out.println("Adding output " + output.getName() + " to
		// inspector");
		if (!(output instanceof InspectDisplayOutput)) {
			return;
		}
		final InspectDisplayOutput out = (InspectDisplayOutput) output;
		final IAgent[] agents = out.getLastValue();
		if (agents == null || agents.length == 0) {
			reset();
			return;
		}

		final IAgent agent = agents[0];
		if (parent == null) {
			super.addOutput(out);
		} else if (editors == null || !editors.getCategories().containsKey(agent)) {
			super.addOutput(out);
			addItem(agent);
		}
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		// System.out.println("Inspector creating its own part control");
		parent.setBackground(parent.getBackground());
		if (!outputs.isEmpty()) {
			final IAgent[] init = getOutput().getLastValue();
			if (init != null && init.length > 0) {
				for (final IAgent a : init) {
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
		final AbstractEditor ed = EditorFactory.create(agent.getScope(), attributes,
				new ParameterAdapter("highlight", IType.BOOL) {

					@Override
					public void setValue(final IScope scope, final Object value) {
						if ((Boolean) value) {
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

				});
		editors.getCategories().get(agent).put("highlight", ed);
		final ISpecies species = agent.getSpecies();
		final Collection<UserCommandStatement> userCommands = species.getUserCommands();
		if (userCommands.isEmpty()) {
			return attributes;
		}
		final Composite buttons = new Composite(attributes, SWT.BORDER_SOLID);
		buttons.moveAbove(null);
		buttons.setBackground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		buttons.setForeground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		final GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		buttons.setLayoutData(data);
		// final GridLayout layout = new GridLayout(3, false);
		// buttons.setLayout(layout);
		buttons.setLayout(new FillLayout());

		for (final UserCommandStatement command : userCommands) {
			final Button b = new Button(buttons, SWT.PUSH);
			b.setText(command.getName());
			GamaUIColor color = GamaColors.get(command.getColor(agent.getScope()));
			if (color == null)
				color = IGamaColors.BLUE;
			b.setBackground(color.color());
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					if (agent.dead()) {
						return;
					}
					// We run into the scope provided by the agent
					final IScope runningScope = agent.getScope();
					runningScope.getSimulationScope().executeAction(new IExecutable() {

						@Override
						public Object executeOn(final IScope scope) {
							final Object[] result = new Object[1];
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
		// System.out.println("Adding item " + agent.getName() + " to
		// inspector");
		if (editors == null) {
			editors = new AgentAttributesEditorsList();
		}
		updatePartName();
		if (!editors.getCategories().containsKey(agent)) {
			editors.add(getParametersToInspect(agent), agent);
			// System.out.println("Asking to create the item " + agent.getName()
			// + " in inspector");
			final ParameterExpandItem item = createItem(parent, agent, true, null);
			if (item == null) {
				return false;
			}
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
		if (names == null) {
			names = agent.getSpecies().getVarNames();
		}
		final List<IParameter> params = new ArrayList();
		for (final IVariable v : agent.getSpecies().getVars()) {
			if (names.contains(v.getName())) {
				params.add(v);
			}
		}
		return params;
	}

	@Override
	public void removeItem(final IAgent a) {
		InspectDisplayOutput found = null;
		for (final IDisplayOutput out : outputs) {
			final InspectDisplayOutput output = (InspectDisplayOutput) out;
			final IAgent[] agents = output.getLastValue();
			if (agents != null && agents.length > 0 && agents[0] == a) {
				found = output;
				break;
			}
		}
		if (found != null) {
			found.close();
			removeOutput(found);
		}
		updatePartName();
	}

	public void updatePartName() {
		if (firstPartName == null) {
			final InspectDisplayOutput out = getOutput();
			firstPartName = out == null ? "Inspect: " : out.getName();
		}
		final Set<String> names = new LinkedHashSet();
		for (final IOutput o : outputs) {
			final InspectDisplayOutput out = (InspectDisplayOutput) o;
			final IAgent a = out.getLastValue()[0];
			if (a != null) {
				names.add(a.getName());
			}
		}
		this.setPartName(firstPartName + " " + (names.isEmpty() ? "" : names.toString()));
	}

	/**
	 * Method pauseChanged()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Pausable#pauseChanged()
	 */
	@Override
	public void pauseChanged() {
	}

	/**
	 * Method synchronizeChanged()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Pausable#synchronizeChanged()
	 */
	@Override
	public void synchronizeChanged() {
	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object,
	 *      int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final IAgent data, final int x, final int y) {
		return null;
	}

	// /**
	// * Method createToolItem()
	// * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int,
	// msi.gama.gui.swt.controls.GamaToolbar2)
	// */
	// @Override
	// public void createToolItems(final GamaToolbar2 tb) {}

}
