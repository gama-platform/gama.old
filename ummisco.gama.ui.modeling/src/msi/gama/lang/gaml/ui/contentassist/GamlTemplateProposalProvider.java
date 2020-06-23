/*********************************************************************************************
 *
 * 'GamlTemplateProposalProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateAcceptor;
import org.eclipse.xtext.ui.editor.templates.ContextTypeIdHelper;
import org.eclipse.xtext.ui.editor.templates.DefaultTemplateProposalProvider;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateStore;

import com.google.inject.Inject;

import msi.gama.lang.gaml.services.GamlGrammarAccess;

/**
 * The class GamlTemplateProposalProvider.
 *
 * @author drogoul
 * @since 21 janv. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class GamlTemplateProposalProvider extends DefaultTemplateProposalProvider {

	/**
	 * @param templateStore
	 * @param registry
	 * @param helper
	 */

	@Inject private XtextTemplateStore store;

	@Inject
	public GamlTemplateProposalProvider(final TemplateStore templateStore, final ContextTypeRegistry registry,
			final ContextTypeIdHelper helper) {
		super(templateStore, registry, helper);
	}

	@Inject private GamlGrammarAccess ga;

	@Override
	protected void createTemplates(final TemplateContext templateContext, final ContentAssistContext context,
			final ITemplateAcceptor acceptor) {
		// Disabling for comments (see Issue 786)
		final EObject grammarElement = context.getCurrentNode().getGrammarElement();
		if (grammarElement == ga.getML_COMMENTRule()) { return; }
		if (grammarElement == ga.getSL_COMMENTRule()) { return; }
		// TemplateContextType contextType = templateContext.getContextType();
		final Template[] templates = store.getTemplates();
		for (final Template template : templates) {
			if (!acceptor.canAcceptMoreTemplates()) { return; }
			if (validate(template, templateContext)) {
				acceptor.accept(
						createProposal(template, templateContext, context, getImage(template), getRelevance(template)));
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
