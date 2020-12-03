/*********************************************************************************************
 *
 * 'GamlHighlightingConfiguration.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;

import com.google.inject.Singleton;

import msi.gama.application.workbench.ThemeHelper;

@Singleton
public class GamlHighlightingConfiguration implements IHighlightingConfiguration {

	private DelegateHighlightingConfiguration delegate;
	final DelegateHighlightingConfiguration light = new LightHighlightingConfiguration();
	final DelegateHighlightingConfiguration dark = new DarkHighlightingConfiguration();

	public GamlHighlightingConfiguration() {
		delegate = ThemeHelper.isDark() ? dark : light;
	}

	public void changeTo(boolean toLight) {
		delegate.saveCurrentPreferences();
		delegate = toLight ? light : dark;
		delegate.restoreCurrentPreferences();
	}

	@Override
	public void configure(final IHighlightingConfigurationAcceptor acceptor) {
		delegate.configure(acceptor);
	}

}
