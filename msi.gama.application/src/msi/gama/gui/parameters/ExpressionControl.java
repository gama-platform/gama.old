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
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlException;
import msi.gaml.types.IType;
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
	// IType expectedType;
	Listener deactivatePopup;

	static {
		popup = new Shell(SwtGui.getDisplay(), SWT.ON_TOP);
		popup.setLayout(new FillLayout());
		result = new Label(popup, SWT.NONE);
		result.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	public ExpressionControl(final Composite comp, final AbstractEditor ed) {
		editor = ed;
		text = createTextBox(comp);
		GridData d = ed.getParameterGridData();
		text.setLayoutData(d);
		// expectedType = ed.getExpectedType();
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
		try {
			modifyValue();
		} catch (GamlException e) {
			// e.printStackTrace();
		}
		displayPopup();
	}

	private void displayPopup() {
		String string = text.getText();
		if ( string.length() == 0 ) {
			popup.setVisible(false);
			return;
		}
		string = getPopupBody();
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
				text.setText(StringUtils.toGaml(editor.getOriginalValue()));
			}
			modifyValue();
			popup.setVisible(false);
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
				result.setBackground(SwtGui.COLOR_OK);
			} else {
				result.setBackground(SwtGui.COLOR_WARNING);
				string += "\nWarning: should be of type " + expectedType.toString();
			}
			return string;
		} catch (Exception e) {
			result.setBackground(SwtGui.COLOR_ERROR);
			return e.getMessage();
		}
	}

	protected Text createTextBox(final Composite comp) {
		return new Text(comp, SWT.SEARCH | SWT.ICON_CANCEL);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		displayPopup();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		/* async is needed to wait until focus reaches its new Control */
		SwtGui.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( SwtGui.getDisplay().isDisposed() ) { return; }
				final Control control = SwtGui.getDisplay().getFocusControl();
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
		displayPopup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(final MouseEvent e) {
		popup.setVisible(false);
		// widgetDefaultSelected(null);

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
