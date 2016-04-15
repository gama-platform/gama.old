/*********************************************************************************************
 * 
 * 
 * 'FileEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class FileEditor extends AbstractEditor<IGamaFile> {

	private FlatButton textBox;

	FileEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	FileEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	FileEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	FileEditor(final IScope scope, final Composite parent, final String title, final String value,
		final EditorListener<IGamaFile> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = FlatButton.menu(comp, IGamaColors.NEUTRAL, "").light().small();
		textBox.setText("No file");
		textBox.addSelectionListener(this);
		// GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		// textBox.setLayoutData(d);
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
		IGamaFile file = currentValue;
		dialog.setFileName(file.getPath());
		dialog.setText("Choose a file for parameter '" + param.getTitle() + "'");
		final String path = dialog.open();
		if ( path != null ) {
			file = Files.from(getScope(), path);
			modifyAndDisplayValue(file);
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		if ( currentValue == null ) {
			textBox.setText("No file");
		} else {
			final IGamaFile file = currentValue;
			textBox.setToolTipText(file.getPath());
			textBox.setText(file.getPath());
		}
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return textBox;
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
