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
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.commands.*;
import msi.gama.gui.swt.commands.GamaColorMenu.IColorRunnable;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaColor;
import msi.gaml.types.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.*;

public class ColorEditor extends AbstractEditor {

	IColorRunnable runnable = new IColorRunnable() {

		@Override
		public void run(final int r, final int g, final int b) {
			modifyAndDisplayValue(new GamaColor(r, g, b, 255));
		}
	};

	SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem i = (MenuItem) e.widget;
			String color = i.getText().replace("#", "");
			GamaColor c = GamaColor.colors.get(color);
			if ( c == null ) { return; }
			modifyAndDisplayValue(c);
		}

	};

	private FlatButton edit;

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
		GamaColorMenu.getInstance().open(edit, event, listener, runnable);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		edit = FlatButton.menu(compo, IGamaColors.WHITE, "").light().small();
		edit.addSelectionListener(this);
		displayParameterValue();
		return edit;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		GamaUIColor color = GamaColors.get(currentValue == null ? GamaColor.getInt(0) : (java.awt.Color) currentValue);
		edit.setText(color.toString()).setColor(color);
		// color.dispose();
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	@Override
	public IType getExpectedType() {
		return Types.COLOR;
	}

	@Override
	protected void applyEdit() {
		GamaColorMenu.getInstance();
		java.awt.Color color = (java.awt.Color) currentValue;
		RGB rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
		GamaColorMenu.openView(runnable, rgb);
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
