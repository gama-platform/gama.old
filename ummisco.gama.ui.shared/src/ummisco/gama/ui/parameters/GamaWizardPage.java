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

public class GamaWizardPage extends WizardPage {

	private final IMap<String, Object> values = GamaMapFactory.createUnordered();
	private final List<IParameter> parameters;
	private final GamaFont font;
	private final IScope scope;

	public GamaWizardPage(final IScope scope, final List<IParameter> parameters, final String title,
			final String description, final GamaFont font) {
		super(title);
		setTitle(title);
		setDescription(description);
		this.scope = scope;
		this.font = font;
		this.parameters = parameters;
		parameters.forEach(p -> {
			values.put(p.getName(), p.getInitialValue(scope));
		});
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
			AbstractEditor ed = EditorFactory.create(scope, composite, param, listener, false, false);
		});
		composite.layout();
		setControl(composite);

		// if (font != null) {
		// composite.setFont(
		// new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle()));
		// }

	}

	public IMap<String, Object> getValues() {
		return values;
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

}
