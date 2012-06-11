/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.linking.GamlDiagnostic;
import msi.gama.lang.gaml.validation.*;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import com.google.inject.Inject;

/**
 * The class GamlResource.
 * 
 * @author drogoul
 * @since 24 avr. 2012
 * 
 */
public class GamlResource extends LazyLinkingResource {

	static int counter = 0;
	private final int count;
	private ISyntacticElement syntacticContents;

	private IGamlBuilderListener listener;
	@Inject
	GamlJavaValidator validator;

	public GamlResource() {

		// GuiUtils.debug("Instantiating a new resource");
		count = counter++;
	}

	@Override
	public String toString() {
		return "resource" + count + "[" + getURI() + "]";
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription model) {

		if ( listener != null ) {
			Set<String> exp = model == null ? Collections.EMPTY_SET : model.getExperimentNames();
			listener.validationEnded(exp, withErrors);
		}
	}

	public IModel doBuild() {
		return GamlBuilder.INSTANCE.build(this);
	}

	public ModelDescription doValidate() {
		long begin = System.currentTimeMillis();
		ModelDescription md = GamlBuilder.INSTANCE.validate(this);
		long end = System.currentTimeMillis();
		GuiUtils.debug("Validation of " + this + " took " + (end - begin) + " milliseconds");
		return md;
	}

