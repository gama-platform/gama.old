/*******************************************************************************************************
 *
 * GamlHoverDocumentationProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.hover;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;

import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Do;
import msi.gama.lang.gaml.gaml.S_Global;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.UnitFakeDefinition;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VarDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.ui.editor.GamlHyperlinkDetector;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.expressions.units.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.interfaces.IGamlDescription;
import msi.gaml.operators.Strings;
import msi.gaml.statements.DoStatement;
import msi.gaml.types.Types;

/**
 * The class GamlHoverDocumentationProvider.
 *
 * @author drogoul
 * @since 30 déc. 2023
 *
 */
public class GamlHoverDocumentationProvider extends GamlSwitch<IGamlDescription> {

	/** The detector. */
	@Inject protected GamlHyperlinkDetector detector;

	/** The documenter. */
	private final IDocManager documenter = GamlResourceServices.getResourceDocumenter();

	/**
	 * The Doc.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 déc. 2023
	 */
	record Result(String title, String doc) implements IGamlDescription {

		/**
		 * Gets the documentation.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the documentation
		 * @date 30 déc. 2023
		 */
		@Override
		public Doc getDocumentation() {

			return new SimpleDoc() {

				@Override
				public String get() {
					return doc;
				}

			};
		}

		/**
		 * Gets the title.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the title
		 * @date 30 déc. 2023
		 */
		@Override
		public String getTitle() { return title; }
	}

	/**
	 * Gets the documentation attached to an EObject or null if no doc can be found. Relies first on polymorphism
	 * through the use of {@link GamlSwitch} and then on specific methods
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param o
	 *            the o
	 * @return the doc
	 * @date 30 déc. 2023
	 */
	IGamlDescription getDoc(final EObject o) {
		int id = o.eClass().getClassifierID();
		IGamlDescription result = doSwitch(id, o);
		if (result == null) {
			if (o instanceof Facet facet) {
				result = specialCaseFacet(facet);
			} else if (o instanceof VariableRef vr) {
				result = specialCaseVariableRef(vr);
			} else if (o instanceof TypeRef type) {
				result = specialCaseTypeRef(type);
			} else {
				final Statement s = EGaml.getInstance().getStatement(o);
				if (s != null && s != o && DescriptionFactory.isStatementProto(EGaml.getInstance().getKeyOf(o))) {
					result = getDoc(s);
				}
			}
		}
		return result;
	}

	@Override
	public IGamlDescription caseImport(final Import imp) {
		String uri = imp.getImportURI();
		uri = uri.substring(uri.lastIndexOf('/') + 1);
		final String model = imp.getName() != null ? "micro-model" : "model";
		String title = "Import of the " + model + " defined in <i>" + uri + "</i>";
		String doc = "ctrl-click or cmd-click on the path to open this model in a new editor";
		return new Result(title, doc);
	}

	@Override
	public IGamlDescription caseS_Global(final S_Global global) {
		EObject model = global.eContainer().eContainer();
		String title = "Global section of <i>" + getDoc(model).getTitle() + "</i>";
		String doc = "";
		return new Result(title, doc);
	}

	@Override
	public IGamlDescription caseStringLiteral(final StringLiteral string) {
		final URI iu = detector.getURI(string);
		if (iu != null) {
			String doc = "";
			IFile file;
			if (FileUtils.isFileExistingInWorkspace(iu)) {
				file = FileUtils.getWorkspaceFile(iu);
				final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
				if (data == null) {
					final String ext = file.getFileExtension();
					doc = "This workspace " + ext + " file has no metadata associated with it";
				} else {
					String s = data.getDocumentation();
					if (s != null) { doc = s.replace(Strings.LN, "<br/>"); }
				}
			} else { // absolute file
				file = FileUtils.createLinkToExternalFile(string.getOp(), string.eResource().getURI());
				if (file == null) {
					doc = "This file is outside the workspace and cannot be found.";
				} else {
					final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
					if (data == null) {
						final String ext = file.getFileExtension();
						doc = "This external " + ext + " file has no metadata associated with it";
					} else {
						String s = data.getDocumentation();
						if (s != null) { doc = s.replace(Strings.LN, "<br/>"); }
					}
				}
			}
			if (file != null) return new Result("File " + file.getFullPath(), doc);
		}
		return null;
	}

	@Override
	public IGamlDescription caseTypeRef(final TypeRef type) {
		// final Statement s = EGaml.getInstance().getStatement(type);
		// if (s instanceof S_Definition sd && sd.getTkey() == type) {
		// final IGamlDescription gd = documenter.getGamlDocumentation(s);
		// if (gd != null) return gd;
		// }
		return null;
	}

	@Override
	public IGamlDescription caseFacet(final Facet facet) {
		// CASE do run_thread interval: 2#s;
		if (facet.eContainer() instanceof S_Do sdo && sdo.getExpr() instanceof VariableRef vr) {
			String key = EGaml.getInstance().getKeyOf(facet);
			if (!DoStatement.DO_FACETS.contains(key)) {
				String title = "Argument " + key + " of action " + EGaml.getInstance().getNameOfRef(sdo.getExpr());
				IGamlDescription action = documenter.getGamlDocumentation(vr);
				String doc = action == null ? "" : action.getDocumentation().get(key).get();
				return new Result(title, doc);
			}
		}
		return null;
	}

