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

import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.types.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class ExpressionControl implements IPopupProvider, SelectionListener, ModifyListener, FocusListener {

	private final Text text;
	private Popup popup = null;
	private final AbstractEditor editor;
	private Color background;
	Object currentValue;
	private Exception currentException;

	public ExpressionControl(final Composite comp, final AbstractEditor ed) {
		editor = ed;
		text = createTextBox(comp);
		final GridData d = ed.getParameterGridData();
		text.setLayoutData(d);
		text.addModifyListener(this);
		text.addFocusListener(this);
		text.addSelectionListener(this);
	}

	public Popup getPopup() {
		boolean withPopup = editor.acceptPopup();
		if ( popup == null && withPopup ) {
			popup = new Popup(this, editor.getLabel(), text);
		}
		return popup;
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if ( editor.internalModification ) { return; }
		modifyValue();
		if ( getPopup() != null ) {
			getPopup().display();
		}
	}

	private void modifyNoPopup() {
		editor.internalModification = true;
		currentException = null;
		text.setText(StringUtils.toGaml(currentValue));
		editor.modifyValue(currentValue);
		editor.internalModification = false;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			if ( me != null && me.detail == SWT.CANCEL ) {
				currentValue = editor.getOriginalValue();
			}
			modifyNoPopup();
			Popup.hide();

		} catch (final Exception e) {}
	}

	private void computeValue() {
		try {
			currentException = null;
			currentValue =
				editor.evaluateExpression() ? GAML.evaluateExpression(text.getText(), editor.getAgent()) : GAML
					.compileExpression(text.getText(), editor.getAgent());
		} catch (final Exception e) {
			currentException = e;
		}
	}

	private void modifyValue() {
		final Object oldValue = currentValue;
		computeValue();
		if ( currentException != null ) {
			currentValue = oldValue;
			return;
		}
		try {
			editor.modifyValue(currentValue);
		} catch (final GamaRuntimeException e) {
			currentValue = oldValue;
			currentException = e;
		}
	}

	String getPopupBody() {
		if ( currentValue == null ) {
			computeValue();
		}
		if ( currentException != null ) {
			background = SwtGui.COLOR_ERROR;
			return currentException.getMessage();
		}
		String string = "Result: " + StringUtils.toGaml(currentValue);
		final IType expectedType = editor.getExpectedType();
		if ( GAMA.run(new InScope<Boolean>() {

			@Override
			public Boolean run(final IScope scope) {
				return expectedType.canBeTypeOf(scope, currentValue);
			}
		}) ) {
			background = SwtGui.COLOR_OK;
		} else {
			background = SwtGui.COLOR_WARNING;
			string += "\nWarning: should be of type " + expectedType.toString();
		}
		return string;

	}

	protected Text createTextBox(final Composite comp) {
		return new Text(comp, SWT.SEARCH | SWT.ICON_CANCEL);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		computeValue();
		getPopup();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		/* async is needed to wait until focus reaches its new Control */
		SwtGui.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( SwtGui.getDisplay().isDisposed() ) { return; }
				final Control control = SwtGui.getDisplay().getFocusControl();
				if ( control != text ) {
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
		final String string = getPopupBody() + "\n" + editor.getTooltipText();
		return string;
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public Color getPopupBackground() {
		return background;
	}

	@Override
	public Shell getControllingShell() {
		if ( text.getVisible() ) { return text.getShell(); }
		return null;
	}

	@Override
	public Point getAbsoluteOrigin() {
		final Control parent = editor.getLabel().getParent();
		return parent.toDisplay(new Point(parent.getLocation().x, editor.getLabel().getLocation().y + 20));
	}

}
