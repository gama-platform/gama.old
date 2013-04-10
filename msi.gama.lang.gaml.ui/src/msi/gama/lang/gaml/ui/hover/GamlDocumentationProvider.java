/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gaml.descriptions.*;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;

public class GamlDocumentationProvider extends MultiLineCommentDocumentationProvider {

	@Override
	public String getDocumentation(final EObject o) {
		String comment = super.getDocumentation(o);

		IGamlDescription description = DescriptionFactory.getGamlDescription(o);
		if ( description == null ) { return comment + "Not yet documented"; }

		// FIXME : Highly experimental right now
		// Try to grab the comment preceding the referenced object
		// Only computed as a proof of concept for variables in species.

		if ( description instanceof VariableExpression ) {
			VariableExpression v = (VariableExpression) description;
			String name = v.getName();
			IDescription sd = v.getDefinitionDescription();
			if ( sd instanceof SpeciesDescription ) {
				VariableDescription vd = ((SpeciesDescription) sd).getVariable(name);
				if ( vd != null ) {
					EObject ref = vd.getUnderlyingElement(null);
					if ( ref != null ) {
						comment = super.getDocumentation(ref);
					}
				}
			}
		}
		if ( comment == null ) {
			comment = "";
		} else {
			comment += "<br/>";
		}

		return comment + description.getDocumentation();
	}
}