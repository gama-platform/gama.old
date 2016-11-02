/*********************************************************************************************
 *
 *
 * 'GamlDocumentationProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.hover;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;

import com.google.inject.Inject;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Global;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.ui.editor.GamlHyperlinkDetector;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;

public class GamlDocumentationProvider extends MultiLineCommentDocumentationProvider {

	@Inject protected GamlHyperlinkDetector detector;

	@Override
	public String getDocumentation(final EObject o) {
		if (o instanceof Import) { return "ctrl-click or cmd-click on the path to open this model in a new editor"; }
		if (o instanceof S_Global) { return getDocumentation(o.eContainer().eContainer()); }
		if (o instanceof StringLiteral) {
			final URI iu = detector.getURI((StringLiteral) o);
			if (iu != null) {
				final IFile file = detector.getFile(iu);
				final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
				if (data != null) {
					String s = data.getDocumentation();
					if (s != null) {
						s = s.replace(Strings.LN, "<br/>");
						return s;
					}
				}
			}
		}

		String comment = super.getDocumentation(o);
		if (comment == null) {
			comment = "";
		}
		if (o instanceof VariableRef) {
			comment = super.getDocumentation(((VariableRef) o).getRef());
		} else if (o instanceof ActionRef) {
			comment = super.getDocumentation(((ActionRef) o).getRef());
		}
		if (comment == null) {
			comment = "";
		} else {
			comment += "<br/>";
		}
		if (o instanceof TypeRef) {
			final Statement s = EGaml.getStatement(o);
			if (s instanceof S_Definition && ((S_Definition) s).getTkey() == o) { return comment
					+ GamlResourceServices.getResourceDocumenter().getGamlDocumentation(s).getDocumentation(); }
		} else if (o instanceof Function) {
			final Function f = (Function) o;
			if (f.getAction() instanceof ActionRef) {
				final ActionRef ref = (ActionRef) f.getAction();
				final String temp = getDocumentation(ref.getRef());
				if (!temp.contains("No documentation"))
					return temp;
			}
		} else if (o instanceof VariableRef) {
			if (((VariableRef) o).getRef() != null) { return ""; }
		}

		// else if (o instanceof VariableRef) {
		// return getDocumentation(((VariableRef) o).getRef());
		// }
		final IGamlDescription description = GamlResourceServices.getResourceDocumenter().getGamlDocumentation(o);

		// TODO Add a swtich for constants

		if (description == null) {
			if (o instanceof Facet) {
				String facetName = ((Facet) o).getKey();
				facetName = facetName.substring(0, facetName.length() - 1);
				final EObject cont = o.eContainer();
				final String key = EGaml.getKeyOf(cont);
				final SymbolProto p = DescriptionFactory.getProto(key, null);
				if (p != null) {
					final FacetProto f = p.getPossibleFacets().get(facetName);
					if (f != null) { return comment + Strings.LN + f.getDocumentation(); }
				}
				return comment;
			}
			if (comment.isEmpty()) { return null; }
			return comment + Strings.LN + "No documentation.";
		}

		return comment + description.getDocumentation();
	}

}