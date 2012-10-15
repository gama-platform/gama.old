package msi.gama.gui.swt.preferencepage;

import msi.gaml.operators.Stats;
import java.util.prefs.*;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
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
		// PlatformUI.getPreferenceStore().putValue("RScript", dfe.getStringValue());
		preferences.put("RScript", ffe.getStringValue());
		// Stats.RPath= dfe.getStringValue();
		System.out.println("isValid : " + this.isValid());
		super.performApply();
		
	}

	@Override
	public boolean performOk() {
		preferences.put("RScript", ffe.getStringValue());
		return super.performOk();
	}

	FileFieldEditorValid ffe;
	
	class FileFieldEditorValid extends FileFieldEditor {

		public FileFieldEditorValid() {
			super();
		}

		public FileFieldEditorValid(String name, String labelText, boolean enforceAbsolute,
			Composite parent) {
			super(name, labelText, enforceAbsolute, parent);
		}

		public FileFieldEditorValid(String name, String labelText, boolean enforceAbsolute,
			int validationStrategy, Composite parent) {
			super(name, labelText, enforceAbsolute, validationStrategy, parent);
		}

		public FileFieldEditorValid(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		public boolean isValid() {
			return true;
		}
		
	}
	
	public void createFieldEditors() {
		ffe = new FileFieldEditorValid("RScript", "&Rscript preference:", getFieldEditorParent());
		ffe.setStringValue(preferences.get("RScript", null));
		addField(ffe);

	}
	
	

	@Override
	protected void performDefaults() {
		String defaultPath = defaultPath();
		ffe.setStringValue(defaultPath);
		super.performDefaults();
	}
	
	public String defaultPath() {
		String os = System.getProperty("os.name");
		String osbit = System.getProperty("os.arch");
		if ( os.startsWith("Mac") ) {
			if ( osbit.endsWith("64") )
				return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript";
			return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/i386/RScript";
		} else if ( os.startsWith("Linux") ) {
			return "usr/bin/RScript";
		}
		if ( os.startsWith("Windows") ) {
			if ( osbit.endsWith("64") ) 
				return "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe";
			return "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe";
		}
		return "";
	}

	@Override
	public void init(IWorkbench workbench) {
		if ( preferences.get("RScript", null) == null ) {
			String defaultPath = defaultPath();
			preferences.put("RScript", defaultPath);
		}
		setDescription("A preference page to config RScript path");
	}
}