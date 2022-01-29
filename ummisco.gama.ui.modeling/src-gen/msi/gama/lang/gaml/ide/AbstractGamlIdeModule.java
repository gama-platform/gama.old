/*******************************************************************************************************
 *
 * AbstractGamlIdeModule.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ide;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import msi.gama.lang.gaml.ide.contentassist.antlr.GamlParser;
import msi.gama.lang.gaml.ide.contentassist.antlr.internal.InternalGamlLexer;
import org.eclipse.xtext.ide.DefaultIdeModule;
import org.eclipse.xtext.ide.LexerIdeBindings;
import org.eclipse.xtext.ide.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;
import org.eclipse.xtext.ide.refactoring.IRenameStrategy2;
import org.eclipse.xtext.ide.server.rename.IRenameService2;
import org.eclipse.xtext.ide.server.rename.RenameService2;

/**
 * Manual modifications go to {@link GamlIdeModule}.
 */
@SuppressWarnings("all")
public abstract class AbstractGamlIdeModule extends DefaultIdeModule {

	/**
	 * Configure content assist lexer.
	 *
	 * @param binder the binder
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureContentAssistLexer(Binder binder) {
		binder.bind(Lexer.class)
			.annotatedWith(Names.named(LexerIdeBindings.CONTENT_ASSIST))
			.to(InternalGamlLexer.class);
	}
	
	/**
	 * Bind I content assist parser.
	 *
	 * @return the class<? extends I content assist parser>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IContentAssistParser> bindIContentAssistParser() {
		return GamlParser.class;
	}
	
	/**
	 * Bind I proposal conflict helper.
	 *
	 * @return the class<? extends I proposal conflict helper>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IProposalConflictHelper> bindIProposalConflictHelper() {
		return AntlrProposalConflictHelper.class;
	}
	
	/**
	 * Bind I prefix matcher.
	 *
	 * @return the class<? extends I prefix matcher>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.exporting.QualifiedNamesFragment2
	public Class<? extends IPrefixMatcher> bindIPrefixMatcher() {
		return FQNPrefixMatcher.class;
	}
	
	/**
	 * Bind I rename service 2.
	 *
	 * @return the class<? extends I rename service 2 >
	 */
	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IRenameService2> bindIRenameService2() {
		return RenameService2.class;
	}
	
	/**
	 * Bind I rename strategy 2.
	 *
	 * @return the class<? extends I rename strategy 2 >
	 */
	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IRenameStrategy2> bindIRenameStrategy2() {
		return IRenameStrategy2.DefaultImpl.class;
	}
	
}
