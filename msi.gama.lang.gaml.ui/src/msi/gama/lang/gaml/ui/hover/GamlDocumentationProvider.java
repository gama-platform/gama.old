/*********************************************************************************************
 * 
 * 
 * 'GamlDocumentationProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.hover;

import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;

public class GamlDocumentationProvider extends MultiLineCommentDocumentationProvider {

	@Override
	public String getDocumentation(final EObject o) {
		GuiUtils.debug("GamlDocumentationProvider.getDocumentation for " + o);
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
		if ( o instanceof TypeRef ) {
			Statement s = EGaml.getStatement(o);
			String key = EGaml.getKeyOf(s);
			if ( s instanceof S_Definition && ((S_Definition) s).getTkey() == o ) { return getDocumentation(s); }
		}
		IGamlDescription description = DescriptionFactory.getGamlDocumentation(o);

		// TODO Add a swtich for constants

		if ( description == null ) {
			if ( o instanceof Facet ) {
				String facetName = ((Facet) o).getKey();
				facetName = facetName.substring(0, facetName.length() - 1);
				EObject cont = o.eContainer();
				String key = EGaml.getKeyOf(cont);
				SymbolProto p = DescriptionFactory.getProto(key, null);
				if ( p != null ) {
					FacetProto f = p.getPossibleFacets().get(facetName);
					if ( f != null ) { return comment + Strings.LN + f.getDocumentation(); }
				}
				return comment;
			}
			if ( comment.isEmpty() ) { return null; }
			return comment + Strings.LN + "No documentation yet";
		}

		return comment + description.getDocumentation();
	}

}