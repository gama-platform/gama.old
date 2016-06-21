/*********************************************************************************************
 * 
 * 
 * 'GamlTemplateProposalProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.ui.modeling.contentassist;

import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.templates.*;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.xtext.ui.editor.contentassist.*;
import org.eclipse.xtext.ui.editor.templates.*;
import com.google.inject.Inject;

/**
 * The class GamlTemplateProposalProvider.
 * 
 * @author drogoul
 * @since 21 janv. 2014
 * 
 */
public class GamlTemplateProposalProvider extends DefaultTemplateProposalProvider {

	/**
	 * @param templateStore
	 * @param registry
	 * @param helper
	 */

	@Inject
	private XtextTemplateStore store;

	@Inject
	public GamlTemplateProposalProvider(final TemplateStore templateStore, final ContextTypeRegistry registry,
		final ContextTypeIdHelper helper) {
		super(templateStore, registry, helper);
	}

	@Inject
	private GamlGrammarAccess ga;

	@Override
	protected void createTemplates(final TemplateContext templateContext, final ContentAssistContext context,
		final ITemplateAcceptor acceptor) {
		// Disabling for comments (see Issue 786)
		EObject grammarElement = context.getCurrentNode().getGrammarElement();
		if ( grammarElement == ga.getML_COMMENTRule() ) { return; }
		if ( grammarElement == ga.getSL_COMMENTRule() ) { return; }
		// TemplateContextType contextType = templateContext.getContextType();
		Template[] templates = store.getTemplates();
		for ( Template template : templates ) {
			if ( !acceptor.canAcceptMoreTemplates() ) { return; }
			if ( validate(template, templateContext) ) {
				acceptor.accept(createProposal(template, templateContext, context, getImage(template),
					getRelevance(template)));
			}
		}

	}

	@Override
	protected boolean validate(final Template template, final TemplateContext context) {
		return true;
		// Accepting all templates
		// FIXME To be changed at one point !
	}

}
