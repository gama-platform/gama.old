/**
 * Created by drogoul, 29 avr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.contentassist.CompletionProposalComputer;

/**
 * The class GamlProposalComputer.
 * 
 * @author drogoul
 * @since 29 avr. 2012
 * 
 */
public class GamlProposalComputer extends CompletionProposalComputer {

	/**
	 * @param state
	 * @param viewer
	 * @param offset
	 */
	public GamlProposalComputer(final State state, final ITextViewer viewer, final int offset) {
		super(state, viewer, offset);
	}

	@Override
	public ICompletionProposal[] exec(final XtextResource resource) throws Exception {
		// Addition of the validation before doing anything on the resource
		// ((GamlResource) resource).doValidate();
		// GuiUtils.debug("ProposalComputer begins validation of " + resource);
		// resource.getResourceServiceProvider().getResourceValidator()
		// .validate(resource, CheckMode.FAST_ONLY, null);
		return super.exec(resource);
	}

}
