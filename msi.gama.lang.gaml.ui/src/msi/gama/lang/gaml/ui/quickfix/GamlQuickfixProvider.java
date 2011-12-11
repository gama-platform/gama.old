
package msi.gama.lang.gaml.ui.quickfix;

import msi.gama.lang.gaml.validation.GamlJavaValidator;

import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

public class GamlQuickfixProvider extends DefaultQuickfixProvider {

//	@Fix(MyJavaValidator.INVALID_NAME)
//	public void capitalizeName(final Issue issue, IssueResolutionAcceptor acceptor) {
//		acceptor.accept(issue, "Capitalize name", "Capitalize the name.", "upcase.png", new IModification() {
//			public void apply(IModificationContext context) throws BadLocationException {
//				IXtextDocument xtextDocument = context.getXtextDocument();
//				String firstLetter = xtextDocument.get(issue.getOffset(), 1);
//				xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
//			}
//		});
//	}	
	public void removeIssue(final String label,final Issue issue, IssueResolutionAcceptor acceptor){		
		acceptor.accept(issue, label, "", "", 
				new IModification(){					
					public void apply(IModificationContext context) throws BadLocationException {
						IXtextDocument xtextDocument = context.getXtextDocument();						
						xtextDocument.replace(issue.getOffset(), issue.getLength(), "");
					}
				}
		);
	}
	@Fix(GamlJavaValidator.QF_NOTKEYOFMODEL)
	public void fixKeyOfModel(final Issue issue, IssueResolutionAcceptor acceptor){		
		removeIssue("Remove this keyword" ,issue, acceptor);		
	}
	
	@Fix(GamlJavaValidator.QF_NOTKEYOFCONTEXT)
	public void fixKeyOfContext(final Issue issue, IssueResolutionAcceptor acceptor){
		removeIssue("Remove this keyword",issue, acceptor);
	}
	
	@Fix(GamlJavaValidator.QF_NOTFACETOFKEY)
	public void fixFacetOfKey(final Issue issue, IssueResolutionAcceptor acceptor){
		removeIssue("Remove this facet", issue, acceptor);		
	}

}
