/*********************************************************************************************
 *
 *
 * 'scope.getGui().java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.util;

import msi.gama.common.interfaces.IGui;
import msi.gama.util.GamaColor;
import msi.gaml.factories.ModelFactory;

/**
 * The class scope.getGui(). A static bridge to the SWT environment. The actual
 * dependency on SWT is represented by an instance of IGui, which must be
 * initialize when the UI plugin launches.
 *
 * @author drogoul
 * @since 18 dec. 2011
 *
 */
public abstract class AbstractGui implements IGui {

	static IGui gui;

	private boolean headlessMode = false;

	public boolean isInHeadLessMode() {
		return headlessMode;
	}

	/**
	 * Method called by headless builder to change the GUI Mode
	 * 
	 * @see ModelFactory
	 */

	public void setHeadLessMode() {
		headlessMode = true;
	}

	@Override
	public void waitStatus(final String string) {
		setStatus(string, IGui.WAIT);
	}

	@Override
	public void informStatus(final String string) {
		setStatus(string, IGui.INFORM);
	}

	@Override
	public void errorStatus(final String error) {
		setStatus(error, IGui.ERROR);
	}

	public void neutralStatus(final String message) {
		setStatus(message, IGui.NEUTRAL);
	}

	protected void setStatus(final String msg, final int code) {
	}

	@Override
	public void setStatus(final String message, final GamaColor color) {
		if (message == null) {
			resumeStatus();
		} else {
			setStatusInternal(message, color);
		}

	}

	/**
	 * @param message
	 * @param color
	 */
	protected abstract void setStatusInternal(String message, GamaColor color);

}
