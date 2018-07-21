/*********************************************************************************************
 *
 * 'GamlDocumentationProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.hover;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.inject.Inject;

import msi.gama.common.interfaces.IDocManager;
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
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VarDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;
import msi.gama.lang.gaml.ui.editor.GamlHyperlinkDetector;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.IUnits;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.commands.FileOpener;

public class GamlDocumentationProvider extends MultiLineCommentDocumentationProvider {

	@Inject protected GamlHyperlinkDetector detector;

	public String getOnlyComment(final EObject o) {
		return super.getDocumentation(o);
	}

	@Override
	public String getDocumentation(final EObject o) {
		if (o instanceof Import) { return "ctrl-click or cmd-click on the path to open this model in a new editor"; }
		if (o instanceof S_Global) { return getDocumentation(o.eContainer().eContainer()); }
		if (o instanceof StringLiteral) {
			final URI iu = detector.getURI((StringLiteral) o);
			if (iu != null) {
				if (FileOpener.isFileExistingInWorkspace(iu)) {
					final IFile file = FileOpener.getWorkspaceFile(iu);
					final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
					if (data != null) {
						String s = data.getDocumentation();
						if (s != null) {
							s = s.replace(Strings.LN, "<br/>");
							return s;
						}
					} else {
						final String ext = file.getFileExtension();
						return "This workspace " + ext + " file has no metadata associated with it";
					}
				} else { // absolute file
					final IFile file = FileOpener.getFileSystemFile(iu, o.eResource().getURI());
					if (file == null) { return "This file is outside the workspace. No further information is available, but you can nevertheless try to open it in an editor by crtl-clicking or cmd-clicking it"; }
					final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
					if (data != null) {
						String s = data.getDocumentation();
						if (s != null) {
							s = s.replace(Strings.LN, "<br/>");
							return s;
						}
					} else {
						final String ext = file.getFileExtension();
						return "This external " + ext + " file has no metadata associated with it";
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
			if (s instanceof S_Definition && ((S_Definition) s).getTkey() == o) {
				final IDocManager dm = GamlResourceServices.getResourceDocumenter();
				final IGamlDescription gd = dm.getGamlDocumentation(s);
				if (gd != null) { return gd.getDocumentation(); }
			}
		} else if (o instanceof Function) {
			final Function f = (Function) o;
			if (f.getLeft() instanceof ActionRef) {
				final ActionRef ref = (ActionRef) f.getLeft();
				final String temp = getDocumentation(ref.getRef());
				if (!temp.contains("No documentation")) { return temp; }
			}
		} else if (o instanceof VariableRef) {
			final VarDefinition vd = ((VariableRef) o).getRef();
			if (vd != null) {
				if (vd.eContainer() == null) {
					final IEObjectDescription desc = BuiltinGlobalScopeProvider.getVar(vd.getName());
					if (desc != null) { return desc.getUserData("doc"); }
				}
			}
		} else if (o instanceof UnitName) {
			final String name = ((UnitName) o).getRef().getName();
			final UnitConstantExpression exp = IUnits.UNITS_EXPR.get(name);
			if (exp != null) { return exp.getDocumentation(); }
		}

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