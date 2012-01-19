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
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlException;
import msi.gaml.types.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class ExpressionControl implements IPopupProvider, SelectionListener, ModifyListener,
	FocusListener {

	Text text;
	Popup popup;
	AbstractEditor editor;
	Color background;

	// IType expectedType;

	public ExpressionControl(final Composite comp, final AbstractEditor ed) {
		editor = ed;
		text = createTextBox(comp);
		GridData d = ed.getParameterGridData();
		text.setLayoutData(d);
		text.addModifyListener(this);
		text.addFocusListener(this);
		text.addSelectionListener(this);
		popup = new Popup(this, text);

	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if ( editor.internalModification ) { return; }
		try {
			modifyValue();
		} catch (GamlException e) {
			// e.printStackTrace();
		}
		popup.display();
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			if ( me != null && me.detail == SWT.CANCEL ) {
				text.setText(StringUtils.toGaml(editor.getOriginalValue()));
			}
			modifyValue();
			Popup.hide();
		} catch (GamlException e) {

		} catch (Exception e) {}
	}

	private Object computeValue() throws GamlException {
		return editor.evaluateExpression() ? GAMA.evaluateExpression(text.getText(),
			editor.getAgent()) : GAMA.compileExpression(text.getText(), editor.getAgent());

	}

	private Object modifyValue() throws GamlException {
		Object newValue = computeValue();
		editor.modifyValue(newValue);
		return newValue;
	}

	String getPopupBody() {
		try {
			Object value = computeValue();
			String string = "Result: " + StringUtils.toGaml(value);
			IType expectedType = editor.getExpectedType();
			if ( expectedType.canBeTypeOf(GAMA.getDefaultScope(), value) ) {
				background = SwtGui.COLOR_OK;
			} else {
				background = SwtGui.COLOR_WARNING;
				string += "\nWarning: should be of type " + expectedType.toString();
			}
			return string;
		} catch (Exception e) {
			background = SwtGui.COLOR_ERROR;
			return e.getMessage();
		}
	}

	protected Text createTextBox(final Composite comp) {
		return new Text(comp, SWT.SEARCH | SWT.ICON_CANCEL);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		popup.display();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		/* async is needed to wait until focus reaches its new Control */
		SwtGui.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( SwtGui.getDisplay().isDisposed() ) { return; }
				final Control control = SwtGui.getDisplay().getFocusControl();
				if ( control == null || control != text ) {
					widgetDefaultSelected(null);
				}
			}
		});

	}

	public Text getControl() {
		return text;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {}

	public void setFocus() {
		text.setFocus();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public String getPopupText() {
		if ( text.getText().isEmpty() ) { return null; }
		String string = getPopupBody() + "\n" + editor.getTooltipText();
		return string;
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPositionControl()
	 */
	@Override
	public Control getPositionControl() {
		return text;
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public Color getPopupBackground() {
		return background;
	}

}
