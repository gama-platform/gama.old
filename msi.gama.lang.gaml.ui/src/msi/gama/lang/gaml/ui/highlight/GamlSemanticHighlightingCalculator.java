package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
/**
 * 
 * @author Pierrick
 *         cf. http://www.eclipse.org/Xtext/documentation/latest/xtext.html#highlighting
 * 
 */
public class GamlSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	@Override
	public void provideHighlightingFor(final XtextResource resource,
		final IHighlightedPositionAcceptor acceptor) {
		if ( resource == null || resource.getParseResult() == null) { return; }

		Iterable<INode> allNodes = resource.getParseResult().getRootNode().getAsTreeIterable();
				
		for ( INode node : allNodes ) {
						
		//Cuong: Because comment is hidden rule, we can not get it as a semantic object	
			final String tokentext = node.getText();
			if (tokentext.startsWith("//") || (tokentext.startsWith("/*") && tokentext.endsWith("*/"))) {
				continue;				
			}	
			
			EObject elt = NodeModelUtils.findActualSemanticObjectFor(node);					
			// apply style to our Gaml defined Keyword / facet / operators
				
			if ( elt instanceof msi.gama.lang.gaml.gaml.GamlKeywordRef ) {
				acceptor.addPosition(node.getOffset(), node.getLength(),
					DefaultHighlightingConfiguration.KEYWORD_ID);
			} else if ( elt instanceof msi.gama.lang.gaml.gaml.GamlBinarOpRef ) {
				acceptor.addPosition(node.getOffset(), node.getLength(),
					GamlHighlightingConfiguration.BINARY_ID);
			} else if ( elt instanceof msi.gama.lang.gaml.gaml.GamlFacetRef ) {
				acceptor.addPosition(node.getOffset(), node.getLength(),
					GamlHighlightingConfiguration.FACET_ID);
			} else if ( elt instanceof msi.gama.lang.gaml.gaml.DoubleLiteral ||
				elt instanceof msi.gama.lang.gaml.gaml.ColorLiteral ||
				elt instanceof msi.gama.lang.gaml.gaml.BooleanLiteral ) {
				acceptor.addPosition(node.getOffset(), node.getLength(),
					DefaultHighlightingConfiguration.NUMBER_ID);
			} 	
		}
	}
}
