/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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

public class AgentInspectView extends AttributesEditorsView<IAgent> implements GamaSelectionListener {

	public static final String ID = GuiUtils.AGENT_VIEW_ID;

	@Override
	public void selectionChanged(final Object entity) {
		if ( entity == null ) {
			reset();
		} else {
			addItem((IAgent) entity);
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
			public void setValue(final Object value) {
				if ( (Boolean) value ) {
					GuiUtils.setHighlightedAgent(agent);
				} else {
					GuiUtils.setHighlightedAgent(null);
				}
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
				// GuiUtils.debug("Asking the value of highlight for " + agent.getName());
				return value();
			}

		});
		editors.getCategories().get(agent).put("highlight", ed);
		// ed.getEditor().setBackground(
		// SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		// ed.getEditor().moveAbove(null);
		final ISpecies species = agent.getSpecies();
		final Collection<UserCommandStatement> userCommands = species.getUserCommands();
		if ( userCommands.isEmpty() ) { return attributes; }
		final Composite buttons = new Composite(attributes, SWT.BORDER_SOLID);
		buttons.moveAbove(null);
		buttons.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		buttons.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		buttons.setLayoutData(data);
		final GridLayout layout = new GridLayout(3, false);
		buttons.setLayout(layout);

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
							return scope.execute(command, agent, null);
						}

					});
				}

			});
		}
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
	protected Integer[] getToolbarActionsId() {
		return new Integer[] { PAUSE, REFRESH };
	}

}
