/*********************************************************************************************
 *
 * 'ColorEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.awt.Color;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.menus.GamaColorMenu.IColorRunnable;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

public class ColorEditor extends AbstractEditor<Color> {

	final IColorRunnable runnable = (r, g, b) -> modifyAndDisplayValue(new GamaColor(r, g, b, 255));

	final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem i = (MenuItem) e.widget;
			final String color = i.getText().replace("#", "");
			final GamaColor c = GamaColor.colors.get(color);
			if (c == null) { return; }
			modifyAndDisplayValue(c);
		}

	};

	private FlatButton edit;

	ColorEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	ColorEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<Color> l) {
		super(scope, agent, param, l);
	}

	ColorEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	ColorEditor(final IScope scope, final Composite parent, final String title, final Object value,
			final EditorListener<java.awt.Color> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		new GamaColorMenu(null).open(edit, event, listener, runnable);
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
		final GamaUIColor color =
				GamaColors.get(currentValue == null ? GamaColor.getInt(0) : (java.awt.Color) currentValue);
		edit.setText(color.toString());
		edit.setColor(color);
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	@Override
	public GamaColorType getExpectedType() {
		return Types.COLOR;
	}

	@Override
	protected void applyEdit() {
		final java.awt.Color color = currentValue;
		final RGB rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
		GamaColorMenu.openView(runnable, rgb);
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
