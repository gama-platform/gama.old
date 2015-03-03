/**
 * Created by drogoul, 3 mars 2015
 * 
 */
package msi.gama.common;

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