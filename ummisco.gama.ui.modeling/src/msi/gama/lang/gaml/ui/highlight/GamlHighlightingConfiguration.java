/*******************************************************************************************************
 *
 * GamlHighlightingConfiguration.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;

import com.google.inject.Singleton;

import msi.gama.application.workbench.ThemeHelper;

/**
 * The Class GamlHighlightingConfiguration.
 */
@Singleton
public class GamlHighlightingConfiguration implements IHighlightingConfiguration {

	/** The delegate. */
	private DelegateHighlightingConfiguration delegate;
	
	/** The light. */
	final DelegateHighlightingConfiguration light = new LightHighlightingConfiguration();
	
	/** The dark. */
	final DelegateHighlightingConfiguration dark = new DarkHighlightingConfiguration();

	/**
	 * Instantiates a new gaml highlighting configuration.
	 */
	public GamlHighlightingConfiguration() {
		delegate = ThemeHelper.isDark() ? dark : light;
	}

	/**
	 * Change to.
	 *
	 * @param toLight the to light
	 */
	public void changeTo(final boolean toLight) {
		// delegate.saveCurrentPreferences();
		delegate = toLight ? light : dark;
		// delegate.restoreCurrentPreferences();
	}

	@Override
	public void configure(final IHighlightingConfigurationAcceptor acceptor) {
		delegate.configure(acceptor);
	}

}
