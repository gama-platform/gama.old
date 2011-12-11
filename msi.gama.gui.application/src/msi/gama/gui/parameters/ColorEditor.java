/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ColorEditor extends AbstractEditor implements DisposeListener {

	Button edit;
	ExpressionControl expression;

	ColorEditor(final IParameter param) {
		super(param);
	}

	ColorEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	ColorEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.awt.Color> whenModified) {
		super(new SupportParameter(title, value), whenModified);
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
			modifyAndDisplayValue(new GamaColor(rgb.red, rgb.green, rgb.blue));
		}
	}

	@Override
	public void widgetDisposed(final DisposeEvent event) {
		if ( edit.getImage() != null ) {
			edit.getImage().dispose();
			edit.setImage(null);
		}
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		Composite comp = new Composite(compo, SWT.None);
		comp.setLayoutData(getParameterGridData());
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		expression = new ExpressionControl(comp, this);
		edit = new Button(comp, SWT.FLAT);
		edit.setText("Edit");
		edit.setAlignment(SWT.CENTER);

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		edit.setLayoutData(d);
		edit.addDisposeListener(this);
		edit.addSelectionListener(this);
		return expression.getControl();
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(Cast.toGaml(currentValue));
		GamaColor c = currentValue == null ? GamaColor.getInt(0) : (GamaColor) currentValue;
		Color color = new Color(Display.getDefault(), c.getRed(), c.getGreen(), c.getBlue());
		Image oldImage = edit.getImage();
		Image image = new Image(Display.getDefault(), 16, 16);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, 16, 16);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(0, 0, 15, 15);
		gc.dispose();
		edit.setImage(image);
		if ( oldImage != null && !oldImage.isDisposed() ) {
			oldImage.dispose();
		}
		color.dispose();
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return expression.getControl();
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.COLOR);
	}

}
