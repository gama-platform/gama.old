/*********************************************************************************************
 *
 * 'EditorsDialog.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

	private final IMap<String, Object> values = GamaMapFactory.createUnordered();
	private final List<IParameter> parameters;
	private final String title;
	private final GamaFont font;
	private final IScope scope;

	public EditorsDialog(final IScope scope, final Shell parentShell, final List<IParameter> parameters,
			final String title, final GamaFont font) {
		super(parentShell);
		this.scope = scope;
		this.title = title;
		this.font = font;
		setShellStyle(SWT.RESIZE | SWT.BORDER);
		this.parameters = parameters;
		parameters.forEach(p -> {
			values.put(p.getName(), p.getInitialValue(scope));
		});
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		final var composite = super.createButtonBar(parent);
		// composite.setBackground(IGamaColors.WHITE.color());
		return composite;
	}

	/**
	 * Method createContents()
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		final var composite = super.createContents(parent);
		// composite.setBackground(IGamaColors.WHITE.color());
		return composite;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final var composite = (Composite) super.createDialogArea(parent);
		// composite.setBackground(IGamaColors.WHITE.color());
		final var layout = (GridLayout) composite.getLayout();
		layout.numColumns = 2;
		final var text = new Label(composite, SWT.None);
		text.setBackground(IGamaColors.OK.inactive());
		text.setForeground(GamaColors.getTextColorForBackground(text.getBackground()).color());
		if (font != null) 
			text.setFont(new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle()));
		text.setText(title);
		var data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		text.setLayoutData(data);
		final var sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
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
	protected boolean isResizable() {
		return true;
	}

	public Map<String, Object> getValues() {
		return values;
	}

}
