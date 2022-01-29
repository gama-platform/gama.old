/*******************************************************************************************************
 *
 * IBoxDecorator.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import org.eclipse.swt.custom.StyledText;

/**
 * The Interface IBoxDecorator.
 */
public interface IBoxDecorator {

	/**
	 * Gets the provider.
	 *
	 * @return the provider
	 */
	IBoxProvider getProvider();

	/**
	 * Sets the provider.
	 *
	 * @param newProvider the new provider
	 */
	void setProvider(IBoxProvider newProvider);

	/**
	 * Sets the styled text.
	 *
	 * @param st the new styled text
	 */
	void setStyledText(StyledText st);

	/**
	 * Sets the settings.
	 *
	 * @param settings the new settings
	 */
	void setSettings(IBoxSettings settings);

	/**
	 * Enable updates.
	 *
	 * @param visible the visible
	 */
	void enableUpdates(boolean visible);

	/**
	 * Decorate.
	 *
	 * @param mouseClickSupport the mouse click support
	 */
	void decorate(boolean mouseClickSupport);

	/**
	 * Undecorate.
	 */
	void undecorate();

	/**
	 * Force update.
	 */
	void forceUpdate();

	// void selectCurrentBox();
	// void unselectCurrentBox();
}
