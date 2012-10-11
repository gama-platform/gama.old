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
	  if(preferences.get("RScript", null)==null)
	  {
		  String os = System.getProperty("os.name");
			if("Mac OS X".equals(os)){
				preferences.put("RScript", "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript");
			}
			if("Windows".equals(os)){
				preferences.put("RScript", "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe");
			}	
	  }

    setDescription("A preference page to config RScript path");
  }
} 