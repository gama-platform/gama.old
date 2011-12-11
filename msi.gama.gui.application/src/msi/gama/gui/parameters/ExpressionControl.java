/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.IType;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.Cast;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ExpressionControl implements SelectionListener, ModifyListener, FocusListener,
	MouseTrackListener {

	Text text;
	private static final Shell popup;
	private static final Label result;
	AbstractEditor editor;
	IType expectedType;
	Listener deactivatePopup;

	static {
		popup = new Shell(GUI.getDisplay(), SWT.ON_TOP);
		popup.setLayout(new FillLayout());
		result = new Label(popup, SWT.NONE);
		result.setForeground(GUI.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	public ExpressionControl(final Composite comp, final AbstractEditor ed) {
		editor = ed;
		text = createTextBox(comp);
		GridData d = ed.getParameterGridData();
		text.setLayoutData(d);
		expectedType = ed.getExpectedType();
		// popup = new Shell(GUI.getDisplay(), SWT.ON_TOP);
		// popup.setLayout(new FillLayout());
		// result = new Label(popup, SWT.NONE);
		// result.setForeground(GUI.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.addModifyListener(this);
		text.addFocusListener(this);
		text.addSelectionListener(this);
		text.addMouseTrackListener(this);
		deactivatePopup = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if ( !popup.isDisposed() ) {
					popup.setVisible(false);
				}
			}
		};

		comp.getShell().addListener(SWT.Move, deactivatePopup);
		comp.getShell().addListener(SWT.Resize, deactivatePopup);
		comp.getShell().addListener(SWT.Close, deactivatePopup);
		comp.getShell().addListener(SWT.Deactivate, deactivatePopup);
		comp.getShell().addListener(SWT.Hide, deactivatePopup);

	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if ( editor.internalModification ) { return; }
		displayPopup();
	}

	private void displayPopup() {
		String string = text.getText();
		if ( string.length() == 0 ) {
			popup.setVisible(false);
			return;
		}
		string = getEvaluationContent(string);
		if ( string == null || string.isEmpty() ) {
			popup.setVisible(false);
			return;
		}
		setPopupText(string);
		final Point point = text.toDisplay(text.getLocation().x, text.getSize().y);
		popup.pack();
		popup.setLocation(point.x, point.y);
		popup.setVisible(true);
	}

	private void setPopupText(final String s) {
		String t = s;
		t += "\n" + editor.getTooltipText();
		result.setText(t);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			if ( me != null && me.detail == SWT.CANCEL ) {
				text.setText(Cast.toGaml(editor.originalValue));
			}
			editor.modifyAndDisplayValue(editor.evaluateExpression() ? GAMA.evaluateExpression(
				text.getText(), editor.getAgent()) : GAMA.compileExpression(text.getText(),
				editor.getAgent()));
			popup.setVisible(false);
		} catch (GamlException e) {

		} catch (Exception e) {}
	}

	String getEvaluationContent(final String s) {
		try {
			Object value = GAMA.evaluateExpression(s, editor.getAgent());
			String string = "Result: " + Cast.toGaml(value);
			if ( expectedType == Types.NO_TYPE || Types.get(value.getClass()) == expectedType ) {
				result.setBackground(GUI.COLOR_OK);
			} else {
				result.setBackground(GUI.COLOR_WARNING);
				string += "\nWarning: should be of type " + expectedType.toString();
			}
			return string;
		} catch (Exception e) {
			result.setBackground(GUI.COLOR_ERROR);
			return e.getMessage();
		}
	}

	protected Text createTextBox(final Composite comp) {
		return new Text(comp, SWT.SEARCH | SWT.ICON_CANCEL);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		modifyText(null);
	}

	@Override
	public void focusLost(final FocusEvent e) {
		/* async is needed to wait until focus reaches its new Control */
		GUI.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( GUI.getDisplay().isDisposed() ) { return; }
				final Control control = GUI.getDisplay().getFocusControl();
				if ( control == null || control != text && control != result && !popup.isDisposed() ) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseEnter(final MouseEvent e) {
		modifyText(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(final MouseEvent e) {
		widgetDefaultSelected(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseHover(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		text.setFocus();
	}

}
