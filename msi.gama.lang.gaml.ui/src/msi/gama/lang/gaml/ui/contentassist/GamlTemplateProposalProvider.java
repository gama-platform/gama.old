/**
 * Created by drogoul, 21 janv. 2014
 * 
 */
package msi.gama.lang.gaml.ui.contentassist;

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
		super.createTemplates(templateContext, context, acceptor);
	}

}
