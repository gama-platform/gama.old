package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import ummisco.gama.ui.interfaces.EditorListener;

public class GamaWizardPage extends WizardPage{

	private final IMap<String, Object> values = GamaMapFactory.createUnordered();
	private final List<IParameter> parameters;
	private final GamaFont font;
	private final IScope scope;
	
	public GamaWizardPage(final IScope scope, final List<IParameter> parameters,
			final String title, final String description, final GamaFont font) {
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
	public void createControl(Composite parent) {
		Composite  composite = new Composite(parent, SWT.NONE);
	        GridLayout layout = new GridLayout();
	        composite.setLayout(layout);
	        layout.numColumns = 2;
	        parameters.forEach(param -> {
			final EditorListener<?> listener = newValue -> {
				param.setValue(scope, newValue);
				values.put(param.getName(), newValue);
			};
			EditorFactory.create(scope, composite, param, listener, false, false);
		});
		composite.layout();
		 setControl(composite);
	}

	public IMap<String, Object> getValues() {
		return values;
	}
	
	
	
}
