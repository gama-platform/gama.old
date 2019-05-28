/*******************************************************************************************************
 *
 * ummisco.gama.ui.interfaces.IDisplayLayoutManager.java, in plugin ummisco.gama.ui.shared, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.interfaces;

public interface IDisplayLayoutManager {

	void applyLayout(Object layout, Boolean keepTabs, Boolean keepToolbars, Boolean showEditors, Boolean showParameters,
			Boolean showConsoles, Boolean showNavigator, Boolean showControls, Boolean showTray);

	void hideScreen();

	void showScreen();

}
