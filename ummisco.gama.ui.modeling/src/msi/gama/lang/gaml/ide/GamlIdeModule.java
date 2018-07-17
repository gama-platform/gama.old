/*********************************************************************************************
 *
 * 'GamlUiModule.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ide;

import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;

import com.google.inject.Binder;

import msi.gama.lang.gaml.ide.contentassist.antlr.GamlParser;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlIdeModule extends AbstractGamlIdeModule {

	/**
	 * @see org.eclipse.xtext.service.AbstractGenericModule#configure(com.google.inject.Binder)
	 */
	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		configureContentAssistLexer(binder);
		binder.bind(IContentAssistParser.class).to(GamlParser.class);
		binder.bind(IProposalConflictHelper.class).to(AntlrProposalConflictHelper.class);
		binder.bind(IPrefixMatcher.class).to(IPrefixMatcher.IgnoreCase.class);
	}
}
