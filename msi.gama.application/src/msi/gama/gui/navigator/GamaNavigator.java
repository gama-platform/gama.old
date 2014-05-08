/*********************************************************************************************
 * 
 * 
 * 'GamaNavigator.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import org.eclipse.ui.navigator.CommonNavigator;

public class GamaNavigator extends CommonNavigator {

	String OPEN_BROWSER_COMMAND_ID = "msi.gama.application.commands.OpenBrowser";

	@Override
	protected Object getInitialInput() {
		return new NavigatorRoot();
	}

}
