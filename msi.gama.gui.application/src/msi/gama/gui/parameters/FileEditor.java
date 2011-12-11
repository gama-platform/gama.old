/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.io.File;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class FileEditor extends AbstractEditor {

	Button textBox;

	FileEditor(final IParameter param) {
		super(param);
	}

	FileEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	FileEditor(final Composite parent, final String title, final Object value,
		final EditorListener<String> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = new Button(comp, SWT.FLAT);
		textBox.addSelectionListener(this);
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
		String path = dialog.open();
		if ( path != null ) {
			File file = new File(path);
			modifyAndDisplayValue(file.isFile() ? file.toString() : file.list()[0]);
		}
	}

	@Override
	protected void displayParameterValue() {
		if ( currentValue == null ) {
			textBox.setText("");
			return;
		}
		textBox.setText((String) currentValue);
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.FILE);
	}

}
