/*******************************************************************************************************
 *
 * EditorsDialog.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class EditorsDialog.
 *
 * @author drogoul
 * @since 10 mai 2012
 *
 */
public class EditorsDialog extends Dialog {

	/** The values. */
	private final IMap<String, Object> values = GamaMapFactory.createUnordered();

	/** The parameters. */
	private final List<IParameter> parameters;

	/** The title. */
	private final String title;

	/** The font. */
	private final GamaFont font;

	/** The scope. */
	private final IScope scope;

	/**
	 * Instantiates a new editors dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param parentShell
	 *            the parent shell
	 * @param parameters
	 *            the parameters
	 * @param title
	 *            the title
	 * @param font
	 *            the font
	 */
	public EditorsDialog(final IScope scope, final Shell parentShell, final List<IParameter> parameters,
			final String title, final GamaFont font) {
		super(parentShell);
		this.scope = scope;
		this.title = title;
		this.font = font;
		setShellStyle(SWT.TITLE | SWT.RESIZE | SWT.TOOL | SWT.ON_TOP);
		this.parameters = parameters;
		parameters.forEach(p -> { values.put(p.getName(), p.getInitialValue(scope)); });
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Method createContents()
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {

		// composite.setBackground(IGamaColors.WHITE.color());
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final var above = (Composite) super.createDialogArea(parent);
		// composite.setBackground(IGamaColors.WHITE.color());
		final var composite = new EditorsGroup(above);
		// final var layout = (GridLayout) composite.getLayout();
		// layout.numColumns = 2;
		final var text = new Label(composite, SWT.None);
		text.setBackground(IGamaColors.OK.inactive());
		text.setForeground(GamaColors.getTextColorForBackground(text.getBackground()).color());
		if (font != null) {
			text.setFont(new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle()));
		}
		text.setText(title);
		var data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		text.setLayoutData(data);
		final var sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		data.heightHint = 20;
		sep.setLayoutData(data);
		parameters.forEach(param -> {
			final EditorListener<?> listener = newValue -> {
				param.setValue(scope, newValue);
				values.put(param.getName(), newValue);
			};
			EditorFactory.create(scope, composite, param, listener, false, false);
		});
		composite.layout();
		return composite;
	}

	@Override
	protected Point getInitialSize() {
		final var p = super.getInitialSize();
		return new Point(p.x * 2, p.y);
	}

	@Override
	protected boolean isResizable() { return true; }

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public Map<String, Object> getValues() { return values; }

}
