/**
 * Created by drogoul, 29 avr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.ui.editor.contentassist.*;

/**
 * The class GamlContentAssistProcessor.
 * 
 * @author drogoul
 * @since 29 avr. 2012
 * 
 */
public class GamlContentAssistProcessor extends XtextContentAssistProcessor {

	@Override
	protected CompletionProposalComputer createCompletionProposalComputer(final ITextViewer viewer,
		final int offset) {
		return new GamlProposalComputer(this, viewer, offset);
	}

}
