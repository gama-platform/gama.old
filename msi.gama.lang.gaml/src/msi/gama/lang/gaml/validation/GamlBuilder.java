/**
 * Created by drogoul, 11 avr. 2012
 * 
 */
package msi.gama.lang.gaml.validation;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import java.io.IOException;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlResource;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.linking.GamlDiagnostic;
import msi.gama.lang.utils.*;
import msi.gama.runtime.GAMA;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.factories.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * The class GamlBuilder.
 * 
 * @author drogoul
 * @since 11 avr. 2012
 * 
 */
public class GamlBuilder {

	private final GamlResource resource;

	static {
		IExpressionFactory fact = GAMA.getExpressionFactory();
		fact.registerParser(new NewGamlExpressionCompiler());
	}

	public GamlBuilder(final GamlResource c) {
		resource = c;
	}

	public ModelDescription validate() {
		return getModelFactory().validate(parse());
	}

	public IModel build() {
		return getModelFactory().compile(parse());
	}

	private ModelStructure parse() {
		final Map<URI, ISyntacticElement> models = buildCompleteSyntacticTree();
		return new ModelStructure(resource.getURI().toString(), new ArrayList(models.values()));
	}

	public Map<URI, ISyntacticElement> buildCompleteSyntacticTree() {
		Model model = (Model) resource.getContents().get(0);
		final Map<URI, ISyntacticElement> models = new LinkedHashMap();
		LinkedHashSet<GamlResource> totalResources = new LinkedHashSet<GamlResource>();
		LinkedHashSet<GamlResource> newResources = new LinkedHashSet<GamlResource>();
		newResources.add(resource);
		while (!newResources.isEmpty()) {
			List<GamlResource> resourcesToConsider = new ArrayList<GamlResource>(newResources);
			newResources.clear();
			for ( GamlResource r : resourcesToConsider ) {
				if ( totalResources.add(r) ) {
					LinkedHashSet<GamlResource> imports =
						listImports((Model) r.getContents().get(0));
					newResources.addAll(imports);
				}
			}
		}
		for ( GamlResource r : totalResources ) {
			try {
				r.load(Collections.EMPTY_MAP);
			} catch (IOException e) {
				e.printStackTrace();
			}

			models.put(r.getURI(), r.getBuilder().convModel());
		}
		return models;
	}

	private LinkedHashSet<GamlResource> listImports(final Model model) {
		LinkedHashSet<GamlResource> imports = new LinkedHashSet<GamlResource>();
		for ( Import imp : model.getImports() ) {
			GamlResource r = (GamlResource) model.eResource();
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			GamlResource ir = (GamlResource) r.getResourceSet().getResource(iu, true);
			if ( ir != null ) {
				if ( !ir.getErrors().isEmpty() ) {
					r.getErrors().add(
						new GamlDiagnostic("", new String[] {}, "Imported file " +
							ir.getURI().lastSegment() + " has errors. Fix them first.",
							NodeModelUtils.findActualNodeFor(imp)));
				}
				imports.add(ir);
			}
		}
		return imports;
	}

	private ISyntacticElement convModel() {
		Model m = (Model) resource.getContents().get(0);
		final ISyntacticElement model = new SyntacticStatement(MODEL, m);
		model.setFacet(NAME, new LabelExpressionDescription(m.getName()));
		for ( final Import i : m.getImports() ) {
			final ISyntacticElement include = new SyntacticStatement(INCLUDE, i);
			include.setFacet(FILE, new LabelExpressionDescription(i.getImportURI()));
			model.addChild(include);
		}
		convStatements(model, m.getStatements());
		return model;
	}

	public void convStatements(final ISyntacticElement elt, final EList<Statement> ss) {
		for ( final Statement stm : ss ) {
			elt.addChild(convStatement(elt.getKeyword(), stm));
		}
	}

