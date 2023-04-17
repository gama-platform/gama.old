/*******************************************************************************************************
 *
 * GamaWizardPage.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class GamaWizardPage.
 */
public class GamaWizardPage extends WizardPage {

	/** The values. */
	private final IMap<String, Object> values = GamaMapFactory.createUnordered();
	
	/** The parameters. */
	private final List<IParameter> parameters;
	
	/** The font. */
	private final GamaFont font;
	
	/** The scope. */
	private final IScope scope;

	/**
	 * Instantiates a new gama wizard page.
	 *
	 * @param scope the scope
	 * @param parameters the parameters
	 * @param title the title
	 * @param description the description
	 * @param font the font
	 */
	public GamaWizardPage(final IScope scope, final List<IParameter> parameters, final String title,
			final String description, final GamaFont font) {
		super(title);
		setTitle(title);
		setDescription(description);
		this.scope = scope;
		this.font = font;
		this.parameters = parameters;
		parameters.forEach(p -> { values.put(p.getName(), p.getInitialValue(scope)); });
	}

	@Override
	public void createControl(final Composite parent) {
		EditorsGroup composite = new EditorsGroup(parent, SWT.NONE);
		// AD The application of font cannot work at this level. It must be passed down to the editor controls.
		// Font f = null;
		// if (font != null) {
		// f = new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle());
		// }
		parameters.forEach(param -> {
			final EditorListener<?> listener = newValue -> {
				param.setValue(scope, newValue);
				values.put(param.getName(), newValue);
			};
			EditorFactory.create(scope, composite, param, listener, false, false);
		});
		composite.layout();
		setControl(composite);

		// if (font != null) {
		// composite.setFont(
		// new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle()));
		// }

	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public IMap<String, Object> getValues() { return values; }

	@Override
	public boolean isPageComplete() { return true; }

}
