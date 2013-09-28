/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.IPopupProvider;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.IScope;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileEditor extends AbstractEditor implements IPopupProvider {

	private Button textBox;

	// private Popup popup;

	FileEditor(final IParameter param) {
		super(param);
	}

	FileEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	FileEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	FileEditor(final Composite parent, final String title, final Object value, final EditorListener<String> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	//
	// @Override
	// protected Object getParameterValue() throws GamaRuntimeException {
	// param.tryToInit();
	// return super.getParameterValue();
	// }

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = new Button(comp, SWT.FLAT);
		textBox.setText("AAA");
		textBox.addSelectionListener(this);
		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		d.heightHint = 20;
		textBox.setLayoutData(d);
		// popup = new Popup(this, textBox, getLabel());
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
		IGamaFile file = (IGamaFile) currentValue;
		dialog.setFileName(file.getPath());
		// dialog.set
		dialog.setText("Choose a file for parameter '" + param.getTitle() + "'");
		final String path = dialog.open();
		if ( path != null ) {
			file = GAMA.run(new InScope<IGamaFile>() {

				@Override
				public IGamaFile run(final IScope scope) {
					return Files.from(scope, path);
				}

			});
			modifyAndDisplayValue(file);
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		if ( currentValue == null ) {
			textBox.setText("No file");
			return;
		}
		IGamaFile file = (IGamaFile) currentValue;
		textBox.setText(file.getPath());
		textBox.update();
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.FILE);
	}

	@Override
	public String getPopupText() {
		return textBox.getText();
	}

	@Override
	public Shell getControllingShell() {
		return textBox.getShell();
	}

	@Override
	public Color getPopupBackground() {
		return SwtGui.getOkColor();
	}

	@Override
	public Point getAbsoluteOrigin() {
		return textBox.toDisplay(textBox.getLocation().x, textBox.getSize().y);
	}

}
