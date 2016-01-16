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

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gaml.types.IType;

public class AgentEditor extends ExpressionBasedEditor {

	// Label agentDisplayer;
	String species;

	// AgentEditor(final IParameter param) {
	// this(null, param);
	// }

	// AgentEditor(final IAgent agent, final IParameter param) {
	// this(agent, param, null);
	// }

	AgentEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		species = param.getType().toString();
	}

	//
	// AgentEditor(final Composite parent, final String title, final Object value,
	// final EditorListener<java.util.List> whenModified) {
	// // Convenience method
	// super(new InputParameter(title, value), whenModified);
	// this.createComposite(parent);
	// }

	// @Override
	// public Control createCustomParameterControl(final Composite compo) {
	// currentValue = getOriginalValue();
	// agentDisplayer = new Label(compo, SWT.NONE);
	// return agentDisplayer;
	// }

	@Override
	public void applyChange() {
		Menu old = items[CHANGE].getParent().getShell().getMenu();
		items[CHANGE].getParent().getShell().setMenu(null);
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

		Menu dropMenu = new Menu(items[CHANGE].getParent().getShell());
		AgentsMenu.fillPopulationSubMenu(dropMenu, GAMA.getSimulation().getMicroPopulation(species), null, action);
		Rectangle rect = items[CHANGE].getBounds();
		Point pt = items[CHANGE].getParent().toDisplay(new Point(rect.x, rect.y));
		dropMenu.setLocation(pt.x, pt.y + rect.height);
		dropMenu.setVisible(true);

	}

	// @Override
	// protected void displayParameterValue() {
	// internalModification = true;
	// agentDisplayer.setText(currentValue instanceof IAgent ? ((IAgent) currentValue).getName() : "No agent");
	// internalModification = false;
	// }

	// @Override
	// public Control getEditorControl() {
	// return agentDisplayer;
	// }

	@Override
	public IType getExpectedType() {
		return GAML.getModelContext().getTypeNamed(species);
	}

	/**
	 * Method getToolItems()
	 * @see msi.gama.gui.parameters.AbstractEditor#getToolItems()
	 */
	@Override
	protected int[] getToolItems() {
		return new int[] { INSPECT, CHANGE, REVERT };
	}

	@Override
	protected void applyInspect() {

		if ( currentValue instanceof IAgent ) {
			IAgent a = (IAgent) currentValue;
			if ( !a.dead() ) {
				a.getScope().getGui().setSelectedAgent(a);
			}
		}

	}

}
