/*********************************************************************************************
 * 
 * 
 * 'ColorEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaColor;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class ColorEditor extends AbstractEditor {

	private Button edit;

	ColorEditor(final IParameter param) {
		super(param);
	}

	ColorEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	ColorEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	ColorEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.awt.Color> whenModified) {
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		Shell shell = new Shell(Display.getDefault(), SWT.MODELESS);
		final ColorDialog dlg = new ColorDialog(shell, SWT.MODELESS);
		dlg.setRGB(edit.getBackground().getRGB());
		dlg.setText("Choose a Color");
		final RGB rgb = dlg.open();
		if ( rgb != null ) {
			modifyAndDisplayValue(new GamaColor(rgb.red, rgb.green, rgb.blue, 255));
		}
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		compo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		edit = new Button(compo, SWT.PUSH);
		GridData d = new GridData(SWT.LEFT, SWT.FILL, false, true);
		d.widthHint = 48;
		edit.setLayoutData(d);
		edit.setAlignment(SWT.LEFT);
		edit.addSelectionListener(this);
		edit.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				displayParameterValue();
			}

		});

		return edit;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		java.awt.Color c = currentValue == null ? GamaColor.getInt(0) : (java.awt.Color) currentValue;
		Color color = new Color(Display.getDefault(), c.getRed(), c.getGreen(), c.getBlue());
		int height = edit.getSize().y;
		int width = edit.getSize().x;
		if ( height <= 0 || width <= 0 ) { return; }
		GC gc = new GC(edit);
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.fillRoundRectangle(6, 6, width - 16, height - 16, 5, 5);
		gc.setBackground(color);
		gc.fillRoundRectangle(7, 7, width - 18, height - 18, 5, 5);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRoundRectangle(7, 7, width - 18, height - 18, 5, 5);
		gc.dispose();
		// WARNING AD 19/04/14: this (commented) line seems to be the cause of Issue 923 (hangs on MacOSX).
		// edit.setText("                " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + " ");
		color.dispose();
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.COLOR);
	}

}