	private ISyntacticElement convStatement(final String upper, final Statement stm) {
		// If the initial statement is null, we return null
		if ( stm == null ) { return null; }
		// We catch its keyword
		String keyword = EGaml.getKey.caseStatement(stm);
		if ( keyword == null ) { return null; }
		// We see if we can provide the temporary fix for the "collision of save(s)"
		if ( upper.equals(EXPERIMENT) && keyword.equals(SAVE) ) {
			keyword = SAVE_BATCH;
		}
		final ISyntacticElement elt = new SyntacticStatement(keyword, stm);

		// We apply some conversions to the facets expressed in the statement
		for ( FacetExpr f : stm.getFacets() ) {
			String fname = EGaml.getKeyOf(f);
			// We change the "<-" and "->" symbols into full names
			if ( fname.equals("<-") ) {
				fname = keyword.equals(LET) || keyword.equals(SET) ? VALUE : INIT;
			} else if ( fname.equals("->") ) {
				fname = FUNCTION;
			}
			// We compute (and convert) the expression attached to the facet
			IExpressionDescription fexpr = conv(f.getExpr());
			// In case it is null and the facet is a definition expr (eg. "returns"), we get the
			// name feature of the statement instead.
			if ( fexpr == null && f instanceof DefinitionFacetExpr ) {
				fexpr = conv(EGaml.createTerminal(((DefinitionFacetExpr) f).getName()));
			}
			elt.setFacet(fname, fexpr);
		}
		// We modify the "var" and "const" declarations in order to keep only the type
		if ( keyword.equals(VAR) || keyword.equals(CONST) || keyword.equals(EXPERIMENT) ) {
			String type = elt.getLabel(TYPE);
			if ( type != null ) {
				elt.setKeyword(type);
			}
			if ( keyword.equals(CONST) ) {
				elt.setFacet(CONST, conv(EGaml.createTerminal(true)));
			}
		}

		// If the statement is "if", we convert its potential "else" part and put it inside the
		// syntactic element (as the legacy GAML expects it)
		if ( keyword.equals(IKeyword.IF) ) {
			convElse(stm, elt);
		}

		// We add the dependencies (only for variable declarations)
		if ( !SymbolMetaDescription.nonVariableStatements.contains(keyword) ) {
			Array a = varDependenciesOf(stm);
			if ( a != null ) {
				elt.setFacet(DEPENDS_ON, conv(a));
			}
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = DescriptionFactory.getModelFactory().getOmissibleFacetForSymbol(keyword);
		if ( def != null && elt.getFacet(def) == null ) {
			Expression expr =
				stm instanceof Definition ? EGaml.createTerminal(((Definition) stm).getName())
					: stm.getExpr();
			if ( expr != null ) {
				elt.setFacet(def, conv(expr));
			}
		}

		// We convert the block of the statement, if any
		Block block = stm.getBlock();
		if ( block != null ) {
			convStatements(elt, block.getStatements());
		}
		return elt;
	}

	public Array varDependenciesOf(final Statement s) {
		Array a = null;
		for ( FacetExpr facet : s.getFacets() ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) {
				for ( TreeIterator<EObject> tree = expr.eAllContents(); tree.hasNext(); ) {
					EObject obj = tree.next();
					if ( obj instanceof VariableRef ) {
						if ( a == null ) {
							a = EGaml.getFactory().createArray();
						}
						a.getExprs().add(
							EGaml.createTerminal(EGaml.getKey.caseVariableRef((VariableRef) obj)));
					}
				}
			}
		}
		return a;
	}

	public void convElse(final Statement stm, final ISyntacticElement elt) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			ISyntacticElement elseElt = new SyntacticStatement(ELSE, elseBlock);
			if ( elseBlock instanceof Statement ) {
				elseElt.addChild(convStatement(IF, (Statement) elseBlock));
			} else {
				convStatements(elseElt, ((Block) elseBlock).getStatements());
			}
			elt.addChild(elseElt);
		}
	}

	public IExpressionDescription conv(final Expression expr) {
		if ( expr != null ) { return new EcoreBasedExpressionDescription(expr); }
		return null;
	}

}
