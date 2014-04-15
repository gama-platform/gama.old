/*********************************************************************************************
 * 
 *
 * 'AgentEditor.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gaml.types.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AgentEditor extends AbstractEditor {

	private Button agentChooser;
	Label agentDisplayer;
	Button agentInspector;
	String species;

	AgentEditor(final IParameter param) {
		this(null, param);
	}

	AgentEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	AgentEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		species = param.getType().toString();
	}

	AgentEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		currentValue = getOriginalValue();
		Composite comp = new Composite(compo, SWT.None);
		comp.setLayoutData(getParameterGridData());
		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		agentDisplayer = new Label(comp, SWT.NONE);

		agentInspector = new Button(comp, SWT.FLAT | SWT.PUSH);
		agentInspector.setAlignment(SWT.CENTER);
		agentInspector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( currentValue instanceof IAgent ) {
					IAgent a = (IAgent) currentValue;
					if ( !a.dead() ) {
						GuiUtils.setSelectedAgent(a);
					}
				}
			}
		});
		agentInspector.setImage(IGamaIcons.MENU_INSPECT.image());
		agentInspector.setText("Inspect");

		agentChooser = new Button(comp, SWT.FLAT | SWT.PUSH);
		agentChooser.setAlignment(SWT.CENTER);
		agentChooser.addSelectionListener(this);
		agentChooser.setImage(IGamaIcons.BUTTON_EDIT.image());
		agentChooser.setText("Change...");

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		agentChooser.setLayoutData(d);
		return agentDisplayer;
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		Menu old = agentChooser.getMenu();
		agentChooser.setMenu(null);
		if ( old != null ) {
			old.dispose();
		}
		// FIXME Not adapted to multiple scales !

		AgentsMenu.MenuAction action = new AgentsMenu.MenuAction(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				MenuItem mi = (MenuItem) e.widget;
				IAgent a = (IAgent) mi.getData("agent");
				if ( a != null && !a.dead() ) {
					modifyAndDisplayValue(a);
				}
			}

		}, IGamaIcons.MENU_AGENT.image(), "Choose");

		Menu dropMenu = new Menu(agentChooser);
		AgentsMenu.fillPopulationSubMenu(dropMenu, GAMA.getSimulation().getMicroPopulation(species), action);
		agentChooser.setMenu(dropMenu);
		dropMenu.setVisible(true);

	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		agentDisplayer.setText(currentValue instanceof IAgent ? ((IAgent) currentValue).getName() : "No agent");
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return agentDisplayer;
	}

	@Override
	public IType getExpectedType() {
		return GAML.getModelContext().getTypeNamed(species);
	}

}
