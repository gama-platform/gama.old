/*******************************************************************************************************
 *
 * msi.gama.common.preferences.PreferencesWiper.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.preferences;

import java.util.prefs.Preferences;

public class PreferencesWiper {

	public static void main(final String[] args) {
		try {
			Preferences store = Preferences.userRoot().node("gama");
			store.removeNode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}