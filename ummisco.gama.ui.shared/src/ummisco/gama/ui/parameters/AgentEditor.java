/*********************************************************************************************
 *
 * 'AgentEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.interfaces.IAgentMenuFactory;
import ummisco.gama.ui.menus.MenuAction;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class AgentEditor extends ExpressionBasedEditor {

	// Label agentDisplayer;
	String species;

	// AgentEditor(final IParameter param) {
	// this(null, param);
	// }

	// AgentEditor(final IAgent agent, final IParameter param) {
	// this(agent, param, null);
	// }

	AgentEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
		species = param.getType().toString();
	}

	//
	// AgentEditor(final Composite parent, final String title, final Object
	// value,
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
		Shell shell = toolbar.getItem(CHANGE).getParent().getShell();
		final Menu old = shell.getMenu();
		shell.setMenu(null);
		if (old != null) { old.dispose(); }
		// FIXME Not adapted to multiple scales !

		final MenuAction action = new MenuAction(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final MenuItem mi = (MenuItem) e.widget;
				final IAgent a = (IAgent) mi.getData("agent");
				if (a != null && !a.dead()) { modifyAndDisplayValue(a); }
			}

		}, GamaIcons.create(IGamaIcons.MENU_AGENT).image(), "Choose");

		final Menu dropMenu = new Menu(shell);
		final IAgent a = (IAgent) (currentValue instanceof IAgent ? currentValue : null);
		if (a != null) {
			final IAgentMenuFactory factory = WorkbenchHelper.getService(IAgentMenuFactory.class);
			if (factory != null) {
				factory.fillPopulationSubMenu(dropMenu, a.getScope().getSimulation().getMicroPopulation(species), null,
						action);
			}
		}
		final Rectangle rect = toolbar.getItem(CHANGE).getBounds();
		final Point pt = toolbar.getItem(CHANGE).getParent().toDisplay(new Point(rect.x, rect.y));
		dropMenu.setLocation(pt.x, pt.y + rect.height);
		dropMenu.setVisible(true);

	}

	// @Override
	// protected void displayParameterValue() {
	// internalModification = true;
	// agentDisplayer.setText(currentValue instanceof IAgent ? ((IAgent)
	// currentValue).getName() : "No agent");
	// internalModification = false;
	// }

	// @Override
	// public Control getEditorControl() {
	// return agentDisplayer;
	// }

	@Override
	public IType getExpectedType() {
		return getScope().getType(species);
	}

	/**
	 * Method getToolItems()
	 *
	 * @see ummisco.gama.ui.parameters.AbstractEditor#getToolItems()
	 */
	@Override
	protected int[] getToolItems() {
		return new int[] { INSPECT, CHANGE, REVERT };
	}

	@Override
	protected void applyInspect() {

		if (currentValue instanceof IAgent) {
			final IAgent a = (IAgent) currentValue;
			if (!a.dead()) { getScope().getGui().setSelectedAgent(a); }
		}

	}

}
