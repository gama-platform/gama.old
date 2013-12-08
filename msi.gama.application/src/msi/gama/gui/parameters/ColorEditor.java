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
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaColor;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class ColorEditor extends AbstractEditor implements DisposeListener {

	private Button edit;

	// private ExpressionControl expression;

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
			modifyAndDisplayValue(new GamaColor(rgb.red, rgb.green, rgb.blue));
		}
	}

	@Override
	public void widgetDisposed(final DisposeEvent event) {
		// if ( edit.getImage() != null ) {
		// edit.getImage().dispose();
		// edit.setImage(null);
		// }
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		compo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		// Composite comp = new Composite(compo, SWT.None);
		// comp.setLayoutData(getParameterGridData());
		// final GridLayout layout = new GridLayout(2, false);
		// layout.verticalSpacing = 0;
		// layout.marginHeight = 1;
		// layout.marginWidth = 1;
		// comp.setLayout(layout);
		// expression = new ExpressionControl(comp, this);

		edit = new Button(compo, SWT.PUSH);
		GridData d = new GridData(SWT.LEFT, SWT.FILL, false, true);
		d.widthHint = 48;
		edit.setLayoutData(d);
		// edit.setText("Edit");
		edit.setAlignment(SWT.LEFT);

		// GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		// edit.setLayoutData(d);
		edit.addDisposeListener(this);
		edit.addSelectionListener(this);
		// edit.setImage(getImage());
		edit.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				displayParameterValue();
			}

		});

		return edit;
		// return expression.getControl();
	}

	// private static Image dumbImage;
	//
	// static Image getImage() {
	// if ( dumbImage == null ) {
	// dumbImage = new Image(Display.getDefault(), 32, 1);
	// }
	// return dumbImage;
	// }

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		// expression.getControl().setText(StringUtils.toGaml(currentValue));
		java.awt.Color c = currentValue == null ? GamaColor.getInt(0) : (java.awt.Color) currentValue;
		Color color = new Color(Display.getDefault(), c.getRed(), c.getGreen(), c.getBlue());
		// Image oldImage = edit.getImage();
		int height = edit.getSize().y;
		int width = edit.getSize().x;
		if ( height <= 0 || width <= 0 ) { return; }
		// Image image = new Image(Display.getDefault(), width, height);
		GC gc = new GC(edit);
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.fillRoundRectangle(6, 6, width - 16, height - 16, 5, 5);
		gc.setBackground(color);
		gc.fillRoundRectangle(7, 7, width - 18, height - 18, 5, 5);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRoundRectangle(7, 7, width - 18, height - 18, 5, 5);
		gc.dispose();
		edit.setText("                " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + " ");
		// edit.setImage(image);
		// if ( oldImage != null && !oldImage.isDisposed() ) {
		// oldImage.dispose();
		// }
		color.dispose();
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return edit;
		// if ( expression == null ) { return null; }
		// return expression.getControl();
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.COLOR);
	}

}
