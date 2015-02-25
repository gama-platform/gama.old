/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package msi.gama.lang.gaml.ui.templates;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.*;
import com.google.inject.*;
import com.google.inject.name.Named;

/**
 * The class GamlEditTemplateDialogFactory.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
public class GamlEditTemplateDialogFactory extends EditTemplateDialogFactory {

	@Inject
	private Provider<TemplatesLanguageConfiguration> configurationProvider;

	@Inject
	private ContextTypeRegistry contextTypeRegistry;

	@Inject
	private TemplateResourceProvider resourceProvider;

	@Inject
	@Named(Constants.LANGUAGE_NAME)
	private String languageName;

	public GamlEditTemplateDialog createDialog(final TemplatePersistenceData template, final boolean edit,
		final Shell shell) {
		GamlEditTemplateDialog dialog =
			new GamlEditTemplateDialog(shell, template, edit, contextTypeRegistry, configurationProvider.get(),
				resourceProvider, languageName);
		return dialog;
	}

}
