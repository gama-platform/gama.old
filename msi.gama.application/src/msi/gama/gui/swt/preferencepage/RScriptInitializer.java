package msi.gama.gui.swt.preferencepage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;


public class RScriptInitializer extends AbstractPreferenceInitializer {

  public RScriptInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PlatformUI.getPreferenceStore();
    store.setDefault("RScript", "http://www.vogella.com");
  }

} 