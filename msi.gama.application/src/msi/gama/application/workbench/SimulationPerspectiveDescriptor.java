/*******************************************************************************************************
 *
 * SimulationPerspectiveDescriptor.java, in msi.gama.application, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import java.util.function.Supplier;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;

import msi.gama.application.workbench.PerspectiveHelper.SimulationPerspectiveFactory;

/**
 * The Class SimulationPerspectiveDescriptor.
 */
public class SimulationPerspectiveDescriptor extends PerspectiveDescriptor {

	/** The keep tabs. */
	Boolean keepTabs = true;

	/** The keep toolbars. */
	Boolean keepToolbars = null;

	/** The keep controls. */
	Boolean keepControls = true;

	/** The keep tray. */
	Boolean keepTray = true;

	/** The show consoles. */
	Boolean showConsoles = true;

	/** The show parameters. */
	Boolean showParameters = true;

	/** The background. */
	Supplier<Color> background = () -> null;

	/** The restore background. */
	private Runnable restoreBackground = () -> {};

	/**
	 * Gets the restore background.
	 *
	 * @return the restore background
	 */
	public Runnable getRestoreBackground() { return restoreBackground; }

	/**
	 * Sets the restore background.
	 *
	 * @param restoreBackground
	 *            the new restore background
	 */
	public void setRestoreBackground(final Runnable restoreBackground) {
		this.restoreBackground = restoreBackground == null ? () -> {} : restoreBackground;
	}

	/**
	 * Instantiates a new simulation perspective descriptor.
	 *
	 * @param id
	 *            the id
	 */
	SimulationPerspectiveDescriptor(final String id) {
		super(id, id, PerspectiveHelper.getSimulationDescriptor());
		PerspectiveHelper.dirtySavePerspective(this);
	}

	@Override
	public IPerspectiveFactory createFactory() {

		try {
			return new SimulationPerspectiveFactory(
					(IPerspectiveFactory) getConfigElement().createExecutableExtension("class"));
		} catch (final CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasCustomDefinition() {
		return true;
	}

	@Override
	public boolean isPredefined() { return false; }

	@Override
	public IConfigurationElement getConfigElement() {
		return PerspectiveHelper.getSimulationDescriptor().getConfigElement();
	}

	@Override
	public String getDescription() { return "Perspective for " + getId(); }

	@Override
	public String getOriginalId() { return getId(); }

	@Override
	public String getPluginId() { return PerspectiveHelper.getSimulationDescriptor().getPluginId(); }

	/**
	 * Keep tabs.
	 *
	 * @return the boolean
	 */
	public Boolean keepTabs() {
		return keepTabs;
	}

	/**
	 * Show consoles.
	 *
	 * @return true, if successful
	 */
	public Boolean showConsoles() {
		return showConsoles;
	}

	/**
	 * Show parameters.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the boolean
	 * @date 14 août 2023
	 */
	public Boolean showParameters() {
		return showParameters;
	}

	/**
	 * Keep tabs.
	 *
	 * @param b
	 *            the b
	 */
	public void keepTabs(final Boolean b) {
		keepTabs = b;
	}

	/**
	 * Keep toolbars.
	 *
	 * @return the boolean
	 */
	public Boolean keepToolbars() {
		return keepToolbars;
	}

	/**
	 * Keep toolbars.
	 *
	 * @param b
	 *            the b
	 */
	public void keepToolbars(final Boolean b) {
		keepToolbars = b;
	}

	/**
	 * Keep controls.
	 *
	 * @param b
	 *            the b
	 */
	public void keepControls(final Boolean b) {
		keepControls = b;
	}

	/**
	 * Keep controls.
	 *
	 * @return the boolean
	 */
	public Boolean keepControls() {
		return keepControls;
	}

	/**
	 * Keep tray.
	 *
	 * @param b
	 *            the b
	 */
	public void keepTray(final Boolean b) {
		keepTray = b;
	}

	/**
	 * Keep tray.
	 *
	 * @return the boolean
	 */
	public Boolean keepTray() {
		return keepTray;
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public Color getBackground() { return background.get(); }

	/**
	 * Sets the background.
	 *
	 * @param c
	 *            the new background
	 */
	public void setBackground(final Supplier<Color> c) { background = c == null ? () -> null : c; }

	/**
	 * Show consoles.
	 *
	 * @param b
	 *            the b
	 */
	public void showConsoles(final Boolean b) {
		showConsoles = b;
	}

	/**
	 * Show parameters.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param b
	 *            the b
	 * @date 14 août 2023
	 */
	public void showParameters(final Boolean b) {
		showParameters = b;
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		getRestoreBackground().run();
		setRestoreBackground(null);

	}

}