	@Override
	public IGamlDescription caseArgumentPair(final ArgumentPair pair) {
		if (pair.eContainer() instanceof ExpressionList el && el.eContainer() instanceof Array array
				&& array.eContainer() instanceof Facet facet) {
			// CASE do run_thread with: [interval::2#s];
			if (facet.eContainer() instanceof S_Do sdo && sdo.getExpr() instanceof VariableRef vr) {
				String key = pair.getOp();
				if (!DoStatement.DO_FACETS.contains(key)) {
					String title = "Argument " + key + " of action " + EGaml.getInstance().getNameOfRef(vr);
					IGamlDescription action = documenter.getGamlDocumentation(vr);
					String doc = action == null ? "" : action.getDocumentation().get(key).get();
					return new Result(title, doc);
				}
			} else
			// CASE create xxx with: [var::yyy]
			if (facet.eContainer() instanceof Statement sdo && IKeyword.CREATE.equals(sdo.getKey())) {
				String key = pair.getOp();
				IGamlDescription species = documenter.getGamlDocumentation(sdo.getExpr());
				if (species != null) {
					String title = "Attribute " + key + " defined in " + species.getTitle();
					String doc = species.getDocumentation().get(key).get();
					return new Result(title, doc);
				}

			}
		}
		return null;
	}

	@Override
	public IGamlDescription caseVariableRef(final VariableRef var) {
		// CASE do run_thread with: (interval::2#s);
		if (var.eContainer() instanceof Parameter pair && pair.eContainer() instanceof ExpressionList el
				&& el.eContainer() instanceof Facet facet && facet.eContainer() instanceof S_Do sdo
				&& sdo.getExpr() instanceof VariableRef v) {
			String key = EGaml.getInstance().getKeyOf(pair);
			if (!DoStatement.DO_FACETS.contains(key)) {
				String title = "Argument " + key + " of action " + EGaml.getInstance().getNameOfRef(sdo.getExpr());
				IGamlDescription action = documenter.getGamlDocumentation(v);
				String doc = action == null ? "" : action.getDocumentation().get(key).get();
				return new Result(title, doc);
			}
		}
		// CASE do run_thread (interval: 2#s); unknown aa <- self.run_thread (interval: 2#s); aa <- run_thread
		// (interval: 2#s);
		if (var.eContainer() instanceof Parameter param && param.eContainer() instanceof ExpressionList el
				&& el.eContainer() instanceof Function function && function.getLeft() instanceof ActionRef ar) {
			final IGamlDescription description = documenter.getGamlDocumentation(function);
			if (description != null) {
				VarDefinition vd = var.getRef();
				String title = "Argument " + vd.getName() + " of action "
						+ EGaml.getInstance().getNameOfRef(function.getLeft());
				String doc = description.getDocumentation().get(vd.getName()).get();
				return new Result(title, doc);
			}
		}
		// Case of species xxx skills: [skill]
		if (var.eContainer() instanceof ExpressionList el && el.eContainer() instanceof Array array
				&& array.eContainer() instanceof Facet facet && facet.getKey().startsWith(IKeyword.SKILLS)) {
			VarDefinition vd = var.getRef();
			String name = vd.getName();
			SkillDescription skill = GamaSkillRegistry.INSTANCE.get(name);
			if (skill != null) return skill;
		}
		return null;
	}

	@Override
	public IGamlDescription caseFunction(final Function function) {
		final ActionRef ref = function.getLeft() instanceof ActionRef ? (ActionRef) function.getLeft() : null;
		if (ref != null && ref.getRef() instanceof S_Definition def && def.getBlock() != null) {
			IGamlDescription doc = getDoc(def);
			if (doc != null) return doc;
		}
		return null;
	}

	@Override
	public IGamlDescription caseUnitName(final UnitName un) {
		final UnitFakeDefinition fake = un.getRef();
		if (fake != null) {
			final UnitConstantExpression unit = GAML.UNITS.get(fake.getName());
			if (unit != null) return unit;
		}
		return null;
	}

	@Override
	public IGamlDescription defaultCase(final EObject o) {
		return documenter.getGamlDocumentation(o);
	}

	/**
	 * Last chance to build a documentation in case we have a reference to a variable which is not documented itself
	 * (like in create xxx with: [var: yyy])
	 *
	 * @param vr
	 * @return
	 */
	private IGamlDescription specialCaseVariableRef(final VariableRef vr) {
		VarDefinition vd = vr.getRef();
		return documenter.getGamlDocumentation(vd);
	}

	/**
	 * Last chance to build a documentation in case the facet has not correctly been documented
	 *
	 * @param facet
	 * @return
	 */
	private IGamlDescription specialCaseFacet(final Facet facet) {
		String facetName = facet.getKey();
		if (facetName.endsWith(":")) { facetName = facetName.substring(0, facetName.length() - 1); }
		final EObject cont = facet.eContainer();
		final String key = EGaml.getInstance().getKeyOf(cont);
		final SymbolProto p = DescriptionFactory.getProto(key, null);
		if (p != null) return p.getPossibleFacets().get(facetName);
		return null;
	}

	/**
	 * If the type is not correctly documented
	 *
	 * @param type
	 * @return
	 */
	private IGamlDescription specialCaseTypeRef(final TypeRef type) {
		String name = EGaml.getInstance().getKeyOf(type);
		IGamlDescription result = Types.get(name);
		return result;
	}

}
