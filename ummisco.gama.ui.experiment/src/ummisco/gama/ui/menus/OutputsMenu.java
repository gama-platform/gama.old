/*******************************************************************************************************
 *
 * OutputsMenu.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.google.common.collect.Iterables;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class OutputsMenu.
 */
public class OutputsMenu extends ContributionItem {

	/**
	 * The Interface ISelecter.
	 */
	@FunctionalInterface
	private interface ISelecter extends SelectionListener {

		/**
		 * Widget default selected.
		 *
		 * @param e
		 *            the e
		 */
		@Override
		default void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

	}

	/**
	 * Instantiates a new outputs menu.
	 */
	public OutputsMenu() {
		super("ummisco.gama.ui.experiment.outputs.menu");
	}

	@Override
	public void fill(final Menu main, final int index) {
		IExperimentPlan exp = GAMA.getExperiment();
		if (exp == null) return;
		ExperimentAgent agent = exp.getAgent();
		if (agent == null) return;
		for (final SimulationAgent sim : agent.getSimulationPopulation()) {
			managementSubMenu(main, sim.getScope(), sim.getOutputManager());
		}
		GamaMenu.separate(main);
		menuItem(main, e -> GAMA.getExperiment().pauseAllOutputs(),
				GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_PAUSE).image(), "Pause all");
		menuItem(main, e -> GAMA.getExperiment().refreshAllOutputs(), GamaIcon.named(IGamaIcons.DISPLAY_UPDATE).image(),
				"Update all");
		menuItem(main, e -> GAMA.getExperiment().resumeAllOutputs(),
				GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_PAUSE).disabled(), "Resume all");
		menuItem(main, e -> GAMA.synchronizeFrontmostExperiment(),
				GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_SYNC).image(), "Synchronize all");
		menuItem(main, e -> GAMA.desynchronizeFrontmostExperiment(),
				GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_SYNC).disabled(), "Desynchronize all");
	}

	/**
	 * Management sub menu.
	 *
	 * @param main
	 *            the main
	 * @param scope
	 *            the scope
	 * @param manager
	 *            the manager
	 */
	public void managementSubMenu(final Menu main, final IScope scope, final IOutputManager manager) {
		if (Iterables.isEmpty(manager.getDisplayOutputs())) return;
		final MenuItem item = new MenuItem(main, SWT.CASCADE);
		item.setText(manager.toString());
		final Menu sub = new Menu(item);
		item.setMenu(sub);
		for (final IDisplayOutput output : manager.getDisplayOutputs()) { outputSubMenu(sub, scope, manager, output); }
	}

	/**
	 * Output sub menu.
	 *
	 * @param main
	 *            the main
	 * @param scope
	 *            the scope
	 * @param manager
	 *            the manager
	 * @param output
	 *            the output
	 */
	public void outputSubMenu(final Menu main, final IScope scope, final IOutputManager manager,
			final IDisplayOutput output) {
		final MenuItem item = new MenuItem(main, SWT.CASCADE);
		item.setText(output.getOriginalName());
		final Menu sub = new Menu(item);
		item.setMenu(sub);
		if (output.isOpen()) {
			if (output.isPaused()) {
				menuItem(sub, e -> output.setPaused(false), null, "Resume");
			} else {
				menuItem(sub, e -> output.setPaused(true), GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_PAUSE).image(),
						"Pause");
			}
			menuItem(sub, e -> output.update(), GamaIcon.named(IGamaIcons.DISPLAY_UPDATE).image(), "Force update");

			if (output instanceof LayeredDisplayOutput ldo) {
				GamaMenu.separate(sub);
				menuItem(sub, e -> ldo.zoom(1), GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_ZOOMIN).image(), "Zoom in");
				menuItem(sub, e -> ldo.zoom(0), GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT).image(),
						"Zoom to fit view");
				menuItem(sub, e -> ldo.zoom(-1), GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_ZOOMOUT).image(),
						"Zoom out");
				GamaMenu.separate(sub);
				menuItem(sub, e -> ldo.getView().takeSnapshot(null),
						GamaIcon.named(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT).image(), "Take a snapshot");
				menuItem(sub, e -> ldo.getView().toggleFullScreen(),
						GamaIcon.named(IGamaIcons.DISPLAY_FULLSCREEN_ENTER).image(), "Toggle fullscreen");
			}
		} else {
			menuItem(sub, e -> manager.open(scope, output), null, "Reopen");
		}

	}

	@Override
	public boolean isDynamic() { return true; }

	/**
	 * Menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param listener
	 *            the listener
	 * @param image
	 *            the image
	 * @param prefix
	 *            the prefix
	 * @return the menu item
	 */
	private static MenuItem menuItem(final Menu parent, final ISelecter listener, final Image image,
			final String prefix) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix);
		if (listener != null) { result.addSelectionListener(listener); }
		if (image != null) { result.setImage(image); }
		return result;
	}
}
