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
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AgentInspectView extends AttributesEditorsView<IAgent> /* implements GamaSelectionListener */{

	public static final String ID = GuiUtils.AGENT_VIEW_ID;

	// @Override
	public void inspectAgent(final IAgent entity) {
		if ( entity == null ) {
			reset();
		} else {
			addItem(entity);
		}
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		super.ownCreatePartControl(parent);
		List<IAgent> init = ((InspectDisplayOutput) output).getLastValue();
		if ( init != null ) {
			for ( IAgent a : init ) {
				inspectAgent(a);
			}
		}
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
					GuiUtils.setHighlightedAgent(agent);
				} else {
					GuiUtils.setHighlightedAgent(null);
				}
				GAMA.getExperiment().getSimulationOutputs().forceUpdateOutputs();
			}

			@Override
			public Boolean value() {
				return agent == GuiUtils.getHighlightedAgent();
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
		if ( editors == null ) {
			editors = new AgentAttributesEditorsList();
		}
		if ( !editors.getCategories().containsKey(agent) ) {
			editors.add(getParametersToInspect(agent), agent);
			createItem(agent, true);
			return true;
		}
		return false;
	}

	private List<IParameter> getParametersToInspect(final IAgent agent) {
		List<String> names = ((InspectDisplayOutput) getOutput()).getAttributes();
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

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { PAUSE, REFRESH };
	}

}
