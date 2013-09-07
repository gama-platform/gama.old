/**
 * Created by drogoul, 5 f�vr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gaml.descriptions.*;
import msi.gaml.factories.*;
import msi.gaml.factories.DescriptionFactory.Documentation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;

public class GamlDocumentationProvider extends MultiLineCommentDocumentationProvider {

	@Override
	public String getDocumentation(final EObject o) {
		String comment = super.getDocumentation(o);
		if ( comment == null ) {
			comment = "";
		}
		if ( o instanceof VariableRef ) {
			comment = super.getDocumentation(((VariableRef) o).getRef());
		} else if ( o instanceof ActionRef ) {
			comment = super.getDocumentation(((ActionRef) o).getRef());
		}
		if ( comment == null ) {
			comment = "";
		} else {
			comment += "<br/>";
		}
		Documentation description = DescriptionFactory.getGamlDocumentation(o);
		if ( description == null && o instanceof TypeRef ) {
			description = DescriptionFactory.getGamlDocumentation(o.eContainer());
		}
		if ( description == null ) {
			if ( o instanceof Facet ) {
				String facetName = ((Facet) o).getKey();
				facetName = facetName.substring(0, facetName.length() - 1);
				EObject cont = o.eContainer();
				String key = EGaml.getKeyOf(cont);
				SymbolProto p = DescriptionFactory.getProto(key);
				if ( p != null ) {
					FacetProto f = p.getPossibleFacets().get(facetName);
					if ( f != null ) { return comment + "Facet " + facetName + " of " + key + "; " +
						(f.doc == null ? "" : f.doc); }
				}
				return comment + "Facet " + ((Facet) o).getKey();
			}

			return comment + "Not yet documented";
		}

		// FIXME : Highly experimental right now
		// Try to grab the comment preceding the referenced object
		// Only computed as a proof of concept for variables in species.

		// if ( description instanceof VariableExpression ) {
		// VariableExpression v = (VariableExpression) description;
		// String name = v.getName();
		// IDescription sd = v.getDefinitionDescription();
		// if ( sd instanceof SpeciesDescription ) {
		// VariableDescription vd = ((SpeciesDescription) sd).getVariable(name);
		// if ( vd != null ) {
		// EObject ref = vd.getUnderlyingElement(null);
		// if ( ref != null ) {
		// comment = super.getDocumentation(ref);
		// }
		// }
		// }
		// }

		return comment + description.getDocumentation();
	}
}