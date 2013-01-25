/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
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
	static final List<String> TOP_LEVEL_STATEMENTS = Arrays.asList(GLOBAL, SPECIES, GRID, OUTPUT);

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
		GuiUtils.debug("Validation of " + md + " took " + (end - begin) + " milliseconds");
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
		ISyntacticElement elt = null;
		/**
		 * Some syntactic rewritings to remove ambiguities inherent to the grammar of GAML and
		 * translate the new compact syntax into the legacy facet-based one
		 */
		if ( upper.equals(BATCH) && keyword.equals(SAVE) ) {
			keyword = SAVE_BATCH;
		} else if ( upper.equals(DISPLAY) || upper.equals(POPULATION) ) {
			if ( keyword.equals(SPECIES) ) {
				keyword = POPULATION;
			} else if ( keyword.equals(GRID) ) {
				keyword = GRID_POPULATION;
			}

		} else if ( keyword.equals(":=") || keyword.equals("<-") ) {
			// Translation of "var := value" or "var <- value" to "set var value: value"
			elt = new SyntacticStatement(SET, stm);
			IExpressionDescription value = convExpr(EGaml.getValueOf(stm));
			addFacet(stm, elt, VALUE, value);
			keyword = SET;
		} else if ( keyword.equals("<<") || keyword.equals("++") ) {
			// Translation of "container << item" or "container ++ item" to
			// "add item: item to: container"
			elt = new SyntacticStatement(ADD, stm);
			addFacet(stm, elt, TO, convExpr(EGaml.getExprOf(stm)));
			addFacet(stm, elt, ITEM, convExpr(EGaml.getValueOf(stm)));
			keyword = ADD;
		} else if ( keyword.equals(">>") || keyword.equals("--") ) {
			// Translation of "container >> item" or "container -- item" to
			// "remove item: item from: container"
			elt = new SyntacticStatement(REMOVE, stm);
			addFacet(stm, elt, FROM, convExpr(EGaml.getExprOf(stm)));
			addFacet(stm, elt, ITEM, convExpr(EGaml.getValueOf(stm)));
			keyword = REMOVE;
		}
		Expression expr = EGaml.getExprOf(stm);

		if ( keyword.equals(EQUATION) ) {
			addFacet(stm, elt, EQUATION_LEFT, convExpr(stm.getFunction()));
			addFacet(stm, elt, EQUATION_RIGHT, convExpr(EGaml.getExprOf(stm)));
		}

		if ( keyword.equals(SET) && expr instanceof Access ) {
			// Translation of "container[index] <- value" to
			// "put item: value in: container at: index"
			if ( elt == null ) {
				elt = new SyntacticStatement(PUT, stm);
			} else {
				elt.setKeyword(PUT);
			}
			addFacet(stm, elt, ITEM, elt.getFacets().remove(VALUE));
			addFacet(stm, elt, IN, convExpr(expr.getLeft()));
			EList<Expression> args = ((Access) expr).getArgs();
			if ( args.size() == 1 ) {
				addFacet(stm, elt, AT, convExpr(args.get(0)));
			} else {
				Point p = EGaml.getFactory().createPoint();
				p.setLeft(args.get(0));
				// Trick as args is modified by setLeft()
				p.setRight(args.get(0));
				addFacet(stm, elt, AT, convExpr(p));
			}
			keyword = PUT;
		}
		if ( elt == null ) {
			elt = new SyntacticStatement(keyword, stm);
		}
		if ( !SymbolProto.nonVariableStatements.contains(keyword) &&
			!TOP_LEVEL_STATEMENTS.contains(upper) ) {
			// Translation of "type var ..." to "let var type: type ..." if we are not in a
			// top-level statement (i.e. not in the declaration of a species or an experiment)
			elt = new SyntacticStatement(LET, stm);
			addFacet(stm, elt, TYPE, convExpr(EGaml.createTerminal(keyword)));
			keyword = LET;
		}

		Contents of = stm.getOf();
		if ( of != null ) {
			// Translation of "type<contents> ..." to "type of: contents..."
			String type = of.getType();
			if ( type != null ) {
				addFacet(stm, elt, OF, convExpr(EGaml.createTerminal(type)));
			}
		}

		// We apply some conversions to the facets expressed in the statement
		for ( Facet f : EGaml.getFacetsOf(stm) ) {
			String fname = EGaml.getKeyOf(f);
			// We change the "<-" and "->" symbols into full names
			if ( fname.equals("<-") ) {
				fname = keyword.equals(LET) || keyword.equals(SET) ? VALUE : INIT;
			} else if ( fname.equals("->") ) {
				fname = FUNCTION;
			}
			// We compute (and convert) the expression attached to the facet
			IExpressionDescription fexpr = convExpr(f.getExpr());
			if ( fexpr == null ) {
				fexpr = convExpr(f);
			}
			addFacet(stm, elt, fname, fexpr);
		}

		// If the statement is "if", we convert its potential "else" part and put it inside the
		// syntactic element (as the legacy GAML expects it)
		if ( keyword.equals(IF) ) {
			convElse(stm, elt);
		}

		// We add the dependencies (only for variable declarations)
		if ( !SymbolProto.nonVariableStatements.contains(keyword) ) {
			// GuiUtils.debug("Building var dependencies for " + keyword);
			String s = varDependenciesOf(stm);
			if ( !s.isEmpty() ) {
				elt.setFacet(DEPENDS_ON, new StringBasedExpressionDescription(s));
			}
			if ( !keyword.equals(VAR) && !keyword.equals(CONST) ) {
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

		if ( def != null && !def.isEmpty() && elt.getFacet(def) == null ) {
			IExpressionDescription ed = findExpr(stm);
			if ( ed != null ) {
				elt.setFacet(def, ed);
			}
		}

		// We modify the "var", "const" and "experiment" declarations in order to put the type as
		// the keyword
		if ( keyword.equals(VAR) || keyword.equals(CONST) || keyword.equals(EXPERIMENT) ) {
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				((GamlResource) stm.eResource()).add(new GamlCompilationError(
					"Facet 'type' is missing", elt));
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

	static final StringBuilder sb = new StringBuilder(30);

	private final String varDependenciesOf(final Statement s) {
		sb.setLength(0);
		// for ( FacetExpr facet : s.getFacets() ) {
		for ( Facet facet : EGaml.getFacetsOf(s) ) {
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
			// EObject elseBlock = ((IfStatement) stm).getElse();
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

	private final IExpressionDescription convExpr(final Facet expr) {
		if ( expr != null && expr.getName() != null ) { return new EcoreBasedExpressionDescription(
			expr); }
		return null;
	}

	private final IExpressionDescription findExpr(final Statement stm) {
		if ( stm == null ) { return null; }
		// The order below should be important
		if ( EGaml.getNameOf(stm) != null ) { return new EcoreBasedExpressionDescription(stm); }
		if ( EGaml.getExprOf(stm) != null ) { return convExpr(EGaml.getExprOf(stm)); } // ??

		return null;
	}

}
