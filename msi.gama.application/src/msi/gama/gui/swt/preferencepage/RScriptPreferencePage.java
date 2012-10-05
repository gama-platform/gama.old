package msi.gama.gui.swt.preferencepage;

import msi.gaml.operators.Stats;
import java.util.prefs.*;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;


public class RScriptPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {
	private static Preferences preferences = Preferences.userRoot().node("gama");

  public RScriptPreferencePage() {
    super(GRID);

  }

@Override
protected void performApply() {
	// TODO Auto-generated method stub
//	PlatformUI.getPreferenceStore().putValue("RScript", dfe.getStringValue());
	preferences.put("RScript", ffe.getStringValue());
//	Stats.RPath= dfe.getStringValue();
	super.performApply();
}

@Override
public boolean performOk() {
	return super.performOk();
}
FileFieldEditor ffe;
public void createFieldEditors() {
	ffe=new FileFieldEditor("RScript", "&Rscript preference:",
        getFieldEditorParent());
	ffe.setStringValue(preferences.get("RScript", null));
    addField(ffe);

  }

  @Override
  public void init(IWorkbench workbench) {
//	  PlatformUI.getPreferenceStore().putValue("RScript", "C:\\Program Files\\R\\R-2.15.1\\bin");
	  if(preferences.get("RScript", null)==null)
	  {
		  preferences.put("RScript", "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe");
	  }
//	  setPreferenceStore(PlatformUI.getPreferenceStore());
    setDescription("A preference page to config RScript path");
  }
} 