	public void setListener(final IGamlBuilderListener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilderListener getListener() {
		return listener;
	}

	public void error(final String message, final EObject object) {
		if ( object == null ) { return; }
		if ( object.eResource() != this ) {
			((GamlResource) object.eResource()).error(message, object);
			return;
		}
		getErrors().add(
			new GamlDiagnostic("", new String[0], message, NodeModelUtils.getNode(object)));
	}

	@Override
	protected void unload(final EObject oldRootObject) {
		if ( oldRootObject != null ) {
			EList<Adapter> list = new BasicEList<Adapter>(oldRootObject.eAdapters());
			for ( Adapter adapter : list ) {
				if ( adapter instanceof ModelDescription ) {
					((ModelDescription) adapter).dispose();
				}
			}
		}
		super.unload(oldRootObject);
	}

	public void add(final GamlCompilationError e) {
		validator.add(e);
	}

	public ISyntacticElement getSyntacticContents() {
		if ( syntacticContents == null ) {
			buildSyntacticContents();
		}
		return syntacticContents;
	}

	public void eraseSyntacticContents() {
		if ( syntacticContents != null ) {
			syntacticContents.dispose();
		}
		syntacticContents = null;
	}

	public void buildSyntacticContents() {
		Model m = (Model) getContents().get(0);
		syntacticContents = new SyntacticStatement(MODEL, m);
		syntacticContents.setFacet(NAME, new LabelExpressionDescription(m.getName()));
		for ( final Import i : m.getImports() ) {
			final ISyntacticElement include = new SyntacticStatement(INCLUDE, i);
			include.setFacet(FILE, new LabelExpressionDescription(i.getImportURI()));
			syntacticContents.addChild(include);
		}
		convStatements(syntacticContents, m.getStatements());
	}

	private final void convStatements(final ISyntacticElement elt, final EList<Statement> ss) {
		for ( final Statement stm : ss ) {
			elt.addChild(convStatement(elt.getKeyword(), stm));
		}
	}

	private void addFacet(final Statement stm, final ISyntacticElement e, final String key,
		final IExpressionDescription expr) {
		if ( e.getFacet(key) != null ) {
			((GamlResource) stm.eResource()).add(new GamlCompilationError(
				"Duplicated definition of facet " + key + ". Only the last one will be considered",
				e, true));
		}
		e.setFacet(key, expr);
	}

	private final ISyntacticElement convStatement(final String upper, final Statement stm) {
		// If the initial statement is null, we return null
		if ( stm == null ) { return null; }
		// We catch its keyword
		String keyword = EGaml.getKey.caseStatement(stm);
		if ( keyword == null ) { return null; }
		// We see if we can provide the temporary fix for the "collision of save(s)" and the
		// "collision of species"
		if ( upper.equals(BATCH) && keyword.equals(SAVE) ) {
			keyword = SAVE_BATCH;
		} else if ( (upper.equals(DISPLAY) || upper.equals(POPULATION)) && keyword.equals(SPECIES) ) {
			keyword = POPULATION;
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
			IExpressionDescription fexpr = convExpr(f.getExpr());
			// In case it is null and the facet is a definition expr (eg. "returns"), we get the
			// name feature of the statement instead.
			if ( fexpr == null && f instanceof DefinitionFacetExpr ) {
				fexpr = convExpr((DefinitionFacetExpr) f);
			}
			addFacet(stm, elt, fname, fexpr);
		}

		// We do the same for the special case of the "ref" + "expr" facet found in statements
		GamlFacetRef ref = stm.getRef();
		if ( ref != null ) {
			String fname = ref.getRef();
			IExpressionDescription ed = convExpr(stm.getExpr());
			addFacet(stm, elt, fname, ed);
		}

		// If the statement is "if", we convert its potential "else" part and put it inside the
		// syntactic element (as the legacy GAML expects it)
		if ( keyword.equals(IKeyword.IF) ) {
			convElse(stm, elt);
		}

		// We add the dependencies (only for variable declarations)
		if ( !SymbolProto.nonVariableStatements.contains(keyword) ) {
			// GuiUtils.debug("Building var dependencies for " + keyword);
			String s = varDependenciesOf(stm);
			if ( !s.isEmpty() ) {
				elt.setFacet(DEPENDS_ON, new StringBasedExpressionDescription(s));
			}
			if ( !keyword.equals(VAR) && !keyword.equals(CONST) && !keyword.equals(EXPERIMENT) &&
				!keyword.equals(METHOD) ) { // FIXME Why are experiment and method here ??
				String type = elt.getLabel(TYPE);
				if ( type != null ) {
					if ( type.equals(keyword) ) {
						((GamlResource) stm.eResource()).add(new GamlCompilationError(
							"Duplicated declaration of type", elt, true));
					} else {
						((GamlResource) stm.eResource()).add(new GamlCompilationError(
							"Conflicting declaration of type (" + type + " and " + keyword +
								"), only the last one will be considered", elt, true));
					}
				}
			}
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = DescriptionFactory.getModelFactory().getOmissibleFacetForSymbol(keyword);

		if ( def != null && elt.getFacet(def) == null ) {
			IExpressionDescription ed = null;
			if ( stm instanceof Definition ) {
				ed = convExpr((Definition) stm);
			} else {
				ed = convExpr(stm.getExpr());
			}
			if ( ed != null ) {
				elt.setFacet(def, ed);
			}
		}
		// We modify the "var" and "const" declarations in order to keep only the type
		if ( keyword.equals(VAR) || keyword.equals(CONST) || keyword.equals(EXPERIMENT) ) {
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				((GamlResource) stm.eResource()).add(new GamlCompilationError(
					"The type of the variable is missing", elt));
			} else {
				elt.setKeyword(type);
			}
			if ( keyword.equals(CONST) ) {
				String constant = elt.getLabel(CONST);
				if ( constant != null && constant.equals(FALSE) ) {
					((GamlResource) stm.eResource()).add(new GamlCompilationError(
						"Is this variable constant or not ?", elt, true));
				}
				elt.setFacet(CONST, convExpr(EGaml.createTerminal(true)));
			}
		}
		// We apply the same conversion for methods (to get the name instead of the "method"
		// keyword)
		else if ( keyword.equals(METHOD) ) {
			String type = elt.getLabel(NAME);
			if ( type != null ) {
				elt.setKeyword(type);
			}
		}

		// We convert the block of the statement, if any
		Block block = stm.getBlock();
		if ( block != null ) {
			convStatements(elt, block.getStatements());
		}
		return elt;
	}

	static final StringBuilder sb = new StringBuilder();

	private final String varDependenciesOf(final Statement s) {
		sb.setLength(0);
		for ( FacetExpr facet : s.getFacets() ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) {
				for ( TreeIterator<EObject> tree = expr.eAllContents(); tree.hasNext(); ) {
					EObject obj = tree.next();
					if ( obj instanceof VariableRef ) {
						sb.append(" ").append(EGaml.getKey.caseVariableRef((VariableRef) obj));
					}
				}
			}
		}
		return sb.toString();
	}

	private final void convElse(final Statement stm, final ISyntacticElement elt) {
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

	private final IExpressionDescription convExpr(final Expression expr) {
		if ( expr != null ) { return new EcoreBasedExpressionDescription(expr); }
		return null;
	}

	private final IExpressionDescription convExpr(final DefinitionFacetExpr expr) {
		if ( expr != null && expr.getName() != null ) { return new EcoreBasedExpressionDescription(
			expr); }
		return null;
	}

	private final IExpressionDescription convExpr(final Definition expr) {
		if ( expr != null && expr.getName() != null ) { return new EcoreBasedExpressionDescription(
			expr); }
		return null;
	}

}
