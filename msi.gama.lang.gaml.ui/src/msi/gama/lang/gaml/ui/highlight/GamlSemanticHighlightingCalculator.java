/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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
