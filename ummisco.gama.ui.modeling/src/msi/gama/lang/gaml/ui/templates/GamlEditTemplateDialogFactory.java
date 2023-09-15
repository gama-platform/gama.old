/*******************************************************************************************************
 *
 * GamlEditTemplateDialogFactory.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.templates;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.EditTemplateDialogFactory;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplateResourceProvider;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplatesLanguageConfiguration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * The class GamlEditTemplateDialogFactory.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class GamlEditTemplateDialogFactory extends EditTemplateDialogFactory {

	/** The configuration provider. */
	@Inject private Provider<TemplatesLanguageConfiguration> configurationProvider;

	/** The context type registry. */
	@Inject private ContextTypeRegistry contextTypeRegistry;

	/** The resource provider. */
	@Inject private TemplateResourceProvider resourceProvider;

	/** The language name. */
	@Inject @Named (Constants.LANGUAGE_NAME) private String languageName;

	/**
	 * Creates a new GamlEditTemplateDialog object.
	 *
	 * @param template
	 *            the template
	 * @param edit
	 *            the edit
	 * @param shell
	 *            the shell
	 * @return the gaml edit template dialog
	 */
	public GamlEditTemplateDialog createDialog(final TemplatePersistenceData template, final boolean edit,
			final Shell shell) {
		final GamlEditTemplateDialog dialog = new GamlEditTemplateDialog(shell, template, edit, contextTypeRegistry,
				configurationProvider.get(), resourceProvider, languageName);
		return dialog;
	}

}
