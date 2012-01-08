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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.GamlException;
import msi.gaml.types.*;
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
	public void valueModified(final Object newValue) throws GamaRuntimeException, GamlException {
		super.valueModified(newValue);
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(StringUtils.toGaml(currentValue));
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
