/*********************************************************************************************
 *
 * 'FileEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFolderFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class FileEditor extends AbstractEditor<IGamaFile> {

	private FlatButton textBox;
	private final boolean isFolder;

	FileEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l,
			final boolean isFolder) {
		super(scope, agent, param, l);
		this.isFolder = isFolder;
	}

	FileEditor(final IScope scope, final EditorsGroup parent, final String title, final String value,
			final EditorListener<IGamaFile> whenModified, final boolean isFolder) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.isFolder = isFolder;
		this.createControls(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = FlatButton.menu(comp, IGamaColors.NEUTRAL, "").light().small();
		textBox.setText("No " + (isFolder ? "folder" : "file"));
		textBox.addSelectionListener(this);
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		IGamaFile file = currentValue;
		String filter = file != null ? file.getPath(getScope()) : GAMA.getModel().getFilePath();
		if (isFolder) {
			final DirectoryDialog dialog = new DirectoryDialog(WorkbenchHelper.getDisplay().getActiveShell(), SWT.NULL);
			if (!(file instanceof GamaFolderFile)) { file = null; }
			dialog.setFilterPath(filter);
			dialog.setText("Choose a folder for parameter '" + param.getTitle() + "'");
			final String path = dialog.open();
			if (path != null) { file = Files.folderFile(getScope(), path, false); }
		} else {
			final FileDialog dialog = new FileDialog(WorkbenchHelper.getDisplay().getActiveShell(), SWT.NULL);
			dialog.setFileName(file != null ? file.getPath(getScope()) : GAMA.getModel().getFilePath());
			dialog.setText("Choose a file for parameter '" + param.getTitle() + "'");
			final String path = dialog.open();
			if (path != null) {
				file = Files.from(getScope(), path);

			}
		}
		modifyAndDisplayValue(file);
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		if (currentValue == null) {
			textBox.setText("No " + (isFolder ? "folder" : "file"));
		} else {
			final IGamaFile file = currentValue;
			String path;
			try {
				path = file.getPath(getScope());
			} catch (final GamaRuntimeException e) {
				path = file.getOriginalPath();
			}

			textBox.setToolTipText(path);
			textBox.setText(path);
		}
		internalModification = false;
	}

	@Override
	public IType getExpectedType() {
		return Types.FILE;
	}

	@Override
	protected void applyEdit() {
		widgetSelected(null);
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
